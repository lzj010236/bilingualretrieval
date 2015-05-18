/**
 * 
 */
package googleapi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import analysis.Tools;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Activities.Search;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.google.common.io.Files;

/**
 * @author dahengwang
 *
 */
public class GoogleExtractor {
	
	/**
	 * Be sure to specify the name of your application. If the application name is {@code null} or
	 * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "My API service account demo";
	
	/** E-mail address of the service account. */
	private static final String SERVICE_ACCOUNT_EMAIL = 
			"908410318752-0c8fpi9cpd6n8igps57qgdlh9ib1diec@developer.gserviceaccount.com";
	
	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static Plus plus;
	
	private void initialize() {
		try {
			try {
		        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		        // check for valid setup
		        if (SERVICE_ACCOUNT_EMAIL.startsWith("Enter ")) {
		          System.err.println(SERVICE_ACCOUNT_EMAIL);
		          System.exit(1);
		        }
		        String p12Content = Files.readFirstLine(new File(this.getClass().getResource("/key.p12").toURI()), Charset.defaultCharset());
		        if (p12Content.startsWith("Please")) {
		          System.err.println(p12Content);
		          System.exit(1);
		        }
		        
		        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
		        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
		            .setJsonFactory(JSON_FACTORY)
		            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		            .setServiceAccountScopes(Collections.singleton(PlusScopes.PLUS_ME))
		            .setServiceAccountPrivateKeyFromP12File(new File(this.getClass().getResource("/key.p12").toURI()))
		            // .setServiceAccountUser("user@example.com")
		            .build();
		        
		        // set up global Plus instance
		        plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
		            .setApplicationName(APPLICATION_NAME).build();
		        
		      } catch (IOException e) {
		        System.err.println(e.getMessage());
		      }
		    } catch (Throwable t) {
		      t.printStackTrace();
		    }
	}
	
	public List<Activity> getActivities(String q, int max_number, String language) {
		List<Activity> activities = new ArrayList<Activity>();
		this.initialize();
		try {
	        // run commands
			// Execute the request for the first page
			Search search = plus.activities().search(q);
			search.setMaxResults(20L);
			search.setOrderBy("best");
			if (language.equals("Chinese")){
				search.setLanguage("zh-CN");
			} else if (language.equals("English")){
				search.setLanguage("en-US");
			}
			ActivityFeed activityFeed = search.execute();
			
			// Unwrap the request and extract the pieces we want
			List<Activity> pageactivities = activityFeed.getItems();
			
			// Loop through until we arrive at an empty page
			while (pageactivities != null && activities.size() < max_number) {
				for (Activity activity : pageactivities) {
					if (Tools.countWords(activity.getTitle() + activity.getAnnotation()) >= 10) {
						activities.add(activity);
						System.out.println("Activity No." + activities.size() + " acquried on Google+");
//						System.out.println("****** " + activity.getTitle() + " ******");
					}
					if (activities.size() >= max_number) {
				    	break;
				    }
				}
				
				// We will know we are on the last page when the next page token is null.
				// If this is the case, break.
				if (activityFeed.getNextPageToken() == null) {
					break;
				}
				
				// Prepare to request the next page of activities
				search.setPageToken(activityFeed.getNextPageToken());

				// Execute and process the next page request
				activityFeed = search.execute();
				pageactivities = activityFeed.getItems();
			}
	        System.out.println("Total " + activities.size() + " activities acquired on Google+!");
	        return activities;
		    } catch (Throwable t) {
		      t.printStackTrace();
		    }
		    System.exit(1);
		return null;
	}
	
//	public List<Comment> getComments(String activityid) throws IOException {
//		List<Comment> comments = new ArrayList<Comment>();
//		
//		Plus.Comments.List listComments = plus.comments().list(activityid);
//		listComments.setMaxResults(500L);
//		CommentFeed commentFeed = listComments.execute();
//		comments.addAll(commentFeed.getItems());
//
//		// Loop through until we arrive at an empty page
//		while (comments != null) {
////		  for (Comment comment : comments) {
////		    System.out.println(comment.getActor().getDisplayName() + " commented " +
////		            comment.getObject().getContent());
////		    
////		  }
//
//		  // We will know we are on the last page when the next page token is null.
//		  // If this is the case, break.
//		  if (commentFeed.getNextPageToken() == null) {
//		    break;
//		  }
//
//		  // Prepare the next page of results
//		  listComments.setPageToken(commentFeed.getNextPageToken());
//
//		  // Execute and process the next page request
//		  commentFeed = listComments.execute();
//		  comments.addAll(commentFeed.getItems());
//		}
//		return comments;
//	}
	
//	public List<Comment> getActivitiesComments(String q, int max_number) {
//		List<Comment> comments = new ArrayList<Comment>();
//		int activities = 1;
//		this.initialize();
//		try {
//	        // run commands
//			// Execute the request for the first page
//			Search search = plus.activities().search(q);
//			search.setMaxResults(20L);
//			ActivityFeed activityFeed = search.execute();
//			
//			// Unwrap the request and extract the pieces we want
//			List<Activity> pageactivities = activityFeed.getItems();
//			
//			// Loop through until we arrive at an empty page
//			while (pageactivities != null && comments.size() < max_number) {
//				for (Activity activity : pageactivities) {
//					System.out.println("INSIDE ACTIVITY: " + activity.getId() + ";");
//					for(Comment comment:this.getComments(activity.getId())){
//						comments.add(comment);
//						System.out.println("Get comment: " + comment.getId() + ";");
//						if (comments.size() >= max_number) {
//					    	break;
//					    }
//					}
//					
////					System.out.println(comments.size() + " comments acquired from No." + activities + ": " + activity.getId());
//					if (comments.size() >= max_number) {
//				    	break;
//				    }
//				}
//				
//				// We will know we are on the last page when the next page token is null.
//				// If this is the case, break.
//				if (activityFeed.getNextPageToken() == null) {
//					break;
//				}
//				
//				// Prepare to request the next page of activities
//				search.setPageToken(activityFeed.getNextPageToken());
//
//				// Execute and process the next page request
//				activityFeed = search.execute();
//				pageactivities = activityFeed.getItems();
//			}
//	        System.out.println("Total " + comments.size() + " activities acquired on Google+!");
//	        return comments;
//		    } catch (Throwable t) {
//		      t.printStackTrace();
//		    }
//		    System.exit(1);
//		return null;
//	}
	
//	// test main method
//	public static void main(String[] args) throws IOException {
//		GoogleExtractor ge = new GoogleExtractor();
//		
//		List<Activity> activities = ge.getActivities("beijing", 100, "English");
//		int flag1 = 1;
//		System.out.println(activities == null);
//		for (Activity activity: activities) {
//			System.out.println(flag1 + ": ***" + activity.getId() + "; " + activity.getUrl());
//			flag1 ++;
//		}
//		
//		List<Comment> comments = ge.getComments("z131idpjourpdrdyo04ci1gippaetpgrxfw");
//		int flag2 = 1;
//		System.out.println(comments.size());
//		for (Comment comment: comments) {
//			System.out.println(flag2 + ": ***" + comment.getObject().getContent());
//			flag2 ++;
//		}
//    }
}