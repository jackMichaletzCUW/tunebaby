package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import edu.cuw.jacmic.tunebaby.YouTube.Downloader;

public class DownloadMeter {
	private static final int displayHeight = 45;
	private static final int padding = 8;  
	
	private static int lastProgress = -1;
	private static BufferedImage last;

	public static BufferedImage getDownloadMeter() {
		if(Downloader.progress < 0) {
			return null;
		} else if(lastProgress == Downloader.progress && (last != null)) {
			return last;
		} else {
			BufferedImage image = new BufferedImage(300, 70, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D graphics = image.createGraphics();
			graphics.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
				    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setRenderingHint(
				    RenderingHints.KEY_RENDERING,
				    RenderingHints.VALUE_RENDER_QUALITY);
			
			Font terminus = FontManager.getFont(displayHeight - (padding * 2));	
			
			graphics.setFont(terminus);
				        			
			graphics.setColor(new Color(0, 0, 0, 0));
			graphics.fillRect(0, 0, 300, 70);
			//graphics.fillRoundRect(0, 0, displayWidth, displayHeight, 20, 20);
			
			graphics.setColor(new Color(0x4af687));
			
			graphics.drawString(Downloader.progressString, (int)(padding * 1.5), displayHeight - (int)(padding * 1.5));
			
			graphics.drawRect(padding, displayHeight, 260, padding * 2);
			
			graphics.fillRect(padding, displayHeight, (int)(((double)Downloader.progress / 100.0) * 260.0), padding * 2);
			
			graphics.dispose();
			
			for(int y = 0; y < 70; y++) {
				for(int x = 0; x < 300; x++) {
					if(((image.getRGB(x, y) >> 24) & 0xFF) == 0xFF) {
						int colorToMix = (y % 2 == 0 ? 0x000000 : 0xFFFFFF);
						image.setRGB(x, y, 0xFF000000 | Filter.mixColors(image.getRGB(x, y), colorToMix, 0.8));
					}
				}
			}
			
			//Filter.shift(image, 'r', -3);
			//image = Filter.curveFrame(image, 20, 20, 500);
			
			lastProgress = Downloader.progress;
			last = image;
			
			return image;
		}
	}
	
}
