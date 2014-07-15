package eu.over9000.skadi.channel;

public interface ChannelEventListener {
	public void added(Channel channel);
	
	public void removed(Channel channel);
	
	public void updatedMetadata(Channel channel);
	
	public void updatedStreamdata(Channel channel);
	
	public String getListenerName();
}
