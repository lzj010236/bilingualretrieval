/**
 * 
 */
package servlet;

import googleapi.GoogleExtractor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import microsoftapi.BingTranslator;
import model.Entry;
import twitter4j.Status;
import twitterapi.TwitterExtractor;
import analysis.EntryMerger;
import analysis.EntrySorter;

import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Comment;

/**
 * @author Daheng Wang, Jing Lin, Xi Zhang
 *
 */
public class MainServlet extends HttpServlet {

	private static int MAX_RETURN_NUMBER = 4;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String query = request.getParameter("query");
		String eQuery = "";
		String cQuery = "";
		BingTranslator bt = new BingTranslator();

		try {
			if (bt.detect(query).equals("zh-CHS")) { // translate Chinese query
														// into English
				cQuery = query;
				eQuery = bt.translate(cQuery, "English");
			} else if (bt.detect(query).equals("en")) { // translate English
														// query into Chinese
				eQuery = query;
				cQuery = bt.translate(eQuery, "Chinese");
			} else {
				eQuery = bt.translate(query, "English");
				cQuery = bt.translate(query, "Chinese");
			}
			System.out.println("Original query: " + query + ";");
			System.out.println("English query: " + eQuery + ", Chinese query: "
					+ cQuery + ";");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get activities from Google+ server
		GoogleExtractor ge = new GoogleExtractor();
		List<Activity> eactivities = ge.getActivities(eQuery,
				MAX_RETURN_NUMBER, "English");
		List<Activity> cactivities = ge.getActivities(cQuery,
				MAX_RETURN_NUMBER, "Chinese");

		// get events from twitter server
		TwitterExtractor te = new TwitterExtractor();
		List<Status> etweets = te.getTweets(eQuery, MAX_RETURN_NUMBER,
				"English");
		List<Status> ctweets = te.getTweets(cQuery, MAX_RETURN_NUMBER,
				"Chinese");

		// merge activities and events into entries
		List<Entry> entries = new ArrayList<Entry>();
		try {
			EntryMerger em = new EntryMerger();

			List<Entry> eentries = em.merge(eactivities, etweets);
			for (Entry entry : eentries) {
				entry.setOriginLanguage("English");
			}
			List<Entry> centries = em.merge(cactivities, ctweets);
			for (Entry entry : centries) {
				entry.setOriginLanguage("Chinese");
			}
			entries.addAll(eentries);
			entries.addAll(centries);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// translate Chinese posts into English and English to Chinese
		for (Entry entry : entries) {
			try {
				if (entry.getOriginLanguage().equals("Chinese")) {
					String translated = bt.translate(
							entry.getTitle() + entry.getDescription(),
							"English");
					entry.setTranslated(translated);
				}
				// else if (bt.detect(entry.getTitle()).equals("English")) {
				// String translated = bt.translate(
				// entry.getTitle() + entry.getDescription(), "Chinese");
				// entry.setTranslated(translated);
				// }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		EntrySorter es = new EntrySorter();
		List<Entry> sortedEntries = es.sort(entries, eQuery);

		// ***************** print out testing ***************** //
		// for (Activity activity:eactivities) {
		// out.println("*** Google+ activity: " + activity.getTitle() + ": " +
		// activity.getAnnotation() + "***</br>");
		// }
		//
		// for (Activity activity:cactivities) {
		// out.println("*** Google+ activity: " + activity.getTitle() + ": " +
		// activity.getAnnotation() + "***</br>");
		// }
		//
		// for (Status tweet:etweets) {
		// out.println("*** Twitter: " + tweet.getText() + "***</br>");
		// }
		//
		// for (Status tweet:ctweets) {
		// out.println("*** Twitter: " + tweet.getText() + "***</br>");
		// }

		// for (Entry entry:entries) {
		// if (entry.getOriginLanguage().equals("Chinese")) {
		// out.println("Entry No." + entry.getId() + " from " +
		// entry.getOrigin()
		// + ", content: " + entry.getTitle() + entry.getDescription() +
		// ";\n</br>");
		// } else if (entry.getOriginLanguage().equals("English")) {
		// out.println("Entry No." + entry.getId() + " from " +
		// entry.getOrigin()
		// + ", translated: " + entry.getTranslated() + ";\n</br>");
		// }
		// }
		//
		// for (Entry entry:sortedEntries) {
		// if (entry.getOriginLanguage().equals("Chinese")) {
		// out.println("SortedEntry No." + entry.getId() + " from " +
		// entry.getOrigin()
		// + ", content: " + entry.getTitle() + entry.getDescription() +
		// ";\n</br>");
		// } else if (entry.getOriginLanguage().equals("English")) {
		// out.println("SortedEntry No." + entry.getId() + " from " +
		// entry.getOrigin()
		// + ", translated: " + entry.getTranslated() + ";\n</br>");
		// }
		// }
		// ***************** print out testing ***************** //

		// write sorted result to http response
		// out.println("Retrieval Success!!!");
		// out.println("");
		out.println("<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "    <head>\n"
				+ "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
				+ "        <title>Federate search on Google+ and Twitter</title>\n"
				+ "         <link type=\"text/css\" rel=\"stylesheet\" href=\"IRstylesheet.css\" />\n"
				+ "    </head>\n"
				+ "    <body>\n"
				+ "        <div class=\"logo\"><img src=\"title.png\" alt=\"Logo\" height=\"50px\" align=\"center\">\n"
				+ "        </div>\n"
				+ "        <form method =\"post\" action=\"search\" align=\"center\">\n"
				+ "            <input type=\"text\" name=\"query\" size=\"60\" placeholder=\""
				+ query
				+ "\">\n"
				+ "            <input type=\"submit\" value=\"Search\">\n"
				+ "        </form>");
		for (Entry entry : sortedEntries) {
			if (entry.getOriginLanguage().equals("English")) {
				out.println("<div class=\"entry\">");
				out.println(entry.getTitle() + ". "
						+ entry.getDescription()
						+ "</div>");
				out.println("<div class=\"entry_info\">"
						+"<span style=\"color:gray\">From: </span>"+ entry.getOrigin() +",    "
						+"<span style=\"color:gray\">Origin Language: </span>" +entry.getOriginLanguage()+",    "
						+"<span style=\"color:gray\">Publisher: </span>" +entry.getPublisher()+",    "
						+"<span style=\"color:gray\">Date: </span>" + entry.getCreated()+",    "
						+"<span style=\"color:gray\">Origin URL: </span>" + "<a href=\"" + entry.getUrl() + "\" class=\"link\">" + entry.getUrl() + "</a>    "
						+"</div>");
				
			} else if (entry.getOriginLanguage().equals("Chinese")) {
				out.println("<div class=\"entry\">");
				out.println(entry.getTranslated() +"</div>");
				out.println("<div class=\"entry_info\">"
						+"<span style=\"color:gray\">From: </span>"+ entry.getOrigin() +",    "
						+"<span style=\"color:gray\">Origin Language: </span>" +entry.getOriginLanguage()+",    "
						+"<span style=\"color:gray\">Publisher: </span>" +entry.getPublisher()+",    "
						+"<span style=\"color:gray\">Date: </span>" + entry.getCreated()+",    "
						+"<span style=\"color:gray\">Origin URL: </span>" + "<a href=\"" + entry.getUrl() + "\" class=\"link\">" + entry.getUrl() + "</a>    "
						+"</div>");
			}
		}
		out.println("    </body>\n"
				+ "    <footer>     \n" + "        <ul>\n"
				+ "            <li>Produced by:</li>\n"
				+ "            <li>J. Lin</li>\n"
				+ "            <li>D. Wang</li>\n"
				+ "            <li>X. Zhang</li>\n" + "        </ul>\n"
				+ "        <p>All copyright &copy; 2015</p>\n"
				+ "    </footer>\n" + "</html>");

		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
