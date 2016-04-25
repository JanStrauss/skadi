/*
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

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.Panel;
import eu.over9000.skadi.remote.EmoteDataRetriever;
import eu.over9000.skadi.remote.PanelDataRetriever;
import eu.over9000.skadi.ui.ChannelDetailPaneContent;
import eu.over9000.skadi.util.*;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Tooltip;

import java.nio.channels.Pipe;
import java.text.NumberFormat;

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

				content.getLbName().textProperty().bind(Bindings.createStringBinding(channel::getName, channel.nameProperty()));
				content.getLbStatus().textProperty().bind(Bindings.createStringBinding(channel::getTitle, channel.titleProperty()));
				content.getLbCurr().textProperty().bind(Bindings.createStringBinding(() -> "current viewers: " + formatter.format(channel.getViewer()), channel.viewerProperty()));
				content.getLbAvg().textProperty().bind(Bindings.createStringBinding(() -> "average viewers: " + formatter.format(channel.getViewerHistoryAverage()), channel.viewerHistoryAverageProperty()));

				content.getLbGame().graphicProperty().bind(Bindings.createObjectBinding(() -> ImageUtil.getGameLogoFromTwitch(channel.getGame()), channel.gameProperty()));
				content.getLbGame().tooltipProperty().bind(Bindings.createObjectBinding(() -> new Tooltip(channel.getGame()), channel.gameProperty()));

				content.getLbFollowers().textProperty().bind(Bindings.createStringBinding(() -> "followers: " + formatter.format(channel.getFollowers()), channel.followersProperty()));

				content.getLbViews().textProperty().bind(Bindings.createStringBinding(() -> "total views: " + formatter.format(channel.getViews()), channel.viewsProperty()));

				content.getLbPartner().textProperty().bind(Bindings.createStringBinding(() -> "partner: " + (channel.getPartner() ? "yes" : "no"), channel.partnerProperty()));

				content.getIvPreview().imageProperty().bind(Bindings.createObjectBinding(channel::getPreview, channel.previewProperty()));

				content.getBtOpenInBrowser().setOnAction(event -> DesktopUtil.openWebpage(StringUtil.toStreamURL(channel)));

				content.getViewerChart().getData().clear();
				content.getViewerChart().getData().add(new LineChart.Series<>("viewers", channel.getViewerHistory()));

				content.getLbLogo().graphicProperty().bind(Bindings.createObjectBinding(() -> ImageUtil.getChannelLogo(channel.getLogoURL()), channel.logoURLProperty()));

				content.getPanelPane().getChildren().clear();
				content.getEmotePane().getChildren().clear();

				PanelUtil.buildPanels(PanelDataRetriever.retrievePanels(channel.getName())).forEach(panel -> content.getPanelPane().getChildren().add(panel));
				EmoteUtil.buildEmotePanel(EmoteDataRetriever.retrieveEmotes(channel.getName())).forEach(box -> content.getEmotePane().getChildren().add(box));

				return null;
			}
		};
	}
}
