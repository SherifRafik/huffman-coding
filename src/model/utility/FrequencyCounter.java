package model.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

public class FrequencyCounter {

	private HashMap<Character, Integer> frequencies;
	public static Character PSEUDO_EOF;
	public static Character CHARACTER_CODE_SEPARATOR;
	public static Character HEADER_ENTRY_SEPARATOR;
	public static Character HEADER_BODY_SEPARATOR;

	public FrequencyCounter() {
		frequencies = new HashMap<Character, Integer>();
	}

	public void readFile(String fileName) {

		File file = new File(fileName);
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int c;
		try {
			while ((c = bufferedReader.read()) != -1) {
				char character = (char) c;
				if (!frequencies.containsKey(character)) {
					frequencies.put(character, 0);
				}
				frequencies.put(character, frequencies.get(character) + 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Character maxKey = Collections.max(frequencies.keySet());
		PSEUDO_EOF = (char) ((int) maxKey + 1);
		CHARACTER_CODE_SEPARATOR = (char) ((int) maxKey + 2);
		HEADER_ENTRY_SEPARATOR = (char) ((int) maxKey + 3);
		HEADER_BODY_SEPARATOR = (char) ((int) maxKey + 4);

		System.out.println(PSEUDO_EOF + " " + CHARACTER_CODE_SEPARATOR + " " + HEADER_ENTRY_SEPARATOR + " "
				+ HEADER_BODY_SEPARATOR);

		return;
	}

	public HashMap<Character, Integer> getFrequencies() {
		return frequencies;
	}

}
