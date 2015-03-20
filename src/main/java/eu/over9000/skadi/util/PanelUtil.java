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

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

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

import eu.over9000.skadi.model.Panel;

/**
 * Markdown handling based on bitbucket.org/shemnon/flowdown/
 *
 * @author Jan
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

	public static VBox buildPanel(final Panel panel) {
		final VBox box = new VBox();
		box.setMaxWidth(200);
		final Label lbTitle = new Label(panel.getTitle());
		lbTitle.setFont(new Font(18));
		box.getChildren().add(lbTitle);

		if ((panel.getLink() != null) && !panel.getLink().isEmpty() && (panel.getImage() != null) && !panel.getImage()
				.isEmpty()) {
			final ImageView img = new ImageView(panel.getImage());
			img.setPreserveRatio(true);
			img.setFitWidth(200);
			final Hyperlink banner = new Hyperlink(null, img);
			banner.setTooltip(new Tooltip(panel.getLink()));
			banner.setOnAction(event -> DesktopUtil.openWebpage(panel.getLink()));

			box.getChildren().add(banner);
		} else if ((panel.getImage() != null) && !panel.getImage().isEmpty()) {
			final ImageView img = new ImageView(panel.getImage());
			img.setPreserveRatio(true);
			img.setFitWidth(200);
			box.getChildren().add(img);
		}
		if ((panel.getDescription() != null) && !panel.getDescription().isEmpty()) {
			box.getChildren().add(PanelUtil.parseDescriptionFromMarkdown(panel.getDescription()));
		}

		return box;
	}

	private static VBox parseDescriptionFromMarkdown(final String markdown) {

		final VBox result = new VBox();
		final PegDownProcessor processor = new PegDownProcessor(Extensions.STRIKETHROUGH | Extensions
				.FENCED_CODE_BLOCKS);

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
		PanelUtil.LOGGER.debug(intend + node);
		node.getChildren().forEach(c -> PanelUtil.visit(c, intend + " "));
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
			this.currentCollector.getChildren().add(link);
		}

		void buildTextNode(String text) {
			while (text.endsWith("\n")) {
				text = text.substring(0, text.length() - 1);
			}
			final Text textNode = new Text(text);
			textNode.getStyleClass().setAll(this.cssClasses);
			this.currentCollector.getChildren().add(textNode);
		}

		public void popNode() {
			if (!this.nodeStack.isEmpty()) {
				this.nodeStack.pop();
			}
			this.currentCollector = this.nodeStack.peek();
		}

		public void pushNode(final Pane n) {
			this.nodeStack.push(n);
			this.currentCollector = n;
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
			vbox.getStyleClass().setAll(this.cssClasses);
			vbox.getStyleClass().add(cssClass);

			this.currentCollector.getChildren().add(vbox);
			vbox.setMinHeight(Region.USE_PREF_SIZE);
			vbox.setMaxHeight(Region.USE_PREF_SIZE);

			this.pushNode(vbox);
		}

		void startListRow(final String bullet) {
			final Text bt = new Text(bullet);
			bt.setTextAlignment(TextAlignment.RIGHT);
			bt.setTextOrigin(VPos.BASELINE);
			bt.getStyleClass().setAll(this.cssClasses);
			bt.getStyleClass().add(PanelUtil.STYLE_CLASS_BULLET);

			final VBox bulletContent = new VBox();
			bulletContent.setMinHeight(Region.USE_PREF_SIZE);
			bulletContent.setMaxHeight(Region.USE_PREF_SIZE);
			bulletContent.getStyleClass().setAll(this.cssClasses);
			bulletContent.getStyleClass().add(PanelUtil.STYLE_CLASS_LIST_CONTENT);

			final HBox hb = new HBox();
			hb.setMinHeight(Region.USE_PREF_SIZE);
			hb.setMaxHeight(Region.USE_PREF_SIZE);
			hb.setAlignment(Pos.BASELINE_LEFT);

			hb.getChildren().setAll(bt, bulletContent);

			this.currentCollector.getChildren().add(hb);

			this.pushNode(bulletContent);
		}

		void stopListBox() {
			this.popNode();
		}

		void stopListRow() {
			this.popNode();
		}

		@Override
		public void visit(final AbbreviationNode node) {
		}

		@Override
		public void visit(final AnchorLinkNode node) {
			this.buildTextNode(node.getText());
		}

		@Override
		public void visit(final AutoLinkNode node) {
			this.buildLinkNode(node.getText(), node.getText());
		}

		@Override
		public void visit(final BlockQuoteNode node) {
			final VBox vBox = new VBox();
			vBox.getStyleClass().setAll(this.cssClasses);
			vBox.getStyleClass().add(PanelUtil.STYLE_CLASS_BLOCKQUOTE);
			this.currentCollector.getChildren().add(vBox);
			this.pushNode(vBox);
			this.visitChildren(node);
			this.popNode();
		}

		@Override
		public void visit(final BulletListNode node) {
			this.startListBox(PanelUtil.STYLE_CLASS_UNORDERED_LIST);
			this.listCount.push(null);
			this.visitChildren(node);
			this.listCount.pop();
			this.stopListBox();
		}

		@Override
		public void visit(final CodeNode node) {
		}

		@Override
		public void visit(final DefinitionListNode node) {
			this.visitChildren(node);
		}

		@Override
		public void visit(final DefinitionNode node) {
			this.visitChildren(node);
		}

		@Override
		public void visit(final DefinitionTermNode node) {
			this.visitChildren(node);
		}

		@Override
		public void visit(final ExpImageNode node) {
		}

		@Override
		public void visit(final ExpLinkNode node) {
			this.isHyperlinkChild = true;
			this.currentHyperlinkURL = node.url;
			this.visitChildren(node);
			this.isHyperlinkChild = false;
		}

		@Override
		public void visit(final HeaderNode node) {
			this.cssClasses.add(PanelUtil.STYLE_CLASS_HEADER_BASE + node.getLevel());
			this.cssClasses.add(PanelUtil.STYLE_CLASS_HEADER);
			final TextFlow fp = new TextFlow();
			fp.getStyleClass().setAll(this.cssClasses);
			this.currentCollector.getChildren().add(fp);
			this.pushNode(fp);
			this.visitChildren(node);
			this.popNode();
			this.cssClasses.remove(PanelUtil.STYLE_CLASS_HEADER_BASE + node.getLevel());
			this.cssClasses.remove(PanelUtil.STYLE_CLASS_HEADER);
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
			if (this.listCount.peek() != null) {
				int i = this.listCount.pop();
				bullet = Integer.toString(i) + ". ";
				this.listCount.push(++i);
			}
			this.startListRow(bullet);
			this.visitChildren(node);
			this.stopListRow();
		}

		@Override
		public void visit(final MailLinkNode node) {
		}

		@Override
		public void visit(final Node node) {
		}

		@Override
		public void visit(final OrderedListNode node) {
			this.startListBox(PanelUtil.STYLE_CLASS_ORDERED_LIST);
			this.listCount.push(1);
			this.visitChildren(node);
			this.listCount.pop();
			this.stopListBox();
		}

		@Override
		public void visit(final ParaNode node) {
			final VBox vbox = new VBox();
			vbox.getStyleClass().setAll(this.cssClasses);
			vbox.getStyleClass().add(PanelUtil.STYLE_CLASS_PARA);
			this.currentCollector.getChildren().add(vbox);
			this.pushNode(vbox);
			this.visitChildren(node);
			this.popNode();
		}

		@Override
		public void visit(final QuotedNode node) {
			switch (node.getType()) {
				case DoubleAngle:
					this.buildTextNode("\u00AB");
					this.visitChildren(node);
					this.buildTextNode("\u00BB");
					break;
				case Double:
					this.buildTextNode("\u201C");
					this.visitChildren(node);
					this.buildTextNode("\u201D");
					break;
				case Single:
					this.buildTextNode("\u2018");
					this.visitChildren(node);
					this.buildTextNode("\u2019");
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
			this.visitChildren(node);
		}

		@Override
		public void visit(final SimpleNode node) {
			switch (node.getType()) {
				case Apostrophe:
					this.buildTextNode("\u2019");
					break;
				case Ellipsis:
					this.buildTextNode("\u2026");
					break;
				case Emdash:
					this.buildTextNode("\u2014");
					break;
				case Endash:
					this.buildTextNode("\u2013");
					break;
				case Nbsp:
					this.buildTextNode("\u00a0");
					break;

				case Linebreak:
					this.popNode();
					final TextFlow tf = new TextFlow();
					tf.getStyleClass().setAll(this.cssClasses);
					this.currentCollector.getChildren().add(tf);
					this.pushNode(tf);
					break;
				case HRule:
					final Separator sep = new Separator();
					sep.getStyleClass().add(PanelUtil.STYLE_CLASS_SEPARATOR);
					this.currentCollector.getChildren().add(sep);
					break;
			}
		}

		@Override
		public void visit(final SpecialTextNode node) {
			this.buildTextNode(node.getText());
		}

		@Override
		public void visit(final StrikeNode node) {
			this.cssClasses.add(PanelUtil.STYLE_CLASS_STRIKE);
			this.visitChildren(node);
			this.cssClasses.remove(PanelUtil.STYLE_CLASS_STRIKE);
		}

		@Override
		public void visit(final StrongEmphSuperNode node) {
			if (node.isStrong()) {
				this.cssClasses.add(PanelUtil.STYLE_CLASS_STRONG);
				this.visitChildren(node);
				this.cssClasses.remove(PanelUtil.STYLE_CLASS_STRONG);
			} else {
				this.cssClasses.add(PanelUtil.STYLE_CLASS_EMPH);
				this.visitChildren(node);
				this.cssClasses.remove(PanelUtil.STYLE_CLASS_EMPH);
			}
		}

		@Override
		public void visit(final SuperNode node) {
			final TextFlow tf = new TextFlow();
			this.currentCollector.getChildren().add(tf);
			this.pushNode(tf);
			this.visitChildren(node);
			this.popNode();
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
			if (this.isHyperlinkChild) {
				this.buildLinkNode(node.getText(), this.currentHyperlinkURL);
			} else {
				this.buildTextNode(node.getText());
			}

		}

		@Override
		public void visit(final VerbatimNode node) {
			this.cssClasses.add(PanelUtil.STYLE_CLASS_VERBATIM);
			this.cssClasses.add(PanelUtil.STYLE_CLASS_VERBATIM + "-" + node.getType());
			final TextFlow tf = new TextFlow();
			tf.getStyleClass().setAll(this.cssClasses);
			this.pushNode(tf);
			this.buildTextNode(node.getText());
			this.popNode();
			this.currentCollector.getChildren().add(tf);
			this.cssClasses.remove(PanelUtil.STYLE_CLASS_VERBATIM);
			this.cssClasses.remove(PanelUtil.STYLE_CLASS_VERBATIM + "-" + node.getType());
		}

		@Override
		public void visit(final WikiLinkNode node) {
		}

	}
}
