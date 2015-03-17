package eu.over9000.skadi.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import eu.over9000.skadi.service.DetailPaneUpdateService;

public class ChannelDetailPane extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDetailPane.class);

	ChannelDetailPaneContent content;
	
	public ChannelDetailPane(final MainWindow main) {
		
		final Button btnHide = GlyphsDude.createIconButton(FontAwesomeIcons.ANGLE_DOUBLE_RIGHT);
		btnHide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnHide.setOnAction((event) -> {
			main.doDetailSlide(false);
		});
		
		this.content = new ChannelDetailPaneContent(this.widthProperty(), btnHide.widthProperty());
		
		main.getDetailChannel().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				ChannelDetailPane.LOGGER.trace("detail channel changed: " + newValue);
				final ProgressIndicator pi = new ProgressIndicator();
				
				pi.setPrefSize(100, 100);
				pi.setMaxSize(100, 100);
				pi.setMinSize(100, 100);
				
				this.setCenter(pi);
				
				final DetailPaneUpdateService service = new DetailPaneUpdateService(newValue, this.content);
				service.setOnSucceeded(event -> {
					this.setCenter(this.content);
				});
				service.setOnFailed(event -> {
					ChannelDetailPane.LOGGER.error("Error building Detail pane", event.getSource().getException());
				});
				service.start();
				
			}
		});

		this.setLeft(btnHide);
	}
}
