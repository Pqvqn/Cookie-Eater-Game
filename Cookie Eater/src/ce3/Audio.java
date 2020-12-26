package ce3;

import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

public class Audio {
	
	private Board board;
	Clip clip;
	
	public Audio(Board frame) {
		board = frame;
        
       //playSound("testmusic");
	}
	
	//plays sound from audio file
	public void playFile(File soundfile) {
		try {
	    	AudioInputStream stream = AudioSystem.getAudioInputStream(soundfile.getAbsoluteFile());  
	        clip = AudioSystem.getClip();
			clip.open(stream);
	        if(clip.isRunning())clip.stop();
	        clip.setFramePosition(0);
			clip.start();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	//plays sound based on name of file from sound package
	public void playSound(String title) {
		playFile(new File("Cookie Eater/src/resources/sounds/"+title+".wav"));
	}
}
