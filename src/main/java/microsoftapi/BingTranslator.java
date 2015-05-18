/**
 * 
 */
package microsoftapi;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.memetix.mst.detect.Detect;

/**
 * @author 王达恒
 *
 */
public class BingTranslator {
	
	private static final String MICROSOFT_CLIENT_ID = 
			"dahengwangmicrosoftapp";
	private static final String MICROSOFT_CLIENT_SECRET = 
			"1lwAaTDv6uThmm3TN910FU3oMD1SQEY9ok53h6/fr2E=";
	
	
	private static void initialize(){
		Translate.setClientId(MICROSOFT_CLIENT_ID);
	    Translate.setClientSecret(MICROSOFT_CLIENT_SECRET);
	}
	
	public String translate(String content, String targetlan) throws Exception{
		String translated = "Content is empty. No translation performed!";
		if (!content.equals("")){
			initialize();
			if (targetlan.equals("Chinese")) {
				translated = Translate.execute(content, Language.CHINESE_SIMPLIFIED);
			} else if (targetlan.equals("English")) {
				translated = Translate.execute(content, Language.ENGLISH);
			}
			System.out.println("Bing translation of \"" + content + "\" into " + targetlan + " success!");
		}
		return translated;
	}
	
	public String detect(String string) throws Exception {
		String detected = "Content is empty. No detection performed!";
		initialize();
		if (!string.equals("")){
			Language detectedLanguage = Detect.execute(string);
			detected = detectedLanguage.toString();
		}
		return detected;
	}
	
//	//test main method
//	public static void main(String[] args) throws Exception {
//	    // Set your Windows Azure Marketplace client info - See http://msdn.microsoft.com/en-us/library/hh454950.aspx
////	    Translate.setClientId(MICROSOFT_CLIENT_ID);
////	    Translate.setClientSecret(MICROSOFT_CLIENT_SECRET);
//		BingTranslator bt = new BingTranslator();
////	    String translatedText = Translate.execute("如果再见不能红着眼，是否还能红着脸", Language.CHINESE_SIMPLIFIED, Language.ENGLISH);
////	    BingTranslator bt = new BingTranslator();
//	    
//		
////	    System.out.println(bt.translate("如果再见不能红着眼，是否还能红着脸", "English"));
//	}
}
