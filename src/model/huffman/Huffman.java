package model.huffman;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import model.utility.NodeComparator;

public class Huffman {

	private Map<Character, String> codes;
	private Map<String, Character> decompressingCodes;

	private static String HEADER_BODY_SEPARATOR = "!@";

	int numberOfZerosToPad;

	public Huffman() {
		codes = new HashMap<Character, String>();
		decompressingCodes = new HashMap<String, Character>();
	}

	public void compress(HashMap<Character, Integer> frequencies) {
		buildHuffmanTree(frequencies);
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

	public void compressToFile(String fileContent, String outputFileName) {

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

			// calculate zero padding
			encoded = calculateZeroPadding(encoded);

			int numberOfBytes = encoded.length() / 8;
			bufferedWriter.write(numberOfBytes + " " + numberOfZerosToPad + System.lineSeparator());

			writeLineSeparatorCodes(bufferedWriter);

			// Write header
			writeHeader(bufferedWriter);
			// Write body
			writeBody(fout, encoded);

			codes.clear();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private StringBuilder calculateZeroPadding(StringBuilder encoded) {
		int remainder = (encoded.length()) % 8;
		if (remainder == 0) {
			numberOfZerosToPad = 0;
			return encoded;
		}

		numberOfZerosToPad = 8 - remainder;

		for (int i = 0; i < numberOfZerosToPad; i++)
			encoded.append('0');

		return encoded;
	}

	private void writeHeader(BufferedWriter bufferedWriter) {
		try {

			for (Entry<Character, String> entry : codes.entrySet()) {
				if (entry.getKey() == '\n' || entry.getKey() == '\r')
					continue;
				bufferedWriter.write(entry.getKey() + ": " + entry.getValue() + System.lineSeparator());
			}

			bufferedWriter.write(HEADER_BODY_SEPARATOR + System.lineSeparator());

			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				// System.out.println(binary);
				fout.write((char) binary);
			}
			// System.out.println("-----");
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeLineSeparatorCodes(BufferedWriter bufferedWriter) {
		try {
			if (codes.containsKey('\n'))
				bufferedWriter.write(codes.get('\n') + " ");
			if (codes.containsKey('\r'))
				bufferedWriter.write(codes.get('\r') + System.lineSeparator());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void decompress(String inputFileName, String outputFileName) {

		try {

			File inputFile = new File(inputFileName);
			File outputFile = new File(outputFileName);

			FileReader fileReader = new FileReader(inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			int numberOfBytes = readHeader(bufferedReader);
			byte[] fileContent = Files.readAllBytes(Paths.get(inputFileName));
			String fileAsBits = readBody(fileContent, numberOfBytes);
			// Node rootNode = buildDecodingTree();
			String decoded = decode(fileAsBits);

			decompressToFile(decoded, bufferedWriter);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int readHeader(BufferedReader bufferedReader) {
		int numberOfBytes = 0;
		numberOfZerosToPad = 0;
		try {
			String[] data = bufferedReader.readLine().split(" ");
			numberOfBytes = Integer.parseInt(data[0]);
			numberOfZerosToPad = Integer.parseInt(data[1]);

			String[] lineSeparators = bufferedReader.readLine().split(" ");
			if (lineSeparators.length == 2) {
				decompressingCodes.put(lineSeparators[0], '\n');
				decompressingCodes.put(lineSeparators[1], '\r');
			} else {
				decompressingCodes.put(lineSeparators[0], '\n');
			}

			// System.out.println(numberOfBytes + " " + numberOfZerosToPad);
			String line;
			while (!((line = bufferedReader.readLine()).equals(HEADER_BODY_SEPARATOR))) {
				String[] currentLine = line.split(": ");
				if (currentLine.length == 2) {
					decompressingCodes.put(currentLine[1], currentLine[0].charAt(0));
				} else
					continue;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberOfBytes;

	}

	private String readBody(byte[] fileContent, int bytesLength) {
		StringBuilder content = new StringBuilder();
		int fileSize = fileContent.length;
		// scan from the beginning of the body till the second to last byte (fileSize -
		// 1)
		for (int i = (fileSize - bytesLength); i < fileSize - 1; i++) {
			// Convert the byte to an unsigned integer
			int temp = fileContent[i] & 0xFF;
			// Convert the integer into an 8 bit string and append it to the string builder
			content.append(String.format("%8s", Integer.toBinaryString(temp)).replace(' ', '0'));
		}

		// Handle the last byte
		int lastByte = fileContent[fileContent.length - 1] & 0xFF;
		content.append(String.format("%8s", Integer.toBinaryString(lastByte)).replace(' ', '0'));
		int length = content.length();
		content.delete(length - numberOfZerosToPad, length);
		// System.out.println(content);
		return content.toString();
	}

	private String decode(String fileAsBits) {
		StringBuilder currentSequenceOfBits = new StringBuilder();
		StringBuilder decoded = new StringBuilder();

		for (int i = 0; i < fileAsBits.length(); i++) {
			currentSequenceOfBits.append(fileAsBits.charAt(i));

			if (decompressingCodes.containsKey(currentSequenceOfBits.toString())) {
				decoded.append(decompressingCodes.get(currentSequenceOfBits.toString()));
				currentSequenceOfBits.setLength(0);
			}

		}
		return decoded.toString();
	}

	private void decompressToFile(String decoded, BufferedWriter bufferedWriter) {
		try {
			bufferedWriter.write(decoded);
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public Map<Character, String> getCodes() {
		return codes;
	}

}
