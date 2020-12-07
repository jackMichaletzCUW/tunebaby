package edu.cuw.jacmic.tunebaby.Sound;

import java.io.*;

public class WaveWriter {

	WaveFile source;
	String destinationPath;
	FileOutputStream out;
	
	
	public WaveWriter(WaveFile source, String destinationPath) {
		this.source = source;
		this.destinationPath = destinationPath;
	}
	
	public void writeWave() throws IOException {
		try {
			out = new FileOutputStream(new File(destinationPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
    	writeInteger(source.riff, true);
    	writeInteger(source.fileSize, false);
    	writeInteger(source.fileTypeHeader, true);
    	
    	writeInteger(source.formatChunkMarker, true);
    	writeInteger(source.lengthOfFormatChunk, false);
    	writeShort(source.soundFormat, false);
    	writeShort(source.numberOfChannels, false);
    	writeInteger(source.sampleRate, false);
    	writeInteger(source.byteRate, false);
    	writeShort(source.blockAlign, false);
    	writeShort(source.bitsPerSample, false);
    	
    	writeInteger(source.dataChunkHeader, true);
    	writeInteger(source.dataSize, false);
    	
    	/*for(int i = 0; i < (source.dataSize / 2); i++) {
    		writeShort(source.data[i], false);
    	}*/
    	
    	out.write(source.data);

    	//out.flush();
	    out.close();
	}
	
	private void writeInteger(int input, boolean bigEndian) throws IOException {
		for(int i = 0; i < 4; i++) {
    		out.write((input >> (bigEndian ? ((3 - i) * 8) : (i * 8))) & 0xFF);
    		out.flush();
    	}
	}
	
	private void writeShort(short input, boolean bigEndian) throws IOException {
		if(bigEndian) {
			out.write((input >> 8) & 0xFF);
			out.write(input & 0xFF);
			out.flush();
		} else {
			out.write(input & 0xFF);
			out.write((input >> 8) & 0xFF);
			out.flush();
		}
	}
	
}
