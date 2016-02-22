package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSocketThread extends Socket{

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	public ServerSocketThread(Socket socket) {
		this.socket = socket;
		try {			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						if (in.ready()) {
							out.println(in.readLine());
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public String getAddress() {
		return socket.getInetAddress().toString().split("/")[1];
	}
}
