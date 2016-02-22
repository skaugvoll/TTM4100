package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.scene.paint.Color;

public class ServerSocketThread {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private JSONParser parser = new JSONParser();
	
	public ServerSocketThread(Server server, Socket socket) {
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
						String message = in.readLine();
						
						String response = "", content = "";
						JSONObject messageObject = (JSONObject) parser.parse(message);
						
						
						
						server.recieve(jSonFormat(response, content));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		send("Server commands:" + Color.BLUE.toString() + "\n"
				+ "\tlogin<username>" + Color.BLUEVIOLET.toString() + "\n"
				+ "\tlogout" + Color.BLUEVIOLET.toString() + "\n"
				+ "\tmsg<message>" + Color.BLUEVIOLET.toString() + "\n"
				+ "\tnames" + Color.BLUEVIOLET.toString() + "\n"
				+ "\thelp" + Color.BLUEVIOLET.toString());
	}
	
	private String jSonFormat(String response, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"timestamp\":\"" + System.currentTimeMillis() + "\",");
		sb.append("\"sender\":\"" + getAddress() + "\",");
		sb.append("\"response\":\"" + response + "\",");
		sb.append("\"content\":\"" + content + "\"}");
		return sb.toString();
	}
	
	public String getAddress() {
		return socket.getInetAddress().toString().split("/")[1];
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void send(String message) {
		out.println(message);
	}
}
