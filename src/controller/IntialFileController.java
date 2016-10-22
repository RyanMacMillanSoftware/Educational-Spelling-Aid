package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import application.ModelUpdateEvent;
import application.StatisticsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class IntialFileController extends SceneController{

	@FXML
    private Label chosenListDisplay;
	@FXML 
	private Button confirmBtn;
	private String currentwordlist;
	

	
	@FXML public void openFileChooser(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Word List");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Text Files", "*.txt"));
		File newWordList = fileChooser.showOpenDialog(application._stage);
		if (newWordList != null){
			
			String filename = newWordList.getName();
			//TODO: Write to wordlist file path file
			
			
			try {
				
				File path = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
				currentwordlist = path.getParent()+"/"+filename;
				application.setCurrentWordList(currentwordlist);
				
				
				chosenListDisplay.setText(filename);

			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			confirmBtn.setDisable(false);
			
		} else {
			
		}

	}

	@FXML public void fileSelected(MouseEvent event) throws URISyntaxException {
		
		application.addStatsModel();
		
		File path = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		File file = new File(path.getParent()+"/src/resources/"+ application.getStatsName());
		if (file.exists()){
			application.changeStatsModel(application.getStatsName());
			application.requestSceneChange("mainMenu");
		} else{
			
			application.changeStatsModel(application.getStatsName());
			application.update(new ModelUpdateEvent(this,"onToLevels"));
			
		}
	}

	@Override
	public void init(String[] args) {
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
