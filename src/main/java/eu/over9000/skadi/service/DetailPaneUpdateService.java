package eu.over9000.skadi.service;

import java.text.NumberFormat;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Tooltip;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.PanelDataRetriever;
import eu.over9000.skadi.ui.ChannelDetailPaneContent;
import eu.over9000.skadi.util.ImageUtil;
import eu.over9000.skadi.util.PanelUtil;

public class DetailPaneUpdateService extends Service<Void> {

	private final Channel channel;
	private final ChannelDetailPaneContent content;

	public DetailPaneUpdateService(final Channel channel, final ChannelDetailPaneContent content) {
		this.channel = channel;
		this.content = content;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				final Channel channel = DetailPaneUpdateService.this.channel;
				final ChannelDetailPaneContent content = DetailPaneUpdateService.this.content;
				final NumberFormat formatter = NumberFormat.getIntegerInstance();
				
				content.getLbName()
				        .textProperty()
				        .bind(Bindings.createStringBinding(() -> channel.getName(),
				                DetailPaneUpdateService.this.channel.nameProperty()));
				content.getLbStatus()
				        .textProperty()
				        .bind(Bindings.createStringBinding(() -> channel.getTitle(),
				                DetailPaneUpdateService.this.channel.titleProperty()));
				content.getLbCurr()
				        .textProperty()
				        .bind(Bindings.createStringBinding(
				                () -> "current viewers: " + formatter.format(channel.getViewer()),
				                channel.viewerProperty()));
				content.getLbAvg()
				        .textProperty()
				        .bind(Bindings.createStringBinding(
						() -> "average viewers: " + formatter.format(channel.getViewerHistoryAverage()),
				                channel.viewerHistoryAverageProperty()));
				
				content.getLbGame()
				        .graphicProperty()
				        .bind(Bindings.createObjectBinding(() -> ImageUtil.getGameLogoFromTwitch(channel.getGame()),
				                channel.gameProperty()));
				content.getLbGame()
				        .tooltipProperty()
				        .bind(Bindings.createObjectBinding(() -> new Tooltip(channel.getGame()), channel.gameProperty()));
				
				content.getLbFollowers()
				.textProperty()
				.bind(Bindings.createStringBinding(
						() -> "followers: " + formatter.format(channel.getFollowers()),
						channel.followersProperty()));

				content.getLbViews()
				        .textProperty()
				        .bind(Bindings.createStringBinding(
						() -> "total views: " + formatter.format(channel.getViews()), channel.viewsProperty()));
				
				content.getLbPartner()
				.textProperty()
				.bind(Bindings.createStringBinding(() -> "partner: " + channel.getPartner(),
						channel.partnerProperty()));
				
				/*
				 * Preview
				 */
				content.getLbPrev().graphicProperty().bind(Bindings.createObjectBinding(() -> {
					channel.getLastUpdated();
					return ImageUtil.getPreviewFromTwitch(channel);
				}, channel.lastUpdatedProperty()));

				content.getViewerChart().getData().clear();
				content.getViewerChart().getData()
				        .add(new LineChart.Series<Number, Number>("viewers", channel.getViewerHistory()));

				content.getLbLogo()
				        .graphicProperty()
				        .bind(Bindings.createObjectBinding(() -> ImageUtil.getChannelLogo(channel.getLogoURL()),
				                channel.logoURLProperty()));
				
				content.getPanelPane().getChildren().clear();
				PanelDataRetriever.retrievePanels(channel.getName()).forEach(
				        panel -> content.getPanelPane().getChildren().add(PanelUtil.buildPanel(panel)));

				return null;
			}
		};
	}
}
