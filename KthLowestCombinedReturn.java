package DSA_SEM_3_Coursework;
import java.util.*;

public class KthLowestCombinedReturn {
    public static void main(String[] args) {
        // Test cases
        int[] returns1a = {2, 5};
        int[] returns2a = {3, 4};
        int k1 = 2;
        System.out.println(findKthLowestReturn(returns1a, returns2a, k1)); // Expected: 8

        int[] returns1b = {-4, -2, 0, 3};
        int[] returns2b = {2, 4};
        int k2 = 6;
        System.out.println(findKthLowestReturn(returns1b, returns2b, k2)); // Expected: 0
    }

    public static long findKthLowestReturn(int[] returns1, int[] returns2, int k) {
        int m = returns1.length;
        int n = returns2.length;
        
        // Min-heap of [product, i, j]
        PriorityQueue<long[]> minHeap = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        // Set to track visited pairs (i, j)
        Set<String> visited = new HashSet<>();
        
        // Start with smallest product
        long product = (long) returns1[0] * returns2[0];
        minHeap.offer(new long[]{product, 0, 0});
        visited.add("0,0");
        
        // Extract k elements
        for (int count = 0; count < k - 1; count++) {
            long[] current = minHeap.poll();
            int i = (int) current[1];
            int j = (int) current[2];
            
            // Add (i+1, j) if valid
            if (i + 1 < m) {
                String key1 = (i + 1) + "," + j;
                if (!visited.contains(key1)) {
                    product = (long) returns1[i + 1] * returns2[j];
                    minHeap.offer(new long[]{product, i + 1, j});
                    visited.add(key1);
                }
            }
            // Add (i, j+1) if valid
            if (j + 1 < n) {
                String key2 = i + "," + (j + 1);
                if (!visited.contains(key2)) {
                    product = (long) returns1[i] * returns2[j + 1];
                    minHeap.offer(new long[]{product, i, j + 1});
                    visited.add(key2);
                }
            }
        }
        
        // The k-th element is at the top
        return minHeap.peek()[0];
    }
}