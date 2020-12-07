package edu.cuw.jacmic.tunebaby.Sound;

import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import edu.cuw.jacmic.tunebaby.Settings;

public class Song implements LineListener {

	private Clip wave;
	private File file;
	private AudioInputStream stream;
	private Runnable finishAction;
	
	private Runnable play;
	
	private int pauseFrame = -1;
	public boolean pauseRequested = false;
	public boolean running = false;
	
	private float fileMax = 0;
	
	AudioInputStream audioStream;
	AudioFormat audioFormat;
	SourceDataLine sourceLine;
	public LineListener sourceLineListener;
	
	public Song(String fileName) {
		file = new File(fileName);

		try {
            audioStream = AudioSystem.getAudioInputStream(file);

	        audioFormat = audioStream.getFormat();
	
	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	       			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		play = () -> {
			try {
				if(!running) {
					sourceLine.open(audioFormat);
			        running = true;
				}
				
		        sourceLine.start();
		       		        
		        int bufferSize = 38400;//9600;/*17640;/*440000;*//*1764000;*/
		        int nBytesRead = 0;
		        
		        byte[] abData = new byte[bufferSize];
		        float[] slowed = new float[4];
	        
		        while (nBytesRead != -1 && !pauseRequested) {      	
		        	if(abData.length != bufferSize) 
		        		abData = new byte[bufferSize];
		        	
		        	byte[] extracted = new byte[4];
		            try {
		                nBytesRead = audioStream.read(abData, 0, abData.length);
		                
		                if(Settings.muffleOn && Settings.reverbOn)
		                	extracted = AudioFilter.extract(abData, 4, 4);
		                else if(Settings.muffleOn)
		                	extracted = AudioFilter.extract(abData, 3, 4);
		                else if(Settings.reverbOn)
		                	extracted = AudioFilter.extract(abData, 2, 4);
		                else
		                	extracted = AudioFilter.extract(abData, 1, 4);
		               
		                
		                if(Settings.slowOn) {
		                	slowed = new float[extracted.length];
			                
			                AudioFilter.unpack(extracted, slowed, extracted.length, audioFormat);
			                
		                	slowed = AudioFilter.changeSpeed(slowed, 1.1);
		                }
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		            if (nBytesRead >= 0) {
		            	if(Settings.slowOn) {
			                byte[] sb = new byte[slowed.length * 2];
			                int len = AudioFilter.pack(slowed, sb, slowed.length / 2, audioFormat);
			                int nBytesWritten = sourceLine.write(sb, 0, len);
		            	} else {
		            		int nBytesWritten = sourceLine.write(extracted, 0, extracted.length);
		            	}
		                //System.out.printf("s-%b, r-%b, m-%b\n", Settings.slowOn, Settings.reverbOn, Settings.muffleOn);
		            }
		        }

		        //pauseRequested = true;
		        if(!pauseRequested) {
			        sourceLine.drain();
			        sourceLine.close();
			        pauseRequested = true;
			        running = false;
		        }
		    } catch (Exception e){
		        e.printStackTrace();
		        System.exit(1);
		    }
		};
		/*try {
			stream = AudioSystem.getAudioInputStream(file);
			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
			wave = (Clip)AudioSystem.getLine(info);
			wave.addLineListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public void start(Runnable finishAction) {
		sourceLineListener = new LineListener() {
			
			@Override
			public void update(LineEvent event) {
				// TODO Auto-generated method stub
				//System.out.println(event.getType());
				if(event.getType() == LineEvent.Type.CLOSE) {
					System.out.println("CLOSED");
					finishAction.run();
				}
			}
		};
		
		sourceLine.addLineListener(sourceLineListener);
		Thread t = new Thread(play);
		t.start();
		
		/*try {
			this.finishAction = finishAction;
			
		    sourceLine.open(audioFormat);
	        
	        sourceLine.start();
	        
	        int bufferSize = 176400;
	        int nBytesRead = 0;
	        
	        byte[] abData = new byte[bufferSize];
	        float[] slowed = new float[4];
        
	        while (nBytesRead != -1 && !pauseRequested) {      	
	        	if(abData.length != bufferSize) 
	        		abData = new byte[bufferSize];
	        	
	        	byte[] extracted = new byte[4];
	            try {
	                nBytesRead = audioStream.read(abData, 0, abData.length);
	                
	                if(Settings.muffleOn)
	                	extracted = AudioFilter.extract(abData, 3, 3);
	                else if(Settings.reverbOn)
	                	extracted = AudioFilter.extract(abData, 2, 3);
	                else
	                	extracted = AudioFilter.extract(abData, 1, 3);
	
	                if(Settings.slowOn) {
	                	slowed = new float[extracted.length];
		                
		                AudioFilter.unpack(extracted, slowed, extracted.length, audioFormat);
		                
	                	slowed = AudioFilter.changeSpeed(slowed, 1.1);
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            if (nBytesRead >= 0) {
	            	if(Settings.slowOn) {
		                byte[] sb = new byte[slowed.length * 2];
		                int len = AudioFilter.pack(slowed, sb, slowed.length / 2, audioFormat);
		                int nBytesWritten = sourceLine.write(sb, 0, len);
	            	} else {
	            		int nBytesWritten = sourceLine.write(extracted, 0, extracted.length);
	            	}
	                //System.out.printf("s-%b, r-%b, m-%b\n", Settings.slowOn, Settings.reverbOn, Settings.muffleOn);
	            }
	        }

	        sourceLine.drain();
	        sourceLine.close();
	    } catch (Exception e){
	        e.printStackTrace();
	        System.exit(1);
	    }*/
	
	
		/*try {
			wave.open(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		wave.start();*/
	}
	
	public void resume()
	{
		System.out.println("resume");
		
		if(pauseRequested) {
			if(!running) {
	            try {
					audioStream = AudioSystem.getAudioInputStream(file);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Thread t = new Thread(play);
			t.start();
		} else {
			sourceLine.stop();
		}
		
		pauseRequested = !pauseRequested;
		
		/*if(!wave.isActive()) {
			if(pauseFrame == -1) {
				// song has not been opened
				start(() -> {});
			} else {
				wave.start();
			}
		} else {
			pauseFrame = wave.getFramePosition();
			pauseRequested = true;
			wave.stop();
		}*/
	}

	public void close() {
		pauseRequested = true;
		sourceLine.stop();
		sourceLine.drain();
		sourceLine.close();
		try {
			audioStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//wave.setFramePosition(0);
		/*wave.close();
		
		try {
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void fastForward() {
		/*int targetFrame = wave.getFramePosition() + (44100 * 10);

		if(targetFrame < wave.getFrameLength()) {
			wave.setFramePosition(targetFrame);
		}*/
	}
	
	public void rewind() {
		/*int targetFrame = wave.getFramePosition() - (44100 * 10);
		
		if(targetFrame >= 0) {
			wave.setFramePosition(targetFrame);
		} else {
			wave.setFramePosition(0);
		}*/
		
		/*try {
			audioStream.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public int getFrame() {
		return wave.getFramePosition();
	}
	
	public void setFrame(int position) {
		wave.setFramePosition(position);
	}
	
	@Override
	public void update(LineEvent event) {
		// TODO Auto-generated method stub
				
		if(event.getType() == LineEvent.Type.STOP && !pauseRequested) {
			wave.setFramePosition(0);
			//close();
			finishAction.run();
			//System.out.println("peppeppoopo");
			pauseFrame = 0;
		}
		
		pauseRequested = false;
	}
	
}
