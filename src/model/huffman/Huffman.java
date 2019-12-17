package model.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import model.utility.NodeComparator;

public class Huffman {

	private Map<Character, String> codes;

	private static final String HEADER_BODY_SEPARATOR = "!@#";

	public Huffman() {
		codes = new HashMap<Character, String>();
	}

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

	public void writeToFile(String fileContent, String outputFileName) {

		try {

			File outputFile = new File(outputFileName);
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			FileOutputStream fout = new FileOutputStream(outputFile, true);

			StringBuilder encoded = new StringBuilder();
			// append all the codes to encoded
			for (char c : fileContent.toCharArray()) {
				encoded.append(codes.get(c));
			}
			// calculate zero padding required
			encoded = calculateZeroPadding(encoded);

			System.out.println(encoded);
			// Write header
			writeHeader(bufferedWriter);
			// Write body
			writeBody(fout, encoded);
			// Clear the hashmap for decoding
			codes.clear();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeHeader(BufferedWriter bufferedWriter) {
		try {

			for (Entry<Character, String> entry : codes.entrySet()) {
				bufferedWriter.write(entry.getKey() + " : " + entry.getValue() + System.lineSeparator());
			}

			bufferedWriter.write(HEADER_BODY_SEPARATOR + System.lineSeparator());

			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuilder calculateZeroPadding(StringBuilder encoded) {
		int remainder = (encoded.length()) % 8;
		int numberOfZerosToPad = 8 - remainder;

		for (int i = 0; i < numberOfZerosToPad; i++)
			encoded.append('0');

		return encoded;
	}

	private void writeBody(FileOutputStream fout, StringBuilder encoded) {
		try {
			int startIndex = 0;
			int endIndex = 8;
			int length = encoded.length() / 8;

			for (int i = 0; i < length; i++) {
				int binary = Integer.parseInt(encoded.substring(startIndex, endIndex), 2);
				startIndex = endIndex;
				endIndex += 8;
				fout.write((char) binary);
			}

			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<Character, String> getCodes() {
		return codes;
	}

}
