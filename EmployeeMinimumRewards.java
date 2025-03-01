package DSA_SEM_3_Coursework;
public class EmployeeMinimumRewards {
    public static void main(String[] args) {
        // Test cases
        int[] ratings1 = {1, 0, 2};
        System.out.println(minRewards(ratings1)); // Expected: 5

        int[] ratings2 = {1, 2, 2};
        System.out.println(minRewards(ratings2)); // Expected: 4
    }

    public static int minRewards(int[] ratings) {
        int n = ratings.length;
        if (n == 0) return 0;
        if (n == 1) return 1;

        // Initialize rewards with 1
        int[] rewards = new int[n];
        for (int i = 0; i < n; i++) {
            rewards[i] = 1;
        }

        // Left-to-right pass
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Right-to-left pass
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Calculate total
        int total = 0;
        for (int reward : rewards) {
            total += reward;
        }
        return total;
    }
}