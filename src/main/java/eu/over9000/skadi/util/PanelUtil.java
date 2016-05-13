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

package eu.over9000.skadi.util;

import eu.over9000.cathode.data.PanelData;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Markdown handling based on bitbucket.org/shemnon/flowdown/
 */
public class PanelUtil {

	public static final String STYLE_CLASS_EMPH = "md-emph";
	public static final String STYLE_CLASS_STRONG = "md-strong";
	public static final String STYLE_CLASS_STRIKE = "md-strike";
	public static final String STYLE_CLASS_HEADER = "md-header";
	public static final String STYLE_CLASS_HEADER_BASE = "md-header-";
	public static final String STYLE_CLASS_VERBATIM = "md-verbatim";
	public static final String STYLE_CLASS_PARA = "md-para";
	public static final String STYLE_CLASS_BLOCKQUOTE = "md-blockquote";
	public static final String STYLE_CLASS_ORDERED_LIST = "md-ordered-list";
	public static final String STYLE_CLASS_UNORDERED_LIST = "md-unordered-list";
	public static final String STYLE_CLASS_BULLET = "md-bullet";
	public static final String STYLE_CLASS_LIST_CONTENT = "md-list-content";
	public static final String STYLE_CLASS_SEPARATOR = "md-separator";

	private static final Logger LOGGER = LoggerFactory.getLogger(PanelUtil.class);

	public static VBox buildPanel(final PanelData panel) {
		final VBox box = new VBox();
		box.setMaxWidth(200);
		final Label lbTitle = new Label(panel.getTitle());
		lbTitle.setFont(new Font(18));
		box.getChildren().add(lbTitle);

		if ((panel.getLink() != null) && !panel.getLink().isEmpty() && (panel.getImage() != null) && !panel.getImage().isEmpty()) {
			final ImageView img = new ImageView(ImageUtil.getImageInternal(panel.getImage()));
			img.setPreserveRatio(true);
			img.setFitWidth(200);
			final Hyperlink banner = new Hyperlink(null, img);
			banner.setTooltip(new Tooltip(panel.getLink()));
			banner.setOnAction(event -> DesktopUtil.openWebpage(panel.getLink()));

			box.getChildren().add(banner);
		} else if ((panel.getImage() != null) && !panel.getImage().isEmpty()) {
			final ImageView img = new ImageView(ImageUtil.getImageInternal(panel.getImage()));
			img.setPreserveRatio(true);
			img.setFitWidth(200);
			box.getChildren().add(img);
		}
		if ((panel.getDescription() != null) && !panel.getDescription().isEmpty()) {
			box.getChildren().add(parseDescriptionFromMarkdown(panel.getDescription()));
		}

		return box;
	}

	private static VBox parseDescriptionFromMarkdown(final String markdown) {

		final VBox result = new VBox();
		final PegDownProcessor processor = new PegDownProcessor(Extensions.STRIKETHROUGH | Extensions.FENCED_CODE_BLOCKS);

		final RootNode rootNode = processor.parseMarkdown(markdown.toCharArray());

		// PanelUtil.visit(rootNode, "");

		final MarkdownVisitor visitor = new MarkdownVisitor();

		visitor.pushNode(result);

		rootNode.accept(visitor);

		result.getStylesheets().add("/styles/markdown.css");
		result.setMaxWidth(200);
		result.layout();

		return result;
	}

	/**
	 * Debug: print the ast
	 */
	@SuppressWarnings("unused")
	private static void visit(final Node node, final String intend) {
		LOGGER.debug(intend + node);
		node.getChildren().forEach(c -> visit(c, intend + " "));
	}

	private static class MarkdownVisitor implements Visitor {

		final Set<String> cssClasses = new TreeSet<>();
		final LinkedList<Integer> listCount = new LinkedList<>();
		private final Deque<Pane> nodeStack = new LinkedList<>();
		private Pane currentCollector;
		private boolean isHyperlinkChild = false;
		private String currentHyperlinkURL = null;

		void buildLinkNode(String text, final String url) {
			while (text.endsWith("\n")) {
				text = text.substring(0, text.length() - 1);
			}
			final Hyperlink link = new Hyperlink(text);
			link.setMaxWidth(200);
			link.setTooltip(new Tooltip(url));
			link.setWrapText(true);
			link.setOnAction(event -> DesktopUtil.openWebpage(url));
			currentCollector.getChildren().add(link);
		}

		void buildTextNode(String text) {
			while (text.endsWith("\n")) {
				text = text.substring(0, text.length() - 1);
			}
			final Text textNode = new Text(text);
			textNode.getStyleClass().setAll(cssClasses);
			textNode.getStyleClass().add("md-text");
			currentCollector.getChildren().add(textNode);
		}

