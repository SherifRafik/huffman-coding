package model.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;

public class FrequencyCounter {

	private HashMap<Character, Integer> frequencies;
	private StringBuilder fileContent;
	private String filePath;
	private double fileSize;
	private String extension;

	public FrequencyCounter(String path) {
		frequencies = new HashMap<Character, Integer>();
		fileContent = new StringBuilder();
		filePath = path;
		extension = "";
		getExtension();
	}

	private void getExtension() {
		int index = filePath.lastIndexOf('.');
		if (index > 0)
			extension = filePath.substring(index + 1);
	}

	public void readFile() {
		if (extension.equals("txt"))
			readTxtFile();
		else
			readBinaryFile();
	}

	private void readTxtFile() {
		try {
			File file = new File(filePath);
			fileSize = file.length();
			FileReader fileReader = null;
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int c;
			while ((c = bufferedReader.read()) != -1) {
				char character = (char) c;
				fileContent.append(character);
				if (!frequencies.containsKey(character)) {
					frequencies.put(character, 0);
				}
				frequencies.put(character, frequencies.get(character) + 1);
			}
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	private void readBinaryFile() {
		try {
			File file = new File(filePath);
			fileSize = file.length();
			byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
			int numberOfBytes = fileBytes.length;

			for (int i = 0; i < numberOfBytes; i++) {
				char character = (char) (fileBytes[i]);
				fileContent.append(character);
				if (!frequencies.containsKey(character)) {
					frequencies.put(character, 0);
				}
				frequencies.put(character, frequencies.get(character) + 1);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public String getFileContent() {
		return fileContent.toString();
	}

	public HashMap<Character, Integer> getFrequencies() {
		return frequencies;
	}

	public double getFileSize() {
		return fileSize;
	}

}
