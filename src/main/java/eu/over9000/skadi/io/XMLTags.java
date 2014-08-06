/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.io;

/**
 * Collection of tags used in the xml config file.
 * 
 * @author Jan Strauß
 * 
 */
public class XMLTags {
	public static final String VERSION = "VERSION";
	public static final String VERSION_VALUE = "1.0";
	public static final String ROOT = "SKADI_DATA";
	public static final String EXECUTABLES = "EXECUTABLES";
	
	public static final String USE_LIVESTREAMER = "USE_LIVESTREAMER";
	public static final String DISPLAY_NOTIFICATIONS = "DISPLAY_NOTIFICATIONS";
	
	public static final String CHROME_EXECUTABLE = "CHROME";
	public static final String LIVESTREAMER_EXECUTABLE = "LIVESTREAMER";
	public static final String VLC_EXECUTABLE = "VLC";
	
	public static final String CHANNELS = "CHANNELS";
	public static final String CHANNEL = "CHANNEL";
	
	public static final String URL = "URL";
	public static final String QUALITY = "QUALITY";
	public static final String HOST = "HOST";
	
}
