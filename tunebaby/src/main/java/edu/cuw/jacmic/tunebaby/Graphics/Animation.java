package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Main;

public class Animation {

	private static int currentAnimationID = 0;
	public static AnimationFrame currentFrame;
	private static AnimationFrame staticFrame;
	public static long timeOfFrameSwitch;
	public static boolean loading;
	
	static int numberOfFrames = 0;
	public static boolean cycle = false;
	
	public static void initialize(JIF starter) {
		currentFrame = new AnimationFrame(starter.frames[0], false, starter.averageDelayTime*10);
		
		for(int i = 1; i < starter.frames.length; i++) {
			AnimationFrame temporary = new AnimationFrame(starter.frames[i], false, starter.averageDelayTime*10);
			
			currentFrame.nextFrame = temporary;
			temporary.previousFrame = currentFrame;
			
			currentFrame = temporary;
		}
		
		AnimationFrame lastFrame = currentFrame;
		
		while(currentFrame.previousFrame != null) {
			currentFrame = currentFrame.previousFrame;
		}
		
		lastFrame.nextFrame = currentFrame;
		
		currentFrame.previousFrame = lastFrame;
	}
	
	public static void initializeStaticFrame(JIF frame) {
		staticFrame = new AnimationFrame(frame.frames[0], false, frame.averageDelayTime * 10);
		
		for(int i = 1; i < frame.frames.length; i++) {
			AnimationFrame temporary = new AnimationFrame(frame.frames[i], false, frame.averageDelayTime*10);
			
			staticFrame.nextFrame = temporary;
			temporary.previousFrame = staticFrame;
			
			staticFrame = temporary;
		}
		
		AnimationFrame lastFrame = staticFrame;
		
		while(staticFrame.previousFrame != null) {
			staticFrame = staticFrame.previousFrame;
		}
		
		lastFrame.nextFrame = staticFrame;
		
		staticFrame.previousFrame = lastFrame;
	}
	
	public static void freeAnimation(AnimationFrame target) {
		AnimationFrame next = target.nextFrame;
		target.nextFrame = null;
		target.previousFrame = null;
		
		if(next != null) {
			freeAnimation(next);
		}
	}
	
	public static void flipForward() {
		Runnable task = () -> {
			if(Animation.currentAnimationID + 1 < Core.animationFiles.length) {
				Animation.currentAnimationID++;
			} else {
				Animation.currentAnimationID = 0;
			}
			
			Animation.loading = true;
			Animation.timeOfFrameSwitch = 0;
			Main.forceRefresh();
			try {
				Animation.loadNew(new JIF(Core.animationFiles[Core.animationFileMap[Animation.currentAnimationID]].getCanonicalPath()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		};
		Thread loadThread = new Thread(task);
		loadThread.start();
	}
	
	public static void flipBackward() {
		Runnable task = () -> {
			if(Animation.currentAnimationID - 1 >= 0) {
				Animation.currentAnimationID--;
			} else {
				Animation.currentAnimationID = Core.animationFiles.length - 1;
			}
			
			Animation.loading = true;
			Animation.timeOfFrameSwitch = 0;
			Main.forceRefresh();
			try {
				Animation.loadNew(new JIF(Core.animationFiles[Core.animationFileMap[Animation.currentAnimationID]].getCanonicalPath()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		};
		Thread loadThread = new Thread(task);
		loadThread.start();
	}
	
	public static void loadNew(JIF replacement, int currentAnimationID) {
		loadNew(replacement);
		Animation.currentAnimationID = currentAnimationID;
	}
	
	public static void loadNew(JIF replacement) {
		loading = true;
		timeOfFrameSwitch = 0;
		
		double splitAmount = Math.random();
		
		//System.out.printf("%d, %d\n", replacement.averageDelayTime, replacement.imageFrames[0].getDelay());
		int delay = (replacement.averageDelayTime < 1 ? 10 : (replacement.averageDelayTime > 19 ? 19 : replacement.averageDelayTime)) * 10;
		
		AnimationFrame[] splitFrames = new AnimationFrame[1 + (int)(Math.random() * 4)];
		
		for(int i = 0; i < splitFrames.length; i++) {
			splitFrames[i] = new AnimationFrame(Filter.splitFrame(replacement.frames[0], (splitFrames.length - i) * (splitAmount / (splitFrames.length + 1))), true, 10);
			if(i != 0) {
				splitFrames[i].previousFrame = splitFrames[i - 1];
				splitFrames[i].previousFrame.nextFrame = splitFrames[i];
			}
		}
		
		
		//AnimationFrame replacementFrame = new AnimationFrame(Filter.splitFrame(replacement.frames[0], splitAmount), true, delay);
		//AnimationFrame splitFrame = new AnimationFrame(Filter.splitFrame(replacement.frames[0], splitAmount / 2), true, delay);

		AnimationFrame next = new AnimationFrame(replacement.frames[0], false, delay);
		//replacementFrame.nextFrame = splitFrame;
		//splitFrame.nextFrame = next;
		
		splitFrames[splitFrames.length - 1].nextFrame = next;
		next.previousFrame = splitFrames[splitFrames.length - 1];
		
		for(int i = 1; i < replacement.frames.length; i++) {
			AnimationFrame temporary = new AnimationFrame(replacement.frames[i], false, delay);
			
			next.nextFrame = temporary;
			temporary.previousFrame = next;
			
			next = temporary;
		}
		
		if(cycle) {
			numberOfFrames = (4000 + ((int)(Math.random() * 6000))) / delay;
			//System.out.printf("\tnumber of frames: %d\n", numberOfFrames);
		}
		
		//next.nextFrame = replacementFrame;
		//replacementFrame.previousFrame = next;
		next.nextFrame = splitFrames[0];
		splitFrames[0].previousFrame = next;
		next = currentFrame;
		currentFrame = splitFrames[0];
		//currentFrame = replacementFrame;
		
		freeAnimation(next);
		System.gc();
		timeOfFrameSwitch = 0;
		loading = false;
	}
	
	public static BufferedImage getFrame() {
		if(!loading) {
			if(System.currentTimeMillis() - timeOfFrameSwitch > currentFrame.frameTime) {
				if(currentFrame.playOnce) {
					currentFrame.previousFrame.nextFrame = currentFrame.nextFrame;
					currentFrame.nextFrame.previousFrame = currentFrame.previousFrame;
				}
				
				currentFrame = currentFrame.nextFrame;
				
				timeOfFrameSwitch = System.currentTimeMillis();
				
				
				if(cycle) {
					numberOfFrames--;
					
					if(numberOfFrames <= 0) {
						
							Runnable task = () -> {
								try {
									int index = (int)(Math.random() * Core.animationFiles.length);
									//System.out.printf("loading %s...\n", Core.animationFiles[index].getName());
									loading = true;
									timeOfFrameSwitch = 0;
									Main.forceRefresh();
									loadNew(new JIF(Core.animationFiles[Core.animationFileMap[index]].getCanonicalPath()), index);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							};
							Thread t = new Thread(task);
							t.start();

					}
				}
			}
			
			return currentFrame.frame;
		} else {
			if(System.currentTimeMillis() - timeOfFrameSwitch > staticFrame.frameTime) {
				staticFrame = staticFrame.nextFrame;
				
				timeOfFrameSwitch = System.currentTimeMillis();
			}
			
			return staticFrame.frame;
		}
	}
	
}
