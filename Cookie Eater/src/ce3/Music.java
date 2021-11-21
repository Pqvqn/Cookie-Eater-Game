package ce3;

import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music {
	
	private Game game;
	private File[] tracks; //temporary organization for music
	Clip clip;
	File file;
	
	public Music(Game frame){
		game = frame;
		tracks = new File[2];
		tracks[0] = new File("Cookie Eater/src/resources/sounds/testmusic.wav");
		tracks[1] = new File("Cookie Eater/src/resources/sounds/exammusic.wav");
		
		Thread musicThread = new Thread(() -> {
			while(true) {
				 
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(game.board!=null && game.board.currLevel!=null) {
					//if no clip is playing or the current song doesn't fit, play new
			        if((clip!=null && clip.isRunning() && currentSongIsValid()) || game.audio.mute) {
			        	if(game.audio.mute && clip!=null && clip.isRunning()) {
			        		clip.close();
			        	}
			        	if(clip!=null && clip.isRunning() && currentSongIsValid())  {
							game.audio.setClipVolume(clip,Audio.VOLUME_NORM);
			        	}
					}else {
						playSong(chosenSong());
					}
			       
				}
			}
		});
		musicThread.start();
	}
	
	//test if the current playing song should continue
	public boolean currentSongIsValid() {
		if(game.board.currLevel.getKeyTheme().equals("harsh")) {
			return tracks[1]==file;
		}else {
			return tracks[0]==file;
		}
	}
	//choose the song to play at this moment
	public File chosenSong() {
		if(game.board.currLevel.getKeyTheme().equals("harsh")) {
			return tracks[1];
		}else {
			return tracks[0];
		}
	}
	//open clip and play from Audio
	public void playSong(File f) {
		if(clip!=null && clip.isRunning())clip.close();
		file = f;
		AudioInputStream stream;
		try {
			stream = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(stream);
			game.audio.setClipVolume(clip,Audio.VOLUME_NORM);
			game.audio.playClip(clip);

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
