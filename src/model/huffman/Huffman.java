package model.huffman;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import model.utility.NodeComparator;

public class Huffman {

	private Map<Character, String> codes;

	public Huffman() {
		codes = new HashMap<Character, String>();
	}

	// build the Huffman tree according to the frequencies
	private void buildHuffmanTree(HashMap<Character, Integer> frequencies) {
		PriorityQueue<Node> queue = new PriorityQueue<Node>(frequencies.size(), new NodeComparator());

		// create the leafs
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			queue.add(new Node(entry.getKey(), entry.getValue()));
		}

		// merge the leafs
		while (queue.size() > 1) {
			// Remove the 2 smallest nodes
			Node left = queue.poll();
			Node right = queue.poll();

			// Create an internal node
			Node internalNode = new Node(left.getFrequency() + right.getFrequency(), left, right);

			queue.add(internalNode);
		}

		assignCodes(queue.poll(), "");

		return;
	}

	private void assignCodes(Node root, String code) {

		if (root == null)
			return;

		if (root.isLeaf())
			codes.put(root.getCharacter(), code);

		assignCodes(root.getLeft(), code + "0");
		assignCodes(root.getRight(), code + "1");
	}

	public void compress(HashMap<Character, Integer> frequencies) {
		buildHuffmanTree(frequencies);
	}

	public void compressToFile(String inputFileName, String outputFileName) {
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);

		FileWriter fileWriter = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(inputFile);
			fileWriter = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader bufferedReader = new BufferedReader(fileReader);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		writeHeader(bufferedWriter);

		String encoded = "";
		int c;

		try {
			while ((c = bufferedReader.read()) != -1) {
				char character = (char) c;
				encoded += (this.codes.get(character));
			}
			// encoded += FrequencyCounter.PSEUDO_EOF;
			// System.out.println(encoded);
			encoded = calculateZeroPadding(encoded);

			int startIndex = 0;
			int endIndex = 8;
			int count = 0;
			for (int i = 0; i < encoded.length() / 8; i++) {
				int binary = Integer.parseInt(encoded.substring(startIndex, endIndex), 2);
				// System.out.println(binary + " " + (char)binary);
				startIndex = endIndex;
				endIndex += 8;
				count++;
				bufferedWriter.write((char) binary);
			}
			System.out.println(count);

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bufferedWriter.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeHeader(BufferedWriter bufferedWriter) {
		for (Entry<Character, String> entry : codes.entrySet()) {
			try {
				bufferedWriter.write(entry.getKey() + " : " + entry.getValue() + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bufferedWriter.write("=" + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] convertToBytes(String encoded) {
		BitSet bitSet = new BitSet(encoded.length());
		int bitCounter = 0;
		for (Character c : encoded.toCharArray()) {
			if (c.equals('1')) {
				bitSet.set(bitCounter);
			}
			bitCounter++;
		}
		return bitSet.toByteArray();
	}

	private String calculateZeroPadding(String encoded) {
		int remainder = (encoded.length()) % 8;
		int numberOfZerosToPad = 8 - remainder;

		for (int i = 0; i < numberOfZerosToPad; i++)
			encoded += '0';

		return encoded;
	}

	public Map<Character, String> getCodes() {
		return codes;
	}

}
