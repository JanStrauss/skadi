/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
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

public class ChannelMetadata {
	private final String title;
	private final Integer viewer;
	private final Long uptime;
	private final Boolean online;
	private final String game;
	private final String logoURL;
	private final Integer followers;
	private final Integer views;
	private final Boolean partner;

	public ChannelMetadata(final String title, final Integer viewer, final Long uptime, final Boolean online, final String game, final String logoURL, final Integer followers, final Integer views, final Boolean partner) {
		this.title = title;
		this.viewer = viewer;
		this.uptime = uptime;
		this.online = online;
		this.game = game;
		this.logoURL = logoURL;
		this.followers = followers;
		this.views = views;
		this.partner = partner;
	}

	// -------------------------------
	public boolean hasTitle() {
		return this.title != null;
	}

	public String getTitle() {
		return this.title;
	}

	// -------------------------------
	public boolean hasViewer() {
		return this.viewer != null;
	}

	public Integer getViewer() {
		return this.viewer;
	}

	// -------------------------------
	public boolean hasUptime() {
		return this.uptime != null;
	}

	public Long getUptime() {
		return this.uptime;
	}

	// -------------------------------
	public boolean hasOnline() {
		return this.online != null;
	}

	public Boolean getOnline() {
		return this.online;
	}

	// -------------------------------
	public boolean hasGame() {
		return this.game != null;
	}

	public String getGame() {
		return this.game;
	}

	// -------------------------------
	public boolean hasLogoURL() {
		return this.logoURL != null;
	}

	public String getLogoURL() {
		return this.logoURL;
	}

	// -------------------------------
	public boolean hasFollowers() {
		return this.followers != null;
	}

	public Integer getFollowers() {
		return this.followers;
	}

	// -------------------------------
	public boolean hasViews() {
		return this.views != null;
	}

	public Integer getViews() {
		return this.views;
	}

	// -------------------------------
	public boolean hasPartner() {
		return this.partner != null;
	}

	public Boolean getPartner() {
		return this.partner;
	}

}
