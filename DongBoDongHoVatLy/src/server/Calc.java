package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.Set;

public abstract class Calc extends UnicastRemoteObject implements InterfaceCalc{
	  public Calc() throws RemoteException{
	      
	  }

	    @Override
	    public Boolean ketnoi() throws RemoteException {
	        System.out.println("Sever.Calc.ketnoi() da let npoi ");
	     return true;
	    }

	    @Override
	    public Boolean yeucauThoigian() throws RemoteException {
	        System.out.println("da yeu cau");
	      return FormServer.yeucaudongbo;
	    }

	    @Override
	    public long guidolechvalayketqua(String ip, long s) throws RemoteException {
	        
	        System.out.println("ket qua client gui len//"+ ip+"//"+ s);
	        
	       long dem =0;
	        long dolechchuan;
	        ListClient.list.put(ip, new Client(ip,s));
//	        if(FormSever.yeucaudongbo== true)
//	        {
	         Set<String> keySet = ListClient.list.keySet();
	        for (String key : keySet) {
	            dem = dem +ListClient.list.get(key).getDifferrence();
	        }
	        dolechchuan =dem/(ListClient.list.size());
	         //ListClient.dolechchuan =dolechchuan;
	          ListClient.list.put("sever",new Client("sever", dolechchuan) );
	           for (String key : keySet) {
	                if(!key.equals("sever"))
	            ListClient.list.put(key, new Client(key,-ListClient.list.get(key).getDifferrence()+dolechchuan));
	        }
	            System.out.println("do lech sau tinh toán:" +ListClient.list.get(ip).getDifferrence());
	            return ListClient.list.get(ip).getDifferrence() ;
//	        }
//	       else
//	       return -1;
	    }
	  @Override
	     public String guithoigiansever()throws RemoteException {
	         System.out.println(LocalDateTime.now());
	        return  LocalDateTime.now().toString();
	     };
	   

	}
