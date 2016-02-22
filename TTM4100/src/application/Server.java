package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;

public class Server implements Runnable{
	private Socket socket;
	private ServerSocket server;
	private final Main main;
	
	public Server(Main main) {
		this.main = main;
		
		try {
			server = new ServerSocket(4321, 10, InetAddress.getLocalHost());
		}
		catch (IOException e) {
			System.out.println("Noe gikk galt");
			e.printStackTrace();
		}
	}
	
	public String getAddress() {
		return server.getInetAddress().toString().split("/")[1];
	}

	@Override
	public void run() {
		try {
			socket = server.accept();
			String clientIP = socket.getRemoteSocketAddress().toString().substring(1).split(":")[0];
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
//					main.connectedToYou(clientIP);
				}
			});
			while (true){
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String line = in.readLine();
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
//						main.recieve(line);
					}
				});
			}
		}
		catch (IOException e) {
			System.out.println("Read failed");
			System.exit(-1);
		}
	}
}
