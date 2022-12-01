package client;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceCalc extends Remote{
	public Boolean ketnoi () throws RemoteException;
	public Boolean yeucauThoigian () throws RemoteException;
	public long guidolechvalayketqua(String ip, long s) throws RemoteException ;
	public String guithoigiansever()throws RemoteException ;
}
