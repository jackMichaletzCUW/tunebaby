package edu.cuw.jacmic.tunebaby;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings {

	public static String soundDirectory;
	private static Map<String, String> settingDictionary = new HashMap<String, String>();
	
	public static boolean[] lastConfig = {true, true, false};
	
	public static boolean slowOn = false;
	public static boolean reverbOn = false;
	public static boolean muffleOn = false;
	public static double soundSpeedConstant = 0.9;
	
	public static boolean hasSoundConfigurationChanged() {
		return !((lastConfig[0] == slowOn) && (lastConfig[1] == reverbOn) && (lastConfig[2] == muffleOn));
	}
	
	public static void loadSettings(String fileName) {
		settingDictionary = new HashMap<String, String>();
		
		ArrayList<String> settingList = new ArrayList<String>();
		
		try {
			settingList = new ArrayList<String>(Files.readAllLines(Paths.get(fileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String setting : settingList) {
			String[] settingSplit = setting.split(":");
			
			if(settingSplit.length == 2) {
				settingDictionary.put(settingSplit[0], settingSplit[1].substring(1));
			}
		}
	}
	
	public static String get(String key) {
		return settingDictionary.get(key).replace("~", System.getProperty("user.home"));
	}
	
	public static void set(String key, String value) {
		settingDictionary.put(key, value);
	}
	
	public static int getInteger(String key) {
		return Integer.parseInt(settingDictionary.get(key));
	}
	
	public static double getDouble(String key) {
		return Double.parseDouble(settingDictionary.get(key));
	}
}
