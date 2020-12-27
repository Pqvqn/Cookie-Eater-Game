package ce3;

import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

public class Audio {
	
	private Board board;
	public static final float VOLUME_LOW = -15.0f,VOLUME_HIGH = 6.0f,VOLUME_NORM = 0f,VOLUME_RANGE=VOLUME_HIGH-VOLUME_LOW;
	
	public Audio(Board frame) {
		board = frame;
        
       //playSound("testmusic",VOLUME_HIGH);
	}
	
	//plays sound from audio file
	public void playFile(File soundfile, float volume) {
		try {
	    	AudioInputStream stream = AudioSystem.getAudioInputStream(soundfile.getAbsoluteFile());  
	        Clip clip = AudioSystem.getClip();
			clip.open(stream);
	        if(clip.isRunning())clip.stop();
	        clip.setFramePosition(0);
	        FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
	        gain.setValue(volume);
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
	public void playSound(String title, float volume) {
		playFile(new File("Cookie Eater/src/resources/sounds/"+title+".wav"),volume);
	}
	public void playSound(String title) {
		playSound(title,VOLUME_NORM);
	}
	
}
