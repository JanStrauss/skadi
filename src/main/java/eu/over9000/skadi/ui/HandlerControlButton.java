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

package eu.over9000.skadi.ui;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;
import eu.over9000.skadi.service.QualityRetrievalService;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;

public class HandlerControlButton {
    private final SplitMenuButton openStream;
    private final Button openChat;

    private final StreamHandler streamHandler;
    private final ChatHandler chatHandler;
    private final StatusBar sb;
    private final MenuItem worstItem;

    private Channel candidate;

    public HandlerControlButton(final ChatHandler chatHandler, final StreamHandler streamHandler, final ToolBar tb, final StatusBar sb) {
        this.streamHandler = streamHandler;
        this.chatHandler = chatHandler;
        this.sb = sb;

        openStream = new SplitMenuButton();
        openStream.setText("Stream: best");

        worstItem = new MenuItem("Stream: worst");
        worstItem.setOnAction(event -> openStreamWithQuality(StreamQuality.getWorstQuality()));

        openStream.getItems().add(worstItem);
        openChat = new Button("Chat");

        openStream.setDisable(true);
        openChat.setDisable(true);

        openStream.setOnAction(event -> openStreamWithQuality(StreamQuality.getBestQuality()));

        openChat.setOnAction(event -> {

            sb.setText("opening chat of " + candidate.getName());
            this.chatHandler.openChat(candidate);

        });

        tb.getItems().add(openStream);
        tb.getItems().add(openChat);
    }

    private void openStreamWithQuality(final StreamQuality quality) {
        sb.setText("opening '" + quality.getQuality() + "' stream of " + candidate.getName());
        streamHandler.openStream(candidate, quality);
    }

    public void setDisable(final boolean b) {
        openStream.setDisable(b);
        openChat.setDisable(b);
    }

    public void updateCandidate(final Channel candidate) {
        this.candidate = candidate;
        openStream.getItems().clear();
        openStream.getItems().add(worstItem);


        if ((candidate != null) && (candidate.isOnline() != null) && candidate.isOnline()) {
            final QualityRetrievalService service = new QualityRetrievalService(this::openStreamWithQuality, candidate);
            service.setOnSucceeded(event -> {
                openStream.getItems().clear();
                openStream.getItems().addAll(service.getValue());
                openStream.getItems().add(new SeparatorMenuItem());
                openStream.getItems().add(worstItem);

            });
            service.start();
        }
    }
}
