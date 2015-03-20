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
package eu.over9000.skadi.ui.cells;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import eu.over9000.skadi.model.Channel;

public class LiveCell extends TableCell<Channel, Boolean> {

	private final ImageView unknown = new ImageView(new Image(this.getClass().getResourceAsStream
			("/icons/stream_unknown.png")));
	private final ImageView up = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/stream_up.png")));
	private final ImageView down = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/stream_down" +
			".png")));

	public LiveCell() {
		this.setAlignment(Pos.CENTER);
	}

	@Override
	protected void updateItem(final Boolean item, final boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			this.setText(null);
			this.setGraphic(null);
		} else if (item == null) {
			this.setGraphic(this.unknown);
		} else {
			this.setGraphic(item ? this.up : this.down);
		}

	}
}
