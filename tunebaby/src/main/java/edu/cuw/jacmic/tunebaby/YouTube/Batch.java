package edu.cuw.jacmic.tunebaby.YouTube;

import java.io.File;
import java.util.ArrayList;

import edu.cuw.jacmic.tunebaby.Settings;

public class Batch {
	
	private static ArrayList<DownloadRequest> batch = new ArrayList<DownloadRequest>();
	
	public static int progress = -1;
	private static boolean isBatchRunning = false;
	private static boolean isExecutionPaused = false;
	
	public static void describeBatch() {
		System.out.printf("BATCH WITH %d ITEMS:\n", batch.size());
		
		for(int i = 0; i < batch.size(); i++) {
			System.out.printf("...\t%02d: %s\tfor\t%s.\n", i, batch.get(i).type.toString(), batch.get(i).url);
		}
		
		System.out.println();
	}
	
	public static void clearBatch() {
		batch.clear();
	}
	
	public static void addToBatch(DownloadRequest request, boolean imperative) {
		if(imperative) {
			batch.add(0, request);
			
			// remove any redundant requests
			for(int i = 1; i < batch.size(); i++) {
				if(batch.get(i).type == request.type && batch.get(i).url == request.url) {
					batch.remove(i);
					i--;
				}
			}
		} else {
			batch.add(request);
		}
		
		if(batch.size() == 1) {
			if(!isExecutionPaused)
				startNextBatchItem();
		}
	}
	
	public static void pauseExecution() {
		isBatchRunning = false;
		isExecutionPaused = true;
	}
	
	public static void resumeExecution() {
		isExecutionPaused = false;
		startNextBatchItem();
	}
	
	public static void startNextBatchItem() {
		if(batch.size() != 0 && !isBatchRunning) {
			isBatchRunning = true;
			DownloadRequest request = batch.remove(0);
			
			if(request.type == Request.DOWNLOAD) {
				System.out.println("STARTING DOWNLOAD BATCH REQUEST");
				Downloader.downloadAudio(request.url, () -> { isBatchRunning = false; request.finish(); if(!isExecutionPaused) {startNextBatchItem();}});
			} else if(request.type == Request.INTERLEAVE) {
				System.out.println("STARTING INTERLEAVE BATCH REQUEST");
				File targetMP3 = new File(Settings.soundDirectory + "/" + Downloader.getVideoID(request.url) + ".ogg");
				File targetWAV = new File(Settings.soundDirectory + "/interleaved/" + Downloader.getVideoID(request.url) + ".wav");
				
				if(targetWAV.exists()) {
					System.out.println("...INTERLEAVE EXISTS");
					isBatchRunning = false;
					request.finish();
					if(!isExecutionPaused)
						startNextBatchItem();
				} else if(targetMP3.exists()) {
					Downloader.convertVideo(
						targetMP3,
						() -> {
							isBatchRunning = false;
							request.finish();
							if(!isExecutionPaused)
								startNextBatchItem();
						}, () -> {
							
						}
					);
				} else {
					pauseExecution();
					addToBatch(request, true);
					addToBatch(new DownloadRequest(request.url, Request.DOWNLOAD), true);
					resumeExecution();
				}
			} else if(request.type == Request.DELETE_INTERLEAVE) {
				System.out.println("STARTING INTERLEAVE DELETE REQUEST");
				File target = new File(Settings.soundDirectory + "/interleaved/" + Downloader.getVideoID(request.url) + ".wav");
				target.delete();
				request.finish();
				isBatchRunning = false;
				if(!isExecutionPaused)
					startNextBatchItem();
			} else if(request.type == Request.LOAD_TITLE) {
				System.out.println("LOADING TITLE... ");
				String title = Downloader.getVideoTitle(request.url);
				System.out.printf("... %s\n", title);
				request.finish();
				isBatchRunning = false;
				if(!isExecutionPaused)
					startNextBatchItem();
			}
		} else {
			isBatchRunning = false;
		}
	}
	
}
