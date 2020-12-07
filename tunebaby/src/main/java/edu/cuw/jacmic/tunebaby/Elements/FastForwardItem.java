package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;
import edu.cuw.jacmic.tunebaby.Sound.Jukebox;

public class FastForwardItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public FastForwardItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.FAST_FORWARD);
		icons[1] = parent.getPressedIcon(parent.FAST_FORWARD);
		
		int[] dimensions = parent.getTagDimensions(parent.FAST_FORWARD);
		
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
				//Audio.fastForward();
				Jukebox.skipForward();
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.FAST_FORWARD);
		icons[1] = parent.getPressedIcon(parent.FAST_FORWARD);
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
