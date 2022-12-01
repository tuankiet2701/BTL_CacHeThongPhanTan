package server;

public class Client {
	String ip;
	long differrence;
	public Client(String ip, long differrence) {
		super();
		this.ip = ip;
		this.differrence = differrence;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getDifferrence() {
		return differrence;
	}
	public void setDifferrence(long differrence) {
		this.differrence = differrence;
	}
	
	
}
