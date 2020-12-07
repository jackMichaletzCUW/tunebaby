package edu.cuw.jacmic.tunebaby.Elements;

import java.io.IOException;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Main;
import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Graphics.JIF;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;

public class ChangeChannelDownItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public ChangeChannelDownItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.CHANGE_CHANNEL_DOWN);
		icons[1] = parent.getPressedIcon(parent.CHANGE_CHANNEL_DOWN);
		
		int[] dimensions = parent.getTagDimensions(parent.CHANGE_CHANNEL_DOWN);
		
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
				Animation.flipBackward();
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.CHANGE_CHANNEL_DOWN);
		icons[1] = parent.getPressedIcon(parent.CHANGE_CHANNEL_DOWN);
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
