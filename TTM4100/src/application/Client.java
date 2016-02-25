package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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

	private Scene scene;
	
	private TextField chatInput = new TextField();
	private ListView<Label> chatOutput = new ListView<Label>();
	private Button sendButton = new Button("Connect");
	private Thread listnerThread;
	private JSONParser parser = new JSONParser();
	
	public Client(){
		super();
		BorderPane root = new BorderPane();
		scene = new Scene(root, 600, 800/3);
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
		
		write("Welcome!\nEnter a server-address to start a connection");
		chatInput.setOnAction(e -> setup());
		sendButton.setOnAction(e -> setup());
		
		chatOutput.getItems().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Object> c) {
				chatOutput.scrollTo(chatOutput.getItems().size());
			}
		});
		
		//TODO remove after develop
		try {
			chatInput.setText(InetAddress.getLocalHost().getHostAddress());
		}
		catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		sendButton.fire();
	}
	
	private boolean connect(String address) {
		write(Color.BLUE, true, "Trying to connect to: " + address);
		socket = new Socket();
		try {
			//Prøver å opprette forbindelse til serveren med gitt adresse og port
			socket.connect(new InetSocketAddress(InetAddress.getByName(address), 4321), 50);
			//in = hva som sendes til denne klienten fra serveren
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//out = hva som sendes til serveren fra denne klienten
			out = new PrintWriter(socket.getOutputStream(), true);
			
			listnerThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true) {
						try {
							formatMessage(in.readLine());
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			listnerThread.start();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}
	
	private void setup() {
		if (connect(chatInput.getText())) {
			write(Color.GREEN, true,"Successfully connected to the server");
			chatInput.setOnAction(e -> send());
			sendButton.setOnAction(e -> send());
			sendButton.setText("Send");
		}
		else {
			write(Color.RED, true, "Could not connect to the given address");
		}
		chatInput.requestFocus();
		chatInput.setText("");
	}
	
	private void send() {
		final String message[] = chatInput.getText().split("<");
		final String request = message[0];
		if (message.length == 2 && message[1].matches("[ A-z0-9]+[>]")) {
			final String content = message[1].substring(0, message[1].length() - 1);
			
			if (request.matches("login|msg")) {
				out.println(jSonFormat(request, content));
			}
		}
		else if (request.matches("logout|names|help")) {
			out.println(jSonFormat(request, "None"));
		}
		else {
			write(Color.RED, true, "Message format not recognized");
		}
		
		chatInput.requestFocus();
		chatInput.setText("");
	}
	
	private String jSonFormat(String request, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"request\":\"" + request + "\",");
		sb.append("\"content\":\"" + content + "\"}");
		return sb.toString();
	}
	
	private void write(String message) {
		write(Color.BLACK, false, message);
	}
	
	private void write(Color color, boolean bold, String... message) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				for (String msg : message) {
					sb.append(msg + "\n");
				}
				Label item = new Label(sb.toString());
				String hex = "#"+ color.toString().substring(2);
				item.styleProperty().set("-fx-text-fill: " + hex + ";");
				if (bold) {
					item.styleProperty().set(item.styleProperty().get() + "-fx-font-weight: bold;");
				}
				chatOutput.getItems().add(item);		
			}
		});
	}
	
	private void formatMessage(String message) {
		try {
			JSONObject messageObject = (JSONObject) parser.parse(message);
			String timestamp = (String) messageObject.get("timestamp");
			String sender = (String) messageObject.get("sender");
			String response = (String) messageObject.get("response");
			String content = (String) messageObject.get("content");
			write(Color.BLUEVIOLET, false, timestamp);
			write(Color.BLUEVIOLET, false, sender);
			write(Color.BLUEVIOLET, false, response);
			write(Color.BLUEVIOLET, false, content);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
