package eu.over9000.skadi.util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.over9000.skadi.stream.StreamQuality;

public class M3UParser {
	
	public static List<StreamQuality> parseString(String stream) throws FileNotFoundException {
		
		final List<StreamQuality> qualities = new ArrayList<>();
		
		stream = stream.replaceAll("#EXTM3U", "").trim();
		
		final String[] lines = stream.split("\n");
		
		for (int index = 1; index < lines.length; index = index + 3) {
			
			final String ext_x_media = lines[index + 0];
			final String ext_x_stream_inf = lines[index + 1];
			final String url = lines[index + 2];
			
			qualities.add(new StreamQuality(url, M3UParser.extractName(ext_x_media), M3UParser
			        .extractBandwidth(ext_x_stream_inf)));
		}
		
		return qualities;
		
	}
	
	private static int extractBandwidth(String ext_x_stream_inf) {
		final Map<String, String> values = new HashMap<>();
		ext_x_stream_inf = ext_x_stream_inf.replace("#EXT-X-STREAM-INF:", "");
		
		final String[] keyVals = ext_x_stream_inf.split(",");
		
		for (final String keyVal : keyVals) {
			final String[] split = keyVal.split("=");
			final String key = split[0];
			final String value = split[1].replaceAll("\"", "");
			values.put(key, value);
		}
		
		return Integer.parseInt(values.get("BANDWIDTH"));
	}
	
	public static String extractName(String ext_x_media) {
		final Map<String, String> values = new HashMap<>();
		ext_x_media = ext_x_media.replace("#EXT-X-MEDIA:", "");
		
		final String[] keyVals = ext_x_media.split(",");
		
		for (final String keyVal : keyVals) {
			final String[] split = keyVal.split("=");
			final String key = split[0];
			final String value = split[1].replaceAll("\"", "");
			values.put(key, value);
		}
		
		return values.get("NAME");
	}
	
}
