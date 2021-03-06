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

import eu.over9000.skadi.util.ImageUtil;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageRetrievalService extends AbstractSkadiService<ImageView> {

	private final String url;
	private int width;
	private int height;
	private boolean resize = false;

	public ImageRetrievalService(final String url) {
		this.url = url;
	}

	public ImageRetrievalService(final String url, final int width, final int height) {
		this.url = url;
		this.width = width;
		this.height = height;
		resize = true;
	}

	@Override
	protected Task<ImageView> createTask() {
		return new Task<ImageView>() {

			@Override
			protected ImageView call() throws Exception {
				final Image img = ImageUtil.getImageInternal(url);

				final ImageView iv = new ImageView(img);

				if (resize) {
					iv.setFitHeight(height);
					iv.setFitWidth(width);
				}

				iv.setSmooth(true);
				iv.setCache(true);
				return iv;
			}
		};
	}


}
