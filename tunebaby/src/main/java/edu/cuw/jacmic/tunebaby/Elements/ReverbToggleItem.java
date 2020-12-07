package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;

public class ReverbToggleItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public ReverbToggleItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.REVERB);
		icons[1] = parent.getPressedIcon(parent.REVERB);
		
		int[] dimensions = parent.getTagDimensions(parent.REVERB);
		
		iconOffsetX = dimensions[0];
		iconOffsetY = dimensions[1];
		
		clickHandler = new ItemClickHandler() {
			
			@Override
			public void reset() {
				//iconIndex = 0;
			}
			
			@Override
			public void onClicked() {
				Settings.reverbOn = !Settings.reverbOn;
				//iconIndex = 1;
			}
			
			@Override
			public void onActivated() {
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.REVERB);
		icons[1] = parent.getPressedIcon(parent.REVERB);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return Settings.reverbOn ? icons[1] : icons[0];
	}
}
