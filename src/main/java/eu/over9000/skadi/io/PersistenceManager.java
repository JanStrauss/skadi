package eu.over9000.skadi.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class PersistenceManager {
	private static PersistenceManager instance;
	
	private static final String PERSISTENCE_DIRECTORY = System.getProperty("user.home") + File.separator + ".skadi"
	        + File.separator;
	private static final String PERSISTENCE_FILE = "skadi_data.xml";
	
	public static final String CHAT_LOG_FILE = PersistenceManager.PERSISTENCE_DIRECTORY + "chat_output.log";
	public static final String STREAM_LOG_FILE = PersistenceManager.PERSISTENCE_DIRECTORY + "stream_output.log";
	
	private final File dataFile;
	
	public static PersistenceManager getInstance() {
		if (PersistenceManager.instance == null) {
			PersistenceManager.instance = new PersistenceManager();
		}
		return PersistenceManager.instance;
	}
	
	private PersistenceManager() {
		final File dir = new File(PersistenceManager.PERSISTENCE_DIRECTORY);
		dir.mkdirs();
		
		this.dataFile = new File(PersistenceManager.PERSISTENCE_DIRECTORY + PersistenceManager.PERSISTENCE_FILE);
		
		if (!this.dataFile.exists()) {
			try {
				this.dataFile.createNewFile();
				this.saveData();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveData() {
		try {
			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			final Document document = documentBuilder.newDocument();
			document.setXmlStandalone(true);
			
			final Element specificationRoot = document.createElement(XMLConstants.ROOT);
			specificationRoot.setAttribute(XMLConstants.VERSION, XMLConstants.VERSION_VALUE);
			document.appendChild(specificationRoot);
			
			final Element execRoot = document.createElement(XMLConstants.EXECUTABLES);
			final Element channelsRoot = document.createElement(XMLConstants.CHANNELS);
			
			specificationRoot.appendChild(execRoot);
			
			final Element chrome_exec = document.createElement(XMLConstants.CHROME_EXECUTABLE);
			chrome_exec.setTextContent(SkadiMain.getInstance().chrome_exec);
			execRoot.appendChild(chrome_exec);
			
			final Element vlc_exec = document.createElement(XMLConstants.VLC_EXECUTABLE);
			vlc_exec.setTextContent(SkadiMain.getInstance().vlc_exec);
			execRoot.appendChild(vlc_exec);
			
			specificationRoot.appendChild(channelsRoot);
			for (final Channel channel : SkadiMain.getInstance().getChannels()) {
				final Element channelRoot = document.createElement(XMLConstants.CHANNEL);
				
				final Element urlElement = document.createElement(XMLConstants.URL);
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
			e.printStackTrace();
		}
		
	}
	
	private List<Channel> loadChannels(final Element channelsRootElement) {
		final List<Channel> loadedChannels = new ArrayList<>();
		
		final NodeList channels = channelsRootElement.getElementsByTagName(XMLConstants.CHANNEL);
		
		for (int index = 0; index < channels.getLength(); index++) {
			final Element channel = (Element) channels.item(index);
			
			final String url = channel.getElementsByTagName(XMLConstants.URL).item(0).getTextContent();
			
			final Channel loaded = new Channel(url);
			
			loadedChannels.add(loaded);
		}
		
		return loadedChannels;
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
			if (!docRoot.equals(XMLConstants.ROOT)) {
				throw new XMLParseException(
				        "The given XML Document does not have the correct format. (Wrong root node).");
			}
			
			final String version = document.getDocumentElement().getAttribute(XMLConstants.VERSION);
			if (!version.equals(XMLConstants.VERSION_VALUE)) {
				throw new XMLParseException("The version of the file (" + version
				        + ") is not compatible with this importer (" + XMLConstants.VERSION_VALUE + ").");
			}
			
			final Element execs = (Element) document.getDocumentElement()
			        .getElementsByTagName(XMLConstants.EXECUTABLES).item(0);
			
			SkadiMain.getInstance().chrome_exec = this.loadChromeExec(execs);
			SkadiMain.getInstance().vlc_exec = this.loadVLCExec(execs);
			
			final Element channels = (Element) document.getDocumentElement()
			        .getElementsByTagName(XMLConstants.CHANNELS).item(0);
			
			SkadiMain.getInstance().setChannels(this.loadChannels(channels));
			
			stream.close();
		} catch (final SAXException | IOException | ParserConfigurationException | XMLParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private String loadVLCExec(final Element execs) {
		try {
			return execs.getElementsByTagName(XMLConstants.VLC_EXECUTABLE).item(0).getTextContent();
		} catch (final Exception e) {
			e.printStackTrace();
			return SkadiMain.getInstance().vlc_exec;
		}
	}
	
	private String loadChromeExec(final Element execs) {
		try {
			return execs.getElementsByTagName(XMLConstants.CHROME_EXECUTABLE).item(0).getTextContent();
		} catch (final Exception e) {
			e.printStackTrace();
			return SkadiMain.getInstance().vlc_exec;
		}
	}
}