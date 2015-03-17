/*******************************************************************************
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
 ******************************************************************************/
package eu.over9000.skadi.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Panel {
	
	private final StringProperty link;
	private final StringProperty image;
	private final StringProperty title;
	private final StringProperty description;

	public Panel(final String link, final String image, final String title, final String description) {
		this.link = new SimpleStringProperty(link);
		this.image = new SimpleStringProperty(image);
		this.title = new SimpleStringProperty(title);
		this.description = new SimpleStringProperty(description);
	}

	@Override
	public String toString() {
		return "Panel [link=" + this.link + ", image=" + this.image + ", title=" + this.title + ", description=" + this.description + "]";
	}
	
	public final StringProperty linkProperty() {
		return this.link;
	}

	public final java.lang.String getLink() {
		return this.linkProperty().get();
	}

	public final void setLink(final java.lang.String link) {
		this.linkProperty().set(link);
	}

	public final StringProperty imageProperty() {
		return this.image;
	}

	public final java.lang.String getImage() {
		return this.imageProperty().get();
	}

	public final void setImage(final java.lang.String image) {
		this.imageProperty().set(image);
	}

	public final StringProperty titleProperty() {
		return this.title;
	}

	public final java.lang.String getTitle() {
		return this.titleProperty().get();
	}

	public final void setTitle(final java.lang.String title) {
		this.titleProperty().set(title);
	}

	public final StringProperty descriptionProperty() {
		return this.description;
	}

	public final java.lang.String getDescription() {
		return this.descriptionProperty().get();
	}

	public final void setDescription(final java.lang.String description) {
		this.descriptionProperty().set(description);
	}

}
