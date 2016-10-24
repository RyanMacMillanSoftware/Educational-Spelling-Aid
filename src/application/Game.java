package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import resources.StoredStats;
import resources.StoredStats.Type;
/**
 * Game model class
 * Contains the logic for evaluating words and storing game state, and acts as the Game data model.
 * Created on demand for "review" and "new" games.
 * 
 * Requires a MainInterface and StatisticsModel to function properly.
 * 
 * @author Mohan Cao
 * @author Ryan Macmillan
 *
 */
public class Game {
	public static final int WORDS_NUM = 10;
	public static final int SAY_SPEED_INTRO = 1;
	public static final int SAY_SPEED_DEFAULT = 1;
	public static final int SAY_SPEED_SLOW = 2;
	public static final int SAY_SPEED_VERYSLOW = 3;
	private StatisticsModel stats;
	private MainInterface main;
	private List<String> wordList;

	public static String wordListFileName;
	private boolean faulted;
	private boolean prevFaulted;
	private boolean review;
	private boolean gameEnded;
	
	private int wordListSize;
	private int _level;
	private int _correct;
	private int _incorrect;
	
	private String voiceType;
	private MediaPlayer player;
	
	public Game(MainInterface app, StatisticsModel statsModel){
		this(app,statsModel,1);
	}
	public Game(MainInterface app, StatisticsModel statsModel, int level){
		main = app;
		stats = statsModel;
		wordList = new LinkedList<String>();
		voiceType = "kal_diphone";
		_level = level;
		wordListFile();
	}
	
