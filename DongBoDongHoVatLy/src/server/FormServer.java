package server;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FormServer {
	
	public static int PORT = 12345;
	public static HashMap<String, Client> listClient = new HashMap<String, Client>();
	public static ArrayList<ServerProcess> listServerProcess = new ArrayList<>();
	public static Boolean yeucaudongbo;
	InetAddress ip;
	Thread getTimeNow;
	DefaultTableModel model;
	Thread capNhatListClient;
	
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
						
					}
				}
			}
		});
		getTimeNow.start();
	}
	
	void getIPServer() {
		try {
            ip = InetAddress.getLocalHost();
            System.out.println("Your current IP address : " + ip);
            txtServerIP.setText(ip.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
	}
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
	void capNhatClient(){
        while(true){
            try {
                Thread.sleep(1000);
               model.setRowCount(0);
                ListClient.list.entrySet().forEach(entry -> {
                    model.addRow(new Object[]{
                        ListClient.list.size() ,entry.getKey(),entry.getValue().getDifferrence()
                    });
                });
        	} catch (InterruptedException ex) {
                Logger.getLogger(FormServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
	void runCapNhatClient() {
		capNhatListClient = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				capNhatClient();
			}
		});
		capNhatListClient.start();
	}
	public FormServer() throws AlreadyBoundException {
		initialize();
		model = (DefaultTableModel) table.getModel();
         
        try {
          Calc c = new Calc() {} ; 
            Registry r = LocateRegistry.createRegistry(Server.PORT);
             r.bind("rmiCalc", c);
        } catch (RemoteException ex) {
            Logger.getLogger(FormServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        runCapNhatClient();
        luongThoiGianHienTai();
        getIPServer();
	}
	

	private JFrame frmServer;
	private JTextField txtServerIP;
	private JTextField txtTimeNow;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormServer window = new FormServer();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void jButton1ActionPerformed(ActionEvent e) {
		FormServer.yeucaudongbo = true;
		try {
			Thread.sleep(1000);
            changeTimeOfSystem(ListClient.list.get("sever").getDifferrence());
        } catch (InterruptedException ex) {
        Logger.getLogger(FormServer.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setTitle("SERVER");
		frmServer.setBounds(100, 100, 756, 385);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("SERVER'S IP:");
		lblNewLabel.setBounds(10, 24, 89, 14);
		frmServer.getContentPane().add(lblNewLabel);
		
		txtServerIP = new JTextField();
		txtServerIP.setEnabled(false);
		txtServerIP.setBounds(109, 21, 226, 20);
		frmServer.getContentPane().add(txtServerIP);
		txtServerIP.setColumns(10);
		
		JLabel lblTimeNow = new JLabel("TIME NOW:");
		lblTimeNow.setBounds(357, 24, 89, 14);
		frmServer.getContentPane().add(lblTimeNow);
		
		txtTimeNow = new JTextField();
		txtTimeNow.setEnabled(false);
		txtTimeNow.setColumns(10);
		txtTimeNow.setBounds(464, 21, 226, 20);
		frmServer.getContentPane().add(txtTimeNow);
		
		JButton btnSynchronization = new JButton("SYNCHRONIZATION");
		btnSynchronization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}
		});
		btnSynchronization.setBounds(24, 123, 200, 23);
		frmServer.getContentPane().add(btnSynchronization);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(253, 65, 477, 270);
		frmServer.getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"IP", "CLIENT'S TIME", "DEVIATION"
			}
		));
		scrollPane.setViewportView(table);
	}
}
