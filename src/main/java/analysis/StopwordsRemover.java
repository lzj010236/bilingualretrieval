package analysis;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Jing Lin
 *
 */
public class StopwordsRemover {
	
	private Set<String> stopwords;
	
	// YOU MUST IMPLEMENT THIS METHOD
	public StopwordsRemover( FileInputStream instream ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		this.stopwords = new HashSet<String>();
		try{
			BufferedReader reader = new BufferedReader( new InputStreamReader( instream, "UTF-8" ) );
			String line = reader.readLine();
			while(line!=null){
				stopwords.add( line.trim().toLowerCase() );
				line = reader.readLine();
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		return stopwords.contains( new String(word) );
	}
	
}
