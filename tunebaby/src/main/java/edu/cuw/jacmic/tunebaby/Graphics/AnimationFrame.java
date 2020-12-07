package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.image.BufferedImage;

public class AnimationFrame {

	public BufferedImage frame;
	public AnimationFrame nextFrame;
	public AnimationFrame previousFrame;
	public int frameTime;
	public boolean playOnce;
	
	public AnimationFrame(BufferedImage frame, boolean playOnce, int frameTime) {
		this.frame = frame;
		this.playOnce = playOnce;
		this.frameTime = frameTime;
	}
	
}
