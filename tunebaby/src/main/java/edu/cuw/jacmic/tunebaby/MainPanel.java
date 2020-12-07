package edu.cuw.jacmic.tunebaby;
import javax.swing.*;

import edu.cuw.jacmic.tunebaby.Elements.*;
import edu.cuw.jacmic.tunebaby.Graphics.Animation;
import edu.cuw.jacmic.tunebaby.Graphics.DownloadMeter;
import edu.cuw.jacmic.tunebaby.Graphics.JIF;
import edu.cuw.jacmic.tunebaby.Graphics.TextDisplay;
import edu.cuw.jacmic.tunebaby.Graphics.Tooltip;
import edu.cuw.jacmic.tunebaby.Interface.*;
import edu.cuw.jacmic.tunebaby.YouTube.Downloader;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainPanel extends JPanel {
	
	int clickedX = 0;
	int clickedY = 0;

	ImageMap map;
	
	public MainPanel(String fileNameOfFirstAnimation, JFrame parent, ImageMap map)
	{
		Animation.initialize(new JIF(Settings.get("open-file")));
		
		Runnable r = () -> {
			try {
				Thread.sleep(9750);
				if(Core.videoTitle.equals("WELCOME TO TUNEBABY ... WELCOME TO TUNEBABY"))
					Animation.loadNew(new JIF(fileNameOfFirstAnimation));
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
		Thread t = new Thread(r);
		t.start();
		
		this.map = map;
		
		this.setSize(905, 715);
		this.setBackground(new Color(0, 0, 0, 0));
		this.setOpaque(false);
		this.setDoubleBuffered(true);
		this.setLayout(null);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//ImageIcon textFrame = new ImageIcon(getClass().getResource("/txtframe.png"));
		//textFrame.paintIcon(this, g, 138, 24);
		
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.drawImage(TextDisplay.getBitmap(Core.videoTitle), 160, 44, this);
		
		g2d.drawImage(Animation.getFrame(), 128, 126, this);
		
		ImageIcon background = new ImageIcon(getClass().getResource(Core.getInterfacePath()));
		background.paintIcon(this, g, 0, 0);
		
		BufferedImage downloadMeter = DownloadMeter.getDownloadMeter();
		
		if(downloadMeter != null) {
			g2d.drawImage(downloadMeter, 170, 300, this);
		}
		
		ImageIcon frame = new ImageIcon(getClass().getResource("/frametest.png"));
		frame.paintIcon(this, g, 127, 122);
		
		for(int tag : map.items.keySet()) {
			int[] dimensions = map.getTagDimensions(tag);
			map.items.get(tag).getCurrentImage().paintIcon(this, g, dimensions[0], dimensions[1]);
		}
		
		if(Animation.cycle) {
			// scanning, so draw scan light
			int[] dimensions = map.getTagDimensions(ImageMap.SCAN_LIGHT);
			map.scanLightImage.paintIcon(this, g, dimensions[0], dimensions[1]);
		}
		
		if(Settings.reverbOn) {
			// reverb, so draw reverb light
			int[] dimensions = map.getTagDimensions(ImageMap.REVERB_LIGHT);
			
			map.reverbLightImage.paintIcon(this, g, dimensions[0], dimensions[1]);
		}

		if(Settings.muffleOn) {
			// muffle, so draw muffle light
			int[] dimensions = map.getTagDimensions(ImageMap.MUFFLE_LIGHT);
			map.muffleLightImage.paintIcon(this, g, dimensions[0], dimensions[1]);
		}
		
		if(Settings.slowOn) {
			// slow, so draw slow light
			int[] dimensions = map.getTagDimensions(ImageMap.SLOW_LIGHT);
			map.slowLightImage.paintIcon(this, g, dimensions[0], dimensions[1]);
		}
		
		if(!Tooltip.getTooltip().equals("")) {
			// there is a tooltip
			
			BufferedImage tooltip = Tooltip.generateTooltip();
			
			boolean tooRight = tooltip.getWidth() + Core.lastX > this.getWidth();
			boolean tooHigh = tooltip.getHeight() + Core.lastY > this.getHeight();
			
			int x = tooRight ? Core.lastX - tooltip.getWidth() : Core.lastX;
			int y = tooHigh ? Core.lastY - tooltip.getHeight() : Core.lastY;
			
			g2d.drawImage(tooltip, x, y, this);
		}

		g2d.dispose();
	}
}