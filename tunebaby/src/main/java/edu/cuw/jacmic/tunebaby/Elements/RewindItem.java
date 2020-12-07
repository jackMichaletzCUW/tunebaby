package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;
import edu.cuw.jacmic.tunebaby.Sound.Jukebox;

public class RewindItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public RewindItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.REWIND);
		icons[1] = parent.getPressedIcon(parent.REWIND);
		
		int[] dimensions = parent.getTagDimensions(parent.REWIND);
		
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
				//Audio.rewind();
				Jukebox.skipBack();
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.REWIND);
		icons[1] = parent.getPressedIcon(parent.REWIND);
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
