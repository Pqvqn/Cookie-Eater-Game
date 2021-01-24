package ce3;

import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

public class Audio {
	
	//private Game game;
	public static final float VOLUME_LOW = -20.0f,VOLUME_HIGH = 6.0f,VOLUME_NORM = 0f,VOLUME_RANGE=VOLUME_HIGH-VOLUME_LOW;
	public float volumeReduction; //negative offset of max volume, all volumes are adjusted relative to original range
	public HashMap<String,File> loaded; //already loaded sound files to be accessed by string name
	private final String[] preload = {"bonk2","chomp"}; //files to load at start of program
	public boolean mute; //whether audio should play at all
	
	public Audio(Game frame) {
		//game = frame;
		mute = true;
		loaded = new HashMap<String,File>();
		volumeReduction = 0;
        for(String p : preload) {
        	File f = new File("Cookie Eater/src/resources/sounds/"+p+".wav");
			loaded.put(p,f);
        }

       //playSound("testmusic",VOLUME_HIGH);
	}
	
	//plays clip
	public void playClip(Clip clip) {
		if(mute)return;
		if(clip.isRunning())clip.stop();
        clip.setFramePosition(0);

		clip.start();
	}
	
	//adjusts clip volume
	public void setClipVolume(Clip clip, float volume) {
        FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        
        //adjust for a reduced volume
        float adjustedVolume = ((volume-VOLUME_LOW)/VOLUME_RANGE) * (VOLUME_HIGH-VOLUME_LOW-volumeReduction) + VOLUME_LOW;

        gain.setValue(adjustedVolume);
	}
	
	//plays sound from audio file
	public void playFile(File soundfile, float volume) {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(soundfile.getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			
			setClipVolume(clip,volume);
			playClip(clip);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	//plays sound based on name of file from sound package
	public void playSound(String title, float volume) {
		if(loaded.get(title)!=null) { //if title is already loaded, use the already loaded input stream
			playFile(loaded.get(title),volume);
		}else {
			playFile(new File("Cookie Eater/src/resources/sounds/"+title+".wav"),volume);
		}
	}
	public void playSound(String title) {
		playSound(title,VOLUME_NORM);
	}
	
	public void setMute(boolean m) {mute = m;}
	public void toggleMute() {setMute(!mute);}
	public void setVolumeReduction(float r) {volumeReduction = r;}
	
}
