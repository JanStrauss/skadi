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

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import org.controlsfx.control.GridCell;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.ui.ChannelGrid;
import eu.over9000.skadi.ui.MainWindow;

public class ChannelGridCell extends GridCell<Channel> {

	public static final String GRID_BOX = "grid-box";

	private final Label name;
	private final Label title;
	private final Label viewer;
	private final Label game;

	private final ImageView imageView;
	private final VBox vBox;

	private final ChannelGrid grid;
	private final MainWindow mainWindow;

	public ChannelGridCell(final ChannelGrid grid, final MainWindow mainWindow) {
		this.grid = grid;
		this.mainWindow = mainWindow;

		getStyleClass().add(GRID_BOX);

		name = new Label();
		name.setPadding(new Insets(5));
		name.setFont(new Font(12));
		name.setStyle("-fx-font-weight: bold");

		title = new Label();
		title.setStyle("-fx-font-weight: bold");

		viewer = new Label();
		game = new Label();

		imageView = new ImageView();
		imageView.setFitWidth(200);
		imageView.setPreserveRatio(true);

		final VBox vBoxSub = new VBox(title, viewer, game);
		vBoxSub.setPadding(new Insets(5));

		vBox = new VBox(name, imageView, vBoxSub);

		setOnMouseClicked(event -> {
			if (isEmpty() || getItem() == null) {
				return;
			}

			if (event.getButton() == MouseButton.MIDDLE) {
				mainWindow.openStream(getItem());
			}
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() == 1) {
					updateSelected(true);
					grid.select(getItem());
					grid.updateItems();
				} else {
					mainWindow.openDetailPage(getItem());
				}
			}
		});
	}

	@Override
	protected void updateItem(final Channel item, final boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			imageView.imageProperty().unbind();
			setGraphic(null);
			setText(null);
		} else {
			updateSelected(grid.isSelected(item));

			name.textProperty().bind(item.nameProperty());

			title.textProperty().bind(item.titleProperty());

			viewer.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(item.getViewer()), item.viewerProperty()));
			viewer.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));

			game.textProperty().bind(item.gameProperty());
			game.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));

			imageView.imageProperty().bind(item.previewProperty());

			setGraphic(vBox);

			setText(null);
		}
	}
}
