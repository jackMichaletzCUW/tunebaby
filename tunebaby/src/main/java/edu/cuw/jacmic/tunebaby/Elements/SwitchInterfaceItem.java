package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public class SwitchInterfaceItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	
	public SwitchInterfaceItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.SWITCH_INTERFACE);
		icons[1] = parent.getPressedIcon(parent.SWITCH_INTERFACE);
		
		int[] dimensions = parent.getTagDimensions(parent.SWITCH_INTERFACE);
		
		iconOffsetX = dimensions[0];
		iconOffsetY = dimensions[1];
		
		clickHandler = new ItemClickHandler() {
			
			@Override
			public void reset() {
				//iconIndex = 0;
			}
			
			@Override
			public void onClicked() {
				Core.cycleInterface(parent);
				//iconIndex = 1;
			}
			
			@Override
			public void onActivated() {
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.SWITCH_INTERFACE);
		icons[1] = parent.getPressedIcon(parent.SWITCH_INTERFACE);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return icons[0];//Settings.reverbOn ? icons[1] : icons[0];
	}
}
