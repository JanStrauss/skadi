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

package eu.over9000.skadi.ui.cells;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.ui.ChannelGrid;
import eu.over9000.skadi.ui.MainWindow;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.GridCell;

public class ChannelGridCell extends GridCell<Channel> {

	public static final String GRID_BOX = "grid-box";

	private final Label name;
	private final Label title;
	private final Label viewer;
	private final Label game;

	private final ImageView imageView;
	private final VBox vBox;

	private final ChannelGrid grid;
	private Channel lastItem;
	private WeakChangeListener<Image> weakPreviewListener;


	public ChannelGridCell(final ChannelGrid grid, final MainWindow mainWindow) {
		this.grid = grid;

		getStyleClass().add(GRID_BOX);

		name = new Label();
		name.setPadding(new Insets(5));
		name.setFont(new Font(12));

		title = new Label();
		title.setStyle("-fx-font-weight: bold");

		viewer = new Label();
		game = new Label();

		imageView = new ImageView();
		imageView.fitWidthProperty().bind(mainWindow.scalingGridCellWidthProperty());
		imageView.setPreserveRatio(true);

		final VBox vBoxSub = new VBox(title, viewer, game);
		vBoxSub.setPadding(new Insets(5));

		vBox = new VBox(name, imageView, vBoxSub);

		setOnMouseClicked(event -> {
			if (isEmpty() || getItem() == null) {
				return;
			}

			if (event.getButton() == MouseButton.MIDDLE) {
				doSelectionUpdate(grid, mainWindow);
				mainWindow.openStream(grid.getSelected());
			}
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() == 1) {
					doSelectionUpdate(grid, mainWindow);
				} else {
					mainWindow.openDetailPage(getItem());
				}
			}
		});
	}

	private void doSelectionUpdate(final ChannelGrid grid, final MainWindow mainWindow) {
		mainWindow.onSelection(getItem());
		updateSelected(true);
		grid.select(getItem());
		grid.updateItems();
	}

	@Override
	protected void updateItem(final Channel item, final boolean empty) {
		super.updateItem(item, empty);

		if (lastItem != null && !lastItem.equals(item)) {
			lastItem.previewProperty().removeListener(weakPreviewListener);
		}
		lastItem = item;

		if (empty || item == null) {
			imageView.setImage(null);
			setGraphic(null);
			setText(null);
		} else {
			updateSelected(grid.isSelected(item));

			if (item.isOnline() != null) {
				if (item.isOnline()) {
					name.setStyle("-fx-font-weight: bold;-fx-text-fill: green");
				} else {
					name.setStyle("-fx-font-weight: bold;-fx-text-fill: red");
				}
			}

			name.textProperty().bind(item.nameProperty());

			title.textProperty().bind(item.titleProperty());

			viewer.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(item.getViewer()), item.viewerProperty()));
			viewer.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));

			game.textProperty().bind(item.gameProperty());
			game.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));

			weakPreviewListener = new WeakChangeListener<>((observable, oldValue, newValue) -> imageView.setImage(newValue));
			item.previewProperty().addListener(weakPreviewListener);

			imageView.setImage(item.getPreview());

			setGraphic(vBox);

			setText(null);
		}
	}
}
