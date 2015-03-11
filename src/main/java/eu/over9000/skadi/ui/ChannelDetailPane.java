package eu.over9000.skadi.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import eu.over9000.skadi.remote.PanelDataRetriever;
import eu.over9000.skadi.util.ImageUtil;
import eu.over9000.skadi.util.PanelUtil;
import eu.over9000.skadi.util.TimeUtil;

public class ChannelDetailPane extends BorderPane {
	final ScrollPane scrollPane;
	
	public ChannelDetailPane(final MainWindow main) {
		
		final VBox detailPane = new VBox(5);
		this.scrollPane = new ScrollPane(detailPane);
		this.scrollPane.setFitToWidth(true);
		
		detailPane.setId("detailPane");
		detailPane.setPadding(new Insets(5));
		detailPane.setOpacity(1);
		detailPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		final VBox upperVBox = new VBox(5);
		
		final BorderPane bp_top = new BorderPane();
		final BorderPane bp_img = new BorderPane();
		
		final Label lbLogo = new Label();
		
		final Label lbName = new Label();
		lbName.setFont(new Font(24));
		
		final Label lbStatus = new Label();
		lbStatus.setFont(new Font(16));
		
		final Label lbPrev = new Label();
		
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
		final LineChart<Number, Number> viewerChart = new LineChart<>(xAxis, yAxis);
		viewerChart.setPrefHeight(180);
		viewerChart.setCreateSymbols(false);
		
		final Label lbAvg = new Label();
		final Label lbCurr = new Label();
		
		final Label lbGame = new Label();
		
		final Button btnHide = GlyphsDude.createIconButton(FontAwesomeIcons.ANGLE_DOUBLE_RIGHT);
		btnHide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnHide.setOnAction((event) -> {
			main.doDetailSlide(false);
		});
		
		upperVBox.getChildren().add(lbName);
		upperVBox.getChildren().add(lbStatus);
		
		bp_top.setLeft(lbLogo);
		bp_top.setCenter(upperVBox);
		bp_top.setRight(lbGame);
		
		bp_img.setLeft(lbPrev);
		bp_img.setCenter(viewerChart);
		
		final Separator bp_img_sep = new Separator();
		final FlowPane panelPane = new FlowPane(10, 10);
		
		detailPane.getChildren().add(bp_top);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(bp_img);
		detailPane.getChildren().add(bp_img_sep);
		detailPane.getChildren().add(lbCurr);
		detailPane.getChildren().add(lbAvg);
		detailPane.getChildren().add(new Separator());
		detailPane.getChildren().add(panelPane);
		
		panelPane.maxWidthProperty().bind(this.widthProperty().subtract(btnHide.widthProperty()).subtract(25));
		panelPane.minWidthProperty().bind(this.widthProperty().subtract(btnHide.widthProperty()).subtract(25));
		
		main.getDetailChannel().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				
				System.out.println("detail channel changed: " + newValue);
				lbName.textProperty().bind(Bindings.createStringBinding(() -> newValue.getName(), newValue.nameProperty()));
				lbStatus.textProperty().bind(Bindings.createStringBinding(() -> newValue.getTitle(), newValue.titleProperty()));
				lbCurr.textProperty().bind(Bindings.createStringBinding(() -> "current viewers: " + String.valueOf(newValue.getViewer()), newValue.viewerProperty()));
				lbAvg.textProperty().bind(Bindings.createStringBinding(() -> "average viewers: " + String.valueOf(newValue.getViewerHistoryAverage()), newValue.viewerHistoryAverageProperty()));
				lbGame.graphicProperty().bind(Bindings.createObjectBinding(() -> ImageUtil.getGameLogoFromTwitch(newValue.getGame()), newValue.gameProperty()));
				lbGame.tooltipProperty().bind(Bindings.createObjectBinding(() -> new Tooltip(newValue.getGame()), newValue.gameProperty()));
				lbPrev.graphicProperty().bind(Bindings.createObjectBinding(() -> ImageUtil.getPreviewFromTwitch(newValue), newValue.lastUpdatedProperty()));
				viewerChart.getData().clear();
				viewerChart.getData().add(new LineChart.Series<Number, Number>("viewers", newValue.getViewerHistory()));

				lbLogo.graphicProperty().bind(Bindings.createObjectBinding(() -> ImageUtil.getChannelLogo(newValue.getLogoURL()), newValue.logoURLProperty()));
				bp_img.visibleProperty().bind(newValue.onlineProperty());
				bp_img_sep.visibleProperty().bind(newValue.onlineProperty());
				
				panelPane.getChildren().clear();
				
				PanelDataRetriever.retrievePanels(newValue.getName()).forEach(p -> panelPane.getChildren().add(PanelUtil.buildPanel(p)));
				
			}
		});
		
		this.setLeft(btnHide);
		this.setCenter(this.scrollPane);
		
	}
}
