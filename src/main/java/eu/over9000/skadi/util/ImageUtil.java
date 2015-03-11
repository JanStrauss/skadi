package eu.over9000.skadi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import eu.over9000.skadi.model.Channel;

public class ImageUtil {
	
	// private static final String BASE_URL_GAME_LOGO = "http://static-cdn.jtvnw.net/ttv-logoart/%s-120x72.jpg";
	private static final String BASE_URL_GAME_BOX = "http://static-cdn.jtvnw.net/ttv-boxart/%s-52x72.jpg";
	private static final String BASE_URL_PREVIEW = "http://static-cdn.jtvnw.net/previews-ttv/live_user_%s-320x180.jpg";

	public static Tooltip getGameTooltip(final String game) {
		return new Tooltip(game);
	}
	
	public static ImageView getGameLogoFromTwitch(final String game) {
		
		try {
			final String url = String.format(ImageUtil.BASE_URL_GAME_BOX, URLEncoder.encode(game, "UTF-8"));
			return new ImageView(url);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ImageView getPreviewFromTwitch(final Channel channel) {
		try {
			final String url = String.format(ImageUtil.BASE_URL_PREVIEW, URLEncoder.encode(channel.getName().toLowerCase(), "UTF-8"));
			return new ImageView(url);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ImageView getChannelLogo(final String logoURL) {
		final ImageView iv = new ImageView(logoURL);
		iv.setFitHeight(72);
		iv.setFitWidth(72);
		iv.setSmooth(true);
		iv.setCache(true);
		return iv;
	}
}
