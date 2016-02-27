// Group Members: Amanda Goonetilleke, Richard Tran, Cheryl Chi

public class Message {
	
	private String message;
	private String timestamp;
	private String id;
	private String ip;
	private int port;
	
	public Message (String message, String timestamp, String id, String ip, int port){
		this.message = message;
		this.timestamp = timestamp;
		this.id = id;
		this.ip = ip;
		this.port = port;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getTimeStamp(){
		return timestamp;
	}
	
	public String getId(){
		return id;
	}
	
	public String getIp(){
		return ip;
	}
	
	public int getPortNum(){
		return port;
	}
	
	
	
}
