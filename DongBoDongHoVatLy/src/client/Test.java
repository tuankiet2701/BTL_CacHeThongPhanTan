package client;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		System.out.println("Bat dau!!!");
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd /C date " +"11-25-22");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		System.out.println("Hoan thanh!!!");
	}

}
