package edu.cuw.jacmic.tunebaby.YouTube;
import com.github.kiulian.downloader.*;
import com.github.kiulian.downloader.model.*;
import com.github.kiulian.downloader.model.formats.*;

import edu.cuw.jacmic.tunebaby.Core;
import edu.cuw.jacmic.tunebaby.Settings;
import edu.cuw.jacmic.tunebaby.Sound.Audio;
import edu.cuw.jacmic.tunebaby.Sound.WaveFile;
import edu.cuw.jacmic.tunebaby.Sound.WaveWriter;
import ie.corballis.sox.SoXEffect;
import ie.corballis.sox.Sox;
import ie.corballis.sox.WrongParametersException;
import ws.schild.jave.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Downloader {
	
	private static ProcessBuilder builder;
	private static HashMap<String, String> videoTitles = new HashMap<String, String>(); // key: url, value: title
	public static int progress = -1;
	public static String progressString = "";
	
	private static final ReentrantLock videoTitleLock = new ReentrantLock(); 
	
	public static String getVideoTitle(String urlString) {
		if(videoTitles.containsKey(urlString)) {
			return videoTitles.get(urlString);
		} else if(isValidAddress(urlString)) {
			URL url;
		    InputStream is = null;
		    BufferedReader br;
		    String line;
	
		    String title = "";
		    
		    try {
		        url = new URL(urlString);
		        is = url.openStream();  // throws an IOException
		        br = new BufferedReader(new InputStreamReader(is));
	
		        while ((line = br.readLine()) != null) {
		            if(line.contains("<title>")) {
		            	int start = line.indexOf("<title>");
		            	int end = line.indexOf(" - YouTube</title>");
		            	
		            	title = line.substring(start + 7, end).replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&");
		            }
		        }
		    } catch (MalformedURLException mue) {
		         mue.printStackTrace();
		    } catch (IOException ioe) {
		         ioe.printStackTrace();
		    } finally {
		        try {
		            if (is != null) is.close();
		        } catch (IOException ioe) {
		            // nothing to see here
		        }
		    }
	
		    videoTitles.put(urlString, title);
		    return title;
		} else {
			return "";
		}
	}
	
	public static boolean videoTitleLoadedYet(String urlString) {
		return videoTitles.containsKey(urlString);
	}
	
	public static void getVideoTitleAsync(String urlString, Consumer<String> completedAction) {
		if(videoTitles.containsKey(urlString)) {
			completedAction.accept(videoTitles.get(urlString));
		} else if(isValidAddress(urlString)) {
			Runnable r = () -> {
				if(videoTitleLock.tryLock()) {
					URL url;
				    InputStream is = null;
				    BufferedReader br;
				    String line;
			
				    String title = "";
				    
				    try {
				        url = new URL(urlString);
				        is = url.openStream();  // throws an IOException
				        br = new BufferedReader(new InputStreamReader(is));
			
				        while ((line = br.readLine()) != null) {
				            if(line.contains("<title>")) {
				            	int start = line.indexOf("<title>");
				            	int end = line.indexOf(" - YouTube</title>");
				            	
				            	title = line.substring(start + 7, end).replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&");
				            }
				        }
				    } catch (MalformedURLException mue) {
				         mue.printStackTrace();
				    } catch (IOException ioe) {
				         ioe.printStackTrace();
				    } finally {
				        try {
				            if (is != null) is.close();
				        } catch (IOException ioe) {
				            // nothing to see here
				        }
				    }
			
				    videoTitles.put(urlString, title);
				    completedAction.accept(title);
				    videoTitleLock.unlock();
				}
			};
			Thread t = new Thread(r);
			t.start();
		} else {
			completedAction.accept("");
		}
	}
	
	/*public static void playPlaylist(File playlistTextFile) {
		Runnable task = () -> {
			try {
				ArrayList<String> links = new ArrayList<String>(Files.readAllLines(Paths.get(playlistTextFile.getAbsolutePath())));
				
				for(int i = 0; i < links.size(); i++) {
					if(!isValidAddress(links.get(i))) {
						links.remove(i);
						i--;
					}
				}
				
				if(links.size() > 0) {
					Batch.addToBatch(new DownloadRequest(links.get(0), Request.DOWNLOAD), false);
					Batch.addToBatch(
						new DownloadRequest(
							links.get(0),
							Request.INTERLEAVE,
							() -> { 
								Audio.loadAudio(Settings.soundDirectory + "/interleaved/" + getVideoID(links.get(0)) + ".wav");
								Audio.playAudio(() -> {
									Audio.loadAudio(Settings.soundDirectory + "/interleaved/" + getVideoID(links.get(1)) + ".wav");
									Audio.playAudio(() -> {System.out.println("FINISHED!!!!!!!!!!!!!");});});
								})
							
						, 
						false
					);
					
					if(links.size() > 1) {
						Batch.addToBatch(new DownloadRequest(links.get(1), Request.DOWNLOAD), false);
						Batch.addToBatch(new DownloadRequest(links.get(1), Request.INTERLEAVE), false);
					}
				}
				
				for(int i = 2; i < links.size(); i++) {
					System.out.println(links.get(i));
					Batch.addToBatch(new DownloadRequest(links.get(i), Request.DOWNLOAD), false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		Thread t = new Thread(task);
		t.start();
	}*/
	
	protected static void convertVideo(File target, Runnable completedAction, Runnable progressUpdateAction) {
		System.out.println(target.getAbsolutePath());
		//File target = new File(Settings.soundDirectory + "/" + downloadedFile.getName().substring(0, downloadedFile.getName().indexOf('.')) + ".mp3");                         
		                                                              
		//Audio Attributes                                       
		/*AudioAttributes audio = new AudioAttributes();              
		audio.setCodec("libmp3lame");                             
		                                                            
		//Encoding attributes                                       
		EncodingAttributes attrs = new EncodingAttributes();        
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);  	
	        
		//Encode                                                    
		Encoder encoder = new Encoder();  
		try {
			encoder.encode(new MultimediaObject(downloadedFile), target, attrs);
		} catch(EncoderException ee) {
			// dn
			ee.printStackTrace();
		}*/
		
		System.out.print("APPLYING EFFECTS...");
		progressString = "FILTER";
		progress = 0;
		long ms = System.currentTimeMillis();
		applyEffects(target);
		System.out.printf("...%dms\nINTERLEAVING...", System.currentTimeMillis() - ms);
		progressString = "INTERLEAVE";
		progress = 0;
        ms = System.currentTimeMillis();
		interleave(target.getName().substring(0, target.getName().indexOf('.')));
		System.out.printf("...%dms\n\n", System.currentTimeMillis() - ms);
		
		progress = -1;
		//downloadedFile.delete();
		
		Batch.progress = -1;
		completedAction.run();
	}
	
	private static void applyEffects(File mp3) {
		try {			
			Sox psock = new Sox(Settings.get("sox-path"));
			Sox rsock = new Sox(Settings.get("sox-path"));
			Sox msock = new Sox(Settings.get("sox-path"));
			Sox bsock = new Sox(Settings.get("sox-path"));
			
			String fileName = mp3.getName().substring(0, mp3.getName().length() - 4);
			
			// Run effect application in parallel
			ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>();
			
			tasks.add(() -> {
				try {
					psock.argument("-v", "0.7").inputFile(mp3.getCanonicalPath()).bits(16).outputFile(Settings.soundDirectory + "/" + fileName + "-plain.wav").execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("PSOCK COMPLETE!");
				progress += 25;
				return "complete";
			});
			
			tasks.add(() -> {
				try {
					msock.argument("-v", "0.75").inputFile(mp3.getCanonicalPath()).bits(16).outputFile(Settings.soundDirectory + "/" + fileName + "-muffle.wav").effect(SoXEffect.LOWPASS, "2000").execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("MSOCK COMPLETE!");
				progress += 25;
				return "complete";
			});
			
			tasks.add(() -> {
				try {
					rsock.argument("-v", "0.7").inputFile(mp3.getCanonicalPath()).bits(16).outputFile(Settings.soundDirectory + "/" + fileName + "-reverb.wav").effect(SoXEffect.REVERB).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("RSOCK COMPLETE!");
				progress += 25;
				return "complete";
			});
			
			tasks.add(() -> {
				try {
					bsock.argument("-v", "0.7").inputFile(mp3.getCanonicalPath()).bits(16).outputFile(Settings.soundDirectory + "/" + fileName + "-both.wav").effect(SoXEffect.REVERB).effect(SoXEffect.LOWPASS, "2000").execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("BSOCK COMPLETE!");
				progress += 25;
				return "complete";
			});
			
			ExecutorService executor = Executors.newFixedThreadPool(4);
			executor.invokeAll(tasks);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void interleave(String fileName) {
		try {
			WaveFile plain = new WaveFile(Settings.soundDirectory + "/" + fileName + "-plain.wav");		
			WaveFile reverb = new WaveFile(Settings.soundDirectory + "/" + fileName + "-reverb.wav");		
			WaveFile muffled = new WaveFile(Settings.soundDirectory + "/" + fileName + "-muffle.wav");
			WaveFile both = new WaveFile(Settings.soundDirectory + "/" + fileName + "-both.wav");
			
			(new File(Settings.soundDirectory + "/" + fileName + "-plain.wav")).delete();
			(new File(Settings.soundDirectory + "/" + fileName + "-reverb.wav")).delete();
			(new File(Settings.soundDirectory + "/" + fileName + "-muffle.wav")).delete();
			(new File(Settings.soundDirectory + "/" + fileName + "-both.wav")).delete();
			
			WaveFile[] t = {reverb,muffled,both};
			
			plain.interleave(t, new Consumer<Integer>() {
				@Override
				public void accept(Integer t) {
					// TODO Auto-generated method stub
					progress = t;
				}
			});
			
			WaveWriter ww = new WaveWriter(plain, Settings.soundDirectory + "/interleaved/" + fileName + ".wav");
			ww.writeWave();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getVideoID(String url) {
		return url.substring(url.indexOf("=") + 1);
	}
	
	public static void downloadAsync(String videoURL, YoutubeVideo video, Format format, File outDir, OnYoutubeDownloadListener listener) throws IOException, YoutubeException {
        VideoDetails details = video.details();
		
		if (details.isLive() || details.lengthSeconds() == 0)
            throw new YoutubeException.LiveVideoException("Can not download live stream");

        File outputFile = new File(outDir, getVideoID(videoURL) + ".m4a");//getOutputFile(video.details(), format, outDir);

        final URL url = new URL(format.url());
        Thread thread = new Thread(new Runnable() {
        	int chunkSize = 4096;
            @Override
            public void run() {
                try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                        double total = 0;
                        byte[] buffer = new byte[chunkSize];
                        int count = 0;
                        int progress = 0;
                        while ((count = bis.read(buffer, 0, chunkSize)) != -1) {
                            bos.write(buffer, 0, count);
                            total += count;
                            int newProgress = (int) ((total / format.contentLength()) * 100);
                            if (newProgress > progress) {
                                progress = newProgress;
                                listener.onDownloading(progress);
                            }
                        }

                        listener.onFinished(outputFile);
                    } catch (IOException e) {
                        listener.onError(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "YtDownloader");
        thread.setDaemon(true);
        thread.start();
    }
	
	public static boolean isValidAddress(String url) {
		return url.contains("youtube.com/watch?v=");
	}
	
	protected static void downloadAudio(String url, Runnable finishAction) {
		progressString = "DOWNLOAD";
		Downloader.progress = 0;
		YoutubeDownloader downloader = new YoutubeDownloader();
		
		String id = getVideoID(url);
		File mp3 = new File(Settings.soundDirectory + "/" + id + ".ogg");
		
		try {			
			//Core.videoTitle = video.details().title();
			
			if(mp3.exists()) {
				// do nothing
				finishAction.run();
			} else {
				YoutubeVideo video = downloader.getVideo(id);

	            int itag = 140;
	            
	            ArrayList<Format> formats = new ArrayList<Format>(video.audioFormats());
	            Format format = null;
	            
	            for(Format f : formats) {
	            	if(f.itag().id() == itag) {
	            		format = f;
	            		break;
	            	}
	            }
	            
	            if(format == null) {
	            	if(formats.size() > 0) {
	            		format = formats.get(0);
	            	} else {
	            		format = video.findFormatByItag(itag);
	            	}
	            }
	            
	            File outDir = new File(Settings.soundDirectory);
	
	            downloadAsync(url, video, format, outDir, new OnYoutubeDownloadListener() {
	                @Override
	                public void onDownloading(int progress) {
	                	Downloader.progress = (int)((double)progress * 1.0);
						//System.out.printf("download %02d%%\n", Batch.progress);
	                }
	
	                @Override
	                public void onFinished(File file) {
	                	Downloader.progressString = "ENCODE";
	                	Downloader.progress = 50;
	                	File target = new File(Settings.soundDirectory + "/" + file.getName().substring(0, file.getName().indexOf('.')) + ".ogg");                         
                        
	            		//Audio Attributes                                       
	            		AudioAttributes audio = new AudioAttributes();              
	            		audio.setCodec("libvorbis");  
	            		                                                            
	            		//Encoding attributes                                       
	            		EncodingAttributes attrs = new EncodingAttributes();        
	            		attrs.setFormat("oga");
	            		attrs.setAudioAttributes(audio);  	
	            	        
	            		//Encode                                                    
	            		Encoder encoder = new Encoder(new FFMPEGLocator() {
							
							@Override
							protected String getFFMPEGExecutablePath() {
								// TODO Auto-generated method stub
								return Settings.get("ffmpeg-path");
							}
						});
	            		
	            		try {
	            			//for(String d : encoder.getSupportedDecodingFormats())
	            			//	System.out.println(d);
	            			
	            			encoder.encode(new MultimediaObject(file), target, attrs);
	            		} catch(EncoderException ee) {
	            			// dn
	            			ee.printStackTrace();
	            			
	            			for(String d : encoder.getUnhandledMessages())
	            				System.out.println(d);
	            		}
	            		
	            		Downloader.progress = -1;
	            		file.delete();
	                    finishAction.run();
	                }
	
	                @Override
	                public void onError(Throwable throwable) {
	                }
	
	            });
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void downloadYoutube(String url, Runnable completedAction, Runnable progressUpdateAction) {
		Runnable task = () -> {
			try {
				
				File ytout = new File(Settings.soundDirectory + "/ytout.wav");
				ytout.delete();
				
				Batch.addToBatch(new DownloadRequest(url, Request.DOWNLOAD), false);
				Batch.addToBatch(new DownloadRequest(url, Request.INTERLEAVE, completedAction), false);
				/*YoutubeDownloader downloader = new YoutubeDownloader();
				
				String id = getVideoID(url);
				File mp3 = new File(Settings.soundDirectory + "/" + id + ".mp3");
				
				YoutubeVideo video = downloader.getVideo(id);
				
				Core.videoTitle = video.details().title();
				
				if(mp3.exists()) {
					System.out.println("AUDIO IN LIBRARY... APPLYING EFFECTS...\n");
					long ms = System.currentTimeMillis();
                    applyEffects(mp3);
                    System.out.printf("...%dms\nINTERLEAVING...", System.currentTimeMillis() - ms);
                    ms = System.currentTimeMillis();
            		interleave(id);
            		System.out.printf("...%dms\n\n", System.currentTimeMillis() - ms);
                    completedAction.run();
				} else {
		            int itag = 140;
		            
		            ArrayList<Format> formats = new ArrayList<Format>(video.audioFormats());
		            Format format = null;
		            
		            for(Format f : formats) {
		            	if(f.itag().id() == itag) {
		            		format = f;
		            		break;
		            	}
		            }
		            
		            if(format == null) {
		            	if(formats.size() > 0) {
		            		format = formats.get(0);
		            	} else {
		            		format = video.findFormatByItag(itag);
		            	}
		            }
		            
		            File outDir = new File(Settings.soundDirectory);

		            downloadAsync(url, video, format, outDir, new OnYoutubeDownloadListener() {
		                @Override
		                public void onDownloading(int progress) {
		                	Downloader.progress = (int)((double)progress * 0.8);
		                	progressUpdateAction.run();
		                }
	
		                @Override
		                public void onFinished(File file) {
		                    convertVideo(file, completedAction, progressUpdateAction);
		                }
	
		                @Override
		                public void onError(Throwable throwable) {
		                }
	
		            });*/
		            
					
					/*String[] environment = {"$PATH=/bin:/usr/local/bin/:/usr/bin"};
					String[][] commands = {
							{"rm", "resources/ytout.wav"},
							{"/usr/local/bin/youtube-dl", "-f", "m4a", "-o",, "\"" + url + "\""},
							{"/usr/local/bin/ffmpeg", "-i", "resources/ytm4a.m4a", "-c:a pcm_s16le", "resources/yt.wav"},
							{"/usr/local/bin/sox", "-v", "0.7", "resources/yt.wav", "-b", "16", "resources/ytout.wav", "reverb", "speed", "0.9"},
							{"rm", "resources/yt.wav"},
							{"rm", "resources/ytm4a.m4a"}
					};*/
					
					/*String[] commandsStr = {
							//"rm resources/ytout.wav",
							"/usr/local/bin/youtube-dl -f m4a -o " +  String.format("'%s/%s.m4a' ", Settings.soundDirectory, url.substring(url.indexOf("=") + 1)) + "'" + url + "'"
							//"/usr/local/bin/ffmpeg -i \"" + file.getCanonicalPath() + "\" -c:a pcm_s16le resources/yt.wav",
							//"/usr/local/bin/sox -v 0.7 resources/yt.wav -b 16 resources/ytout.wav reverb speed 0.9",
							//"/usr/local/bin/sox resources/yt.wav -b 16 resources/ytd.wav",
							//"resources/waverunner resources/ytd.wav resources/ytout.wav 600 60",
							//"rm resources/yt.wav",
							//"rm " + file.getCanonicalPath()
					};*/
	
					
					/*for(String command : commandsStr) {
						
							System.out.printf("%s ", command);
						System.out.printf("\n");
						
						Process process = Runtime.getRuntime().exec(new String[] {"bash", "-c", command});//builder.start();
						//builder = new ProcessBuilder(command);
						//Process process = builder.start();
						
						process.waitFor();
						
						/BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line = "";
					    while ((line = reader.readLine()) != null) {
					        System.out.println(line);
					    }/
					}
			        File outputFile = new File(Settings.soundDirectory, url.substring(url.indexOf("=") + 1) + ".m4a");//getOutputFile(video.details(), format, outDir);

                    convertVideo(outputFile, completedAction, progressUpdateAction);*/

				//}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
		

		/*try {
			URL yahoo = new URL(url);
			BufferedReader in = new BufferedReader(
			            new InputStreamReader(
			            yahoo.openStream()));

			String inputLine;
			String urlLine = "";
			boolean foundCodec = false;
			boolean foundURL = false;
			
			while (!foundURL && (inputLine = in.readLine()) != null) {
				if(inputLine.contains("mimeType\\\":\\\"audio")) {
					//System.out.println(inputLine);
					
					String[] split = inputLine.split("u0026");
					
					for(int i = 0; i < split.length; i++) {
						//System.out.println(split[i]);
						if(!foundCodec && split[i].contains("mimeType\\\":\\\"audio\\/mp4")) {
							
							System.out.printf("-1:\t%s\n\n00:\t%s\n\n+1:\t%s\n", split[i-1], split[i], split[i+1]);
							
							if(split[i].contains("url=")) {
								urlLine = split[i];
								foundURL = true;
								break;
							} else {
								foundCodec = true;
							}
							/*for(int j = i - 1; j >= 0; j--) {
								if(split[j].contains("sp=sig\\\\")) {
									break;
								} else {
									urlLine = split[j] + urlLine;
								}
							}
						} else if(foundCodec) {
							if(split[i].contains("url=")) {
								urlLine = split[i];
								foundURL = true;
							}
						}
					}
					
					int idx = urlLine.indexOf("url=");
					urlLine = urlLine.substring(idx + 4);
					//idx = urlLine.indexOf('\\');
					//urlLine = urlLine.substring(0, idx);
					
					urlLine = urlLine.replace("%3A", ":");
					urlLine = urlLine.replace("%2F", "/");
					urlLine = urlLine.replace("%3F", "?");
					urlLine = urlLine.replace("%3D", "=");
					urlLine = urlLine.replace("%26", "&");
					urlLine = urlLine.replace("%25", "%");
					System.out.println(urlLine);
				}
			}

			in.close();
			URL website;
			website = new URL("https://r1---sn-vgqsknez.googlevideo.com/videoplayback?expire=1586727414&ei=ljWTXrXoLIb0Daj0oagC&ip=216.180.205.68&id=o-AOUarimcXVG24DQdzsuJMW15sOkSSGUkPqa5q2QIfdUf&itag=251&source=youtube&requiressl=yes&mh=o-&mm=31%2C26&mn=sn-vgqsknez%2Csn-p5qlsnsr&ms=au%2Conr&mv=m&mvi=0&pl=19&initcwndbps=746250&vprv=1&mime=audio%2Fwebm&gir=yes&clen=1257061&otfp=1&dur=104.661&lmt=1561871697141159&mt=1586705738&fvip=1&keepalive=yes&c=WEB&txp=2201222&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cgir%2Cclen%2Cotfp%2Cdur%2Clmt&sig=AJpPlLswRAIgL5sAzeNXfNmHivXbQeZZ0S4_WIweyr3Msqx7FfPByJYCIE_R7briSaSmjikn3BtRW_OwvOOirW71hORz66SLKg2q&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=ALrAebAwQwIfX15GC1TzSIiJqdwbPpjiGLUm5pbeCWIabFdLzHzqvgIgUxvYng8lbsFEmaxxMVKRj1dFdLruHZU4pxjwd3EfxcU%3D");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream("resources/test.webm");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}*/
		
	}
	
}