		public void popNode() {
			if (!nodeStack.isEmpty()) {
				nodeStack.pop();
			}
			currentCollector = nodeStack.peek();
		}

		public void pushNode(final Pane n) {
			nodeStack.push(n);
			currentCollector = n;
		}

		void visitChildren(final Node node) {
			if (node == null) {
				return; // defensive parsing
			}
			for (final Node child : node.getChildren()) {
				child.accept(this);
			}
		}

		void startListBox(final String cssClass) {
			final VBox vbox = new VBox();
			vbox.getStyleClass().setAll(cssClasses);
			vbox.getStyleClass().add(cssClass);

			currentCollector.getChildren().add(vbox);
			vbox.setMinHeight(Region.USE_PREF_SIZE);
			vbox.setMaxHeight(Region.USE_PREF_SIZE);

			pushNode(vbox);
		}

		void startListRow(final String bullet) {
			final Text bt = new Text(bullet);
			bt.setTextAlignment(TextAlignment.RIGHT);
			bt.setTextOrigin(VPos.BASELINE);
			bt.getStyleClass().setAll(cssClasses);
			bt.getStyleClass().add(STYLE_CLASS_BULLET);

			final VBox bulletContent = new VBox();
			bulletContent.setMinHeight(Region.USE_PREF_SIZE);
			bulletContent.setMaxHeight(Region.USE_PREF_SIZE);
			bulletContent.getStyleClass().setAll(cssClasses);
			bulletContent.getStyleClass().add(STYLE_CLASS_LIST_CONTENT);

			final HBox hb = new HBox();
			hb.setMinHeight(Region.USE_PREF_SIZE);
			hb.setMaxHeight(Region.USE_PREF_SIZE);
			hb.setAlignment(Pos.BASELINE_LEFT);

			hb.getChildren().setAll(bt, bulletContent);

			currentCollector.getChildren().add(hb);

			pushNode(bulletContent);
		}

		void stopListBox() {
			popNode();
		}

		void stopListRow() {
			popNode();
		}

		@Override
		public void visit(final AbbreviationNode node) {
		}

		@Override
		public void visit(final AnchorLinkNode node) {
			buildTextNode(node.getText());
		}

		@Override
		public void visit(final AutoLinkNode node) {
			buildLinkNode(node.getText(), node.getText());
		}

		@Override
		public void visit(final BlockQuoteNode node) {
			final VBox vBox = new VBox();
			vBox.getStyleClass().setAll(cssClasses);
			vBox.getStyleClass().add(STYLE_CLASS_BLOCKQUOTE);
			currentCollector.getChildren().add(vBox);
			pushNode(vBox);
			visitChildren(node);
			popNode();
		}

		@Override
		public void visit(final BulletListNode node) {
			startListBox(STYLE_CLASS_UNORDERED_LIST);
			listCount.push(null);
			visitChildren(node);
			listCount.pop();
			stopListBox();
		}

		@Override
		public void visit(final CodeNode node) {
		}

		@Override
		public void visit(final DefinitionListNode node) {
			visitChildren(node);
		}

		@Override
		public void visit(final DefinitionNode node) {
			visitChildren(node);
		}

		@Override
		public void visit(final DefinitionTermNode node) {
			visitChildren(node);
		}

		@Override
		public void visit(final ExpImageNode node) {
		}

		@Override
		public void visit(final ExpLinkNode node) {
			isHyperlinkChild = true;
			currentHyperlinkURL = node.url;
			visitChildren(node);
			isHyperlinkChild = false;
		}

		@Override
		public void visit(final HeaderNode node) {
			cssClasses.add(STYLE_CLASS_HEADER_BASE + node.getLevel());
			cssClasses.add(STYLE_CLASS_HEADER);
			final TextFlow fp = new TextFlow();
			fp.getStyleClass().setAll(cssClasses);
			currentCollector.getChildren().add(fp);
			pushNode(fp);
			visitChildren(node);
			popNode();
			cssClasses.remove(STYLE_CLASS_HEADER_BASE + node.getLevel());
			cssClasses.remove(STYLE_CLASS_HEADER);
		}

		@Override
		public void visit(final HtmlBlockNode node) {
		}

		@Override
		public void visit(final InlineHtmlNode node) {
		}

		@Override
		public void visit(final ListItemNode node) {
			String bullet = "\u2022 ";
			if (listCount.peek() != null) {
				int i = listCount.pop();
				bullet = Integer.toString(i) + ". ";
				listCount.push(++i);
			}
			startListRow(bullet);
			visitChildren(node);
			stopListRow();
		}

