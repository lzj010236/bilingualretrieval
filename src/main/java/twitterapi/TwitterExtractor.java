package twitterapi;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;

import analysis.Tools;

public class TwitterExtractor {
	
//	private final static String[] credentials= {"zIt19vRYg6wlipTX1TDfhQGiF", "L0GR21dR37p5Km6hHW1XKBPjIeiwCgvH4mfbxbJDqek4MXbXSz", 
//		"1673922156-93jOMR9D93PIXtIxjOzHnaFIeXXg4lSh9TaJyIA", "KerIlKHUjSPK0ZGRLCAfIRYQXvX8RnxwxHrFmyiwvTLaP"};
	
	// back up Twitter API credentials
	private final static String[] credentials= {"HfIK4WKQbAdaBdMEiB8aZbvdE", "ym92BQvDCs4KRUvtzMOQd10V1lDkjdSl4vUe2LoC1EsuLrDA1u", 
		"2821462985-g34pDcvf0Z7RLNFrSH1dhe8Aa2U9kMjDNz5YyYw", "Pphz6FSlODc4sJIH7uT8sa4E3NRMFyGnoMoxdlAf0GX6L"};
	
	private Twitter initialize() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(credentials[0])
		.setOAuthConsumerSecret(credentials[1])
		.setOAuthAccessToken(credentials[2])
		.setOAuthAccessTokenSecret(credentials[3]);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}

	public List<Status> getTweets(String q, int max_number, String language) {
		List<Status> rawTweets = new ArrayList<Status>();
		List<Status> tweets = new ArrayList<Status>();
		TwitterExtractor ct = new TwitterExtractor();

		Twitter twitter = ct.initialize();
		Query query = new Query(q);
		if (language.equals("Chinese")) {
			query.setLang("zh");
		} else if (language.equals("English")) {
			query.setLang("en");
		}
		query.setResultType(Query.ResultType.recent);
		// AND until:2014-11-23 ???
//		query.setUntil("2014-11-23");

		long lastID = Long.MAX_VALUE;

		while (tweets.size() < max_number) {
			if (max_number - tweets.size() >= 100)
				query.setCount(100);
			else
				query.setCount(max_number - tweets.size() + 50);
			try {
				QueryResult result = twitter.search(query);
				rawTweets.addAll(result.getTweets());
				
				for (Status t : rawTweets) {
//					if (countWords(processTweetContent(t.getText())) >= 20) {
//						if(uniqueGoodTweetsContent.add(processTweetContent(t.getText()))) {
//							goodTweets.add(t);
//						}
//					}
					
					// do any filtering before add rawTweets into tweets
					if (Tools.countWords(t.getText()) >= 20) {
						tweets.add(t);
						System.out.println("Tweet No. " + tweets.size() + " acquired on Twitter");
					}
					
					if(tweets.size() >= max_number) {
						break;
					}
				}
				
				System.out.println("Gathered " + tweets.size()
						+ " tweets");
				for (Status t : rawTweets)
					if (t.getId() < lastID)
						lastID = t.getId();
			}

			catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te);
			};
			query.setMaxId(lastID - 1);
		}
		return tweets;
	}
	
//	// test main method
//	public static void main(String[] args) {
//		TwitterExtractor test = new TwitterExtractor();
//		
////		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////		DateFormat dateFormat = new SimpleDateFormat("dd");
////		Date date = new Date();
////		System.out.println(dateFormat.format(date));
////		System.out.println(date);
//		
////		Calendar calendar = Calendar.getInstance();
////		calendar.add(Calendar.DAY_OF_MONTH, -1);
////		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
////		System.out.println(timeStamp);
//		
//		List<Status> tweets = test.getTweets("haha", 100, "Chinese");
//		int flag = 1;
//		for (Status tweet: tweets) {
//			System.out.println(flag + ": ***" + tweet.getText());
//			flag ++;
//		}
//    }
}