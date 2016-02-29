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
	private ServerSocketThread sst;
	private BufferedReader in;
	private PrintWriter out;
	private String threadUserName = "Empty";
	
	private JSONParser parser = new JSONParser();
	
	public ServerSocketThread(Server server, Socket socket) {
		this.socket = socket;
		sst = this;
		try {			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Thread serverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Thread.currentThread().setName("ServerSocketThread");
				while(true) {
					try {
						String message = in.readLine();
						
						String response = "", content = "";
						JSONObject messageObject = (JSONObject) parser.parse(message);
						
						//check what request, and content.
						//check if user is logged in, or valid commands when not logged in.
						if(threadUserName.equals("Empty") && (!messageObject.get("request").equals("login") && !messageObject.get("request").equals("help"))){
							send(jSonFormat("Error","You must be logged in to do that"));
						}
						
						else if(messageObject.get("request").equals("login")){
							String usrName = messageObject.get("content").toString();
							if(usrName.matches("[a-zA-Z0-9]+")){
								if(server.addUser(sst, usrName)){
									threadUserName = usrName;
									send(jSonFormat("Info", "Login successful"));
									send(jSonFormat("History",server.getHistory()));
								}
								else{
									send(jSonFormat("Error", "Username taken"));
								}
							}
							else{
								send(jSonFormat("Error", "Not leagal username. must match [a-zA-Z0-9]+"));
							}
						}
						
						else if(messageObject.get("request").equals("logout")){
							if(server.removeUser(sst, threadUserName)){
								send(jSonFormat("Logout", threadUserName));
								Thread.currentThread().interrupt();
								return;
							}
							else{
								send(jSonFormat("Error", "Something went wrong while loggoing out"));
							}
						}
						
						else if (messageObject.get("request").equals("ForceExit")) {
							if(server.removeUser(sst, threadUserName)){
								send(jSonFormat("ForceExit", threadUserName));
								Thread.currentThread().interrupt();
								return;
							}
							else {
								send(jSonFormat("ForceExit", threadUserName));
								Thread.currentThread().interrupt();
								return;
							}
						}
						
						else if(messageObject.get("request").equals("msg")){
							String mes = messageObject.get("content").toString();
							String[] history = {threadUserName, mes};
							server.setHistory(history);
							server.recieve(jSonFormat("Message", messageObject.get("content").toString()));
						}
						
						else if(messageObject.get("request").equals("names")){
							String currentUsers = "";
							for(String user : server.getUsers()){
								currentUsers += user + "\n";
							}
							send(currentUsers);
						}
						
						else if(messageObject.get("request").equals("help")){
							send(jSonFormat("Help", "Server commands: "
									+ "login<username>, "
									+ "logout, "
									+ "msg<message>, "
									+ "names, "
									+ "help"));
						}
						
						else{
							server.recieve(jSonFormat(response, content));
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		serverThread.start();
		
	}
	
	private String jSonFormat(String response, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"timestamp\":\"" + System.currentTimeMillis() + "\",");
		sb.append("\"sender\":\"" + threadUserName + "\",");
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
