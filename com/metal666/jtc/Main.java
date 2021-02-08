package com.metal666.jtc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

public class Main extends Application {

	private static String PATH;

	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("fxml/MainWindow.fxml"));
		primaryStage.setTitle("JarToConsole");

		Scene primaryScene = new Scene(root);

		primaryScene.getStylesheets().add("com/metal666/jtc/style.css");

		primaryStage.setScene(primaryScene);
		primaryStage.show();

	}

	public static void main(String[] args) {

		Options options = new Options();

		Option input = new Option("f", "file", true, "input jar path");
		input.setRequired(false);
		options.addOption(input);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {

			cmd = parser.parse(options, args);

			PATH = cmd.getOptionValue("file");

		}

		catch(ParseException e) {

			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		launch(args);

	}

	public static String getPath() {

		return PATH;

	}

}
