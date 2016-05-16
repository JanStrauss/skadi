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

package eu.over9000.skadi.ui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.ui.label.CopyableLabel;
import eu.over9000.skadi.util.TimeUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class ChannelDetailPaneContent extends ScrollPane {

	private final DoubleBinding widthBinding;

	private final Label lbLogo;
	private final CopyableLabel lbName;
	private final CopyableLabel lbStatus;
	private final ImageView ivPreview;
	private final LineChart<Number, Number> viewerChart;
	private final CopyableLabel lbAvg;
	private final CopyableLabel lbCurr;
	private final Label lbGame;
	private final CopyableLabel lbFollowers;
	private final CopyableLabel lbViews;
	private final CopyableLabel lbPartner;
	private final FlowPane panelPane;
	private final Button btOpenInBrowser;
	private final FlowPane emotePane;

	public ChannelDetailPaneContent(final ReadOnlyDoubleProperty widthPanel, final ReadOnlyDoubleProperty widthButton) {
		widthBinding = widthPanel.subtract(widthButton).subtract(25);

		final VBox detailPane = new VBox(5);
		setFitToWidth(true);

		detailPane.setId("detailPane");
		detailPane.setPadding(new Insets(5));
		//detailPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		final VBox upperVBox = new VBox(5);

		final BorderPane bp_top = new BorderPane();
		final BorderPane bp_img = new BorderPane();
		final BorderPane bp_info = new BorderPane();

		lbLogo = new Label();

		lbName = new CopyableLabel();
		lbName.setFont(new Font(24));

		lbStatus = new CopyableLabel();
		lbStatus.setFont(new Font(16));

		ivPreview = new ImageView();
		ivPreview.setFitWidth(640);
		ivPreview.setFitHeight(360);

		final NumberAxis xAxis = new NumberAxis();
		xAxis.setForceZeroInRange(false);
		xAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(final Number object) {
				return TimeUtil.getStringFromMillis(object);
			}

			@Override
			public Number fromString(final String string) {
				return null;
			}
		});
		final NumberAxis yAxis = new NumberAxis();
		yAxis.setForceZeroInRange(false);
		viewerChart = new LineChart<>(xAxis, yAxis);
		viewerChart.setPrefHeight(180);
		viewerChart.setCreateSymbols(false);


		lbAvg = new CopyableLabel();
		lbCurr = new CopyableLabel();

		lbFollowers = new CopyableLabel();
		lbViews = new CopyableLabel();
		lbPartner = new CopyableLabel();


		btOpenInBrowser = GlyphsDude.createIconButton(FontAwesomeIcon.EXTERNAL_LINK, "Open in Browser");

		emotePane = new FlowPane(5, 5);

		final HBox boxInfo1 = new HBox(10, new VBox(10, lbCurr, lbAvg, lbFollowers, lbViews, lbPartner, btOpenInBrowser), new Separator(Orientation.VERTICAL));
		final VBox boxInfo2 = new VBox(10, new Label("Subscriber Emotes:"), emotePane);

		bp_info.setLeft(boxInfo1);
		bp_info.setCenter(boxInfo2);

		lbGame = new Label();

		upperVBox.getChildren().add(lbName);
		upperVBox.getChildren().add(lbStatus);

		bp_top.setLeft(lbLogo);
		bp_top.setCenter(upperVBox);
		bp_top.setRight(lbGame);

		bp_img.setLeft(ivPreview);
		bp_img.setCenter(viewerChart);

		panelPane = new FlowPane(10, 10);

		detailPane.getChildren().add(bp_top);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(bp_img);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(bp_info);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(panelPane);

		panelPane.maxWidthProperty().bind(widthBinding);
		panelPane.minWidthProperty().bind(widthBinding);
		panelPane.prefWidthProperty().bind(widthBinding);

		setContent(detailPane);
	}

	public CopyableLabel getLbFollowers() {
		return lbFollowers;
	}

	public CopyableLabel getLbViews() {
		return lbViews;
	}

	public CopyableLabel getLbPartner() {
		return lbPartner;
	}

	public DoubleBinding getWidthBinding() {
		return widthBinding;
	}

	public Label getLbLogo() {
		return lbLogo;
	}

	public CopyableLabel getLbName() {
		return lbName;
	}

	public CopyableLabel getLbStatus() {
		return lbStatus;
	}

	public ImageView getIvPreview() {
		return ivPreview;
	}

	public LineChart<Number, Number> getViewerChart() {
		return viewerChart;
	}

	public CopyableLabel getLbAvg() {
		return lbAvg;
	}

	public CopyableLabel getLbCurr() {
		return lbCurr;
	}

	public Label getLbGame() {
		return lbGame;
	}

	public FlowPane getPanelPane() {
		return panelPane;
	}

	public FlowPane getEmotePane() {
		return emotePane;
	}

	public Button getBtOpenInBrowser() {
		return btOpenInBrowser;
	}
}
