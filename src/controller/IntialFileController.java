package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import application.ApplicationUtility;
import application.Main;
import application.ModelUpdateEvent;
import application.StatisticsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class IntialFileController extends SceneController {

	@FXML
	private Label chosenListDisplay;
	@FXML
	private Button confirmBtn;
	private String currentwordlist;

	@FXML
	public void openFileChooser(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Word List");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
		File newWordList = fileChooser.showOpenDialog(application._stage);
		try {
			FileReader fi = new FileReader(newWordList);
			BufferedReader br = new BufferedReader(fi);
			String line = null;
			if (!br.readLine().contains("%Level")) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("This is not a valid wordlist!");
				alert.showAndWait();
				br.close();
			} else {
				if (newWordList != null) {

					String filename = newWordList.getName();

					try {

						File path = new File(
								this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
						currentwordlist = path.getParent() + "/" + filename;
						application.setCurrentWordList(currentwordlist);

						File filee = new File(path.getParent() + "/src/.listpath.txt");
						if (!filee.exists()) {
							filee.createNewFile();
						}
						FileWriter fw = new FileWriter(filee.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(path.getParent() + "/" + filename + "\n");
						bw.close();

						chosenListDisplay.setText(filename);

					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
					confirmBtn.setDisable(false);

				}

			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void fileSelected(MouseEvent event) throws URISyntaxException {

		boolean firsttime = true;
		File path = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		File file = new File(path.getParent() + "/src/resources/" + application.getStatsName());
		if (file.exists()) {
			application.requestSceneChange("mainMenu");
			firsttime = false;
		} else {

			application.requestSceneChange("firstTime");
		}
		application.addStatsModel();
		application.changeStatsModel(application.getStatsName());
		if (!firsttime) {

			for (Integer i : application.getStatsModel().getGlobalStats().getUnlockedLevelSet()) {
				application.getStatsModel().getSessionStats().unlockLevel(i);
				// application.getStatsModel().getGlobalStats().unlockLevel(i);
			}
		}
	}

	@Override
	public void init(String[] args) {
		if (application.hasStatsModel()) {
			application.getStatsModel().sessionEnd();
		}
		confirmBtn.setDisable(true);
		chosenListDisplay.setText("Please choose a file");
	}

	@Override
	public void cleanup() {

	}

	@Override
	public void onModelChange(String notificationString, Object... objectsParameters) {

	}

}
