package study.test.StudyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import study.test.StudyTest.KidsDinnerTableGame.Word;

/*
FIXISLLP
ENOBNTHE
NFEWSATF
LIEETTOE
TVVTHUEN
RESGRINI
NTIREAIN
NEOREDER

ONE
TWO
THREE
FOUR
FIVE
SIX
SEVEN
EIGHT
NINE

*/

public class KidsDinnerTableGame {

	public static final String[] INPUT = {
			"FIXISLLP", 
			"ENOBNTHE", 
			"NFEWSATF", 
			"LIEETTOE", 
			"TVVTHUEN", 
			"RESGRINI", 
			"NTIREAIN", 
			"NEOREDER"
	
	};
	
	public static final String[] DICT = {
			"ONE", 
			"TWO", 
			"THREE", 
			"FOUR", 
			"FIVE", 
			"SIX", 
			"SEVEN", 
			"EIGHT", 
			"NINE"
	};
	
	public static final Set<String> DICT_SET = new HashSet<>(Arrays.asList(DICT));
	
	/**
	 * Direction Enum - we can portray it as a 3-bit number, and use toValue() and fromValue()
	 * to simplify the code. 
	 * 
	 * @author mishe
	 */
	public enum DIRECTION {
		LEFT, 
		UP, 
		LEFT_UP, 
		LEFT_DOWN, 
		RIGHT,
		DOWN, 
		RIGHT_UP, 
		RIGHT_DOWN
	};
	
	/**
	 * "Struct" class holding information about a word, within the context of the input matrix. 
	 * 
	 * @author mishe
	 *
	 */
	public static class Word {

		/**
		 * Start row number. 
		 */
		public int beginRow;
		/**
		 * Start column number. 
		 */
		public int beginCol;
		/**
		 * End row number. 
		 */
		public int endRow;
		/**
		 * End column number. 
		 */
		public int endCol;
		// NOTE: uncomment the below if you want the direction + length. It is kinda painful to calculate. 
//		/**
//		 * Direction to go in - one of the Direction enum values. 
//		 */
//		public DIRECTION direction;
//		/**
//		 * The length of the word. 
//		 */
//		public int length;
		/**
		 * The actual word
		 */
		public String string;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			return sb.append("{")
					.append("beginRow = ").append(beginRow)
					.append(", beginCol = ").append(beginCol)
					.append(", endRow = ").append(endRow)
					.append(", endCol = ").append(endCol)
//					.append(", DIRECTION = ").append(direction)
//					.append(", length = ").append(length)
					.append(", word = ").append(string)
					.append("}")
					.toString();
		}

	}

	public static void main(String[] args) {
		System.out.println(new KidsDinnerTableGame(INPUT, DICT_SET).findWords());
	}

	/**
	 * Computes the direction based on the start and end row, resp. column indices.
	 * @param i start row
	 * @param j start column
	 * @param p end row
	 * @param q end column
	 * 
	 * @return null if it's not diagonal, or the computed {@link DIRECTION} otherwise. 
	 */
	private static DIRECTION findDirection(int i, int j, int p, int q) {
     if (i == p) {
    	 return q > j ? DIRECTION.LEFT : DIRECTION.RIGHT;
     } else if (q == j) {
    	 return p > i ? DIRECTION.DOWN : DIRECTION.UP;
     } else if (Math.abs(p - i) == Math.abs(q - j)) {
    	 return i < p 
    			 	? (j < q ? DIRECTION.RIGHT_DOWN : DIRECTION.LEFT_DOWN)
		 			: (j < q ? DIRECTION.RIGHT_UP : DIRECTION.LEFT_UP);
     }
     return null;
 }

	private final String[] input;
	
	private final Set<String> dict;
	
	public KidsDinnerTableGame(String[] input, Set<String> dict) {
		this.input = input;
		this.dict = dict;
		validateMatrix();
	}

	/**
	 * check if the input is really a non-null, square matrix
	 */
	private void validateMatrix() {
		if (input == null || input.length < 1) throw new IllegalArgumentException("Empty word matrix");
		int len = input.length;
		for (int i = 0; i < input.length; i++) {
			if (input[i] == null) throw new IllegalArgumentException("Null row " + i); 
			if (input[i].length() != len) {
				throw new IllegalArgumentException("Row " + i + " has " + input[i].length() 
						+ " != " + len + " length");
			}
		}
		System.out.println("**** Validated square matrix of length " + len);
	}
	
	private List<Word> findWords() {
		List<Word> outputList = new ArrayList<Word>();
		
		for (int i = 0; i < this.input.length; i++) {
			for (int j = 0; j < this.input[i].length(); j++) {
				outputList.addAll(findWordsForCell(i, j));
			}
		}
		return outputList;
	}

	/**
	 * Finds all the words starting from the given cell.
	 * 
	 * @param i the row number
	 * @param j the column number
	 * 
	 * @return the list of {@link #Word words} identified, starting from the current cell. 
	 */
	private List<Word> findWordsForCell(int i, int j) {
		System.out.println("findWordsForCell(" + i + ", " + j + ")");
		List<Word> list = new ArrayList<Word>();
		for (int p = 0; p < this.input.length; p++) { // each row
			for (int q = 0; q < this.input[i].length(); q++) { // each column => each cell on the row
				System.out.println("Checking [" + p + ", " + q + "]");
				int vLen = Math.abs(p - i);
				int hLen = Math.abs(q - j);
				if (vLen == 0 || hLen == 0 || vLen == hLen) {
					StringBuilder sb = new StringBuilder();
					int vStep = Integer.signum(p - i); 
					System.out.println("vStep = " + vStep);
					int hStep = Integer.signum(q - j);
					System.out.println("hStep = " + hStep);
					int pp = 0, qq = 0;
					while (pp <= vLen && qq <= hLen) {
						int row = i + pp * vStep;
						int col = j + qq * hStep;
						System.out.println("Adding cell [" + row + ", " + col + "]");
						sb.append((this.input[row] // row
								.charAt(col))); // cell in the row
						if (vStep == 0 && hStep == 0) break;
						pp++;
						qq++;
					}
					// check it's a valid word
					if (this.dict.contains(sb.toString())) {
						System.out.println("Found word " + sb);
						Word word = new Word();
						word.beginRow = i;
						word.beginCol = j;
//						word.direction = findDirection(i, j, p, q);
						word.endRow = p;
						word.endCol = q;
//						word.length = sb.length();
						word.string = sb.toString();
						list.add(word);
					}
				}
			}
		}
		
		return list;
	}
	
}
