package controller;

import java.util.HashMap;

import model.huffman.Huffman;
import model.utility.FrequencyCounter;

public class Controller {

	FrequencyCounter fc;
	Huffman huffman = new Huffman();
	HashMap<Character, Integer> frequencies;
	String fileContent;
	double inputFileSize;

	public void load(String path) {
		FrequencyCounter fc = new FrequencyCounter(path);
		fc.readFile();
		frequencies = fc.getFrequencies();
		fileContent = fc.getFileContent();
		inputFileSize = fc.getFileSize();
	}

	public void compress() {
		huffman.compress(frequencies);
	}

	public void decompress(String inputFilePath, String outputFilePath) {
		huffman.decompress(inputFilePath, outputFilePath);
	}

	public void saveAs(String path) {
		huffman.compressToFile(fileContent, path);
		double compressedFileSize = huffman.getCompressedFileSize();
		double compressionRatio = compressedFileSize / inputFileSize;
		System.out.println("Compression Ratio = " + compressionRatio);
	}

}
