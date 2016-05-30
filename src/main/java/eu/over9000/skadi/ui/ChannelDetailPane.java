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

package eu.over9000.skadi.ui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.service.DetailPaneUpdateService;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelDetailPane extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDetailPane.class);

	final ChannelDetailPaneContent content;

	public ChannelDetailPane(final MainWindow main) {

		final Button btnHide = GlyphsDude.createIconButton(FontAwesomeIcon.ANGLE_DOUBLE_RIGHT);
		btnHide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnHide.setOnAction((event) -> main.doDetailSlide(false));

		content = new ChannelDetailPaneContent(widthProperty(), btnHide.widthProperty());

		main.getDetailChannel().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				LOGGER.trace("detail channel changed: " + newValue);
				final ProgressIndicator pi = new ProgressIndicator();

				pi.setPrefSize(100, 100);
				pi.setMaxSize(100, 100);
				pi.setMinSize(100, 100);

				setCenter(pi);

				final DetailPaneUpdateService service = new DetailPaneUpdateService(newValue, content);
				service.setOnSucceeded(event -> setCenter(content));
				service.setOnFailed(event -> LOGGER.error("Error building Detail pane", event.getSource().getException()));
				service.start();

			}
		});

		setLeft(btnHide);
	}
}
