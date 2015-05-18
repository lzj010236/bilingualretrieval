/**
 * 
 */
package analysis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dahengwang
 *
 */
public class Tools {
	
	public static java.util.Date parseRFC3339Date(String datestring) throws java.text.ParseException, IndexOutOfBoundsException{
	    Date d = new Date();

	        //if there is no time zone, we don't need to do any special parsing.
	    if(datestring.endsWith("Z")){
	      try{
	        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//spec for RFC3339					
	        d = s.parse(datestring);		  
	      }
	      catch(java.text.ParseException pe){//try again with optional decimals
	        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//spec for RFC3339 (with fractional seconds)
	        s.setLenient(true);
	        d = s.parse(datestring);		  
	      }
	      return d;
	    }

	    //step one, split off the timezone. 
	    String firstpart = datestring.substring(0,datestring.lastIndexOf('-'));
	    String secondpart = datestring.substring(datestring.lastIndexOf('-'));
	    
	    //step two, remove the colon from the timezone offset
	    secondpart = secondpart.substring(0,secondpart.indexOf(':')) + secondpart.substring(secondpart.indexOf(':')+1);
	    datestring  = firstpart + secondpart;
	    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//spec for RFC3339		
	    try{
	      d = s.parse(datestring);		  
	    }
	    catch(java.text.ParseException pe){//try again with optional decimals
	      s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");//spec for RFC3339 (with fractional seconds)
	      s.setLenient(true);
	      d = s.parse(datestring);		  
	    }
	    return d;
	  }
	
	public static int countWords(String s){

	    int wordCount = 0;

	    boolean word = false;
	    int endOfLine = s.length() - 1;

	    for (int i = 0; i < s.length(); i++) {
	        // if the char is a letter, word = true.
	        if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
	            word = true;
	            // if char isn't a letter and there have been letters before,
	            // counter goes up.
	        } else if (!Character.isLetter(s.charAt(i)) && word) {
	            wordCount++;
	            word = false;
	            // last word of String; if it doesn't end with a non letter, it
	            // wouldn't count without this.
	        } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
	            wordCount++;
	        }
	    }
	    return wordCount;
	}
	
	public static String processEntry(String entry) {
		// remove line breaks
		entry = entry.replaceAll("\\r\\n|\\r|\\n", " ");
//		//remove urls
//		String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(tweetContent);
//        int i = 0;
//        while (m.find()) {
//        	tweetContent = tweetContent.replaceAll(m.group(i),"").trim();
//            i++;
//        }
////		tweetContent = tweetContent.replaceAll("https?://\\S+\\s?", "");
//        
//		// remove hashtags
//        tweetContent = tweetContent.replaceAll(" #[^\\s]+","");
//        tweetContent = tweetContent.replaceAll("#[^\\s]+","");
//        
//		// remove mentionings
//        tweetContent = tweetContent.replaceAll(" @[^\\s]+","");
//        tweetContent = tweetContent.replaceAll("@[^\\s]+","");
//        
//		// remove RT identifiers
//		tweetContent = tweetContent.replaceAll("RT ","");
//		tweetContent = tweetContent.replaceAll("RT","");
		
		return entry;
	}
	
//	//test main()
//	public static void main(String[] args)throws java.text.ParseException{
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26-07:00"));
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26.3-07:00"));
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26.3452-07:00"));	
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26.3452Z"));	
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26.3Z"));
//		System.out.println(parseRFC3339Date("2007-05-01T15:43:26Z"));
//	}
}
