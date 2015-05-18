/**
 * 
 */
package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import model.Entry;
import analysis.StopwordsRemover;
import analysis.TextNormalizer;

/**
 * @author Jing Lin
 *
 */
public class EntrySorter {

	public List<Entry> sort(List<Entry> entries, String eQuery)
			throws IOException {

		List<Entry> sortedEntries = new ArrayList<Entry>();

		Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();

		HashMap<Integer, String> id_content_map = new HashMap<Integer, String>();
		HashMap<Integer, Entry> id_entry_map = new HashMap<Integer, Entry>();
		int entryCounter = 0;
		for (Entry e : entries) {
			if(e.getOriginLanguage().equals("Chinese")){
				id_content_map.put(entryCounter, (e.getTranslated()));
				System.out.println("--------CHINESE"+e.getTranslated());
			}
			else if(e.getOriginLanguage().equals("English")){
				id_content_map.put(entryCounter, (e.getTitle() + " " + e.getDescription()));
				System.out.println("--------ENGLISH"+e.getTitle() + " " + e.getDescription());
			}
			id_entry_map.put(entryCounter, e);
			entryCounter++;
		}

		// normalize, tokenize, remove stopwords for query
		List<String> query = normalizer(eQuery);
		
		//index entries
		index(id_content_map);
		
		
		
		

		//get collection length
		int collectionLength = 0;
		Iterator<java.util.Map.Entry<String, HashMap<Integer, Integer>>> it = dictionary
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, HashMap<Integer, Integer>> pair = (Map.Entry<String, HashMap<Integer, Integer>>) it
					.next();
			Iterator<java.util.Map.Entry<Integer, Integer>> it2 = pair
					.getValue().entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry<Integer, Integer> pair2 = (Map.Entry<Integer, Integer>) it2
						.next();
				collectionLength += pair2.getValue();
			}
		}

		//for every terms in the query
		Iterator<String> itr = query.iterator();
		while (itr.hasNext()) {
			String word = itr.next();

			int docFreq = dictionary.get(word).size();
			if(docFreq>0){
				// get collectionFreq
				int collectionFreq = 0;
				Iterator<java.util.Map.Entry<Integer, Integer>> itr3 = dictionary.get(word).entrySet().iterator();
				while(itr3.hasNext()){
					Map.Entry<Integer, Integer> e_3 = (Map.Entry<Integer, Integer>) itr3.next();
					collectionFreq+=e_3.getValue();
				}
				
				
				//iterate through every entry and calculate its score
				Iterator<java.util.Map.Entry<Integer, String>> itr2 = id_content_map
						.entrySet().iterator();
				while (itr2.hasNext()) {
					Map.Entry<Integer, String> e = (Map.Entry<Integer, String>) itr2
							.next();
					int docid = e.getKey();
					if (dictionary.get(word).containsKey(docid)){
						int docLength = docLengthMap.get(docid);
						int termFreq = dictionary.get(word).get(docid);
						double score = DirichletPriorSmoothing(docLength,
								collectionFreq, termFreq, collectionLength);
						if (scoreMap.containsKey(docid)) {
							double temp = scoreMap.get(docid);
							score += temp;
							scoreMap.put(docid, score);
						} else {
							scoreMap.put(docid, score);
						}
					}
					else if (dictionary.get(word).containsKey(docid)==false){
						if (scoreMap.containsKey(docid)==false) {
							scoreMap.put(docid, (double) 0);
						}
					}
					
				}
			}
			
			
		}
		
		//sort score in decreasing order
		List<Map.Entry<Integer, Double>> scoreList = sortScore(scoreMap);
		
		//put them back to entry list
		Iterator<Map.Entry<Integer, Double>> itr_sl = scoreList.iterator();
		while (itr_sl.hasNext()) {
			Map.Entry<Integer, Double> temp = itr_sl.next();
			sortedEntries.add(id_entry_map.get(temp.getKey()));
			if(id_entry_map.get(temp.getKey()).getOriginLanguage().equals("Chinese")){
				System.out.println("-------sortedCHinese"+id_entry_map.get(temp.getKey()).getTranslated());
			}
			if(id_entry_map.get(temp.getKey()).getOriginLanguage().equals("English")){
				System.out.println("-------sortedEnglish"+id_entry_map.get(temp.getKey()).getTitle());
			}
		}

		System.out.println("Entries merge and sort success!");
		return sortedEntries;
	}

	public double DirichletPriorSmoothing(int docLength, long collectionFreq,
			int termFreq, long collectionLength) {
		int u = 1;
		double p = (u * ((double) (collectionFreq / collectionLength)) + termFreq)
				/ (docLength + u);
		p = Math.log(p);

		return p;
	}

	// sort score in decreasing order
	public static List<Map.Entry<Integer, Double>> sortScore(
			Map<Integer, Double> scoreMap) {
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(
				scoreMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> map_a,
					Map.Entry<Integer, Double> map_b) {
				return -map_a.getValue().compareTo(map_b.getValue());
			}
		});
		return list;
	}

	private static HashMap<String, HashMap<Integer, Integer>> dictionary = new HashMap<String, HashMap<Integer, Integer>>();
	private static HashMap<Integer, Integer> docLengthMap = new HashMap<Integer, Integer>();

	// term,<eventID, termFreq>

	private void index(HashMap<Integer, String> eventContent)
			throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("stop_words.txt")
				.getFile());
		FileInputStream instream_stopwords = new FileInputStream(file);
		StopwordsRemover stoprmv = new StopwordsRemover(instream_stopwords);

		// for every event
		Iterator<java.util.Map.Entry<Integer, String>> it = eventContent
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> a = (Map.Entry<Integer, String>) it
					.next();
			int docLength = 0;
			StringTokenizer st = new StringTokenizer(a.getValue());
			while (st.hasMoreTokens()) {
				char[] word = TextNormalizer.normalize(st.nextToken()
						.toCharArray());
				if (!stoprmv.isStopword(word)) {
					String termString = String.valueOf(word);
					if (!dictionary.containsKey(termString)) {
						// if term doesn't exist in hashmap, add term
						HashMap<Integer, Integer> termFreq = new HashMap<Integer, Integer>();
						termFreq.put(a.getKey(), 1);
						dictionary.put(termString, termFreq);
					} else {// if term already exist in hashmap
						if (dictionary.get(termString).containsKey(a.getKey())) {
							// if docno exists in hashmap, freq++
							int termFreq = dictionary.get(termString).get(
									a.getKey());
							termFreq++;
							dictionary.get(termString)
									.put(a.getKey(), termFreq);
						} else {
							// if docno doesn't exist in hashmap, add docno
							dictionary.get(termString).put(a.getKey(), 1);
						}
					}
				}
				docLength++;
			}
			docLengthMap.put(a.getKey(), docLength);
		}
	}

	// normalize, tokenize, remove stopwords for query
	private List<String> normalizer(String origin) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("stop_words.txt")
				.getFile());
		FileInputStream instream_stopwords = new FileInputStream(file);
		StopwordsRemover stoprmv = new StopwordsRemover(instream_stopwords);
		List<String> analyzed = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(origin);
		while (st.hasMoreTokens()) {
			char[] originChar = st.nextToken().toCharArray();
			originChar = TextNormalizer.normalize(originChar);
			if (!stoprmv.isStopword(originChar)) {
				analyzed.add(String.valueOf(originChar));
			}
		}

		return analyzed;
	}

}
