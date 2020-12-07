package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;

public class PlayPauseItem implements ClickableItem {
	
	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[4];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public PlayPauseItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.PLAY_PAUSE);
		icons[1] = parent.getPressedIcon(parent.PLAY_PAUSE);
		icons[2] = icons[0];
		icons[3] = icons[1];
		
		int[] dimensions = parent.getTagDimensions(parent.PLAY_PAUSE);
		
		iconOffsetX = dimensions[0];
		iconOffsetY = dimensions[1];
		
		clickHandler = new ItemClickHandler() {
			
			@Override
			public void reset() {
				iconIndex = (iconIndex == 1 ? 0 : (iconIndex == 3 ? 2 : iconIndex));
			}
			
			@Override
			public void onClicked() {
				iconIndex = (iconIndex == 0 ? 1 : (iconIndex == 2 ? 3 : iconIndex));
			}
			
			@Override
			public void onActivated() {
				Audio.resumeAudio();
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.PLAY_PAUSE);
		icons[1] = parent.getPressedIcon(parent.PLAY_PAUSE);
		icons[2] = icons[0];
		icons[3] = icons[1];
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
