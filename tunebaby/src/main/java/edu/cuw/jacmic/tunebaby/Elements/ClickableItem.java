package edu.cuw.jacmic.tunebaby.Elements;

import javax.swing.ImageIcon;
import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public interface ClickableItem {

	public ItemClickHandler getClickHandler();
	public ImageIcon getCurrentImage();
	public void updateIcons(ImageMap parent);
}
