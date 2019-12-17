package controller;

import java.util.HashMap;

import model.huffman.CharacterGenerator;
import model.huffman.Huffman;
import model.utility.FrequencyCounter;

public class Main {

	public static void main(String[] args) {

		/*
		 * HashMap<Character, Integer> frequencies = new HashMap<Character, Integer>();
		 * 
		 * frequencies.put('a', 5); frequencies.put('b', 9); frequencies.put('c', 12);
		 * frequencies.put('d', 13); frequencies.put('e', 16); frequencies.put('f', 45);
		 * 
		 * Huffman huffman = new Huffman(); huffman.compress(frequencies);
		 * 
		 * for (Map.Entry<Character, String> entry : huffman.getCodes().entrySet()) {
		 * System.out.println("Character: " + entry.getKey() + " Code: " +
		 * entry.getValue()); }
		 */

		CharacterGenerator.generate("input.txt", 40000);
		FrequencyCounter fc = new FrequencyCounter();
		fc.readFile("input.txt");

		HashMap<Character, Integer> frequencies = fc.getFrequencies();
		/*
		 * for (Entry<Character, Integer> entry : frequencies.entrySet()) {
		 * System.out.println("Character: " + entry.getKey() + " Frequency: " +
		 * entry.getValue()); }
		 */

		Huffman huffman = new Huffman();
		huffman.compress(frequencies);

		/*
		 * for (Map.Entry<Character, String> entry : huffman.getCodes().entrySet()) {
		 * System.out.println("Character: " + entry.getKey() + " Code: " +
		 * entry.getValue()); }
		 */

		huffman.compressToFile("input.txt", "output.txt");

	}

}
