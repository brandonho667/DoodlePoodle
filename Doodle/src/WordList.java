import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordList {
	ArrayList<ArrayList<String>> wordList = new ArrayList<ArrayList<String>>();
	HashMap<Integer, String> categories = new HashMap<>();

	/**
	 * initlalizes a 2D ArrayList that stores all the words with their
	 * categories
	 * 
	 * @throws IOException
	 */
	public WordList() throws IOException {
		categories.put(0, "General");
		categories.put(1, "Famous People");
		categories.put(2, "Fictional Characters");
		categories.put(3, "Famous Places");

		BufferedReader br = new BufferedReader(new FileReader("wordList.in"));
		String categ;
		while ((categ = br.readLine()) != null) {
			StringTokenizer line = new StringTokenizer(categ);
			ArrayList<String> category = new ArrayList<String>();
			while (line.hasMoreTokens()) {
				String word = line.nextToken().replace('_', ' ');
				category.add(word);
			}
			wordList.add(category);
		}
		br.close();
	}

	/**
	 * gets a random word from 2D ArrayList
	 * 
	 * @return a random word
	 */
	public String getRandomWord() {
		int randCategory = (int) (Math.random() * wordList.size());
		int randWord = (int) (Math.random() * wordList.get(randCategory).size());
		return wordList.get(randCategory).get(randWord);
	}

	/**
	 * gets the category for the given word from 2D ArrayList
	 * 
	 * @param word
	 *            the given word (the randomly chosen word from the 2D
	 *            ArrayList)
	 * @return the category for the given word
	 */
	public String getCategory(String word) {
		for (int c = 0; c < wordList.size(); c++) {
			for (int w = 0; w < wordList.get(c).size(); w++) {
				if (wordList.get(c).get(w).equals(word)) {
					return categories.get(c);
				}
			}
		}
		return "";
	}
}