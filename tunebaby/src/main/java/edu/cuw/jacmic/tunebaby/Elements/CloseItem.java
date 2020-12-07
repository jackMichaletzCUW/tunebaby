package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public class CloseItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public CloseItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.CLOSE_WINDOW);
		icons[1] = parent.getPressedIcon(parent.CLOSE_WINDOW);
		
		int[] dimensions = parent.getTagDimensions(parent.CLOSE_WINDOW);
		
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
				System.exit(0);
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.CLOSE_WINDOW);
		icons[1] = parent.getPressedIcon(parent.CLOSE_WINDOW);
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
