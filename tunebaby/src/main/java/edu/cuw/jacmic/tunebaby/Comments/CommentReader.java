package edu.cuw.jacmic.tunebaby.Comments;

import java.awt.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.YouTube.*;

public class CommentReader {
	
	private final String REGEX_EXP = "((\\d+:)?\\d+:\\d+)\\w+";
	private final String API_KEY = "bbbb";
	private Pattern regex;
	
	public CommentReader() {
		regex = Pattern.compile(REGEX_EXP);
	}
	
	private String generateCommentThreadAPIURL(String videoID) {
		return String.format("https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=%s&maxResults=100&order=relevance&key=%s", videoID, API_KEY);
	}
	
	private String generateDescriptionAPIURL(String videoID) {
		return String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=%s&key=%s", videoID, API_KEY);
	}
	
	public String getCommentJSON(String ytURL) {
		return WebHelper.getJSON(generateCommentThreadAPIURL(WebHelper.getVideoId(ytURL)));
	}
	
	public String getDescriptionJSON(String ytURL) {
		return WebHelper.getJSON(generateDescriptionAPIURL(WebHelper.getVideoId(ytURL)));
	}
	
	public ArrayList<String> getJSONStrings(String json) {
		ArrayList<String> list = new ArrayList<String>();
		
		boolean quoteEncountered = false;
		String str = "";
		
		for(char c : json.toCharArray()) {
			if(c == '"') {
				if(quoteEncountered) {
					list.add(str);
					str = "";
					quoteEncountered = false;
				} else {
					quoteEncountered = true;
				}
			} else {
				if(quoteEncountered) {
					str += c;
				}
			}
		}
		
		return list;
	}
	
	public String[] splitTimeSignatures(String input) {
		return regex.split(input);
	}
	
	public ArrayList<String> getTimeSignatures(String input) {
		 ArrayList<String> signatures = new ArrayList<String>();
		 
		 Matcher matcher = regex.matcher(input);
		 while(matcher.find()) {
			 signatures.add(matcher.group());
		 }
		 
		 return signatures;
	}
	
	public ArrayList<Track> getTracklist(String ytURL) {
		ArrayList<Track> tracklist = null;
		
		tracklist = getTracks(getComments(getJSONStrings(getCommentJSON(ytURL))));
		
		if(tracklist == null || tracklist.size() == 0) {
			tracklist = getTracks(getDescription(getJSONStrings(getDescriptionJSON(ytURL))));
		}
		
		if(tracklist != null && tracklist.size() != 0) {
			if(!tracklist.get(0).startTime.replace(":", "").replace("0", "").equals("")) {
				tracklist.add(0, new Track("0:00", tracklist.get(0).startTime, "To start us out..."));
			}
		}
		
		return tracklist;
	}
	
	private int getOccurrenceCount(String target, String token) {
		int index = target.indexOf(token);
		
		if(index == -1) {
			return 0;
		} else {
			return 1 + getOccurrenceCount(target.substring(index + token.length()), token);
		}
	}
	
	public ArrayList<Track> getTracks(ArrayList<String> comments) {
		ArrayList<Track> trackList = new ArrayList<Track>();
		
		for(String string : comments) {
			String[] titles = splitTimeSignatures(string);
			ArrayList<String> signatures = getTimeSignatures(string);

			
			if(titles.length > 3/* && signatures.size() == titles.length - 1*/) {
				// at least two time stamps
				
				boolean firstLine = (titles[0].length() == 0 || getOccurrenceCount(titles[0], "\\n") > 1);

				for(int i = 0; i < signatures.size(); i++) {
					System.out.printf("\t%s :: %s\n",  signatures.get(i), titles[i + (firstLine ? 1 : 0)]);
					
					if(i != signatures.size() - 1) {
						trackList.add(new Track(signatures.get(i), signatures.get(i + 1), titles[i + (firstLine ? 1 : 0)]));
					} else {
						trackList.add(new Track(signatures.get(i), "", titles[i + (firstLine ? 1 : 0)]));
					}
				}
				break;
			}
		}
		
		return trackList;
	}
	
	public ArrayList<Track> getTracks(String comment) {
		ArrayList<Track> trackList = new ArrayList<Track>();

		String[] titles = splitTimeSignatures(comment);
		ArrayList<String> signatures = getTimeSignatures(comment);
			
		if(titles.length > 3/* && signatures.size() == titles.length - 1*/) {
			// at least two time stamps
			boolean firstLine = titles[0].length() == 0;
			String[] firstLineContents = titles[0].split("\\\\n");
			String first = firstLineContents[firstLineContents.length - 1];
			
			for(int i = 0; i < signatures.size(); i++) {
				System.out.printf("\t%s :: %s\n",  signatures.get(i), titles[i + (firstLine ? 1 : 0)]);
				
				if(i != signatures.size() - 1) {
					trackList.add(new Track(signatures.get(i), signatures.get(i + 1), (i == 0 ? first : titles[i + (firstLine ? 1 : 0)])));
				} else {
					trackList.add(new Track(signatures.get(i), "", titles[i + (firstLine ? 1 : 0)]));
				}
			}
		}
		
		return trackList;
	}
	
	public ArrayList<String> getComments(ArrayList<String> jsonStrings) {
		ArrayList<String> comments = new ArrayList<String>();
		
		boolean nextStringIsAComment = false;
		
		for(String string : jsonStrings) {
			if(nextStringIsAComment) {
				comments.add(string);
				nextStringIsAComment = false;
			} else if(string.equals("textOriginal")) {
				nextStringIsAComment = true;
			}
		}
		
		return comments;
	}
	
	public String getDescription(ArrayList<String> jsonStrings) {		
		boolean nextStr = false;
		for(String string : jsonStrings) {
			if(nextStr) {
				return string;
			} else if(string.equals("description")) {
				nextStr = true;
			}
		}
		
		return "";
	}
}
