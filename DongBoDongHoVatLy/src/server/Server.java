package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import server.ServerProcess;


public class Server {
	public static int PORT = 12345;
	public static HashMap<String, Client> listClient = new HashMap<String, Client>();
	public static ArrayList<ServerProcess> listServerProcess = new ArrayList<>();
	
	public static void startServer() throws IOException {
		ServerSocket server = new ServerSocket(PORT);
		
		System.out.println("Server is running...");
		
		while (true) {
			Socket socket = server.accept();
			ServerProcess process = new ServerProcess(socket);
			process.start();
			listServerProcess.add(process);
			
		}
	}
	
	public void calCulateDifferenTime() {
		
		long different=0;
		long standard;
		
		//Server.listClient.put(ip, new Client(ip, s));
		Set<String> keys = Server.listClient.keySet();
		//sum all different time of all client
		for(String key: keys) {
			
			different+= Server.listClient.get(key).getDifferrence();
		}
		standard = different/(Server.listClient.size());
		listClient.put("server", new Client("server", standard));
		
		
		//calculate and update different time of all client
		for(String key: keys) {
			if(!key.equals("server")) {
				listClient.put(key, new Client(key, standard-Server.listClient.get(key).getDifferrence()));
			}
		}
		
	}
	
	public void sendDifferenTimeToClient() throws IOException {
		for(int i=0;i<listServerProcess.size();i++) {
			ServerProcess serverProcess =  listServerProcess.get(i);
			String diffrent = listClient.get(serverProcess.getIp()).getDifferrence()+"";
			serverProcess.sendDifferentToClient(diffrent);
			serverProcess.closeSocket();
		}
	}
	public void synchronizedAll() throws IOException {
		calCulateDifferenTime();
		sendDifferenTimeToClient();
		changeTimeOfSystem(listClient.get("server").getDifferrence());	
	}
	void changeTimeOfSystem(long timeNano) {
		try {
			Runtime runtime = Runtime.getRuntime();
			LocalDateTime timeNow = LocalDateTime.now();
			timeNow = timeNow.plusNanos(timeNano);
			DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("MM-dd-yy");
			DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
			String date = timeNow.format(dateTimeFormatter1).toString();
			String time = timeNow.format(dateTimeFormatter2).toString();
			runtime.exec("cmd /C date " +date);
			runtime.exec("cmd /C time " +time);
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		try {
			startServer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
