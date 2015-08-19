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

public class ChannelMetadataBuilder {
	private String title = null;
	private Integer viewer = null;
	private Long uptime = null;
	private Boolean online = null;
	private String game = null;
	private String logoURL = null;
	private Integer followers = null;
	private Integer views = null;
	private Boolean partner = null;

	public ChannelMetadataBuilder() {
	}

	public ChannelMetadataBuilder setTitle(final String title) {
		this.title = title;
		return this;
	}

	public ChannelMetadataBuilder setViewer(final Integer viewer) {
		this.viewer = viewer;
		return this;
	}

	public ChannelMetadataBuilder setUptime(final Long uptime) {
		this.uptime = uptime;
		return this;
	}

	public ChannelMetadataBuilder setOnline(final Boolean online) {
		this.online = online;
		return this;
	}

	public ChannelMetadataBuilder setGame(final String game) {
		this.game = game;
		return this;
	}

	public ChannelMetadataBuilder setLogoURL(final String logoURL) {
		this.logoURL = logoURL;
		return this;
	}

	public ChannelMetadataBuilder setFollowers(final Integer followers) {
		this.followers = followers;
		return this;
	}

	public ChannelMetadataBuilder setViews(final Integer views) {
		this.views = views;
		return this;
	}

	public ChannelMetadataBuilder setPartner(final Boolean partner) {
		this.partner = partner;
		return this;
	}

	public ChannelMetadata build() {
		return new ChannelMetadata(title, viewer, uptime, online, game, logoURL, followers, views, partner);
	}

}
