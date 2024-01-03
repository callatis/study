/**
 * 
 */
package study.test.StudyTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

/**
 * Given a dictionary of Morse-written words, and a Morse input string, determine the sentence
 * encoded in the input string, or if no valid sentence, return null.  The return will be the same 
 * string as the input, with the words separated by a "/". 
 * 
 * If there are multiple combinations, return the one with the longest words possible, from left to right. 
 * 
 * E.g. dictionary is: 
 * 
 * 
 * @author mishe
 */
public class SentenceIdentifier {
	
	public static String[] DICT1 = {
			".", // A
//			"-", // B
			".....", // AN
			"......-.-", // ANT
			"-..-.-", // BAT
			"-.--.." // BAR
	};
	
	public static List<String> DICT1_LIST = Arrays.asList(DICT1);
	
	public static Set<String> DICT1_SET = new HashSet<>(DICT1_LIST);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.assertEquals("......-.-/-..-.-", identifySentence("......-.--..-.-", 
				DICT1_SET, new HashMap<String, String>()));
		Assert.assertEquals("-.--../......-.-", identifySentence("-.--........-.-", 
				DICT1_SET, new HashMap<String, String>()));
		System.out.println("PASSED");
	}

	private static String getSentence(String input, Set<String> dict, Map<String, String> cache) {
		if (cache.containsKey(input)) {
			return cache.get(input);
		}
		String sentence = identifySentence(input, dict, cache);
		cache.put(input, sentence);
		
		return sentence;
	}
	
	private static String identifySentence(String input, Set<String> dict, Map<String, String> cache) {
		System.out.println("Identify sentence in '" + input + "'");
		// negative base case
		if (input == null || input.length() == 0) {
			return null;
		}
		// positive base case
		if (input.length() == 1) {
			return dict.contains(input) ? input : null;
		}
		
		// try with each length, starting with the longest
		for (int i = input.length() - 1; i > 0 ; i--) {
			System.out.println("Checking breakdown at position " + i + " in " + input);
			String left = input.substring(0, i);
			String right = input.substring(i);
			String leftSentence = dict.contains(left) ? left : getSentence(left, dict, cache);
			if (leftSentence != null) {
				String rightSentence = dict.contains(right) ? right : getSentence(right, dict, cache);
				if (rightSentence != null) {
					System.out.println("Found valid breakdown at position " + i + " in " + input);
					return leftSentence + "/" + rightSentence;
				}
			}
			
		}
		
		return null;
	}

}
