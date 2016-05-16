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

package eu.over9000.skadi.service;

import eu.over9000.skadi.model.ChannelStore;
import eu.over9000.skadi.ui.StatusBarWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ImportFollowedService extends RetrieveFollowedService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportFollowedService.class);


	@SuppressWarnings("unchecked")
	public ImportFollowedService(final ChannelStore channelStore, final String user, final StatusBarWrapper statusBar) {
		super(user);

		setOnSucceeded(event -> {
			final Set<String> result = (Set<String>) event.getSource().getValue();

			statusBar.unbindFromService();

			if (result != null) {
				channelStore.addChannels(result, statusBar);
			}


		});
		setOnFailed(event -> {
			LOGGER.error("import followed failed ", event.getSource().getException());
			statusBar.unbindFromService();
		});

		statusBar.bindToService(this);
	}
}
