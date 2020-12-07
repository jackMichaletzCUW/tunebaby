package edu.cuw.jacmic.tunebaby.Interface;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Elements.ClickableItem;
import edu.cuw.jacmic.tunebaby.Elements.ItemClickHandler;

public class ImageMap {

	public static final int DRAGGABLE           = 0xff00ff;
	public static final int PLAY_PAUSE          = 0xa00000;
	public static final int REWIND              = 0x0000ff;
	public static final int FAST_FORWARD        = 0xa000ff;
	public static final int REVERB              = 0x0000a0;
	public static final int SLOW                = 0x00ff00;
	public static final int MUFFLE              = 0xffff00;
	public static final int DOWNLOAD            = 0xe0e0e0;
	public static final int CHANGE_CHANNEL_UP   = 0xe0e0ff;
	public static final int CHANGE_CHANNEL_DOWN = 0xffe0e0;
	public static final int TOGGLE_AUTO_FLIP    = 0x404040;
	public static final int CLOSE_WINDOW        = 0x40FF40;
	public static final int MINIMIZE            = 0xFF4040;
	public static final int NULLSPACE           = 0x000000;
	public static final int OPEN_PLAYLIST       = 0xaababe;
	public static final int SWITCH_INTERFACE    = 0xbdcddd;
	
	public static final int SCAN_LIGHT          = 0xcfbfdf;
	public static final int REVERB_LIGHT        = 0x1a1a1a;
	public static final int SLOW_LIGHT          = 0x8a8a8a;
	public static final int MUFFLE_LIGHT        = 0xeaeaea;
	
	private BufferedImage imageMap;
	private BufferedImage normalImage;
	private BufferedImage pressedImage;
	
	private Map<Integer, int[]> dimensions = new HashMap<Integer, int[]>();
	
	public Map<Integer, ClickableItem> items = new HashMap<Integer, ClickableItem>();
	
	public ImageIcon scanLightImage;
	public ImageIcon reverbLightImage;
	public ImageIcon slowLightImage;
	public ImageIcon muffleLightImage;
	
	public ImageMap(URL imageMapFileLocation) {
		try {
			imageMap = ImageIO.read(imageMapFileLocation);
			normalImage = ImageIO.read(getClass().getResource(Core.getInterfacePath()));
			pressedImage = ImageIO.read(getClass().getResource(Core.getPressedPath()));
			
			scanLightImage = getPressedIcon(SCAN_LIGHT);
			reverbLightImage = getPressedIcon(REVERB_LIGHT);
			slowLightImage = getPressedIcon(SLOW_LIGHT);
			muffleLightImage = getPressedIcon(MUFFLE_LIGHT);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshIcons() {
		try {
			normalImage = ImageIO.read(getClass().getResource(Core.getInterfacePath()));
			pressedImage = ImageIO.read(getClass().getResource(Core.getPressedPath()));
			
			Runnable r = () -> {
				items.forEach((key, value) -> {
					value.updateIcons(this); 
				});
			};
			
			Thread t = new Thread(r);
			t.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int[] getTagDimensions(int tag) {
		if(dimensions.containsKey(tag)) {
			return dimensions.get(tag);
		} else {
			// 0: upper left corner x
			// 1: upper left corner y
			// 2: lower right corner x
			// 3: lower right corner y
			int[] tagDimensions = new int[4];
			
			int lastEncounteredX = -1;
			int lastEncounteredY = -1;
			
			int color = (tag | 0xFF000000);
			
			for(int x = 0; x < imageMap.getWidth(); x++) {
				for(int y = 0; y < imageMap.getHeight(); y++) {
					if(imageMap.getRGB(x, y) == color) {
						// set left / up if they haven't been yet
						if(lastEncounteredX == -1) {
							tagDimensions[0] = x;
						}
						
						if(lastEncounteredY == -1 || y < tagDimensions[1]) {
							tagDimensions[1] = y;
						}
						
						lastEncounteredX = x;
						
						if(y > lastEncounteredY)
							lastEncounteredY = y;
					}
				}
			}
			
			tagDimensions[2] = lastEncounteredX;
			tagDimensions[3] = lastEncounteredY;
			
			dimensions.put(tag, tagDimensions);
			
			return tagDimensions;
		}
	}
	
	public ImageIcon getNormalIcon(int tag) {
		int[] dimensions = getTagDimensions(tag);
		
		//System.out.printf("TAG: %x\nLEFT: %d\nUP: %d\nRIGHT: %d\nDOWN: %d\n\n", tag, dimensions[0], dimensions[1], dimensions[2], dimensions[3]);
		
		int width = dimensions[2] - dimensions[0];
		int height = dimensions[3] - dimensions[1];
		
		BufferedImage normalIcon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); 
		int color = (tag | 0xFF000000);
		
		for(int x = dimensions[0]; x < dimensions[2]; x++) {
			for(int y = dimensions[1]; y < dimensions[3]; y++) {
				if(imageMap.getRGB(x, y) == color) {
					normalIcon.setRGB(x - dimensions[0], y - dimensions[1], normalImage.getRGB(x, y));
				} else
				{
					normalIcon.setRGB(x - dimensions[0], y - dimensions[1], 0x00000000);
				}
			}
		}
		
		return new ImageIcon(normalIcon);
	}
	
	public ImageIcon getPressedIcon(int tag) {
		int[] dimensions = getTagDimensions(tag);
		
		int width = dimensions[2] - dimensions[0];
		int height = dimensions[3] - dimensions[1];
		
		BufferedImage pressedIcon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); 
		int color = (tag | 0xFF000000);
		
		for(int x = dimensions[0]; x < dimensions[2]; x++) {
			for(int y = dimensions[1]; y < dimensions[3]; y++) {
				if(imageMap.getRGB(x, y) == color) {
					pressedIcon.setRGB(x - dimensions[0], y - dimensions[1], pressedImage.getRGB(x, y));
				} else
				{
					pressedIcon.setRGB(x - dimensions[0], y - dimensions[1], 0x00000000);
				}
			}
		}
		
		return new ImageIcon(pressedIcon);
	}
	
	public void addMapItem(int key, ClickableItem value) {
		items.put(key, value);
	}
	
	public int getKey(int x, int y) {
		if(x < imageMap.getWidth() && y < imageMap.getHeight())
			return 0xffffff & imageMap.getRGB(x, y);
		else
			return -1;
	}
	
	public ItemClickHandler getItemByKey(int key) {
		if(items.containsKey(key)) {
			return items.get(key).getClickHandler();
		} else {
			return null;
		}
	}
	
	public ItemClickHandler getItemAtCoordinates(int x, int y) {
		int key = 0xffffff & imageMap.getRGB(x, y);
		
		if(items.containsKey(key)) {
			return items.get(key).getClickHandler();
		} else {
			return null;
		}
	}
	
}