package eu.over9000.skadi.mosaic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.stream.StreamRetriever;
import eu.over9000.skadi.util.StringUtil;

public class Mosaic extends Thread {
	
	private Process process;
	
	@Override
	public void run() {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			
			this.process.waitFor();
			System.out.println("listener exit");
		} catch (final InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createMosaic(final List<Channel> channels) {
		try {
			this.createConfigFile(channels);
			this.process = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "-I telnet", "--vlm-conf",
			        PersistenceManager.MOSAIC_CONFIG_FILE, "--mosaic-width=1280", "--mosaic-height=720",
			        "--mosaic-keep-picture", "--mosaic-rows=2", "--mosaic-cols=2", "--mosaic-position=1",
			        "--mosaic-order=1,2,3,4", "--network-caching=2500").redirectErrorStream(true).start();
			this.start();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createConfigFile(final List<Channel> channels) throws FileNotFoundException,
	        UnsupportedEncodingException {
		final PrintWriter writer = new PrintWriter(PersistenceManager.MOSAIC_CONFIG_FILE, "UTF-8");
		
		int index = 1;
		for (final Channel channel : channels) {
			final String id = StringUtil.extractChannelName(channel.getURL());
			
			writer.println("new " + id + " broadcast enabled");
			writer.println("setup " + id + " input " + StreamRetriever.getStreams(channel).getHighestQuality().getUrl());
			writer.println("setup " + id + " output #duplicate{dst=mosaic-bridge{id=" + index
			        + ",width=640,height=360},select=video,dst=bridge-out{id=" + index + "},select=audio}");
			writer.println();
			index++;
		}
		
		writer.println("new background broadcast enabled");
		writer.println("setup background input C:\\Users\\Jan\\Desktop\\vlc_test\\bg.png");
		writer.println("setup background option image-duration=-1");
		writer.println("setup background option image-fps=30/1");
		writer.println("setup background output #transcode{sfilter=mosaic,vcodec=mp4v,vb=4000}:bridge-in{id-offset=0}:display");
		writer.println();
		
		writer.println("control background play");
		for (final Channel channel : channels) {
			final String id = StringUtil.extractChannelName(channel.getURL());
			
			writer.println("control " + id + " play");
		}
		writer.close();
	}
	
	public static void main(final String[] args) throws IOException {
		
		new Mosaic().createMosaic(Arrays.asList(new Channel[] { new Channel("twitch.tv/draskyl/"),
		        new Channel("twitch.tv/weppas/"), new Channel("twitch.tv/sheevergaming/") }));
	}
}
