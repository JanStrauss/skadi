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


import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.util.DesktopUtil;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URI;

public class AuthorizationDialog extends Dialog<String> {

	private final StateContainer state;

	private TextField tfToken = new TextField();

	public AuthorizationDialog(final StateContainer container) {
		state = container;

		setTitle("Skadi Authorization");
		setHeaderText(null);
		setGraphic(null);

		final ButtonType saveButtonType = new ButtonType("Use Token", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		getDialogPane().setContent(buildAuthPane());

		setResultConverter(btn -> {
			if (btn == saveButtonType) {
				return tfToken.getText();
			} else {
				return null;
			}
		});
	}

	private GridPane buildAuthPane() {

		final GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(10);


		// Step 1: Btn
		final Button btOpenTwitchPage = GlyphsDude.createIconButton(FontAwesomeIcon.EXTERNAL_LINK, "Open Twitch Authorization Page");
		btOpenTwitchPage.setOnAction(event -> {
			final URI authUrl = TwitchUtil.buildAuthUrl();
			DesktopUtil.openWebpage(authUrl);
		});

		pane.add(new Label("Step 1: "), 0, 0);
		pane.add(new Label("Press the Start-Button to open the Twitch authorization page: "), 1, 0);
		pane.add(btOpenTwitchPage, 2, 0);

		// Step 2: desc twitch auth
		pane.add(new Label("Step 2: "), 0, 1);
		pane.add(new Label("Follow the instructions on the Twitch authorization page"), 1, 1);

		// Step 3: Result page copy auth token#
		pane.add(new Label("Step 3: "), 0, 2);
		pane.add(new Label("After you authorized Skadi, you will be redirected to the Skadi authorization page"), 1, 2);

		// Step 4: Paste auth token
		pane.add(new Label("Step 4: "), 0, 3);
		pane.add(new Label("Paste the Authorization token from the Skadi authorization page here: "), 1, 3);
		pane.add(tfToken, 2, 3);

		// Step 5: Verify
		//TODO
		return pane;
	}
}
