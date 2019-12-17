package model.huffman;

import java.io.PrintWriter;

public class CharacterGenerator {

	public static void generate(String fileName, int numOfChars) {

		PrintWriter writer;

		try {

			writer = new PrintWriter(fileName, "UTF-8");
		} catch (Exception e) {

			e.printStackTrace();
			return;
		}

		for (int i = 0; i < numOfChars; i++) {
			writer.print((char) (32 + (int) (Math.random() * ((126 - 32) + 1))));
		}

		writer.close();
	}

}