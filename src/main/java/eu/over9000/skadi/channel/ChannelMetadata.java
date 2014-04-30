package eu.over9000.skadi.channel;

import eu.over9000.skadi.util.TimeUtil;

public class ChannelMetadata {
	private final boolean online;
	private final int viewers;
	private final String title;
	private final String game;
	private final long uptime;
	
	public ChannelMetadata(final int viewers, final String title, final String game, final long uptime) {
		this.online = true;
		this.viewers = viewers;
		this.title = title;
		this.game = game;
		this.uptime = uptime;
	}
	
	public ChannelMetadata() {
		this.online = false;
		this.viewers = 0;
		this.title = "OFFLINE";
		this.game = "UNKNOWN";
		this.uptime = 0;
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
	
}
