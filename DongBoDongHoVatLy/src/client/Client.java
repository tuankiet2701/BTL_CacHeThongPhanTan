package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import server.Server;


public class Client {
	public static String ADDRESS = "localhost";
	Thread waitSynchronized;
	String ip;
	
	
	

	public  void run() throws UnknownHostException, IOException {
		Socket socket = new Socket(ADDRESS, Server.PORT);
		BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter netOut = new PrintWriter(socket.getOutputStream());
		String data = netIn.readLine();
		System.out.println(data);

		
		//recive time of Server
		String timeNowOfServer = netIn.readLine();
		System.out.println("Time of Server: "+timeNowOfServer);
		
		//calculater different time between server and client 
		String differentTime = calculateDifferentTime(timeNowOfServer)+"";
		System.out.println("differentTime: "+differentTime);
		
		//send different time
		netOut.println(differentTime);
		netOut.flush();
		
		//recive different time of server caculator
		String differentTimeServerCal = netIn.readLine();
		System.out.println("differentTimeServerCal: "+differentTimeServerCal);
		
		while(true) {
			if(differentTimeServerCal!=null&&differentTimeServerCal!="") {
				changeTimeOfSystem(Long.parseLong(differentTimeServerCal));
				socket.close();
			}
		}
		
		
	}
    
	public void changeTimeOfSystem(long timeNano) {
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
	
	public  String standardized(String time) {
		LocalDateTime localDateTime = LocalDateTime.parse(time);
		String stringStandardized;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
		stringStandardized = time.formatted(dateTimeFormatter);
		return stringStandardized;
	}
	
	public  long calculateSecond(String time1, String time2) {
		
		Date date = new Date();
		LocalDateTime localDateTime1 = LocalDateTime.parse(standardized(time1));
	    LocalDateTime localDateTime2 = LocalDateTime.parse(standardized(time2));
	    long seconds = ChronoUnit.NANOS.between(localDateTime1, localDateTime2);
		return seconds;
	}
	
	public long calculateDifferentTime(String time) {
		long differentTime=0;
		String timeOfServer = standardized(time);
		String timeOfClient = standardized(LocalDateTime.now().toString());
		if(!(timeOfClient==null || timeOfClient=="" || timeOfServer==null || timeOfServer=="")) {
			differentTime = calculateSecond(timeOfServer, timeOfClient);
		}
		return differentTime;
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		try {
			client.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
