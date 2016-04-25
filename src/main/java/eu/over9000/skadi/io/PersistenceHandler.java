/*
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

package eu.over9000.skadi.io;

import eu.over9000.skadi.model.StateContainer;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PersistenceHandler {

	public static final String SKADI_DIRECTORY_NAME = ".skadi";
	public static final String PERSISTENCE_DIRECTORY = SystemUtils.USER_HOME + File.separator + SKADI_DIRECTORY_NAME + File.separator;
	public static final String PERSISTENCE_FILE = "skadi_state.xml";
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceHandler.class);
	private final Object fileLock = new Object();
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public PersistenceHandler() {
		try {
			final JAXBContext context = JAXBContext.newInstance(StateContainer.class);
			marshaller = context.createMarshaller();
			unmarshaller = context.createUnmarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		} catch (final JAXBException e) {
			LOGGER.error("exception construction persistence handler", e);
		}
	}

	public StateContainer loadState() {
		StateContainer result;
		try {
			if (Files.exists(getStateFilePath())) {
				result = readFromFile();
			} else {
				checkDir();
				result = new StateContainer();
				writeToFile(result);
			}
		} catch (IOException | JAXBException e) {
			LOGGER.error("exception loading state, will fallback to default settings", e);
			result = new StateContainer();
		}
		return result;
	}

	public void saveState(final StateContainer state) {
		try {
			checkDir();
			writeToFile(state);
		} catch (IOException | JAXBException e) {
			LOGGER.error("exception saving state", e);
		}
	}

	private Path getStateFilePath() {
		return Paths.get(PERSISTENCE_DIRECTORY, PERSISTENCE_FILE);
	}

	private void writeToFile(final StateContainer state) throws IOException, JAXBException {
		final Path stateFile = getStateFilePath();
		synchronized (fileLock) {
			marshaller.marshal(state, stateFile.toFile());
		}
		LOGGER.debug("wrote state to file");
	}

	private StateContainer readFromFile() throws IOException, JAXBException {
		final Path stateFile = getStateFilePath();
		final StateContainer state;
		synchronized (fileLock) {
			state = (StateContainer) unmarshaller.unmarshal(stateFile.toFile());
		}
		LOGGER.debug("load state from file");
		return state;
	}

	private void checkDir() throws IOException {
		Files.createDirectories(Paths.get(PERSISTENCE_DIRECTORY));
	}
}
