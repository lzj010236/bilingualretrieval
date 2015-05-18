/**
 * 
 */
package analysis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.plus.model.Activity;

import twitter4j.Status;
import model.Entry;

/**
 * @author dahengwang
 *
 */
public class EntryMerger {
	
	public List<Entry> merge(List<Activity> activities, List<Status> tweets) throws IndexOutOfBoundsException, ParseException{
		List<Entry> entries = new ArrayList<Entry>();
		int id = 1;
		
		for (Activity activity: activities) {
			Entry entry = new Entry();
			entry.setId(id);
			entry.setTitle(activity.getTitle());
			entry.setDescription(activity.getAnnotation());
			entry.setOrigin("googleplus");
			entry.setCreated(Tools.parseRFC3339Date(activity.getPublished().toStringRfc3339()));
			entry.setPublisher(activity.getActor().getDisplayName());
			entry.setUrl(activity.getUrl());
			entries.add(entry);
			id ++;
			System.out.println("Activity " + activity.getId() + " merged into entries " + entry.getId() + ";");
		}
		
		for (Status tweet: tweets) {
			Entry entry = new Entry();
			entry.setId(id);
			entry.setTitle(tweet.getText());
			entry.setDescription("");
			entry.setOrigin("twitter");
			entry.setCreated(tweet.getCreatedAt());
			entry.setPublisher(tweet.getUser().getScreenName());
			entry.setUrl("https://twitter.com/" + tweet.getUser().getScreenName()
					+ "/status/" + String.valueOf(tweet.getId()));
			entries.add(entry);
			id ++;
			System.out.println("Tweet " + tweet.getId() + " merged into entries " + entry.getId() + ";");
		}
		
		return entries;
	}

}
