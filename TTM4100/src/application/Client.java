package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	public Client(String address){
		try {			
			socket = new Socket(InetAddress.getByName(address), 4321);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String msg) {
		out.println(msg);
	}
}
