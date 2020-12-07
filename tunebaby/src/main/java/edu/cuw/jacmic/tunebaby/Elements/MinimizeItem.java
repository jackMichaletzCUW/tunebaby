package edu.cuw.jacmic.tunebaby.Elements;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public class MinimizeItem implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public MinimizeItem(ImageMap parent, JFrame targetWindow) {
		icons[0] = parent.getNormalIcon(parent.MINIMIZE);
		icons[1] = parent.getPressedIcon(parent.MINIMIZE);
		
		int[] dimensions = parent.getTagDimensions(parent.MINIMIZE);
		
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
				targetWindow.setState(Frame.ICONIFIED);
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.MINIMIZE);
		icons[1] = parent.getPressedIcon(parent.MINIMIZE);
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
