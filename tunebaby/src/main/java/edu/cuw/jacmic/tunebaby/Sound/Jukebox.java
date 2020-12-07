package edu.cuw.jacmic.tunebaby.Sound;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Graphics.JIF;
import edu.cuw.jacmic.tunebaby.YouTube.*;

public class Jukebox {

	private static ArrayList<String> history = new ArrayList<String>();
	private static ArrayList<String> future = new ArrayList<String>();
	private static String current = "";
	
	public static String getCurrent() { 
		return current;
	}
	
	public static String getNext() {
		if(future.size() > 0) {
			return future.get(0);
		} else {
			return "";
		}
	}
	
	public static String getPrevious() {
		if(history.size() > 0) {
			return history.get(history.size() - 1);
		} else {
			return "";
		}
	}
	
	public static void addToEndOfHistory(String url) {
		history.add(url);
	}
	
	public static void addToBeginningOfFuture(String url) {
		future.add(0, url);
	}
	
	public static void addToEndOfFuture(String url) {
		future.add(url);
	}
	
	public static void setCurrent(String url) {
		current = url;
	}
	
	public static void skipBack() {
		System.out.println("SKIP BACK REQUESTED!!");
		if(history.size() >= 1) {
			Batch.clearBatch();
			future.add(0, current);
			current = history.remove(history.size() - 1);
			updateBatch();
		}
	}
	
	public static void skipForward() {
		System.out.println("SKIP FORWARD REQUESTED!!");
		if(future.size() >= 1) {
			Batch.clearBatch();
			history.add(current);
			current = future.remove(0);
			updateBatch();
		}
	}
	
	public static void clearQueue() {
		history = new ArrayList<String>();
		future = new ArrayList<String>();
		current = "";
	}
	
	public static void loadURL(String url) {
		clearQueue();
		Runnable task = () -> {
			Jukebox.setCurrent(url);
			Jukebox.updateBatch();
		};
		
		Thread t = new Thread(task);
		t.start();
	}
	
	public static void loadPlaylist(File playlistTextFile) {
		clearQueue();
		Runnable task = () -> {
			try {
				ArrayList<String> links = new ArrayList<String>(Files.readAllLines(Paths.get(playlistTextFile.getAbsolutePath())));
				
				for(int i = 0; i < links.size(); i++) {
					if(!Downloader.isValidAddress(links.get(i))) {
						links.remove(i);
						i--;
					}
				}
				
				if(links.size() > 0) {
					for(int i = 1; i < links.size(); i++) {
						Jukebox.addToEndOfFuture(links.get(i));
					}
					
					Jukebox.setCurrent(links.get(0));
					
					Jukebox.updateBatch();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		Thread t = new Thread(task);
		t.start();
	}
	
	private static void updateBatch() {
		Runnable task = () -> {
			//Batch.clearBatch();
			Batch.pauseExecution();
			
			for(String url : history) {
				//Batch.addToBatch(new DownloadRequest(url, Request.DOWNLOAD), false);
				Batch.addToBatch(new DownloadRequest(url, Request.LOAD_TITLE), false);
			}
			
			for(String url : future) {
				//Batch.addToBatch(new DownloadRequest(url, Request.DOWNLOAD), false);
				Batch.addToBatch(new DownloadRequest(url, Request.LOAD_TITLE), false);
			}
			
			ArrayList<String> keepers = new ArrayList<String>();
			
			keepers.add(Downloader.getVideoID(current));
			
			if(future.size() >= 3) {
				Batch.addToBatch(new DownloadRequest(future.get(2), Request.INTERLEAVE), true);
				keepers.add(Downloader.getVideoID(future.get(2)));
			}
			
			if(history.size() >= 2) {
				Batch.addToBatch(new DownloadRequest(history.get(1), Request.INTERLEAVE), true);
				keepers.add(Downloader.getVideoID(history.get(1)));
			}
			
			if(future.size() >= 2) {
				Batch.addToBatch(new DownloadRequest(future.get(1), Request.INTERLEAVE), true);
				keepers.add(Downloader.getVideoID(future.get(1)));
			}
			
			if(history.size() >= 1) {
				Batch.addToBatch(new DownloadRequest(history.get(0), Request.INTERLEAVE), true);
				keepers.add(Downloader.getVideoID(history.get(0)));
			}
			
			if(future.size() >= 1) {
				Batch.addToBatch(new DownloadRequest(future.get(0), Request.INTERLEAVE), true);
				keepers.add(Downloader.getVideoID(future.get(0)));
			}
			
			
			File[] interleaves = (new File(Settings.soundDirectory + "/interleaved")).listFiles();
			
			for(File interleave : interleaves) {
				System.out.println(interleave.getName().substring(0, interleave.getName().indexOf(".wav")));
				
				for(String keeper : keepers) {
					System.out.printf("KEEPING %s\n", keeper);
				}
				
				if(interleave.getName().charAt(0) != '.' && !keepers.contains(interleave.getName().substring(0, interleave.getName().indexOf(".wav")))) {
					System.out.printf("DELETING %s FROM INTERLEAVES...\n", interleave.getName());
					interleave.delete();
				}
			}
			
			if(Core.videoTitle.equals("WELCOME TO TUNEBABY ... WELCOME TO TUNEBABY")) {
				try {
					int animationID = (int)(Math.random() * Core.animationFiles.length);
					Animation.loadNew(new JIF(Core.animationFiles[Core.animationFileMap[animationID]].getCanonicalPath()), animationID);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			Core.videoTitle = Downloader.getVideoTitle(current);
			
			Batch.addToBatch(new DownloadRequest(current, Request.INTERLEAVE, () -> {
				//if(Audio.currentSong != null)
				//	Audio.currentSong.close();
				
				Audio.loadAudio(Settings.soundDirectory + "/interleaved/" + Downloader.getVideoID(current) + ".wav", true);
				Audio.playAudio(() -> {
					System.out.println("SKIP FORWARD FROM PLAY AUDIO LAMBDA!!");
					Jukebox.skipForward();
				});
			}), true);
			
			Batch.resumeExecution();
		};
		
		Thread t = new Thread(task);
		t.start();
	}
	
}
