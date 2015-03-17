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
		return new ChannelMetadata(this.title, this.viewer, this.uptime, this.online, this.game, this.logoURL,
		        this.followers, this.views, this.partner);
	}
	
}
