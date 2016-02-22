package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Stage{
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private TextField chatInput = new TextField();
	private ListView<Label> chatOutput = new ListView<Label>();
	private Button sendButton = new Button("Connect");
	
	public Client(){
		super();
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 600, 800);
		super.setScene(scene);
		super.setTitle("TTM4100 [Client]");
		super.show();
		
		chatOutput.setEditable(false);
		chatOutput.setFocusTraversable(false);
		
		root.setCenter(chatOutput);
		
		chatInput.setMinWidth(scene.getWidth() - 100);
		sendButton.setMinWidth(100);
		HBox holder = new HBox(chatInput, sendButton);
		root.setBottom(holder);
		
		write("Welcome!\nEnter a server-address to start a connection\n");
		chatInput.setOnAction(e -> setup());
		sendButton.setOnAction(e -> setup());
		
		chatInput.setText("78.91.30.177");
	}
	
	private boolean connect(String address) {
		write("Trying to connect to: " + address + "\n", Color.BLUE);
		socket = new Socket();
		try {
			//Prøver å opprette forbindelse til serveren med gitt adresse og port
			socket.connect(new InetSocketAddress(InetAddress.getByName(address), 4321), 50);
			//in = hva som sendes til denne klienten fra serveren
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//out = hva som sendes til serveren fra denne klienten
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			System.out.println(".");
			return false;
		}
		return true;
	}
	
	private void setup() {
		if (connect(chatInput.getText())) {
			write("Successfully connected to the server\n", Color.GREEN);
			chatInput.setOnAction(e -> send());
			sendButton.setOnAction(e -> send());
			sendButton.setText("Send");
		}
		else {
			write("Could not connect to the given address\n", Color.RED);
		}
		chatInput.requestFocus();
		chatInput.setText("");
	}
	
	private void send() {
		chatInput.requestFocus();
		chatInput.setText("");
		out.println(chatInput.getText());
	}
	
	private void write(String message) {
		write(message, Color.BLACK);
	}
	
	private void write(String message, Color color) {
		Label item = new Label(message);
		String hex = "#"+ color.toString().substring(2);
		item.styleProperty().set("-fx-text-fill: " + hex + ";");
		chatOutput.getItems().add(item);		
	}
}
