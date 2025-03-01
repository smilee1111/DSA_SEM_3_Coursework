package DSA_SEM_3_Coursework;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetHashtagTrendAnalyzer {
    // Class to represent a Tweet
    static class Tweet {
        int user_id;
        int tweet_id;
        String tweet_date;
        String tweet;

        public Tweet(int user_id, int tweet_id, String tweet_date, String tweet) {
            this.user_id = user_id;
            this.tweet_id = tweet_id;
            this.tweet_date = tweet_date;
            this.tweet = tweet;
        }
    }

    public static void main(String[] args) {
        // Hardcoded input tweets from the example
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(135, 13, "2024-02-01", "Enjoying a great start to the day, #HappyDay #MorningVibes"));
        tweets.add(new Tweet(137, 14, "2024-02-03", "Another #HappyDay with good vibes! #FeelGood"));
        tweets.add(new Tweet(137, 15, "2024-02-04", "Productivity peaks! #WorkLife #ProductiveDay"));
        tweets.add(new Tweet(138, 16, "2024-02-05", "Exploring new tech frontiers, #TechLife #Innovation"));
        tweets.add(new Tweet(139, 17, "2024-02-06", "Gratitude for today's moments, #HappyDay #Thankful"));
        tweets.add(new Tweet(141, 18, "2024-02-07", "Connecting with nature's serenity, #TechLife #Peaceful"));

        // Find top 3 trending hashtags in February 2024
        Map<String, Integer> hashtagCounts = new HashMap<>();
        Pattern hashtagPattern = Pattern.compile("#\\w+"); // Matches hashtags (e.g., #HappyDay)

        // Filter and process tweets from February 2024
        for (Tweet tweet : tweets) {
            if (isFebruary2024(tweet.tweet_date)) {
                // Extract hashtags from the tweet
                Matcher matcher = hashtagPattern.matcher(tweet.tweet);
                while (matcher.find()) {
                    String hashtag = matcher.group();
                    hashtagCounts.put(hashtag, hashtagCounts.getOrDefault(hashtag, 0) + 1);
                }
            }
        }

        // Convert to list of entries for sorting
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCounts.entrySet());
        // Sort by count (descending) and then hashtag (descending)
        sortedHashtags.sort((e1, e2) -> {
            int countCompare = e2.getValue().compareTo(e1.getValue()); // Descending count
            if (countCompare != 0) return countCompare;
            return e2.getKey().compareTo(e1.getKey()); // Descending hashtag for ties
        });

        // Print top 3 hashtags
        System.out.println("hashtag    | count");
        System.out.println("------------+-------");
        int count = Math.min(3, sortedHashtags.size());
        for (int i = 0; i < count; i++) {
            Map.Entry<String, Integer> entry = sortedHashtags.get(i);
            System.out.printf("%-12s | %d%n", entry.getKey(), entry.getValue());
        }
    }

    // Check if the date is in February 2024
    private static boolean isFebruary2024(String date) {
        return date.startsWith("2024-02-");
    }
}