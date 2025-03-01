package DSA_SEM_3_Coursework;

import java.util.*;

public class MinimumCostForNetwork {
    public static void main(String[] args) {
        // Test case
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minCost(n, modules, connections)); // Expected: 3
    }

    public static int minCost(int n, int[] modules, int[][] connections) {
        // List of edges: [u, v, cost]
        List<int[]> edges = new ArrayList<>();
        
        // Add edges from virtual node 0 to each device
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{0, i + 1, modules[i]});
        }
        
        // Add connection edges (1-based to 0-based internally)
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0], conn[1], conn[2]});
        }
        
        // Sort edges by cost
        edges.sort(Comparator.comparingInt(e -> e[2]));
        
        // Union-Find for MST
        UnionFind uf = new UnionFind(n + 1); // +1 for virtual node
        int totalCost = 0;
        int edgesUsed = 0;
        
        // Kruskal's algorithm
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int cost = edge[2];
            if (uf.union(u, v)) {
                totalCost += cost;
                edgesUsed++;
                if (edgesUsed == n) break; // MST has n edges with virtual node
            }
        }
        
        // Check if all devices are connected
        if (edgesUsed < n) return -1; // Not fully connected
        return totalCost;
    }
}

// Union-Find data structure
class UnionFind {
    private int[] parent;
    private int[] rank;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) return false;
        
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}