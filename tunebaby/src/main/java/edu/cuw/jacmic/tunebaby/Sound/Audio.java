package edu.cuw.jacmic.tunebaby.Sound;
import java.io.File;

import javax.sound.sampled.*;


public class Audio {

	static Song currentSong;
	
	public static void loadAudio(String fileName)
	{
		if(currentSong != null) {
			currentSong.close();
		}
		
		currentSong = new Song(fileName);
	}
	
	public static void loadAudio(String fileName, boolean ignoreFinishAction)
	{
		if(currentSong != null) {
			if(ignoreFinishAction)
				currentSong.sourceLine.removeLineListener(currentSong.sourceLineListener);
				
			currentSong.close();
		}
		
		currentSong = new Song(fileName);
	}
	
	public static void playAudio(Runnable finishAction)
	{
		
		currentSong.start(finishAction);
	}
	
	public static void resumeAudio() {
		if(currentSong != null) {
			currentSong.resume();
		}
	}
	
	public static void fastForward() {
		if(currentSong != null) {
			currentSong.fastForward();
		}
	}
	
	public static void rewind() {
		if(currentSong != null) {
			currentSong.rewind();
		}
	}
	
	public static int getFrame() {
		if(currentSong != null) {
			return currentSong.getFrame();
		} else {
			return -1;
		}
	}
	
	public static void setFrame(int frame) {
		if(currentSong != null) {
			currentSong.setFrame(frame);
		}
	}
}
