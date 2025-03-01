package DSA_SEM_3_Coursework;

public class PointWithSmallestDistance {
    public static void main(String[] args) {
        // Test case
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};
        int[] result = findClosestPair(x_coords, y_coords);
        System.out.println("[" + result[0] + ", " + result[1] + "]"); // Expected: [0, 3]
    }

    public static int[] findClosestPair(int[] x_coords, int[] y_coords) {
        int n = x_coords.length;
        if (n < 2) return new int[]{-1, -1}; // Invalid input

        int minDistance = Integer.MAX_VALUE;
        int[] result = new int[2];
        
        // Iterate all pairs (i, j) where i < j
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int dist = Math.abs(x_coords[i] - x_coords[j]) + 
                          Math.abs(y_coords[i] - y_coords[j]);
                
                // Update if distance is smaller or equal with lexicographically smaller pair
                if (dist < minDistance || 
                    (dist == minDistance && (i < result[0] || (i == result[0] && j < result[1])))) {
                    minDistance = dist;
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        
        return result;
    }
}