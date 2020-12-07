package edu.cuw.jacmic.tunebaby.Sound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WaveReader {

	byte[] contents = null;
	int currentIndex = 0;
	
	public WaveReader(String filePath) throws IOException {
		contents = Files.readAllBytes(Paths.get(filePath));
	}
	
	public int read() {
		return (int)(contents[currentIndex++] & 0xff);
	}
	
	public byte readByte() {
		return contents[currentIndex++];
	}
	
}
