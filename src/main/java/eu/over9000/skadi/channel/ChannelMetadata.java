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
package eu.over9000.skadi.channel;

import eu.over9000.skadi.util.TimeUtil;

/**
 * Model class storing the meta information about a channel retrieved from the twitch API.
 * 
 * @author Jan Strauß
 * 
 */
public class ChannelMetadata {
	private final boolean online;
	private final int viewers;
	private final String streamer;
	private final String title;
	private final String game;
	private final long uptime;
	
	public ChannelMetadata(final boolean online, final int viewers, final String streamer, final String title,
	        final String game, final long uptime) {
		this.online = online;
		this.viewers = viewers;
		this.streamer = streamer;
		this.title = title;
		this.game = game;
		this.uptime = uptime;
	}
	
	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return this.online;
	}
	
	/**
	 * @return the viewers
	 */
	public int getViewers() {
		return this.viewers;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * @return the game
	 */
	public String getGame() {
		return this.game;
	}
	
	/**
	 * @return the uptime
	 */
	public long getUptime() {
		return this.uptime;
	}
	
	public String buildMetadataString() {
		if (this.online) {
			return "ON" + this.getGame() + " | " + this.getTitle() + " | " + this.getViewers() + " | "
			        + TimeUtil.getDurationBreakdown(this.getUptime());
		} else {
			return "OFF";
		}
	}
	
	public String getStreamer() {
		return this.streamer;
	}
	
}
