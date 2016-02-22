package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Server extends Stage{
	
	private ServerSocket server;
	private ListView<Label> clients = new ListView<Label>();
	
	public Server() {
		super();
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 400, 400);
		super.setScene(scene);
		super.setTitle("TTM4100 [Server]");
		super.show();
		
		try {
			//Prøver å opprette en serversocket på lokal IP adresse med port 4321
			server = new ServerSocket(4321, 10, InetAddress.getLocalHost());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		root.setTop(new Label("IP: " + getAddress()));
		
		VBox holder = new VBox(new Label("Connected clients:"), clients);
		holder.setSpacing(5);
		root.setCenter(holder);
		clients.setEditable(false);
		clients.setFocusTraversable(false);
		
		BorderPane.setAlignment(root.getTop(), Pos.CENTER);
		
		Thread acceptThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {				
						ServerSocketThread socket = new ServerSocketThread(server.accept());
						clients.getItems().add(new Label(socket.getAddress()));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		acceptThread.start();
	}
	
	public String getAddress() {
		return server.getInetAddress().toString().split("/")[1];
	}
}
