package com.metal666.jtc;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.metal666.jtc.config.ConfigItem;
import com.metal666.jtc.config.ConfigRoot;
import com.metal666.jtc.config.view.ConfigController;
import com.sun.istack.internal.Nullable;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;

public class MainWindowController {

	@FXML
	Text errorText;

	@FXML
	TextArea pathTextArea;

	@FXML
	VBox configsBox;

	private ConfigRoot config;

	final private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

	private File jarDir, yaml;

	public void initialize() {

		String path = Main.getPath();

		File jar = new File(Main.class.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath());

		jarDir = new File(jar.getParent());

		if(path != null) {

			if(new File(path).exists()) {

				pathTextArea.setText(path);

				try {

					yaml = new File(jarDir.getPath() + File.separator + "configs.yml");

					yaml.createNewFile();

					try(FileReader fileReader = new FileReader(yaml)) {

						config = mapper.readValue(fileReader, ConfigRoot.class);

					}

					catch(JsonMappingException e) {

						config = new ConfigRoot();

						config.configs = new ArrayList<>();

					}

					catch(IOException e) {

						e.printStackTrace();

						onError("Wasn't able to process configs.yml!");

					}

					if(config != null) {

						config.configs.forEach(config -> {

							System.out.println("CONFIG: " + config.name + "|" + config.params);

							try {

								FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ConfigCell.fxml"));

								GridPane pane = loader.load();

								ConfigController configController = loader.<ConfigController>getController();

								configController.init(config, this);

								if(config.jar != null && config.jar.equals(Main.getPath())) {

									configController.setSelected(true);

								}

								configsBox.getChildren().add(pane);

							}

							catch(IOException e) {

								e.printStackTrace();

							}

						});

					}

				}

				catch(IOException e) {

					e.printStackTrace();

					onError("Wasn't able to create configs.yml!");

				}

			}

			else {

				errorText.setText("The provided path is invalid!");

			}

		}

		else {

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
					"No launch arguments provided. Would you like to initialize app in current directory?",
					ButtonType.YES,
					ButtonType.NO);

			alert.showAndWait();

			if(alert.getResult() == ButtonType.YES) {

				try {

					if((Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
							"Software\\Classes\\*\\shell\\Run in console") ||
							Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER,
								"Software\\Classes\\*\\shell",
								"Run in console")) &&
							(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
								"Software\\Classes\\*\\shell\\Run in console\\command") ||
									Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER,
										"Software\\Classes\\*\\shell\\Run in console",
										"command"))) {

						File bat = new File(jarDir.getPath() + File.separator + "JarToConsole.bat");

						Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
								"Software\\Classes\\*\\shell\\Run in console\\command",
								"",
								bat.getPath() + " %1");

						try {

							bat.createNewFile();

							try(FileWriter fileWriter = new FileWriter(bat)) {

								fileWriter.write("java -jar " + jar.getPath() + " -f=%1\n" +
										"set /p command=<command\n" +
										"del command\n" +
										"echo Running java with parameters '%command%'\n" +
										"java -jar %command%");

								onRegSuccess();

							}

							catch(IOException e) {

								e.printStackTrace();

								onError("Wasn't able to write to .bat file!");

							}

						}

						catch(IOException e) {

							e.printStackTrace();

							onError("Wasn't able to create .bat file!");

						}

					}

					else {

						onError(null);

					}

				}

				catch(Win32Exception e) {

					onError("Wasn't able to create registry keys!");

				}

			}

			else {

				Platform.exit();

			}

		}

	}

	@FXML
	public void onNewConfig(ActionEvent event) {

		try {

			ConfigItem item = new ConfigItem();

			item.name = "config";

			config.configs.add(item);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ConfigCell.fxml"));

			GridPane pane = loader.load();

			loader.<ConfigController>getController().init(item, this);

			configsBox.getChildren().add(pane);

			save();

		}

		catch(IOException e) {

			e.printStackTrace();

		}

	}

	@FXML
	private void onRun(ActionEvent event) {

		try(FileWriter fileWriter = new FileWriter(new File(jarDir.getPath() + File.separator + "command"))) {

			String params = " ";

			for(ConfigItem configItem : config.configs) {

				if(configItem.jar != null && configItem.jar.equals(Main.getPath())) {

					params += configItem.params;

					break;

				}

			}

			fileWriter.write(Main.getPath() + params);

		}

		catch(IOException e) {

			e.printStackTrace();

		}

		Platform.exit();

	}

	private void onError(@Nullable String message) {

		Alert alert = new Alert(Alert.AlertType.ERROR,
				message == null ? "Something went wrong!" : message,
				ButtonType.CLOSE);

		alert.showAndWait();

		Platform.exit();

	}

	private void onRegSuccess() {

		Alert alert = new Alert(Alert.AlertType.INFORMATION,
				"App was successfully initialized! If you move this jar to another location, rerun it without arguments to regenerate files and registry values",
				ButtonType.CLOSE);

		alert.showAndWait();

		Platform.exit();

	}

	public void save() {

		if(yaml != null && yaml.exists()) {

			try(FileWriter fileWriter = new FileWriter(yaml)) {

				mapper.writeValue(fileWriter, config);

			}

			catch(IOException e) {

				e.printStackTrace();

				onError("Wasn't able to save configs.yml!");

			}

		}

		else {

			onError("Wasn't able to save configs.yml!");

		}

	}

	public void deleteConfig(ConfigItem configItem) {

		config.configs.remove(configItem);

		save();

	}

	public void selectConfig(RadioButton radioButton, ConfigItem item) {

		configsBox.getChildren().forEach(grid -> ((GridPane) grid).getChildren().forEach(child -> {

			if(child.getId() != null && child.getId().equals("radioButton") && child != radioButton) {

				((RadioButton) child).selectedProperty().set(false);

			}

		}));

		config.configs.forEach(configItem -> {

			if(configItem == item) {

				configItem.jar = Main.getPath();

			}

			else if(configItem.jar.equals(Main.getPath())) {

				configItem.jar = null;

			}

		});

		save();

	}

}
