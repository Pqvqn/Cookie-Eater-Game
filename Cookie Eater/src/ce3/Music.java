package ce3;

import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music {
	
	private Board board;
	private File[][] tracks; //temporary organization for music
	Clip clip;
	
	
	public Music(Board frame){
		board = frame;
		
		File soundFile = new File("Cookie Eater/src/resources/sounds/testmusic.wav");
		AudioInputStream stream;
		try {
			stream = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(stream);
			board.audio.playClip(clip);

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread musicThread = new Thread(() -> {
			while(true) {
		        System.out.println(clip.isRunning());
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		musicThread.start();
	}
	
	
}
