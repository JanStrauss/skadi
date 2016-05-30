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
import eu.over9000.cathode.data.Follow;
import eu.over9000.cathode.data.parameters.PutFollowsOptions;
import eu.over9000.skadi.ui.StatusBarWrapper;
import eu.over9000.skadi.util.ExecutorUtil;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class PutFollowedService extends AbstractSkadiService<Void> {


	private static final Logger LOGGER = LoggerFactory.getLogger(PutFollowedService.class);

	private final String user;
	private final List<String> toPut;
	private int successCnt = 0;

	public PutFollowedService(final String user, final List<String> toPut, final StatusBarWrapper sb) {
		this.user = user;
		this.toPut = toPut;

		sb.bindToService(this);

		setOnSucceeded(event -> {
			sb.unbindFromService();
			sb.updateStatusText("Pushed " + toPut.size() + " follows to twitch, " + successCnt + " successful.");
		});

		setOnFailed(event -> {
			LOGGER.warn("put follow failed", event.getSource().getException());
			sb.unbindFromService();
		});
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				final List<Callable<Result<Follow>>> tasks = new ArrayList<>(toPut.size());

				toPut.forEach(channel -> tasks.add(() -> TwitchUtil.getTwitch().users.putFollows(user, channel, new PutFollowsOptions(true))));

				final List<Future<Result<Follow>>> futures = ExecutorUtil.getExecutorService().invokeAll(tasks);

				for (int i = 0; i < futures.size(); i++) {
					try {
						futures.get(i).get().getOrException();
						successCnt++;
					} catch (final Exception e) {
						LOGGER.warn("failed to put follow", e);
					}
					updateProgress(i, futures.size() - 1);
				}

				return null;
			}
		};
	}
}
