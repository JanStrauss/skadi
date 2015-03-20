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
package eu.over9000.skadi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;
import eu.over9000.skadi.remote.StreamQualityRetriever;

public class QualityRetrievalService extends Service<List<MenuItem>> {

	private final Consumer<StreamQuality> consumer;
	private final Channel channel;

	public QualityRetrievalService(final Consumer<StreamQuality> consumer, final Channel channel) {
		this.consumer = consumer;
		this.channel = channel;
	}

	@Override
	protected Task<List<MenuItem>> createTask() {
		return new Task<List<MenuItem>>() {

			@Override
			protected List<MenuItem> call() throws Exception {

				final List<MenuItem> result = new ArrayList<>();

				StreamQualityRetriever.getQualities(QualityRetrievalService.this.channel).forEach(quality -> {
					final MenuItem mi = new MenuItem("Stream: " + quality.getQuality());
					mi.setOnAction(event -> QualityRetrievalService.this.consumer.accept(quality));
					result.add(mi);
				});

				return result;
			}
		};
	}
}
