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
import eu.over9000.cathode.data.Panel;
import eu.over9000.cathode.data.PanelData;
import eu.over9000.cathode.data.PanelList;
import eu.over9000.skadi.util.TwitchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PanelDataRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanelDataRetriever.class);

	public static List<PanelData> retrievePanels(final String channel) {
		final List<PanelData> result = new ArrayList<>();

		final Result<PanelList> panelResponse = TwitchUtil.getTwitch().undocumented.getPanels(channel);

		if (!panelResponse.isOk()) {
			LOGGER.error("error getting panels data for " + channel + ": ", panelResponse.getErrorRaw());
			return result;
		}

		panelResponse.getResultRaw().getPanels().stream().map(Panel::getData).forEach(result::add);

		return result;
	}
}
