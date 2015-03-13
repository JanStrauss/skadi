package eu.over9000.skadi.util;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.service.ImageRetrievalService;

public class NotificationUtil {
	
	private static final Stage dummyStage = NotificationUtil.buildDummyStage();
	
	public static void showOnlineNotification(final Channel channel) {
		
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
	
	public static void onShutdown() {
		NotificationUtil.dummyStage.hide();
	}

	private static Stage buildDummyStage() {
		final Stage dummy = new Stage(StageStyle.UTILITY);
		final Pane root = new Pane();
		root.setStyle("-fx-background-color: TRANSPARENT");
		final Scene scene = new Scene(root, 1, 1);
		scene.setFill(Color.TRANSPARENT);
		dummy.setScene(scene);
		dummy.setMaxHeight(0);
		dummy.setMaxWidth(0);
		dummy.setX(Double.MAX_VALUE - 1);
		dummy.toBack();
		dummy.show();
		return dummy;
	}
}