	/**
	 * reads wordlist name as stored in file .listpath.txt
	 * @author ryan
	 */
	public void wordListFile(){
		try {
			File path = new File(main.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			File file = new File(path.getParent()+"/src/.listpath.txt");
			if (!file.exists()) {
				//Alert alert = new Alert//TODO: idk mayn
				System.out.println("doesnt exist");
			}
			FileInputStream fileIn = new FileInputStream(file);
			BufferedReader r = new BufferedReader(new InputStreamReader(fileIn));
			String line = r.readLine();
			wordListFileName = line;
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		
		
	}
	
	
	/**
	 * Get word list
	 * @return
	 * @author Mohan Cao
	 */
	public List<String> wordList(){
		return wordList;
	}
	
	/**
	 * Checks if game has ended
	 * @return true/false
	 * @author Mohan Cao
	 */
	public boolean isGameEnded(){
		return gameEnded;
	}
	

	public boolean isReview(){
		return review;
	}
	/**
	 * Toggles from kal_diphone voice to akl_nz_jdt_diphone or vice versa
	 * @author Ryan MacMillan
	 */
	public void changeVoice(){
		if(voiceType.equals("kal_diphone")){
			voiceType = "akl_nz_jdt_diphone";
			main.sayWord(SAY_SPEED_DEFAULT, voiceType, "Hello, I am harry.");
		}
		else {
			voiceType = "kal_diphone";
			main.sayWord(SAY_SPEED_DEFAULT, voiceType, "Hello, I am rob.");
		}
	}
	
	/**
	 * Returns voice type
	 * @return voice string
	 */
	public String getVoice(){
		final String voice = voiceType;
		return voice;
	}
	
	/**
	 * Get current level.
	 * @return level
	 */
	public int level() {
		return _level;
	}
	
	/**
	 * Gets word list from file system path
	 * @return whether the word list has been successfully fetched to the wordList variable
	 * @author Mohan Cao
	 * @author Ryan MacMillan
	 */
	private boolean getWordList(){
		try {
			File file = new File(wordListFileName);
			if(!file.exists()){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("You don't have a word list!\nPlease put one in the folder that you ran the spelling app from.");
				alert.showAndWait();
				return false;
			}
			FileReader fi = new FileReader(file);
			BufferedReader br = new BufferedReader(fi);
			String line = null;
			while((line= br.readLine())!=null){
				if(line.contains("%Level "+_level)){
					line = line.split("%Level ")[1];
					_level = Integer.parseInt(line);
					line = br.readLine();
					while(line!=null&&!line.startsWith("%Level ")){
						wordList.add(line.trim());
						line = br.readLine();
					}
					break;
				}
			}
			Collections.shuffle(wordList);
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Starts game in previous mode
	 */
	public void startGame(){
		startGame(review);
	}
	/**
	 * Starts the game with option for practice mode (review)
	 * @param practice review -> true
	 * @author Mohan Cao
	 */
	public void startGame(boolean practice){
		_correct=0;
		_incorrect=0;
		gameEnded=false;
		main.tell("gameStartConfigure", _level);
		review=false; //assume not reviewing words
		if(practice){
			HashSet<String> set = new HashSet<String>();
			set.addAll(stats.getGlobalStats().getKeys(Type.FAILED,_level));
			set.addAll(stats.getGlobalStats().getKeys(Type.FAULTED,_level));
			set.addAll(stats.getSessionStats().getKeys(Type.FAILED,_level));
			set.addAll(stats.getSessionStats().getKeys(Type.FAULTED,_level));
			wordList.addAll(set);
			if(wordList.size()==0){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("");
				alert.setHeaderText("No words to review");
				alert.setContentText("You haven't any words to review!\nDo a spelling quiz first.");
				alert.showAndWait();
				gameEnded=true;
				main.requestSceneChange("levelMenu","failed");
				return;
			}
			Collections.shuffle(wordList);
			review=true; //reviewing words
		}else{
			getWordList();
			if(wordList.size()==0){
				main.tell("gameWin");
				gameEnded=true;
			}
		}
		if(!wordList.isEmpty()){
			wordList = wordList.subList(0, (wordList.size()>=WORDS_NUM)?WORDS_NUM:wordList.size());
			URL url = getClass().getClassLoader().getResource("src/resources/please_spell.mp3");
			Media media = new Media(url.toString());
			player = new MediaPlayer(media); 
			player.play();
			main.tell("wait", 0d);
		    	
		    	player.setOnEndOfMedia(new Runnable() {
		            @Override public void run() {
		            	main.sayWord(SAY_SPEED_DEFAULT,voiceType,wordList.get(0));
		            	main.tell("resume", 0d);
		            }
		          });
		    	
		    	
				
		}
		//set faulted=false for first word
		main.tell("setProgress",0d);
		wordListSize=(wordList.size()!=0)?wordList.size():1;
		faulted=false;
	}
	
	/**
	 * Repeat the word using festival
	 * @author Ryan MacMillan
	 */
	public void repeatWord(){
		if(!gameEnded){
		main.sayWord(SAY_SPEED_DEFAULT, voiceType, wordList.get(0));
		}
	}
	
	/**
	 * Called when game is going to exit.
	 * @return true (default) or false to indicate cancellation of exiting
	 * @author Mohan Cao
	 */
	public boolean onExit(){
		if(gameEnded){
			return true;
		}
		Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure you want to quit?");
        alert.setContentText("You will lose progress\nIf you are in the middle of a word,\nit will be incorrect");
        Optional<ButtonType> response = alert.showAndWait();
        if(response.get()==ButtonType.OK){
        	return true;
        }else{
        	return false;
        }
	}
	/**
	 * Check word against game logic
	 * @param word
	 * @author Mohan Cao
	 */
	public void submitWord(String word){
		boolean failedd = false;
		boolean playReward=false;
		if(!gameEnded){
			int speed = SAY_SPEED_DEFAULT;
			boolean prev2Faulted = prevFaulted;
			prevFaulted = faulted;
			String testWord = wordList.get(0);
			//mark as faulted if word is wrong
			faulted=!word.toLowerCase().equals(testWord.toLowerCase());
			if(!faulted&&!prevFaulted){
				//mastered
				main.tell("masteredWord",testWord);
				faulted=false;
				stats.getSessionStats().addStat(Type.MASTERED,testWord, 1, _level);
				_correct++;
				// if review, remove from failedlist and faultedlist
				if(review){
				stats.getGlobalStats().setStats(Type.FAILED, testWord, 0);
				stats.getSessionStats().setStats(Type.FAILED, testWord, 0);
				stats.getGlobalStats().setStats(Type.FAULTED, testWord, 0);
				stats.getSessionStats().setStats(Type.FAULTED, testWord, 0);
				
				}
				playReward = true;
				
				wordList.remove(0);
			}else if(faulted&&!prevFaulted){
				//faulted once => set faulted
				main.tell("faultedWord",testWord);
				speed = SAY_SPEED_SLOW;
			}else if(!faulted&&prevFaulted){
				//correct after faulted => store faulted
				main.tell("masteredWord",testWord);
				stats.getSessionStats().addStat(Type.FAULTED,testWord, 1, _level);
				_incorrect++;
				wordList.remove(0);
			}else if(review&&!prev2Faulted){
				//give one more chance in review, set speed to very slow
				main.tell("lastChanceWord",testWord);
				speed = SAY_SPEED_VERYSLOW;
			}else{
				//faulted twice => failed
				main.tell("failedWord",testWord);
				faulted=false;
				stats.getSessionStats().addStat(Type.FAILED, testWord, 1, _level);
				wordList.remove(0);
				_incorrect++;
				failedd = true;
				URL url = getClass().getClassLoader().getResource("src/resources/weepwow.mp3");
				Media media = new Media(url.toString());
				player = new MediaPlayer(media); 
				player.play();
				main.tell("wait", 0d);
			    	
			    	player.setOnEndOfMedia(new Runnable() {
			            @Override public void run() {
			            	main.sayWord(SAY_SPEED_DEFAULT,voiceType, wordList.get(0));
			            	main.tell("resume", 0d);
			            }
			          });
			   
			   
			}
			if(wordList.size()!=0){
				if (playReward){
					Random rand = new Random();
					int n = rand.nextInt(3)+1;
					URL url = getClass().getClassLoader().getResource("src/resources/woohoo.mp3");
					if(n%3==1){
						url = getClass().getClassLoader().getResource("src/resources/yay.mp3");
					}else if(n%3==2){
						url = getClass().getClassLoader().getResource("src/resources/yeehaw.mp3");
					}
					Media media = new Media(url.toString());
					player = new MediaPlayer(media); 
					player.play();
					main.tell("wait", 0d);
				    	
				    	player.setOnEndOfMedia(new Runnable() {
				            @Override public void run() {
				            	main.sayWord(SAY_SPEED_DEFAULT,voiceType,wordList.get(0));
				            	main.tell("resume",0d);
				            }
				          });
				    	
			    	
				} else if(!failedd){
					main.sayWord(speed,voiceType, wordList.get(0));
				}
				
			}else{
				//end game
				if(prevFaulted||faulted||prev2Faulted){
					main.tell("resetGame",_correct,(_correct+_incorrect),testWord);
				}else{
					main.tell("resetGame",_correct,(_correct+_incorrect));
				}
				
				if(!review&&(_correct/(double)(_incorrect+_correct))>=0.9){
					stats.getSessionStats().unlockLevel(_level+1);
					main.tell("showRewards");
				}
				gameEnded=true;
				URL url = getClass().getClassLoader().getResource("src/resources/good_work.m4a");
				Media media = new Media(url.toString());
				player = new MediaPlayer(media); 
		    	player.play();
			}
			//set progressbars for progress through quiz and also denote additional separation for faulted words
			main.tell("setProgress",(wordListSize-wordList.size()+((faulted)?0.5:0))/(double)wordListSize);
		}
	}
	
	
}
