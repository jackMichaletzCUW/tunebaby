package edu.cuw.jacmic.tunebaby.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class Tooltip {
	
	private static final int displayHeight = 35;
	private static final int padding = 8;  
	private static String currentTooltip = "";
	
	private static String lastTooltip = "";
	private static BufferedImage last;
	
	public static void setTooltip(String contents) {
		currentTooltip = contents;
	}
	
	public static String getTooltip() {
		return currentTooltip;
	}
	
	public static void clearToolTip() {
		currentTooltip = "";
	}
	
	public static BufferedImage generateTooltip() {
		if(currentTooltip.equals(lastTooltip) && last != null) {
			return last;
		} else {	
			BufferedImage image = new BufferedImage(100, displayHeight, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D graphics = image.createGraphics();
			graphics.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
				    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setRenderingHint(
				    RenderingHints.KEY_RENDERING,
				    RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setFont(new Font("Terminus (TTF)", Font.PLAIN, displayHeight - (padding * 2)));
			
			int displayWidth = graphics.getFontMetrics().stringWidth(currentTooltip) + (padding * 3);
			
			image = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_4BYTE_ABGR);
			graphics = image.createGraphics();
			graphics.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
				    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setRenderingHint(
				    RenderingHints.KEY_RENDERING,
				    RenderingHints.VALUE_RENDER_QUALITY);
			
	        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        
			//graphics.setFont(new Font("Terminus (TTF)", Font.PLAIN, displayHeight - (padding * 2)));
			graphics.setFont(FontManager.getFont(displayHeight - (padding * 2)));	
			graphics.setColor(new Color(0, 0, 0, 190));
			//graphics.fillRect(0, 0, displayWidth, displayHeight);
			graphics.fillRoundRect(0, 0, displayWidth, displayHeight, 20, 20);
			
			graphics.setColor(new Color(0x4af687));
			
			graphics.drawString(currentTooltip, (int)(padding * 1.5), displayHeight - (int)(padding * 1.5));
					
			graphics.dispose();
			
			for(int y = 0; y < displayHeight; y++) {
				for(int x = 0; x < displayWidth; x++) {
					if((image.getRGB(x, y) & 0xFFFFFF) != 0x000000) {
						int colorToMix = (y % 2 == 0 ? 0x000000 : 0xFFFFFF);
						image.setRGB(x, y, 0xFF000000 | Filter.mixColors(image.getRGB(x, y), colorToMix, 0.8));
					}
				}
			}
			
			Filter.shift(image, 'r', -3);	
			
			lastTooltip = currentTooltip;
			last = image;
			
			return image;
		}
	}
	
}