package edu.cuw.jacmic.tunebaby;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.cuw.jacmic.tunebaby.Comments.CommentReader;
import edu.cuw.jacmic.tunebaby.Comments.Track;
import edu.cuw.jacmic.tunebaby.Graphics.Tooltip;
import edu.cuw.jacmic.tunebaby.Sound.Audio;
import edu.cuw.jacmic.tunebaby.Sound.Jukebox;
import edu.cuw.jacmic.tunebaby.Sound.WaveFile;
import edu.cuw.jacmic.tunebaby.Sound.WaveWriter;
import edu.cuw.jacmic.tunebaby.YouTube.Batch;
import edu.cuw.jacmic.tunebaby.YouTube.Downloader;

import java.io.FileFilter;

public class Main {
	static MainWindow mw;
	public static boolean JAR = false;
	
	public static void forceRefresh() {
		mw.panel.repaint();
	}
	
	public static void main(String[] args) {
		Settings.loadSettings(System.getProperty("user.home") + "/Library/Application Support/tunebaby/settings.txt");
		
		System.out.println(Settings.get("ffmpeg-path"));
		
		Settings.soundDirectory = System.getProperty("user.home") + "/Music/TuneBaby";
		
		File interleavesDirectory = new File(Settings.soundDirectory + "/interleaved");
		
		if(!interleavesDirectory.exists()) {
			interleavesDirectory.mkdirs();
		}
		
		Core.videoTitle = "WELCOME TO TUNEBABY ... WELCOME TO TUNEBABY";

		File animationDirectory = new File(Settings.get("gif-directory"));
				
		System.out.println(animationDirectory.exists());
		
		Core.animationFiles = animationDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".gif");
			}
		});
		
		Core.generateAnimationFileMap();
		
		File[] interleaves = (new File(Settings.soundDirectory + "/interleaved")).listFiles();
		
		if(interleaves != null) {
			for(File interleave : interleaves) {
				System.out.printf("DELETING %s FROM INTERLEAVES...\n", interleave.getName());
				interleave.delete();
			}
		}
		
		mw = new MainWindow();
	}
}