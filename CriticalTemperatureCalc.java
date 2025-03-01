public class CriticalTemperatureCalc {
    public static void main(String[] args) {
        // Test cases
        System.out.println(minMeasurements(1, 2)); // Expected: 2
        System.out.println(minMeasurements(2, 6)); // Expected: 3
        System.out.println(minMeasurements(3, 14)); // Expected: 4
    }

    // Calculating minimum measurements required
    public static int minMeasurements(int k, int n) {
        int m = 0;
        while (true) {
            if (binomialCoefficient(m + k, k) >= n + 1) {
                return m;
            }
            m++;
        }
    }

    // Computing binomial coefficient C(n, k) iteratively
    public static long binomialCoefficient(int n, int k) {
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;

        // Optimizing by using min(k, n-k)
        k = Math.min(k, n - k);
        long result = 1;
        for (int i = 0; i < k; i++) {
            result *= (n - i);
            result /= (i + 1);
        }
        return result;
    }
}