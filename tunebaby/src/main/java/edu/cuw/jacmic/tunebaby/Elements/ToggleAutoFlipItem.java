package edu.cuw.jacmic.tunebaby.Elements;

import java.io.IOException;

import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Main;
import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Graphics.JIF;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public class ToggleAutoFlipItem  implements ClickableItem {

	private ItemClickHandler clickHandler;
	private ImageIcon[] icons = new ImageIcon[2];
	public int iconOffsetX = 0;
	public int iconOffsetY = 0;
	private int iconIndex = 0;
	
	public ToggleAutoFlipItem(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.TOGGLE_AUTO_FLIP);
		icons[1] = parent.getPressedIcon(parent.TOGGLE_AUTO_FLIP);
		
		int[] dimensions = parent.getTagDimensions(parent.TOGGLE_AUTO_FLIP);
		
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
				
				Animation.cycle = !Animation.cycle;
				
				if(Animation.cycle) {
					Runnable task = () -> {
						Animation.loading = true;
						Animation.timeOfFrameSwitch = 0;
						Main.forceRefresh();
						try {
							int animationID = (int)(Math.random() * Core.animationFiles.length);
							Animation.loadNew(new JIF(Core.animationFiles[Core.animationFileMap[animationID]].getCanonicalPath()), animationID);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					};
					Thread loadThread = new Thread(task);
					loadThread.start();
				}
			}
			
			@Override
			public void onActivated() {
				//System.out.println("cycling") ;
				
			}
		};
	}
	
	public void updateIcons(ImageMap parent) {
		icons[0] = parent.getNormalIcon(parent.TOGGLE_AUTO_FLIP);
		icons[1] = parent.getPressedIcon(parent.TOGGLE_AUTO_FLIP);
	}
	
	@Override
	public ItemClickHandler getClickHandler() {
		return clickHandler;
	}

	@Override
	public ImageIcon getCurrentImage() {
		return Animation.cycle ? icons[1] : icons[0];//icons[iconIndex];
	}
	
}
