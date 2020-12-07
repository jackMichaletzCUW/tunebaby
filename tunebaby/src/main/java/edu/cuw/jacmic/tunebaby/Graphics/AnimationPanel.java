package edu.cuw.jacmic.tunebaby.Graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class AnimationPanel extends JPanel {
	
	public AnimationPanel(String fileNameOfFirstAnimation) {
		Animation.initialize(new JIF(fileNameOfFirstAnimation));
		
		this.setSize(Animation.currentFrame.frame.getWidth(), Animation.currentFrame.frame.getHeight());
		
		this.setBackground(Color.BLACK);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.drawImage(Animation.getFrame(), 0, 0, this);
		g2d.dispose();
	}
	
}
