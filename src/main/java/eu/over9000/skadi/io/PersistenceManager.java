package eu.over9000.skadi.io;

import java.util.HashMap;
import java.util.Map;

import eu.over9000.skadi.channel.ChannelInstance;

public class PersistenceManager {
	private static PersistenceManager instance;
	
	public static PersistenceManager getInstance() {
		if (PersistenceManager.instance == null) {
			PersistenceManager.instance = new PersistenceManager();
		}
		return PersistenceManager.instance;
	}
	
	public Map<String, ChannelInstance> loadChannels() {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}
}