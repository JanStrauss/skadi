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

/**
 * Model class for stream qualities (source, high, .., mobile).
 *
 * @author Jan Strauß
 */
public class StreamQuality {
	private final boolean isDummy;
	private final String url;
	private final String quality;
	private final int bandwidth;

	public StreamQuality(final String url, final String quality, final int bandwidth) {
		this.url = url;
		this.quality = quality;
		this.bandwidth = bandwidth;
		this.isDummy = false;
	}

	private StreamQuality(final String quality) {
		this.url = null;
		this.quality = quality;
		this.bandwidth = 0;
		this.isDummy = true;
	}

	public static StreamQuality[] getDefaultQualities() {
		return new StreamQuality[]{StreamQuality.getBestQuality(), StreamQuality.getWorstQuality()};
	}

	public static StreamQuality getWorstQuality() {
		return new StreamQuality("worst");
	}

	public static StreamQuality getBestQuality() {
		return new StreamQuality("best");
	}

	public String getUrl() {
		return this.url;
	}

	public String getQuality() {
		return this.quality;
	}

	public int getBandwidth() {
		return this.bandwidth;
	}

	public boolean isDummy() {
		return this.isDummy;
	}

	@Override
	public String toString() {
		return "StreamQuality [url=" + this.url + ", quality=" + this.quality + ", bandwidth=" + this.bandwidth + "]";
	}

}
