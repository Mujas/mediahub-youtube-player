package settergetter;

public class Medias {
	
	int id;
	String drawable;
	String Name;
	String ChannelId;
	
	
	
	
	@Override
	public String toString() {
		return Name;
	}
	public String getChannelId() {
		return ChannelId;
	}
	public void setChannelId(String channelId) {
		ChannelId = channelId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDrawable() {
		return drawable;
	}
	public void setDrawable(String drawable) {
		this.drawable = drawable;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}

}
