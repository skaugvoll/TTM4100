package application;
	
import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,420,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Button clientButton = new Button("Client");
			Button serverButton = new Button("Server");
			HBox holder = new HBox(clientButton, serverButton);
			holder.setSpacing(10);
			holder.setAlignment(Pos.CENTER);
			root.setCenter(holder);
			
			clientButton.setOnAction(e -> {
				Client client = new Client();
				client.setX(primaryStage.getX() - client.getScene().getWidth() - 10);
			});
			
			serverButton.setOnAction(e -> {
				Server server = new Server();
				server.setX(primaryStage.getX() + primaryStage.getScene().getWidth() + 10);
			});
			
			primaryStage.setTitle("TTM4100 [Client/Server] chat");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			serverButton.fire();
			clientButton.fire();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
