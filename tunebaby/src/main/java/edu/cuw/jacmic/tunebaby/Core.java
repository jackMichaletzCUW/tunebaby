package edu.cuw.jacmic.tunebaby;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Random;

import edu.cuw.jacmic.tunebaby.Interface.ImageMap;

public class Core {

	public static File[] animationFiles;
	public static int[] animationFileMap;
	public static String videoTitle = "";
	
	private static int interfaceIndex = 0;
	
	private static String[] interfacePNGs = {
		"/interfacetest.png",
		"/imagemap.png",
		"/interfacepressed.png"
	};
	
	private static String[] pressedPNGs = {
		"/interfacepressed.png",
		"/interfacetest.png",
		"/imagemap.png"
	};
	
	public static int lastX;
	public static int lastY;
	
	public static String getInterfacePath() {
		return interfacePNGs[interfaceIndex];
	}
	
	public static String getPressedPath() {
		return pressedPNGs[interfaceIndex];
	}
	
	public static void cycleInterface(ImageMap map) {
		if(interfaceIndex + 1 > interfacePNGs.length - 1) {
			interfaceIndex = 0;
		} else {
			interfaceIndex++;
		}
		
		map.refreshIcons();
	}
	
	public static String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasStringText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasStringText) {
		    try {
		        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
		    } catch (UnsupportedFlavorException | IOException ex) {
		        System.out.println(ex); ex.printStackTrace();
		    }
		}
		return result;
	}
	
	public static File getResourceAsFile(String name) throws URISyntaxException, IOException {	
		return Paths.get(Core.class.getClass().getResource(name).toURI()).toFile();
	}
	
	public static void generateAnimationFileMap() {
		int length = animationFiles.length;
		
		animationFileMap = new int[length];
		Random r = new Random();
		
		for(int i = 0; i < length; i++) {
			boolean usedBefore = false;
			
			do {
				usedBefore = false;
				animationFileMap[i] = r.nextInt(length);
				
				for(int j = 0; j < i; j++) {
					if(animationFileMap[j] == animationFileMap[i]) {
						usedBefore = true;
					}
				}
			} while(usedBefore);
			
			//System.out.printf("%03d: %03d\n", i, map[i]);
		}
	}
	
	public static String clipboard = "";
	
	public static boolean updateClipboard() {
		String temporary = getClipboardContents();
		
		if(temporary.equals(clipboard)) {
			return false;
		} else {
			clipboard = getClipboardContents();
			return true;
		}
	}
}