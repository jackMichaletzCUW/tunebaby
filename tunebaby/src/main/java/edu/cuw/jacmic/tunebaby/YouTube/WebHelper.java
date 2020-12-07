package edu.cuw.jacmic.tunebaby.YouTube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WebHelper {

	public static String getVideoId(String url) {
		String videoID = url.substring(url.indexOf('=') + 1);
		if(videoID.contains("&")) {
			videoID = videoID.substring(0, videoID.indexOf('&'));
		}
		return videoID;
	}
	
	public static String getJSON(String apiURL) {
		try {
			StringBuilder json = new StringBuilder();
			URL url = new URL(apiURL);
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String jsonLine;
			while((jsonLine = reader.readLine()) != null) {
				json.append(jsonLine);
			}
			reader.close();
			return json.toString();
		} catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	
}
