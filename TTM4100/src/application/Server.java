package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Server extends Stage{
	
	private ServerSocket serverSocket;
	private Server server;
	private ListView<Label> clientlist = new ListView<Label>();
	private ArrayList<ServerSocketThread> clients = new ArrayList<ServerSocketThread>();
	private ArrayList<String> users = new ArrayList<String>();
	
	public Server() {
		super();
		server = this;
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 400, 400);
		super.setScene(scene);
		super.setTitle("TTM4100 [Server]");
		super.show();
		
		try {
			//Prøver å opprette en serversocket på lokal IP adresse med port 4321
			serverSocket = new ServerSocket(4321, 10, InetAddress.getLocalHost());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		root.setTop(new Label("IP: " + getAddress()));
		
		VBox holder = new VBox(new Label("Connected clients:"), clientlist);
		holder.setSpacing(5);
		root.setCenter(holder);
		clientlist.setEditable(false);
		clientlist.setFocusTraversable(false);
		
		BorderPane.setAlignment(root.getTop(), Pos.CENTER);
		
		Thread acceptThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {				
						ServerSocketThread socket = new ServerSocketThread(server, serverSocket.accept());
						clients.add(socket);
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								clientlist.getItems().add(new Label(socket.getAddress()));
							}
						});
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		acceptThread.setName("ServerAccept Thread");
		acceptThread.start();
	}
	
	public String getAddress() {
		return serverSocket.getInetAddress().toString().split("/")[1];
	}
	
	public void recieve(String message) {
		for (ServerSocketThread sSocket: clients) {
			sSocket.send(message);
		}
	}
	
	public boolean addUser(String name) {
		for (String user : users) {
			if (user.equals(name)) {
				return false;
			}
		}
		users.add(name);
		return true;
	}
	
	public boolean removeUser(String name) {
		if (users.contains(name)) {
			users.remove(name);
			return true;
		}
		return false;
	}
	
	public String[] getUsers() {
		String[] brukere = new String[users.size()];
		
		for (int i = 0; i < brukere.length; i ++) {
			brukere[i] = users.get(i);
		}
		
		return brukere;
	}
}
