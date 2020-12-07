package edu.cuw.jacmic.tunebaby.Interface;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

import javax.swing.event.MouseInputListener;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Elements.ItemClickHandler;
import edu.cuw.jacmic.tunebaby.Graphics.Tooltip;
import edu.cuw.jacmic.tunebaby.Sound.Jukebox;
import edu.cuw.jacmic.tunebaby.YouTube.Downloader;

public class MouseHandler implements MouseInputListener, MouseMotionListener {

	private int activeKey = 0;
	
	private ImageMap map;
	
	private MouseAction dragAction;
	private MouseAction clickAction;
	
	private boolean isClicked = false;
	
	public MouseHandler(ImageMap map, MouseAction dragAction, MouseAction clickAction) {
		this.dragAction = dragAction;
		this.clickAction = clickAction;
		this.map = map;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		ItemClickHandler handler = map.getItemByKey(map.getKey(e.getX(), e.getY()));
		if(handler != null) {
			handler.onActivated();
		}
		//System.out.printf("clicked%x\n", activeKey);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!isClicked) {
			activeKey = map.getKey(e.getX(), e.getY());
			ItemClickHandler handler = map.getItemByKey(activeKey);
			if(handler != null) {
				handler.onClicked();
			}
			
			clickAction.exec(e.getX(), e.getY());
			//System.out.printf("pressed%x\n", activeKey);
		}		
		isClicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ItemClickHandler handler = map.getItemByKey(activeKey);
		if(handler != null) {
			handler.reset();
		}
		
		isClicked = false;
		//activeKey = 0;
		//System.out.printf("released%x\n", activeKey);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//activeKey = 0;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.printf("dragged%x\n", activeKey);
		
		if(activeKey == map.DRAGGABLE) {
			dragAction.exec(e.getLocationOnScreen().x, e.getLocationOnScreen().y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Core.lastX = e.getX();
		Core.lastY = e.getY();
		
		int key = map.getKey(e.getX(), e.getY());
		
		if(key == map.FAST_FORWARD) {
			if(Downloader.videoTitleLoadedYet(Jukebox.getNext())) {
				Tooltip.setTooltip(Downloader.getVideoTitle(Jukebox.getNext()));
			} else {
				Tooltip.setTooltip("loading title...");
				Downloader.getVideoTitleAsync(Jukebox.getNext(), new Consumer<String>() {
					@Override
					public void accept(String t) {
						Tooltip.setTooltip(t);
					}
				});	
			}
		} else if(key == map.REWIND) {
			if(Downloader.videoTitleLoadedYet(Jukebox.getPrevious())) {
				Tooltip.setTooltip(Downloader.getVideoTitle(Jukebox.getPrevious()));
			} else {
				Tooltip.setTooltip("loading title...");
				Downloader.getVideoTitleAsync(Jukebox.getPrevious(), new Consumer<String>() {
					@Override
					public void accept(String t) {
						Tooltip.setTooltip(t);
					}
				});	
			}
		} 
		else if(key == map.DOWNLOAD) {
			String clipboard = Core.getClipboardContents();
			
			if(Downloader.isValidAddress(clipboard) && !Jukebox.getCurrent().equals(clipboard)) {
				/*Runnable task = () -> {
					Tooltip.setTooltip("play \"" + Downloader.getVideoTitle(clipboard) + "\"");
				};
				Thread thread = new Thread(task);
				thread.run();*/
				if(Downloader.videoTitleLoadedYet(clipboard)) {
					Tooltip.setTooltip(Downloader.getVideoTitle(clipboard));
				} else {
					Tooltip.setTooltip("loading title...");
					Downloader.getVideoTitleAsync(clipboard, new Consumer<String>() {
						@Override
						public void accept(String t) {
							Tooltip.setTooltip(t);
						}
					});
				}
				//Tooltip.setTooltip("play \"" + Downloader.getVideoTitle(clipboard) + "\"");
			} else {
				Tooltip.setTooltip("load playlist from file...");
			}
		} else {
			Tooltip.setTooltip("");
		}
		
		if(activeKey == key) {
			ItemClickHandler handler = map.getItemByKey(key);
			if(handler != null) {
				//handler.onClicked();
			}
		} else {
			ItemClickHandler handler = map.getItemByKey(key);
			if(handler != null) {
				//handler.reset();
			}
		}
	}

	
	
}
