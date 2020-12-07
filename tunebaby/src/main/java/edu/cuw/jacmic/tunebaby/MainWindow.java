package edu.cuw.jacmic.tunebaby;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import edu.cuw.jacmic.tunebaby.Elements.*;
import edu.cuw.jacmic.tunebaby.Graphics.*;
import edu.cuw.jacmic.tunebaby.Interface.*;

public class MainWindow extends JFrame {

	int clickedX = 0;
	int clickedY = 0;
	
	public MainPanel panel;
	
	PlayPauseItem playPause;
	FastForwardItem fastForward;
	RewindItem rewind;
	MuffleToggleItem muffleToggle;
	ReverbToggleItem reverbToggle;
	SlowToggleItem slowToggle;
	DownloadItem download;
	MinimizeItem minimize;
	CloseItem close;
	ToggleAutoFlipItem toggleAutoFlip;
	ChangeChannelUpItem changeChannelUp;
	ChangeChannelDownItem changeChannelDown;
	//SwitchInterfaceItem switchInterface;
	//OpenPlaylistItem openPlaylist;
	
	public MainWindow()
	{
		this.setSize(792, 600);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));

		ImageMap map = new ImageMap(getClass().getResource("/imagemap.png"));
		
		playPause = new PlayPauseItem(map);
		fastForward = new FastForwardItem(map);
		rewind = new RewindItem(map);
		muffleToggle = new MuffleToggleItem(map);
		reverbToggle = new ReverbToggleItem(map);
		slowToggle = new SlowToggleItem(map);
		download = new DownloadItem(map);
		minimize = new MinimizeItem(map, this);
		close = new CloseItem(map);
		toggleAutoFlip = new ToggleAutoFlipItem(map);
		changeChannelUp = new ChangeChannelUpItem(map);
		changeChannelDown = new ChangeChannelDownItem(map);
		//switchInterface = new SwitchInterfaceItem(map);
				
		map.addMapItem(ImageMap.PLAY_PAUSE, playPause);
		map.addMapItem(ImageMap.FAST_FORWARD, fastForward);
		map.addMapItem(ImageMap.REWIND, rewind);
		map.addMapItem(ImageMap.MUFFLE, muffleToggle);
		map.addMapItem(ImageMap.REVERB, reverbToggle);
		map.addMapItem(ImageMap.SLOW, slowToggle);
		map.addMapItem(ImageMap.CHANGE_CHANNEL_UP, changeChannelUp);
		map.addMapItem(ImageMap.CHANGE_CHANNEL_DOWN, changeChannelDown);
		map.addMapItem(ImageMap.CLOSE_WINDOW, close);
		map.addMapItem(ImageMap.DOWNLOAD, download);
		map.addMapItem(ImageMap.TOGGLE_AUTO_FLIP, toggleAutoFlip);
		map.addMapItem(ImageMap.MINIMIZE, minimize);
		//map.addMapItem(ImageMap.SWITCH_INTERFACE, switchInterface);		
		
		MouseHandler mouseListener = new MouseHandler(map,
			(int x, int y) -> { // drag
				this.setLocation(x - clickedX, y - clickedY);
			},
			(int x, int y) -> { // press down
				
				this.clickedX = x;
				this.clickedY = y;
			}
		);
		
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		
		Animation.initializeStaticFrame(new JIF(Settings.get("static-file")));

		panel = new MainPanel(Settings.get("instructions-file"), this, map);
		
		this.add(panel);
		
		
		Timer timer  = new Timer(25, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.repaint();
			}
		});
		timer.start();
				
		this.setLocationRelativeTo(null);
		
		this.setVisible(true);
	}
	
}
