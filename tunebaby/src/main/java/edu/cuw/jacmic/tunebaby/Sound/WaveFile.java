package edu.cuw.jacmic.tunebaby.Sound;

import java.io.*;
import java.util.function.Consumer;

public class WaveFile {

	int riff;
    int fileSize;
    int fileTypeHeader;
    
    int formatChunkMarker;
    int lengthOfFormatChunk;
    short soundFormat;
    short numberOfChannels;
    int sampleRate;
    int byteRate;
    short blockAlign;
    short bitsPerSample;
    
    int dataChunkHeader;
    int dataSize;
    
    byte[] data;
    
    public WaveFile(String filePath) throws IOException {
    	WaveReader in = null;
    	
    	try {
    		in = new WaveReader(filePath);
    		
    		riff = readInteger(in, true);
    		fileSize = readInteger(in, false);
    		fileTypeHeader = readInteger(in, true);
    		formatChunkMarker = readInteger(in, true);
    		lengthOfFormatChunk = readInteger(in, false);
    		soundFormat = readShort(in, false);
    		numberOfChannels = readShort(in, false);
    		sampleRate = readInteger(in, false);
    		byteRate = readInteger(in, false);
    		blockAlign = readShort(in, false);
    		bitsPerSample = readShort(in, false);
    		dataChunkHeader = readInteger(in, true);
    		dataSize = readInteger(in, false);
    		
    		data = new byte[dataSize];
    		
    		for(int i = 0; i < dataSize; i++) {
    			data[i] = in.readByte();
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		if(in != null) {
    			//in.close();
    		}
    	}
    }
    
    public void interleave(WaveFile[] others, Consumer<Integer> progressFunction) {
    	
    	for(WaveFile file : others) {
    		this.fileSize += file.dataSize;
    		this.dataSize += file.dataSize;
    	}
    	
    	byte[] interleavedData = new byte[this.dataSize];
    	
    	/*for(int i = 0; i < interleavedData.length / 4; i += 4) {
			interleavedData[i * 2] = this.data[i];
			interleavedData[(i*2) + 1] = this.data[i + 1];
			interleavedData[(i*2) + 2] = other.data[i];
			interleavedData[(i*2) + 3] = other.data[i + 1];
    	}*/
    	
    	for(int i = 0; i < interleavedData.length; i++) {
    		if(i % (1 + others.length) == 0) {
    			interleavedData[i] = this.data[i / (1 + others.length)];
    		} else {
    			interleavedData[i] = others[(i % (1 + others.length)) - 1].data[i / (1 + others.length)];
    		}
    		
    		if(i % 100000 == 0) {
    			progressFunction.accept((int)(100 * ((double)i / (double)interleavedData.length)));
    		}
    	}
    	
    	this.data = interleavedData;
    }
    
    @Override
    public String toString() {
    	return String.format(
    			"RIFF:\t%d\nfsiz:\t%d\nfthd:\t%d\n\n"
    			 + "fchm:\t%d\nlofc:\t%d\nsfmt:\t%d\n# ch:\t%d\nsara:\t%d\nbyra:\t%d\nblal:\t%d\nbips:\t%d\n\n"
    			 + "dchh:\t%d\nsize:\t%d\n\n",
    			 riff,                
    			 fileSize,            
    			 fileTypeHeader,            
    			 formatChunkMarker,   
    			 lengthOfFormatChunk, 
    			 soundFormat,       
    			 numberOfChannels,  
    			 sampleRate,          
    			 byteRate,            
    			 blockAlign,        
    			 bitsPerSample,            
    			 dataChunkHeader,     
    			 dataSize
    );
    }
    
    private int readInteger(WaveReader stream, boolean bigEndian) throws IOException {
    	int target = 0;
    	
    	for(int i = 0; i < 4; i++) {
    		target |= (stream.read() << (bigEndian ? ((3 - i) * 8) : (i * 8)));
    	}
    	
    	return target;
    }
    
    private short readShort(WaveReader stream, boolean bigEndian) throws IOException {
    	/*short target = 0;
    	
    	for(int i = 0; i < 2; i++) {
    		target |= (stream.read() << (bigEndian ? ((1 - i) * 8) : (i * 8)));
    	}*/
    	
    	if(bigEndian)
    		return (short)(((stream.read() & 0xFF) << 8) | (stream.read() & 0xFF));
    	else
    		return (short)((stream.read() & 0xFF) | ((stream.read() & 0xFF) << 8));
    }
	
}
