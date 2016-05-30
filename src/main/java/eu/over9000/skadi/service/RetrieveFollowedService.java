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
import eu.over9000.cathode.data.FollowList;
import eu.over9000.cathode.data.parameters.Direction;
import eu.over9000.cathode.data.parameters.GetFollowsSortBy;
import eu.over9000.cathode.data.parameters.OffsetPagination;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

public class RetrieveFollowedService extends AbstractSkadiService<Set<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveFollowedService.class);

	private final String user;

	@SuppressWarnings("unchecked")
	public RetrieveFollowedService(final String user) {
		this.user = user;
	}

	@Override
	protected Task<Set<String>> createTask() {
		return new Task<Set<String>>() {

			@Override
			protected Set<String> call() throws Exception {
				updateProgress(-1, 0);
				updateMessage("importing follwed channels from user " + user + "...");

				final Set<String> channels = new TreeSet<>();

				final OffsetPagination pagination = new OffsetPagination();

				long total;
				Result<FollowList> responseFollows;

				do {
					responseFollows = TwitchUtil.getTwitch().users.getFollows(user, pagination, new Direction(), new GetFollowsSortBy());

					if (!responseFollows.isOk()) {
						final String error = responseFollows.getErrorRaw().getMessage();
						LOGGER.warn("error retrieving follows: ", responseFollows.getErrorRaw());
						updateMessage("Error: " + error);
						break;
					}

					final FollowList currentBatch = responseFollows.getResultRaw();

					total = currentBatch.getTotal();
					currentBatch.getFollows().stream().map(follow -> follow.getChannel().getName()).forEach(channels::add);

					updateProgress(channels.size(), total);
					pagination.next(currentBatch);

				} while (pagination.getOffset() < total);

				return channels;
			}
		};
	}
}
