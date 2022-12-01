package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;

import server.Server;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FormClient {
	
	public static String ADDRESS = "localhost";
	Thread waitSynchronized;
	Thread getTimeNow;
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
		txtServerTime.setText(timeNowOfServer);
		
		//calculater different time between server and client 
		String differentTime = calculateDifferentTime(timeNowOfServer)+"";
		System.out.println("differentTime: "+differentTime);
		txtTimeDeviation.setText(differentTime);
		
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
	
	String getIP() {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("google.com", 80));
			return socket.getLocalAddress().toString();
		} catch (IOException ex) {
			Logger.getLogger(FormClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "127.0.0.1:8080";
	}
	
	void timeNow() {
		LocalDateTime t = LocalDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSSSSS");
		String dat = t.format(fmt).toString();
		txtTimeNow.setText(dat);
	}
	
	void luongThoiGianHienTai() {
		getTimeNow = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Thread.sleep(20);
						timeNow();
					} catch (InterruptedException ex) {
						Logger.getLogger(FormClient.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});
		getTimeNow.start();
	}

	private JFrame frmClient;
	private JTextField txtServersIP;
	private JTextField txtCntStatus;
	private JTextField txtTimeNow;
	private JTextField txtServerTime;
	private JTextField txtTimeDeviation;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormClient window = new FormClient();
					window.frmClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FormClient() {
		initialize();
		luongThoiGianHienTai();
		this.ip = getIP();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void bt_connectActionPerformed(ActionEvent e) {
		try {
			String address = txtServersIP.getText();
			Registry r = LocateRegistry.getRegistry(address, Server.PORT);
			InterfaceCalc ic = (InterfaceCalc) r.lookup("rmiCalc");
			Boolean check = false;
			check = ic.ketnoi();
			System.out.println("Kiem tra ket noi: " + check);
			if (check == true) {
				System.err.println("Chua dong bo");
				ic.guidolechvalayketqua(this.ip, 0);
				System.err.println("Da dong bo");
				run();
				txtCntStatus.setText("Ket noi hoan thanh");
			}
			else {
				txtCntStatus.setText("Khong tim thay server");
			}
		} catch (Exception ex) {
			
		}
	}
	
	private void initialize() {
		frmClient = new JFrame();
		frmClient.setFont(new Font("Dialog", Font.BOLD, 14));
		frmClient.setTitle("CLIENT");
		frmClient.setBounds(100, 100, 704, 277);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClient.getContentPane().setLayout(null);
		
		txtServersIP = new JTextField();
		txtServersIP.setText("SERVER'S IP");
		txtServersIP.setBounds(51, 52, 187, 20);
		frmClient.getContentPane().add(txtServersIP);
		txtServersIP.setColumns(10);
		
		JButton btnConnect = new JButton("CONNECT");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bt_connectActionPerformed(e);
			}
		});
		btnConnect.setBounds(51, 124, 187, 23);
		frmClient.getContentPane().add(btnConnect);
		
		JLabel lblNewLabel = new JLabel("CONNECTION STATUS:");
		lblNewLabel.setBounds(280, 52, 138, 14);
		frmClient.getContentPane().add(lblNewLabel);
		
		txtCntStatus = new JTextField();
		txtCntStatus.setEnabled(false);
		txtCntStatus.setBounds(410, 49, 227, 20);
		frmClient.getContentPane().add(txtCntStatus);
		txtCntStatus.setColumns(10);
		
		JLabel lblTimeNow = new JLabel("TIME NOW:");
		lblTimeNow.setBounds(280, 99, 138, 14);
		frmClient.getContentPane().add(lblTimeNow);
		
		txtTimeNow = new JTextField();
		txtTimeNow.setEnabled(false);
		txtTimeNow.setColumns(10);
		txtTimeNow.setBounds(410, 96, 227, 20);
		frmClient.getContentPane().add(txtTimeNow);
		
		JLabel lblServersTime = new JLabel("SERVER'S TIME:");
		lblServersTime.setBounds(280, 145, 138, 14);
		frmClient.getContentPane().add(lblServersTime);
		
		JLabel lblTime = new JLabel("TIME DEVIATION:");
		lblTime.setBounds(280, 189, 138, 14);
		frmClient.getContentPane().add(lblTime);
		
		txtServerTime = new JTextField();
		txtServerTime.setEnabled(false);
		txtServerTime.setColumns(10);
		txtServerTime.setBounds(410, 142, 227, 20);
		frmClient.getContentPane().add(txtServerTime);
		
		txtTimeDeviation = new JTextField();
		txtTimeDeviation.setEnabled(false);
		txtTimeDeviation.setColumns(10);
		txtTimeDeviation.setBounds(410, 186, 227, 20);
		frmClient.getContentPane().add(txtTimeDeviation);
	}
}
