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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.logging.SkadiLogging;

/**
 * Singleton class used for config file IO.
 * 
 * @author Jan Strauß
 * 
 */
public class PersistenceManager {
	private static PersistenceManager instance;
	
	public static final String PERSISTENCE_DIRECTORY = System.getProperty("user.home") + File.separator + ".skadi"
	        + File.separator;
	private static final String PERSISTENCE_FILE = "skadi_data.xml";
	
	public static final String CHAT_LOG_FILE = PersistenceManager.PERSISTENCE_DIRECTORY + "chat_output.log";
	public static final String STREAM_LOG_FILE = PersistenceManager.PERSISTENCE_DIRECTORY + "stream_output.log";
	public static final String SKADI_LOG_FILE = PersistenceManager.PERSISTENCE_DIRECTORY + "skadi_output.log";
	
	private final File dataFile;
	
	public static PersistenceManager getInstance() {
		if (PersistenceManager.instance == null) {
			PersistenceManager.instance = new PersistenceManager();
		}
		return PersistenceManager.instance;
	}
	
	private PersistenceManager() {
		checkAndCreateDir();
		
		this.dataFile = new File(PersistenceManager.PERSISTENCE_DIRECTORY + PersistenceManager.PERSISTENCE_FILE);
		
		if (!this.dataFile.exists()) {
			try {
				this.dataFile.createNewFile();
				this.saveData();
			} catch (final IOException e) {
				SkadiLogging.log(e);
			}
		}
	}
	
	public void saveData() {
		try {
			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			final Document document = documentBuilder.newDocument();
			document.setXmlStandalone(true);
			
			final Element dataRoot = document.createElement(XMLTags.ROOT);
			dataRoot.setAttribute(XMLTags.VERSION, XMLTags.VERSION_VALUE);
			document.appendChild(dataRoot);
			
			final Element execRoot = document.createElement(XMLTags.EXECUTABLES);
			final Element channelsRoot = document.createElement(XMLTags.CHANNELS);
			
			dataRoot.appendChild(execRoot);
			
			final Element chrome_exec = document.createElement(XMLTags.CHROME_EXECUTABLE);
			chrome_exec.setTextContent(SkadiMain.getInstance().chrome_exec);
			execRoot.appendChild(chrome_exec);
			
			final Element livestreamer_exec = document.createElement(XMLTags.LIVESTREAMER_EXECUTABLE);
			livestreamer_exec.setTextContent(SkadiMain.getInstance().livestreamer_exec);
			execRoot.appendChild(livestreamer_exec);
			
			final Element vlc_exec = document.createElement(XMLTags.VLC_EXECUTABLE);
			vlc_exec.setTextContent(SkadiMain.getInstance().vlc_exec);
			execRoot.appendChild(vlc_exec);
			
			final Element use_livestreamer = document.createElement(XMLTags.USE_LIVESTREAMER);
			use_livestreamer.setTextContent(Boolean.toString(SkadiMain.getInstance().use_livestreamer));
			dataRoot.appendChild(use_livestreamer);
			
			final Element display_notifications = document.createElement(XMLTags.DISPLAY_NOTIFICATIONS);
			display_notifications.setTextContent(Boolean.toString(SkadiMain.getInstance().display_notifications));
			dataRoot.appendChild(display_notifications);
			
			dataRoot.appendChild(channelsRoot);
			for (final Channel channel : ChannelManager.getInstance().getChannels()) {
				final Element channelRoot = document.createElement(XMLTags.CHANNEL);
				
				final Element urlElement = document.createElement(XMLTags.URL);
				urlElement.appendChild(document.createTextNode(channel.getURL()));
				channelRoot.appendChild(urlElement);
				
				channelsRoot.appendChild(channelRoot);
			}
			
			final FileOutputStream stream = new FileOutputStream(this.dataFile);
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			final DOMSource source = new DOMSource(document);
			final StreamResult result = new StreamResult(stream);
			transformer.transform(source, result);
			stream.close();
			
		} catch (ParserConfigurationException | IOException | TransformerException e) {
			SkadiLogging.log(e);
		}
		
	}
	
