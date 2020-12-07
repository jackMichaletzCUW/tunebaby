package edu.cuw.jacmic.tunebaby.Elements;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;
import edu.cuw.jacmic.tunebaby.Sound.Jukebox;
import edu.cuw.jacmic.tunebaby.YouTube.Batch;
import edu.cuw.jacmic.tunebaby.YouTube.Downloader;

public class DownloadItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public DownloadItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.DOWNLOAD);
		icons[1] = parent.getPressedIcon(parent.DOWNLOAD);
		
		int[] dimensions = parent.getTagDimensions(parent.DOWNLOAD);
		
		iconOffsetX = dimensions[0];
		iconOffsetY = dimensions[1];
		
		clickHandler = new ItemClickHandler() {
			
			@Override
			public void reset() {
				iconIndex = 0;
			}
			
			@Override
			public void onClicked() {
				iconIndex = 1;
			}
			
			@Override
			public void onActivated() {
				boolean newSong = Core.updateClipboard();
				if((newSong) && Downloader.isValidAddress(Core.clipboard)) {
					Downloader.downloadYoutube(Core.clipboard, 
						() -> {
							Jukebox.loadURL(Core.clipboard);
						}, () -> {
							System.out.printf("download %d%%\n", Batch.progress);
					}); 
					Settings.lastConfig[0] = Settings.slowOn;
					Settings.lastConfig[1] = Settings.reverbOn;
					Settings.lastConfig[2] = Settings.muffleOn;
				} else {
					// no youtube clip in clipboard, prompt user for text file playlist or audio file
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(null);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            //Downloader.playPlaylist(file);
			            Jukebox.loadPlaylist(file);
			        }
				}
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.DOWNLOAD);
		icons[1] = parent.getPressedIcon(parent.DOWNLOAD);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return icons[iconIndex];
	}
}
