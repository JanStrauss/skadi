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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.SystemUtils;

@XmlRootElement(name = "skadi_data")
@XmlAccessorType(XmlAccessType.NONE)
public final class StateContainer {
	
	private static StateContainer instance;

	@XmlElement(name = "executable_chrome")
	private String executableChrome;
	
	@XmlElement(name = "executable_livestreamer")
	private String executableLivestreamer;
	
	@XmlElement(name = "executable_vlc")
	private String executableVLC;
	
	@XmlElement(name = "display_notifications")
	private boolean displayNotifications;
	
	@XmlElement(name = "minimize_to_tray")
	private boolean minimizeToTray;
	
	@XmlElement(name = "online_filter_active")
	private boolean onlineFilterActive;
	
	@XmlElementWrapper(name = "channels")
	@XmlElement(name = "channel")
	private final List<String> channels = new ArrayList<>();
	
	public StateContainer() {
		StateContainer.instance = this;
	}

	public static StateContainer fromDefault() {
		final StateContainer result = new StateContainer();

		// TODO add osx defaults
		result.setExecutableChrome(SystemUtils.IS_OS_LINUX ? "chromium-browser"
				: "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
		result.setExecutableLivestreamer(SystemUtils.IS_OS_LINUX ? "livestreamer"
				: "C:\\Program Files (x86)\\Livestreamer\\livestreamer.exe");
		result.setExecutableVLC(SystemUtils.IS_OS_LINUX ? "vlc" : "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe");

		result.setDisplayNotifications(true);
		result.setMinimizeToTray(true);
		result.setOnlineFilterActive(false);

		return result;
	}
	
	public static StateContainer getInstance() {
		return StateContainer.instance;
	}
	
	public String getExecutableChrome() {
		return this.executableChrome;
	}
	
	public void setExecutableChrome(final String executableChrome) {
		this.executableChrome = executableChrome;
	}
	
	public String getExecutableLivestreamer() {
		return this.executableLivestreamer;
	}
	
	public void setExecutableLivestreamer(final String executableLivestreamer) {
		this.executableLivestreamer = executableLivestreamer;
	}
	
	public String getExecutableVLC() {
		return this.executableVLC;
	}
	
	public void setExecutableVLC(final String executableVLC) {
		this.executableVLC = executableVLC;
	}
	
	public boolean isDisplayNotifications() {
		return this.displayNotifications;
	}
	
	public void setDisplayNotifications(final boolean displayNotifications) {
		this.displayNotifications = displayNotifications;
	}
	
	public boolean isMinimizeToTray() {
		return this.minimizeToTray;
	}
	
	public void setMinimizeToTray(final boolean minimizeToTray) {
		this.minimizeToTray = minimizeToTray;
	}
	
	public List<String> getChannels() {
		return this.channels;
	}

	public void setOnlineFilterActive(final boolean onlineFilterActive) {
		this.onlineFilterActive = onlineFilterActive;
	}
	
	public boolean isOnlineFilterActive() {
		return this.onlineFilterActive;
	}
}