	private void loadChannels(final Element channelsRootElement) {
		
		final NodeList channels = channelsRootElement.getElementsByTagName(XMLTags.CHANNEL);
		
		for (int index = 0; index < channels.getLength(); index++) {
			final Element channel = (Element) channels.item(index);
			
			final String url = channel.getElementsByTagName(XMLTags.URL).item(0).getTextContent();
			
			ChannelManager.getInstance().addChannel(url, false);
		}
		
	}
	
	public void loadData() {
		
		try {
			final FileInputStream stream = new FileInputStream(this.dataFile);
			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			final Document document = documentBuilder.parse(stream);
			
			document.getDocumentElement().normalize();
			
			// Check doc basics
			final String docRoot = document.getDocumentElement().getNodeName();
			if (!docRoot.equals(XMLTags.ROOT)) {
				throw new XMLParseException(
				        "The given XML Document does not have the correct format. (Wrong root node).");
			}
			
			final String version = document.getDocumentElement().getAttribute(XMLTags.VERSION);
			if (!version.equals(XMLTags.VERSION_VALUE)) {
				throw new XMLParseException("The version of the file (" + version
				        + ") is not compatible with this importer (" + XMLTags.VERSION_VALUE + ").");
			}
			
			final Element execs = (Element) document.getDocumentElement().getElementsByTagName(XMLTags.EXECUTABLES)
			        .item(0);
			
			SkadiMain.getInstance().chrome_exec = this.loadChromeExec(execs);
			SkadiMain.getInstance().vlc_exec = this.loadVLCExec(execs);
			SkadiMain.getInstance().livestreamer_exec = this.loadLivestreamerExec(execs);
			SkadiMain.getInstance().use_livestreamer = this.loadUseLivestreamer(document.getDocumentElement());
			SkadiMain.getInstance().display_notifications = this
			        .loadDisplayNotifications(document.getDocumentElement());
			
			final Element channels = (Element) document.getDocumentElement().getElementsByTagName(XMLTags.CHANNELS)
			        .item(0);
			
			this.loadChannels(channels);
			
			stream.close();
		} catch (final SAXException | IOException | ParserConfigurationException | XMLParseException e) {
			SkadiLogging.log(e);
		}
		
	}
	
	private String loadLivestreamerExec(final Element execs) {
		try {
			return execs.getElementsByTagName(XMLTags.LIVESTREAMER_EXECUTABLE).item(0).getTextContent();
		} catch (final Exception e) {
			SkadiLogging.log("could not find livestreamer exec in data file, will use default value");
			return SkadiMain.getInstance().livestreamer_exec;
		}
	}
	
	private String loadVLCExec(final Element execs) {
		try {
			return execs.getElementsByTagName(XMLTags.VLC_EXECUTABLE).item(0).getTextContent();
		} catch (final Exception e) {
			SkadiLogging.log("could not find vlc exec in data file, will use default value");
			return SkadiMain.getInstance().vlc_exec;
		}
	}
	
	private String loadChromeExec(final Element execs) {
		try {
			return execs.getElementsByTagName(XMLTags.CHROME_EXECUTABLE).item(0).getTextContent();
		} catch (final Exception e) {
			SkadiLogging.log("could not find chrome exec in data file, will use default value");
			return SkadiMain.getInstance().chrome_exec;
		}
	}
	
	private boolean loadUseLivestreamer(final Element doc) {
		try {
			return Boolean.valueOf(doc.getElementsByTagName(XMLTags.USE_LIVESTREAMER).item(0).getTextContent());
		} catch (final Exception e) {
			SkadiLogging.log("could not find use_livestreamer var in data file, will use default value");
			return SkadiMain.getInstance().use_livestreamer;
		}
	}
	
	private boolean loadDisplayNotifications(final Element doc) {
		try {
			return Boolean.valueOf(doc.getElementsByTagName(XMLTags.DISPLAY_NOTIFICATIONS).item(0).getTextContent());
		} catch (final Exception e) {
			SkadiLogging.log("could not find display_notifications var in data file, will use default value");
			return SkadiMain.getInstance().use_livestreamer;
		}
	}

	public static void checkAndCreateDir() {
		final File dir = new File(PersistenceManager.PERSISTENCE_DIRECTORY);
		dir.mkdirs();
	}
}
