package view;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Compression {

	Controller controller = new Controller();
	FileChooser fileChooser;

	public Stage window;
	public MenuItem loadFile;
	public MenuItem saveAsFile;

	public Label msgLabel;
	public Label pathLabel;

	public Button compressButton;
	public Button decompressButton;
	String path;

	boolean isEmpty;

	private long timeTaken;

	public void initialize(Stage primaryStage) throws Exception {

		window = primaryStage;
		window.setTitle("Huffman Compression");
		window.setResizable(false);

		try {
			Path currentRelativePath = Paths.get("");
			String path = currentRelativePath.toAbsolutePath().toString() + "/src/view/Compression.fxml";

			Parent root = FXMLLoader.load(new File(path).toURI().toURL());
			Scene scene = new Scene(root);
			scene.getStylesheets().add("view/darkTheme.css");
			primaryStage.setScene(scene);
			primaryStage.setTitle("Huffman Compression");
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh(String path) {
		pathLabel.setText(path);
	}

	public void load() {

		fileChooser = new FileChooser();
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/resources";
		fileChooser.setInitialDirectory(new File(currentPath));
		File file = fileChooser.showOpenDialog(window);
		if (file != null) {
			path = file.getAbsolutePath();
			long start = System.currentTimeMillis();
			isEmpty = controller.load(path);
			long end = System.currentTimeMillis();
			timeTaken = end - start;
			if (!isEmpty) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Empty File");
				alert.show();
			}

			refresh(path);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File loaded successfuly.");
		}
	}

	public void compress() {
		if (!isEmpty) {
			return;
		}
		long start = System.currentTimeMillis();
		controller.compress();
		long end = System.currentTimeMillis();
		timeTaken = timeTaken + (end - start);
		fileChooser = new FileChooser();
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/resources";
		fileChooser.setInitialDirectory(new File(currentPath));
		File file = fileChooser.showSaveDialog(window);
		if (file != null) {
			path = file.getAbsolutePath();
			start = System.currentTimeMillis();
			double compressionRatio = controller.saveAs(path);
			end = System.currentTimeMillis();
			timeTaken = timeTaken + (end - start);
			if (compressionRatio == -1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Input file is too small for compression");
				alert.show();
				return;
			}
			refresh(path);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File is compressed in: " + timeTaken + "ms" + System.lineSeparator()
					+ "Compression Ratio = " + compressionRatio);
		}

	}

	public void decompress() {

		fileChooser = new FileChooser();
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/resources";

		fileChooser.setInitialDirectory(new File(currentPath));
		File file = fileChooser.showOpenDialog(window);

		if (file != null) {
			path = file.getAbsolutePath();
			refresh(path);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File loaded successfuly.");
		}

		String outputPath = "";
		fileChooser.setInitialDirectory(new File(currentPath));
		File outputFile = fileChooser.showSaveDialog(window);
		if (outputFile != null) {
			outputPath = outputFile.getAbsolutePath();
			long start = System.currentTimeMillis();
			controller.decompress(path, outputPath);
			long end = System.currentTimeMillis();
			timeTaken = end - start;
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File is decompressed in: " + timeTaken + "ms");
		}

	}

}
