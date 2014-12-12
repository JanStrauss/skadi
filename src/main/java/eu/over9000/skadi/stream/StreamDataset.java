/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;

/**
 * Model class storing stream quality infomration.
 * 
 * @author Jan Strauß
 * 
 */
public class StreamDataset {
	private final Channel channel;
	private final List<StreamQuality> qualitiesList;
	private final Map<String, StreamQuality> qualitiesMap;
	
	public StreamDataset(final Channel channel, final List<StreamQuality> qualities) {
		this.channel = channel;
		this.qualitiesList = qualities;
		this.qualitiesMap = new HashMap<>();
		
		for (final StreamQuality streamQuality : qualities) {
			this.qualitiesMap.put(streamQuality.getQuality(), streamQuality);
		}
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public List<StreamQuality> getQualities() {
		return this.qualitiesList;
	}
	
	public StreamQuality getHighestQuality() {
		return this.qualitiesList.get(0);
	}
	
	public StreamQuality getLowestQuality() {
		return this.qualitiesList.get(this.qualitiesList.size() - 1);
	}
	
	public StreamQuality getQuality(final String quality) {
		final StreamQuality result = this.qualitiesMap.get(quality);
		
		if (result == null) {
			
			switch (quality) {
				case "best":
					return this.getHighestQuality();
				case "worst":
					return this.getLowestQuality();
				default:
					SkadiLogging.log("found no match for quality " + quality + ", will use highest quality");
					return this.getHighestQuality();
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "StreamDataset [channel=" + this.channel + ", qualitiesList=" + this.qualitiesList + ", qualitiesMap="
		        + this.qualitiesMap + "]";
	}
	
}
