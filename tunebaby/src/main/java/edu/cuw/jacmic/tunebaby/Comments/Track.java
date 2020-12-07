package edu.cuw.jacmic.tunebaby.Comments;

public class Track {

	public String startTime = "";
	public String endTime = "";
	public String title = "";
	
	public Track(String startTime, String endTime, String title) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.title = title.replace("\\n", "").replace("\\r", "");
	}
	
	@Override
	public String toString() {
		return String.format("%s-%s:\t%s", startTime, endTime, title);
	}
	
}
