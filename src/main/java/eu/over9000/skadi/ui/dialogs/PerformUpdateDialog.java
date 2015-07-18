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

package eu.over9000.skadi.ui.dialogs;

import java.io.File;
import java.nio.file.Paths;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.service.DownloadService;
import eu.over9000.skadi.service.helper.RemoteVersionResult;

public class PerformUpdateDialog extends Dialog<File> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerformUpdateDialog.class);

	private final ObjectProperty<File> chosen;
	private DownloadService downloadService;

	public PerformUpdateDialog(RemoteVersionResult newVersion) {
		this.chosen = new SimpleObjectProperty<>(Paths.get(SystemUtils.USER_HOME, newVersion.getVersion() + ".jar").toFile());

		this.setHeaderText("Updating to " + newVersion.getVersion());
		this.setTitle("Skadi Updater");
		this.getDialogPane().getStyleClass().add("alert");
		this.getDialogPane().getStyleClass().add("information");

		final ButtonType restartButtonType = new ButtonType("Start New Version", ButtonBar.ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(restartButtonType, ButtonType.CANCEL);


		Node btn = this.getDialogPane().lookupButton(restartButtonType);
		btn.setDisable(true);

		Label lbPath = new Label("Save as");
		TextField tfPath = new TextField();
		tfPath.textProperty().bind(Bindings.createStringBinding(() -> this.chosen.get().getAbsolutePath(), this.chosen));
		tfPath.setPrefColumnCount(40);
		tfPath.setEditable(false);

		Button btChangePath = GlyphsDude.createIconButton(FontAwesomeIcon.FOLDER_OPEN, "Browse...");
		btChangePath.setOnAction(event -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Save downloaded jar..");
			fc.setInitialFileName(this.chosen.getValue().getName());
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar File", ".jar"));
			fc.setInitialDirectory(this.chosen.getValue().getParentFile());
			File selected = fc.showSaveDialog(this.getOwner());
			if (selected != null) {
				this.chosen.set(selected);
			}
		});

		ProgressBar pbDownload = new ProgressBar(0);
		pbDownload.setDisable(true);
		pbDownload.setMaxWidth(Double.MAX_VALUE);
		Label lbDownload = new Label("Download");
		Label lbDownloadValue = new Label();
		Button btDownload = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD, "Start");
		btDownload.setMaxWidth(Double.MAX_VALUE);
		btDownload.setOnAction(event -> {
			btChangePath.setDisable(true);
			btDownload.setDisable(true);

			this.downloadService = new DownloadService(newVersion.getDownloadURL(), this.chosen.getValue());

			lbDownloadValue.textProperty().bind(this.downloadService.messageProperty());
			pbDownload.progressProperty().bind(this.downloadService.progressProperty());

			this.downloadService.setOnSucceeded(dlEvent -> btn.setDisable(false));
			this.downloadService.setOnFailed(dlFailed -> {
				LOGGER.error("new version download failed", dlFailed.getSource().getException());
				lbDownloadValue.textProperty().unbind();
				lbDownloadValue.setText("Download failed, check log file for details.");
			});

			this.downloadService.start();
		});


		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(lbPath, 0, 0);
		grid.add(tfPath, 1, 0);
		grid.add(btChangePath, 2, 0);
		grid.add(new Separator(), 0, 1, 3, 1);
		grid.add(lbDownload, 0, 2);
		grid.add(pbDownload, 1, 2);
		grid.add(btDownload, 2, 2);
		grid.add(lbDownloadValue, 1, 3);


		this.getDialogPane().setContent(grid);


		this.setResultConverter(btnType -> {
			if (btnType == restartButtonType) {
				return this.chosen.getValue();
			}

			if (btnType == ButtonType.CANCEL) {
				if (this.downloadService.isRunning()) {
					this.downloadService.cancel();
				}
			}

			return null;
		});

	}
}
