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

package eu.over9000.skadi.remote;

import eu.over9000.cathode.Result;
import eu.over9000.cathode.data.Stream;
import eu.over9000.cathode.data.StreamBox;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.util.ImageUtil;
import eu.over9000.skadi.util.TwitchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * This class provides static methods to retrieve channel metadata from the twitch API.
 */
public class ChannelDataRetriever {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDataRetriever.class);

	public static ChannelMetadata getChannelMetadata(final Channel channel) {
		ImageUtil.getPreviewAsyncFromTwitch(channel);

		final Result<StreamBox> streamResponse = TwitchUtil.getTwitch().streams.getStream(channel.getName());
		if (!streamResponse.isOk()) {
			LOGGER.error("Exception getting metadata for channel " + channel + ": " + streamResponse.getErrorRaw());
			return null;
		}

		final StreamBox stream = streamResponse.getResultRaw();

		final eu.over9000.cathode.data.Channel channelObject;
		final Boolean online = stream.isOnline();
		final Long uptime;
		final Long viewer;

		if (online) {
			channelObject = stream.getStream().getChannel();

			uptime = getChannelUptime(stream.getStream());
			viewer = stream.getStream().getViewers();

		} else {
			final Result<eu.over9000.cathode.data.Channel> channelResponse = TwitchUtil.getTwitch().channels.getChannel(channel.getName());
			if (!channelResponse.isOk()) {
				LOGGER.error("Exception getting metadata for channel " + channel + ": " + channelResponse.getErrorRaw());
				return null;
			}

			channelObject = channelResponse.getResultRaw();

			uptime = 0L;
			viewer = 0L;
		}

		return new ChannelMetadata(channelObject, viewer, uptime, online);

	}

	private static long getChannelUptime(final Stream stream) {
		final Date startDate = stream.getCreatedAt();
		final Date nowDate = new Date();

		return nowDate.getTime() - startDate.getTime();
	}

	public static boolean checkIfChannelExists(final String channel) {
		return TwitchUtil.getTwitch().channels.getChannel(channel).isOk();
	}

}
