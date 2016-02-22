package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	
	public Client(String address) throws Exception{
		socket = new Socket(InetAddress.getByName(address), 4321);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void send(String msg) {
		try {
			out.println(msg);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