		@Override
		public void visit(final MailLinkNode node) {
		}

		@Override
		public void visit(final Node node) {
		}

		@Override
		public void visit(final OrderedListNode node) {
			startListBox(STYLE_CLASS_ORDERED_LIST);
			listCount.push(1);
			visitChildren(node);
			listCount.pop();
			stopListBox();
		}

		@Override
		public void visit(final ParaNode node) {
			final VBox vbox = new VBox();
			vbox.getStyleClass().setAll(cssClasses);
			vbox.getStyleClass().add(STYLE_CLASS_PARA);
			currentCollector.getChildren().add(vbox);
			pushNode(vbox);
			visitChildren(node);
			popNode();
		}

		@Override
		public void visit(final QuotedNode node) {
			switch (node.getType()) {
				case DoubleAngle:
					buildTextNode("\u00AB");
					visitChildren(node);
					buildTextNode("\u00BB");
					break;
				case Double:
					buildTextNode("\u201C");
					visitChildren(node);
					buildTextNode("\u201D");
					break;
				case Single:
					buildTextNode("\u2018");
					visitChildren(node);
					buildTextNode("\u2019");
					break;
			}

		}

		@Override
		public void visit(final ReferenceNode node) {
		}

		@Override
		public void visit(final RefImageNode node) {
		}

		@Override
		public void visit(final RefLinkNode node) {
		}

		@Override
		public void visit(final RootNode node) {
			visitChildren(node);
		}

		@Override
		public void visit(final SimpleNode node) {
			switch (node.getType()) {
				case Apostrophe:
					buildTextNode("\u2019");
					break;
				case Ellipsis:
					buildTextNode("\u2026");
					break;
				case Emdash:
					buildTextNode("\u2014");
					break;
				case Endash:
					buildTextNode("\u2013");
					break;
				case Nbsp:
					buildTextNode("\u00a0");
					break;

				case Linebreak:
					popNode();
					final TextFlow tf = new TextFlow();
					tf.getStyleClass().setAll(cssClasses);
					currentCollector.getChildren().add(tf);
					pushNode(tf);
					break;
				case HRule:
					final Separator sep = new Separator();
					sep.getStyleClass().add(STYLE_CLASS_SEPARATOR);
					currentCollector.getChildren().add(sep);
					break;
			}
		}

		@Override
		public void visit(final SpecialTextNode node) {
			buildTextNode(node.getText());
		}

		@Override
		public void visit(final StrikeNode node) {
			cssClasses.add(STYLE_CLASS_STRIKE);
			visitChildren(node);
			cssClasses.remove(STYLE_CLASS_STRIKE);
		}

		@Override
		public void visit(final StrongEmphSuperNode node) {
			if (node.isStrong()) {
				cssClasses.add(STYLE_CLASS_STRONG);
				visitChildren(node);
				cssClasses.remove(STYLE_CLASS_STRONG);
			} else {
				cssClasses.add(STYLE_CLASS_EMPH);
				visitChildren(node);
				cssClasses.remove(STYLE_CLASS_EMPH);
			}
		}

		@Override
		public void visit(final SuperNode node) {
			final TextFlow tf = new TextFlow();
			currentCollector.getChildren().add(tf);
			pushNode(tf);
			visitChildren(node);
			popNode();
		}

		@Override
		public void visit(final TableBodyNode node) {
		}

		@Override
		public void visit(final TableCaptionNode node) {
		}

		@Override
		public void visit(final TableCellNode node) {
		}

		@Override
		public void visit(final TableColumnNode node) {
		}

		@Override
		public void visit(final TableHeaderNode node) {
		}

		@Override
		public void visit(final TableNode node) {
		}

		@Override
		public void visit(final TableRowNode node) {
		}

		@Override
		public void visit(final TextNode node) {
			if (isHyperlinkChild) {
				buildLinkNode(node.getText(), currentHyperlinkURL);
			} else {
				buildTextNode(node.getText());
			}

		}

		@Override
		public void visit(final VerbatimNode node) {
			cssClasses.add(STYLE_CLASS_VERBATIM);
			cssClasses.add(STYLE_CLASS_VERBATIM + "-" + node.getType());
			final TextFlow tf = new TextFlow();
			tf.getStyleClass().setAll(cssClasses);
			pushNode(tf);
			buildTextNode(node.getText());
			popNode();
			currentCollector.getChildren().add(tf);
			cssClasses.remove(STYLE_CLASS_VERBATIM);
			cssClasses.remove(STYLE_CLASS_VERBATIM + "-" + node.getType());
		}

		@Override
		public void visit(final WikiLinkNode node) {
		}

	}
}
