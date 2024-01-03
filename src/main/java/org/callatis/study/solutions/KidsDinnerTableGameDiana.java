package org.callatis.study.solutions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
 * grid, represents the letters in the grid of size N*M.
word, represents the words to be searched of size K.
 */
public class KidsDinnerTableGameDiana {

	public static void funcPuzzle(char[][] grid, String[] words) {
		String[] res = new String[words.length];

		String colWords = verticalStrings(grid);
		String rowWords = horizontalStrings(grid);

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String reversed = reversedString(word);

			if (rowWords.contains(word)) res[i] = "rows";
			else if (rowWords.contains(reversed)) res[i] = "-row";
			else if(colWords.contains(word)) res[i] = "cols";
			else if (colWords.contains(reversed)) res[i] = "-cols";
			else res[i] = "No";
		}
		System.out.println(String.join(" ", res));
	}

	public static String horizontalStrings(char[][] grid) {
		StringBuilder b = new StringBuilder();
		for (int row = 0; row < grid.length; row++) {
			String curr = new String(grid[row]);
			b.append(curr);
			b.append("-!-");
		}
		// words corresponding to reading the rows
		String rowWords = b.toString();
		return rowWords;
	}

	public static String verticalStrings(char[][] grid) {
		StringBuilder b = new StringBuilder();
		for (int col = 0; col < grid[0].length; col++) {
			for (int row = 0; row < grid.length; row++) {
				b.append(grid[row][col]);
			}
			b.append("-!-");
		}
		return b.toString();
	}

	public static String reversedString(String word) {
		StringBuilder b = new StringBuilder();
		for (int i = word.length() - 1; i >= 0; i--) {
			b.append(word.charAt(i));
		}
		return b.toString();
	}

	// OLD METHODS

	public static boolean checkHorizontal(char[][] grid, String word) {
		boolean found = false;
		for (int row = 0; row < grid.length && !found; row++) {
			for (int col = 0; col < grid[0].length && !found; col++) {
				// you still have to manually build the string
				int c = col;
				int word_index = 0;

				while (c < grid[0].length && word_index < word.length() && word.charAt(word_index) == grid[row][c]) {
					c++;
					word_index++;
				}

				if (word_index == word.length())
					return true;
				word_index = 0;
				c = col;

				while (!found && c >= 0 && word_index < word.length() && word.charAt(word_index) == grid[row][c]) {
					c--;
					word_index++;
				}
				if (word_index == word.length())
					return true;
			}
		}
		return false;
	}

	public static boolean checkVertical(char[][] grid, String word) {
		int word_index = 0;
		for (int col = 0; col < grid[0].length; col++) {
			for (int row = 0; row < grid.length; row++) {
				int r = row;
				while (r < grid.length && word_index < word.length() && grid[r][col] == word.charAt(word_index)) {
					r++;
					word_index++;
				}
				if (word_index == word.length())
					return true;
				word_index = 0;
				r = row;
				while (r >= 0 && word_index < word.length() && grid[r][col] == word.charAt(word_index)) {
					r--;
					word_index++;
				}
				if (word_index == word.length())
					return true;
				word_index = 0;
			}
		}
		return false;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		if (args.length > 0) {
			Thread.currentThread().getContextClassLoader();
			InputStream inputStream = new FileInputStream(args[0]);
			in = new Scanner(inputStream);
		} else {
			in = new Scanner(System.in);
		}
		try {
			// input for grid
			int grid_row = in.nextInt();
			int grid_col = in.nextInt();
			char grid[][] = new char[grid_row][grid_col];
			for (int idx = 0; idx < grid_row; idx++) {
				for (int jdx = 0; jdx < grid_col; jdx++) {
					grid[idx][jdx] = in.next().charAt(0);
				}
			}
			// input for word
			int word_size = in.nextInt();
			String word[] = new String[word_size];
			for (int idx = 0; idx < word_size; idx++) {
				word[idx] = in.next();
			}
	
			funcPuzzle(grid, word);
		
		} finally {
			in.close();
		}
	}
}
