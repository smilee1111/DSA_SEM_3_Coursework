package DSA_SEM_3_Coursework;

import java.util.*;

public class MinimumNoOfRoads {
    public static void main(String[] args) {
        // Test cases
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println(minRoads(packages1, roads1)); // Output: 2

        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println(minRoads(packages2, roads2)); // Output: 2
    }

    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length;
        // Build adjacency list for the graph (bidirectional)
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            int u = road[0];
            int v = road[1];
            graph.get(u).add(v);
            graph.get(v).add(u); // Bidirectional
        }

        int minRoads = Integer.MAX_VALUE;
        // Try starting at each location
        for (int start = 0; start < n; start++) {
            int roadsTraversed = findMinRoadsFromStart(start, packages, graph);
            minRoads = Math.min(minRoads, roadsTraversed);
        }

        return minRoads == Integer.MAX_VALUE ? -1 : minRoads;
    }

    private static int findMinRoadsFromStart(int start, int[] packages, List<List<Integer>> graph) {
        int n = packages.length;
        boolean[] collected = new boolean[n]; // Track collected packages
        int packagesToCollect = 0;
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) packagesToCollect++;
        }
        if (packagesToCollect == 0) return 0; // No packages to collect

        int roads = 0;
        Set<Integer> visited = new HashSet<>(); // Track visited nodes for path
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);

        while (packagesToCollect > 0) {
            // Collect packages within distance 2 from current location
            Set<Integer> nodesWithin2 = new HashSet<>();
            nodesWithin2.add(start); // Current location
            Queue<Integer> bfsQueue = new LinkedList<>();
            Set<Integer> bfsVisited = new HashSet<>();
            bfsQueue.offer(start);
            bfsVisited.add(start);

            // BFS to find nodes within distance 2
            for (int dist = 0; dist < 2; dist++) {
                int size = bfsQueue.size();
                for (int i = 0; i < size; i++) {
                    int curr = bfsQueue.poll();
                    nodesWithin2.add(curr);
                    for (int next : graph.get(curr)) {
                        if (!bfsVisited.contains(next)) {
                            bfsVisited.add(next);
                            bfsQueue.offer(next);
                        }
                    }
                }
            }

            // Collect packages from nodes within distance 2
            int packagesCollected = 0;
            for (int loc : nodesWithin2) {
                if (packages[loc] == 1 && !collected[loc]) {
                    collected[loc] = true;
                    packagesToCollect--;
                    packagesCollected++;
                }
            }

            if (packagesCollected == 0) {
                // Need to move to an unvisited location with packages
                int nextLoc = -1;
                for (int i = 0; i < n; i++) {
                    if (packages[i] == 1 && !collected[i] && !visited.contains(i)) {
                        nextLoc = i;
                        break;
                    }
                }
                if (nextLoc == -1) return Integer.MAX_VALUE; // Cannot collect all

                // Find shortest path to nextLoc using BFS
                Map<Integer, Integer> parent = new HashMap<>();
                Queue<Integer> pathQueue = new LinkedList<>();
                Set<Integer> pathVisited = new HashSet<>();
                pathQueue.offer(start);
                pathVisited.add(start);

                while (!pathQueue.isEmpty() && !pathVisited.contains(nextLoc)) {
                    int curr = pathQueue.poll();
                    for (int next : graph.get(curr)) {
                        if (!pathVisited.contains(next)) {
                            pathVisited.add(next);
                            parent.put(next, curr);
                            pathQueue.offer(next);
                        }
                    }
                }

                if (!pathVisited.contains(nextLoc)) return Integer.MAX_VALUE; // No path

                // Count roads to nextLoc
                int pathLen = 0;
                int curr = nextLoc;
                while (parent.containsKey(curr)) {
                    pathLen++;
                    curr = parent.get(curr);
                }
                roads += pathLen;
                start = nextLoc;
                visited.add(start);
            }
        }

        // Return to starting location
        // Find shortest path back using BFS
        Map<Integer, Integer> returnParent = new HashMap<>();
        Queue<Integer> returnQueue = new LinkedList<>();
        Set<Integer> returnVisited = new HashSet<>();
        returnQueue.offer(start);
        returnVisited.add(start);

        while (!returnQueue.isEmpty() && !returnVisited.contains(start)) { // Back to original start
            int curr = returnQueue.poll();
            for (int next : graph.get(curr)) {
                if (!returnVisited.contains(next)) {
                    returnVisited.add(next);
                    returnParent.put(next, curr);
                    returnQueue.offer(next);
                }
            }
        }

        int returnPathLen = 0;
        int curr = start;
        while (returnParent.containsKey(curr)) {
            returnPathLen++;
            curr = returnParent.get(curr);
        }
        roads += returnPathLen;

        return roads;
    }
}