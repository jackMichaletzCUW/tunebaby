package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;

public class SlowToggleItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public SlowToggleItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.SLOW);
		icons[1] = parent.getPressedIcon(parent.SLOW);
		
		int[] dimensions = parent.getTagDimensions(parent.SLOW);
		
		iconOffsetX = dimensions[0];
		iconOffsetY = dimensions[1];
		
		clickHandler = new ItemClickHandler() {
			
			@Override
			public void reset() {
				//iconIndex = 0;
			}
			
			@Override
			public void onClicked() {
				//iconIndex = 1;
				Settings.slowOn = !Settings.slowOn;
			}
			
			@Override
			public void onActivated() {
				//Settings.slowOn = !Settings.slowOn;
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.SLOW);
		icons[1] = parent.getPressedIcon(parent.SLOW);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return Settings.slowOn ? icons[1] : icons[0];
	}
}
