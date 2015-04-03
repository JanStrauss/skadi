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

package eu.over9000.skadi.util;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.service.ImageRetrievalService;

public class NotificationUtil {

	private static Stage dummyStage;

	public static void init() {
		dummyStage = buildDummyStage();
	}

	public static void showOnlineNotification(final Channel channel) {

		if (StateContainer.getInstance().isDisplayNotifications()) {
			final ImageRetrievalService service = new ImageRetrievalService(channel.getLogoURL(), 72, 72);
			service.setOnSucceeded(event -> {
				final ImageView img = (ImageView) event.getSource().getValue();

				Notifications.create().text(channel.getName() + " went live").title("Skadi").hideAfter(Duration.seconds(5)).graphic(img).show();
			});
			service.start();
		}
	}

	public static void onShutdown() {
		dummyStage.hide();
	}

	private static Stage buildDummyStage() {
		final Stage dummy = new Stage(StageStyle.UTILITY);
		dummy.setScene(new Scene(new Pane(), 1, 1));
		dummy.setMaxHeight(1);
		dummy.setMaxWidth(1);
		dummy.setX(Double.MAX_VALUE - 1);
		dummy.show();
		return dummy;
	}
}
