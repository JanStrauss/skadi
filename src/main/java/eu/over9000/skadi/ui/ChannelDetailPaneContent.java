/*******************************************************************************
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
 ******************************************************************************/
package eu.over9000.skadi.ui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import eu.over9000.skadi.util.TimeUtil;

public class ChannelDetailPaneContent extends ScrollPane {
	
	private DoubleBinding widthBinding;
	
	private final Label lbLogo;
	private final Label lbName;
	private final Label lbStatus;
	private final Label lbPrev;
	private final LineChart<Number, Number> viewerChart;
	private final Label lbAvg;
	private final Label lbCurr;
	private final Label lbGame;
	private final Label lbFollowers;
	private final Label lbViews;
	private final Label lbPartner;
	private final FlowPane panelPane;

	public ChannelDetailPaneContent(final ReadOnlyDoubleProperty widthPanel, final ReadOnlyDoubleProperty widthButton) {
		this.widthBinding = widthPanel.subtract(widthButton).subtract(25);
		
		final VBox detailPane = new VBox(5);
		this.setFitToWidth(true);
		
		detailPane.setId("detailPane");
		detailPane.setPadding(new Insets(5));
		detailPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		final VBox upperVBox = new VBox(5);
		
		final BorderPane bp_top = new BorderPane();
		final BorderPane bp_img = new BorderPane();
		
		this.lbLogo = new Label();
		
		this.lbName = new Label();
		this.lbName.setFont(new Font(24));
		
		this.lbStatus = new Label();
		this.lbStatus.setFont(new Font(16));
		
		this.lbPrev = new Label();
		this.lbPrev.setPrefSize(320, 180);
		
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
		this.viewerChart = new LineChart<>(xAxis, yAxis);
		this.viewerChart.setPrefHeight(180);
		this.viewerChart.setCreateSymbols(false);
		
		this.lbAvg = new Label();
		this.lbCurr = new Label();

		this.lbFollowers = new Label();
		this.lbViews = new Label();
		this.lbPartner = new Label();
		
		this.lbGame = new Label();

		upperVBox.getChildren().add(this.lbName);
		upperVBox.getChildren().add(this.lbStatus);
		
		bp_top.setLeft(this.lbLogo);
		bp_top.setCenter(upperVBox);
		bp_top.setRight(this.lbGame);
		
		bp_img.setLeft(this.lbPrev);
		bp_img.setCenter(this.viewerChart);
		
		final Separator bp_img_sep = new Separator();
		this.panelPane = new FlowPane(10, 10);
		
		detailPane.getChildren().add(bp_top);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(bp_img);
		detailPane.getChildren().add(bp_img_sep);
		detailPane.getChildren().add(this.lbCurr);
		detailPane.getChildren().add(this.lbAvg);
		detailPane.getChildren().add(this.lbFollowers);
		detailPane.getChildren().add(this.lbViews);
		detailPane.getChildren().add(this.lbPartner);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(this.panelPane);
		
		this.panelPane.maxWidthProperty().bind(this.widthBinding);
		this.panelPane.minWidthProperty().bind(this.widthBinding);
		this.panelPane.prefWidthProperty().bind(this.widthBinding);
		
		this.setContent(detailPane);
	}
	
	public Label getLbFollowers() {
		return this.lbFollowers;
	}
	
	public Label getLbViews() {
		return this.lbViews;
	}
	
	public Label getLbPartner() {
		return this.lbPartner;
	}
	
	public DoubleBinding getWidthBinding() {
		return this.widthBinding;
	}
	
	public Label getLbLogo() {
		return this.lbLogo;
	}
	
	public Label getLbName() {
		return this.lbName;
	}
	
	public Label getLbStatus() {
		return this.lbStatus;
	}
	
	public Label getLbPrev() {
		return this.lbPrev;
	}
	
	public LineChart<Number, Number> getViewerChart() {
		return this.viewerChart;
	}
	
	public Label getLbAvg() {
		return this.lbAvg;
	}
	
	public Label getLbCurr() {
		return this.lbCurr;
	}
	
	public Label getLbGame() {
		return this.lbGame;
	}
	
	public FlowPane getPanelPane() {
		return this.panelPane;
	}
}
