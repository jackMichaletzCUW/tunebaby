package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;
import edu.cuw.jacmic.tunebaby.Sound.Audio;

public class MuffleToggleItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public MuffleToggleItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.MUFFLE);
		icons[1] = parent.getPressedIcon(parent.MUFFLE);
		
		int[] dimensions = parent.getTagDimensions(parent.MUFFLE);
		
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
				Settings.muffleOn = !Settings.muffleOn;
			}
			
			@Override
			public void onActivated() {
				//System.out.println("MUFF");
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.MUFFLE);
		icons[1] = parent.getPressedIcon(parent.MUFFLE);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return Settings.muffleOn ? icons[1] : icons[0];
	}
}
