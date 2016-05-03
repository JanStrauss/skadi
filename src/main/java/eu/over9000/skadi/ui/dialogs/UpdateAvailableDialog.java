/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
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

import eu.over9000.skadi.service.helper.RemoteVersionResult;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.apache.commons.io.FileUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateAvailableDialog extends Alert {
	public static final ButtonType UPDATE_BUTTON_TYPE = new ButtonType("Download new version", ButtonData.OK_DONE);
	public static final ButtonType IGNORE_BUTTON_TYPE = new ButtonType("Not now", ButtonData.CANCEL_CLOSE);

	public UpdateAvailableDialog(final RemoteVersionResult newVersion) {
		super(AlertType.INFORMATION, null, UPDATE_BUTTON_TYPE, IGNORE_BUTTON_TYPE);

		setTitle("Update available");
		setHeaderText(newVersion.getVersion() + " is available");

		final Label lbChangeLog = new Label("Changelog:");
		final TextArea taChangeLog = new TextArea(newVersion.getChangeLog());
		taChangeLog.setEditable(false);
		taChangeLog.setWrapText(true);

		final Label lbSize = new Label("Size:");
		final Label lbSizeValue = new Label(FileUtils.byteCountToDisplaySize(newVersion.getSize()));

		final Label lbPublished = new Label("Published");
		final Label lbPublishedValue = new Label(ZonedDateTime.parse(newVersion.getPublished()).format(DateTimeFormatter.RFC_1123_DATE_TIME));

		final GridPane grid = new GridPane();
		final RowConstraints vAlign = new RowConstraints();
		vAlign.setValignment(VPos.TOP);
		grid.getRowConstraints().add(vAlign);
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(lbChangeLog, 0, 0);
		grid.add(taChangeLog, 1, 0);
		grid.add(lbPublished, 0, 1);
		grid.add(lbPublishedValue, 1, 1);
		grid.add(lbSize, 0, 2);
		grid.add(lbSizeValue, 1, 2);

		getDialogPane().setContent(grid);
	}
}
