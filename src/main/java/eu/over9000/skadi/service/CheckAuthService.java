/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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

import eu.over9000.cathode.Result;
import eu.over9000.cathode.data.RootBox;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckAuthService extends AbstractSkadiService<RootBox> {

	private final static Logger LOGGER = LoggerFactory.getLogger(CheckAuthService.class);

	@Override
	protected Task<RootBox> createTask() {
		return new Task<RootBox>() {
			@Override
			protected RootBox call() throws Exception {

				final Result<RootBox> result = TwitchUtil.getTwitch().root.getRoot();

				if (result.isOk()) {
					return result.getResultRaw();
				} else {
					LOGGER.error("check auth failed: " + result.getResultRaw());
					throw new Exception(result.getErrorRaw());
				}
			}
		};
	}
}
