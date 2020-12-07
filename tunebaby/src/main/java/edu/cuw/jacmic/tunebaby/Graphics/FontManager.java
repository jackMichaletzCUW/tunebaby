package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;

import edu.cuw.jacmic.tunebaby.Settings;

public class FontManager {

	public static Font getFont(float size) {
		try {
		     Font font = Font.createFont(Font.TRUETYPE_FONT, new File(Settings.get("font-path")));

		     return font.deriveFont(size);

		} catch (Exception e) {
		     e.printStackTrace();
		}
		
		return null;
	}
	
}
