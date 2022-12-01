package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import server.Server;


public class ServerProcess  extends Thread{
	private Socket socket;
	private BufferedReader netIn;
	private PrintWriter netOut;
//	Thread updatelistClient;
	Thread getTimeNow;
	
	public ServerProcess(Socket socket) throws IOException {
		this.socket = socket;
		this.netIn = netIn;
		this.netOut = netOut;
	}
	
	
	public Socket getSocket() {
		return socket;
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}


	public BufferedReader getNetIn() {
		return netIn;
	}


	public void setNetIn(BufferedReader netIn) {
		this.netIn = netIn;
	}


	public PrintWriter getNetOut() {
		return netOut;
	}


	public void setNetOut(PrintWriter netOut) {
		this.netOut = netOut;
	}


	@Override
	public void run() {
		try {
			startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startServer() throws IOException {
		BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter netOut = new PrintWriter(socket.getOutputStream());
		netOut.println("Connect to server success!");
		netOut.flush();
		
		//Send time current of Server
		String timeNow = LocalDateTime.now().toString();
		netOut.println(timeNow);
		netOut.flush();
		
		//recive different time from client
		String differentTime = netIn.readLine();
		System.out.println("differentTime: "+differentTime);
		
		// save client
		Server.listClient.put(socket.getInetAddress().toString(), new Client(socket.getInetAddress().toString(), Long.parseLong(differentTime)));
	
		
		
//		closeSocket();
	}
	
	public void sendDifferentToClient(String different) {
		netOut.println(different);
		netOut.flush();
	}
	
	void closeSocket() throws IOException {
		socket.close();
	}
	
	public String getIp() {
		return this.socket.getInetAddress().toString();
	}
	
	
	void printTimeNow() {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSSSSS");
		String stringTime =  time.format(dateTimeFormatter).toString();
		System.out.println(stringTime);
	}
	
	void thredTimeNow() {
		getTimeNow = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(10);
					printTimeNow();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}
			}
		});
	}
	

	
	
//	public long sendDiffrentAndGetResult(String ip, long s) {
//		System.out.println("Send result to ip: "+ip+" with s= "+s);
//		long different=0;
//		long standard;
//		
//		Server.listClient.put(ip, new Client(ip, s));
//		Set<String> keys = Server.listClient.keySet();
//		//sum all different time of all client
//		for(String key: keys) {
//			
//			different+= Server.listClient.get(key).getDifferrence();
//		}
//		standard = different/(Server.listClient.size());
//		ListClient.listClient.put("server", new Client("server", standard));
//		
//		
//		//calculate and update different time of all client
//		for(String key: keys) {
//			if(!key.equals("server")) {
//				ListClient.listClient.put(key, new Client(key, standard-Server.listClient.get(key).getDifferrence()));
//			}
//		}
//		System.out.println("Different time after");
//		return Server.listClient.get(ip).getDifferrence();
//	}

}