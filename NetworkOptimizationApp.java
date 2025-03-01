package DSA_SEM_3_Coursework;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class NetworkOptimizationApp extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final int NODE_SIZE = 20;

    private JPanel canvas;
    private List<Node> nodes;
    private List<Edge> edges;
    private Node selectedNode;
    private JLabel statusBar;

    public NetworkOptimizationApp() {
        setTitle("Network Optimization Tool");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize data structures
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        // Canvas for drawing the network
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawNetwork(g);
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCanvasClick(e);
            }
        });

        // Control panel for buttons
        JPanel controlPanel = new JPanel();
        JButton addNodeButton = new JButton("Add Node");
        JButton addEdgeButton = new JButton("Add Edge");
        JButton removeNodeButton = new JButton("Remove Node");
        JButton removeEdgeButton = new JButton("Remove Edge");
        JButton optimizeButton = new JButton("Optimize");
        JButton shortestPathButton = new JButton("Calculate Shortest Path");

        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        removeNodeButton.addActionListener(e -> removeNode());
        removeEdgeButton.addActionListener(e -> removeEdge());
        optimizeButton.addActionListener(e -> optimizeNetwork());
        shortestPathButton.addActionListener(e -> calculateShortestPath());

        controlPanel.add(addNodeButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(removeNodeButton);
        controlPanel.add(removeEdgeButton);
        controlPanel.add(optimizeButton);
        controlPanel.add(shortestPathButton);

        // Status bar for real-time metrics
        statusBar = new JLabel("Total Cost: 0 | Average Latency: 0 ms");
        statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add components to the frame
        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusBar, BorderLayout.NORTH);

        setVisible(true);
    }

    private void drawNetwork(Graphics g) {
        // Draw edges
        for (Edge edge : edges) {
            g.setColor(edge.highlighted ? Color.RED : Color.BLACK);
            g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            g.drawString("C: " + edge.cost + ", B: " + edge.bandwidth, 
                         (edge.from.x + edge.to.x) / 2, (edge.from.y + edge.to.y) / 2);
        }

        // Draw nodes
        for (Node node : nodes) {
            g.setColor(node == selectedNode ? Color.RED : Color.BLUE);
            g.fillOval(node.x - NODE_SIZE / 2, node.y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
            g.drawString(node.id, node.x + NODE_SIZE / 2, node.y + NODE_SIZE / 2);
        }
    }

    private void handleCanvasClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Check if a node is clicked
        for (Node node : nodes) {
            if (Math.abs(node.x - x) <= NODE_SIZE / 2 && Math.abs(node.y - y) <= NODE_SIZE / 2) {
                selectedNode = node;
                canvas.repaint();
                return;
            }
        }

        // If no node is clicked, add a new node
        nodes.add(new Node("N" + (nodes.size() + 1), x, y));
        selectedNode = null;
        canvas.repaint();
        updateMetrics();
    }

    private void addNode() {
        JOptionPane.showMessageDialog(this, "Click on the canvas to add a node.");
    }

    private void addEdge() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "At least two nodes are required to add an edge.");
            return;
        }

        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String fromId = (String) JOptionPane.showInputDialog(this, "Select source node:", "Add Edge", 
                JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String toId = (String) JOptionPane.showInputDialog(this, "Select target node:", "Add Edge", 
                JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);

        if (fromId == null || toId == null || fromId.equals(toId)) {
            return;
        }

        int cost = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter cost:"));
        int bandwidth = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter bandwidth:"));

        Node from = nodes.stream().filter(n -> n.id.equals(fromId)).findFirst().orElse(null);
        Node to = nodes.stream().filter(n -> n.id.equals(toId)).findFirst().orElse(null);

        if (from != null && to != null) {
            edges.add(new Edge(from, to, cost, bandwidth));
            canvas.repaint();
            updateMetrics();
        }
    }

    private void removeNode() {
        if (selectedNode == null) {
            JOptionPane.showMessageDialog(this, "Please select a node to remove.");
            return;
        }

        edges.removeIf(edge -> edge.from == selectedNode || edge.to == selectedNode);
        nodes.remove(selectedNode);
        selectedNode = null;
        canvas.repaint();
        updateMetrics();
    }

    private void removeEdge() {
        if (edges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No edges to remove.");
            return;
        }

        String[] edgeLabels = edges.stream()
                .map(edge -> edge.from.id + " -> " + edge.to.id)
                .toArray(String[]::new);
        String selectedEdge = (String) JOptionPane.showInputDialog(this, "Select edge to remove:", 
                "Remove Edge", JOptionPane.PLAIN_MESSAGE, null, edgeLabels, edgeLabels[0]);

        if (selectedEdge != null) {
            edges.removeIf(edge -> (edge.from.id + " -> " + edge.to.id).equals(selectedEdge));
            canvas.repaint();
            updateMetrics();
        }
    }

    private void optimizeNetwork() {
        // Use Kruskal's algorithm to find the minimum spanning tree
        List<Edge> mst = kruskalMST();
        edges = mst;
        canvas.repaint();
        updateMetrics();
        JOptionPane.showMessageDialog(this, "Network optimized using Kruskal's algorithm.");
    }

    private List<Edge> kruskalMST() {
        List<Edge> mst = new ArrayList<>();
        Collections.sort(edges, Comparator.comparingInt(e -> e.cost));

        DisjointSet disjointSet = new DisjointSet(nodes.size());
        for (Edge edge : edges) {
            int fromIndex = nodes.indexOf(edge.from);
            int toIndex = nodes.indexOf(edge.to);

            if (disjointSet.find(fromIndex) != disjointSet.find(toIndex)) {
                mst.add(edge);
                disjointSet.union(fromIndex, toIndex);
            }
        }

        return mst;
    }

    private void calculateShortestPath() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "At least two nodes are required to calculate the shortest path.");
            return;
        }

        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String fromId = (String) JOptionPane.showInputDialog(this, "Select source node:", "Shortest Path", 
                JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String toId = (String) JOptionPane.showInputDialog(this, "Select target node:", "Shortest Path", 
                JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);

        if (fromId == null || toId == null || fromId.equals(toId)) {
            return;
        }

        Node from = nodes.stream().filter(n -> n.id.equals(fromId)).findFirst().orElse(null);
        Node to = nodes.stream().filter(n -> n.id.equals(toId)).findFirst().orElse(null);

        if (from != null && to != null) {
            List<Node> path = dijkstra(from, to);
            if (path != null) {
                // Highlight the path
                for (Edge edge : edges) {
                    edge.highlighted = false;
                    for (int i = 0; i < path.size() - 1; i++) {
                        if ((edge.from == path.get(i) && edge.to == path.get(i + 1)) ||
                            (edge.from == path.get(i + 1) && edge.to == path.get(i))) {
                            edge.highlighted = true;
                        }
                    }
                }
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Shortest path: " + path);
            } else {
                JOptionPane.showMessageDialog(this, "No path found.");
            }
        }
    }

    private List<Node> dijkstra(Node start, Node end) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current == end) {
                break;
            }

            for (Edge edge : edges) {
                if (edge.from == current || edge.to == current) {
                    Node neighbor = (edge.from == current) ? edge.to : edge.from;
                    int latency = 1000 / edge.bandwidth; // Latency = 1000 / bandwidth
                    int newDistance = distances.get(current) + latency;
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previous.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        if (previous.get(end) == null) {
            return null;
        }

        List<Node> path = new ArrayList<>();
        for (Node node = end; node != null; node = previous.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }

    private void updateMetrics() {
        int totalCost = edges.stream().mapToInt(edge -> edge.cost).sum();
        double totalLatency = edges.stream().mapToDouble(edge -> 1000.0 / edge.bandwidth).sum();
        double averageLatency = totalLatency / edges.size();
        statusBar.setText("Total Cost: " + totalCost + " | Average Latency: " + String.format("%.2f", averageLatency) + " ms");
    }

    public static void main(String[] args) {
        new NetworkOptimizationApp();
    }
}

class Node {
    String id;
    int x, y;

    public Node(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return id;
    }
}

class Edge {
    Node from, to;
    int cost, bandwidth;
    boolean highlighted;

    public Edge(Node from, Node to, int cost, int bandwidth) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.bandwidth = bandwidth;
        this.highlighted = false;
    }
}

class DisjointSet {
    int[] parent;

    public DisjointSet(int size) {
        parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        parent[find(x)] = find(y);
    }
}
