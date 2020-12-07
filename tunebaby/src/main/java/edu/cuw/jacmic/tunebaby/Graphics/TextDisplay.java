package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class TextDisplay {

	private static final int displayHeight = 35;
	private static final int displayWidth = 302;
	private static final int padding = 8;  
	
	private static String lastString = "asdf982p35gk";
	private static BufferedImage lastBufferedImage;
	
	private static int refreshRequestsSinceLastChange = -200;
	private static int textPosition = 0;
	
	private static int maxStringLength = 1000;
	
	public static BufferedImage getBitmap(String string) {
		if(string.equals(lastString)) {
			if(string.length() > maxStringLength && ++refreshRequestsSinceLastChange > 20) {
				textPosition = (textPosition + 1) % ((string.length() - maxStringLength) + 1);
				refreshRequestsSinceLastChange = (textPosition == 0 ? -200 : 0);
			}
			else {
				return lastBufferedImage;
			}
		} else {
			refreshRequestsSinceLastChange = 0;
			textPosition = 0;
		}

		lastString = string;
				
		BufferedImage image = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(
			    RenderingHints.KEY_RENDERING,
			    RenderingHints.VALUE_RENDER_QUALITY);
				
		//graphics.setColor(Color.BLACK);
		//graphics.fillRect(0, 0, displayWidth, displayHeight);
				
		graphics.setColor(new Color(0x4af687));
		//graphics.setFont(new Font("Terminus (TTF)", Font.PLAIN, displayHeight - (padding * 2)));
		graphics.setFont(FontManager.getFont(displayHeight - (padding * 2)));	
		
		int letterWidth = graphics.getFontMetrics().stringWidth("p");
		maxStringLength = ((displayWidth - (padding * 2)) / letterWidth) - 2;
		
		if(string.length() > maxStringLength)
		{
			string = string.substring(textPosition, textPosition + maxStringLength);
		}
		
		graphics.drawString(string, padding * 3, displayHeight - (padding));
				
		graphics.dispose();
		
		for(int y = 0; y < displayHeight; y++) {
			for(int x = 0; x < displayWidth; x++) {
				if(image.getRGB(x, y) != 0xFF000000) {
					int colorToMix = (y % 2 == 0 ? 0x000000 : 0xFFFFFF);
					image.setRGB(x, y, Filter.mixColors(image.getRGB(x, y), colorToMix, 0.8));
				}
			}
		}
		
		Filter.shift(image, 'r', -3);
		
		lastBufferedImage = image;
		
		return image;
	}
	
}