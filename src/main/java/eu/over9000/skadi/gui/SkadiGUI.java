package eu.over9000.skadi.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import eu.over9000.skadi.SkadiMain;

public class SkadiGUI extends Application {
	
	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("Skadi");
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(final WindowEvent e) {
				e.consume();
				Platform.exit();
				System.exit(0);
			}
		});
		
		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		final Scene scene = new Scene(grid, 340, 200);
		stage.setScene(scene);
		
		final Label channelURLLabel = new Label("URL");
		final TextField channelURLField = new TextField();
		final Button channelURLButton = new Button("Open Stream & Chat");
		
		final HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(channelURLButton);
		
		grid.add(channelURLLabel, 0, 0);
		grid.add(channelURLField, 1, 0);
		grid.add(hbBtn, 1, 1);
		
		channelURLButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(final ActionEvent event) {
				if (channelURLField.getText().isEmpty()) {
					return;
				}
				
				SkadiMain.getInstance().addAndOpenNewChannel(channelURLField.getText());
			}
		});
		
		stage.show();
	}
}