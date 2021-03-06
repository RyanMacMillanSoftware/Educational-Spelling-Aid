package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/*A utility class that reads a wordlist and determines the number of levels that
 * a user needs to be able to choose from.
 */

public final class ApplicationUtility {
	public static int evaluateMaxLevelInFile(){
		try{
			File file = new File(Main.getCurrentWordList());
			if(!file.exists()){
				return 0;
			}
			FileReader fi = new FileReader(file);
			BufferedReader br = new BufferedReader(fi);
			String line = null;
			int level = 0;
			while((line= br.readLine())!=null){
				if(line.contains("%Level ")){
					line = line.split("%Level ")[1];
					level = Integer.parseInt(line);
				}
			}
			br.close();
			return level;
		}catch(IOException io){
			io.printStackTrace();
		} 
		return 0;
	}
}
