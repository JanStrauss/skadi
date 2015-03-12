package eu.over9000.skadi.util;

import javafx.scene.image.ImageView;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import eu.over9000.skadi.io.StateContainer;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.service.ImageRetrievalService;

public class NotificationUtil {
	public static void showNotification(final Channel channel) {

		if (StateContainer.getInstance().isDisplayNotifications()) {
			final ImageRetrievalService service = new ImageRetrievalService(channel.getLogoURL(), 72, 72);
			service.setOnSucceeded(event -> {
				final ImageView img = (ImageView) event.getSource().getValue();

				Notifications.create().text(channel.getName() + " went live").title("Skadi")
				.hideAfter(Duration.seconds(5)).graphic(img).show();
			});
			service.start();
		}
		
	}
}