/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.remote.data;

import eu.over9000.cathode.data.Channel;

public class ChannelMetadata {
	private final String title;
	private final Long viewer;
	private final Long uptime;
	private final Boolean online;
	private final String game;
	private final String logoURL;
	private final Long followers;
	private final Long views;
	private final Boolean partner;

	public ChannelMetadata(final Channel channel, final Long viewer, final Long uptime, final Boolean online) {
		this.title = channel.getStatus();
		this.viewer = viewer;
		this.uptime = uptime;
		this.online = online;
		this.game = channel.getGame();
		this.logoURL = channel.getLogo();
		this.followers = channel.getFollowers();
		this.views = channel.getViews();
		this.partner = channel.isPartner();
	}

	// -------------------------------
	public boolean hasTitle() {
		return title != null;
	}

	public String getTitle() {
		return title;
	}

	// -------------------------------
	public boolean hasViewer() {
		return viewer != null;
	}

	public Long getViewer() {
		return viewer;
	}

	// -------------------------------
	public boolean hasUptime() {
		return uptime != null;
	}

	public Long getUptime() {
		return uptime;
	}

	// -------------------------------
	public boolean hasOnline() {
		return online != null;
	}

	public Boolean getOnline() {
		return online;
	}

	// -------------------------------
	public boolean hasGame() {
		return game != null;
	}

	public String getGame() {
		return game;
	}

	// -------------------------------
	public boolean hasLogoURL() {
		return logoURL != null;
	}

	public String getLogoURL() {
		return logoURL;
	}

	// -------------------------------
	public boolean hasFollowers() {
		return followers != null;
	}

	public Long getFollowers() {
		return followers;
	}

	// -------------------------------
	public boolean hasViews() {
		return views != null;
	}

	public Long getViews() {
		return views;
	}

	// -------------------------------
	public boolean hasPartner() {
		return partner != null;
	}

	public Boolean getPartner() {
		return partner;
	}
}
