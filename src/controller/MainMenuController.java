package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import application.ModelUpdateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
/**
 * A view-controller that is bound to the levels_layout fxml
 * @author Mohan Cao
 * @author Ryan MacMillan
 */
public class MainMenuController extends SceneController{
	@FXML private Button nQuizBtn;
	@FXML private Button vStatsBtn;
	@FXML private Button cStatsBtn;
	@FXML private Button rMistakesBtn;
	@FXML private Button changeWordlistBtn;
	
	@FXML public void chooseFile(MouseEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Word List");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Text Files", "*.txt"));
		File newWordList = fileChooser.showOpenDialog(application._stage);
		if (newWordList != null){
			String filename = newWordList.getName();
			//TODO: Write to wordlist file path file
			
			
			try {
				
				final String dir = System.getProperty("user.dir");
				File path = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
				 
				File file = new File(path.getParent()+"/src/.listpath.txt");
				
				//File file = new File(dir +"/src/.listpath.txt");

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(path + "/"+filename+"\n");
				bw.close();


			} catch (IOException ex) {
				ex.printStackTrace();
			}catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			
		} else {
			
		}

		
	}
	
	/**
	 * Listener for new quiz navigation button
	 * @param e MouseEvent
	 */
	@FXML public void newQuiz(MouseEvent e){
		application.requestSceneChange("levelMenu");
	}
	/**
	 * Listener for Stats view navigation button
	 * @param e MouseEvent
	 */
	@FXML public void viewStats(MouseEvent e){
		application.requestSceneChange("statsMenu");
	}
	/**
	 * Listener for review mistakes view navigation button
	 * @param e MouseEvent
	 */
	@FXML public void reviewMistakes(MouseEvent e){
		application.requestSceneChange("levelMenu","failed");
	}
	@Override
	public void init(String[] args) {}
	@Override
	public void onModelChange(String fieldName, Object...objects ) {}
	@Override
	public void cleanup() {}
}
