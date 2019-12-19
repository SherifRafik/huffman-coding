package view;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
			primaryStage.setTitle("English Huffman Compression");
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
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/resources";
		fileChooser.setInitialDirectory(new File(currentPath));
		File file = fileChooser.showOpenDialog(window);
		if (file != null) {
			path = file.getAbsolutePath();
			controller.load(path);
			refresh(path);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File loaded successfuly.");
		}
	}

	public void compress() {
		controller.compress();
		fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/resources";
		fileChooser.setInitialDirectory(new File(currentPath));
		File file = fileChooser.showSaveDialog(window);
		if (file != null) {
			path = file.getAbsolutePath();
			controller.saveAs(path);
			refresh(path);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File compressed successfully.");
		}

	}

	public void decompress() {

		fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
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
			controller.decompress(path, outputPath);
			msgLabel.setTextFill(Color.WHITE);
			msgLabel.setText("File decompressed successfully.");
		}

	}

}
