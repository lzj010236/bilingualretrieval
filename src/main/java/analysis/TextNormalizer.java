package analysis;

/**
 * @author Jing Lin
 *
 */
public class TextNormalizer {
	
	// YOU MUST IMPLEMENT THIS METHOD
	public static char[] normalize( char[] chars ) {
		// return the normalized version of the word characters (replacing all uppercase characters into the corresponding lowercase characters)
		return new String(chars).toLowerCase().toCharArray();
	}
}