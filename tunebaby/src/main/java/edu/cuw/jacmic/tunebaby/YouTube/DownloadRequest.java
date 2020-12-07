package edu.cuw.jacmic.tunebaby.YouTube;

public class DownloadRequest {
	
	public String url;
	public Request type;
	private Runnable finishedAction;
	//public DownloadRequest previous;
	//public DownloadRequest next;
	
	public DownloadRequest(String url, Request type) {
		this.url = url;
		this.type = type;
		this.finishedAction = null;
	}
	
	public DownloadRequest(String url, Request type, Runnable finishedAction) {
		this.url = url;
		this.type = type;
		this.finishedAction = finishedAction;
	}
	
	public void finish() {
		if(finishedAction != null) {
			finishedAction.run();
		}
	}
	
	/*public void addToEnd(DownloadRequest request) {
		if(next == null) {
			next = request;
		} else {
			next.addToEnd(request);
		}
	}
	
	public void addToBeginning(DownloadRequest request) {
		if(previous == null) {
			previous = request;
		} else {
			previous.addToBeginning(request);
		}
	}*/
	
 }
