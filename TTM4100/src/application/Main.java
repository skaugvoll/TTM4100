package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	
	Button clientButton;
	Button serverButton;
	int counter = 0;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,420,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			clientButton = new Button("Client");
			serverButton = new Button("Server");
			HBox holder = new HBox(clientButton, serverButton);
			holder.setSpacing(10);
			holder.setAlignment(Pos.CENTER);
			root.setCenter(holder);
			
			clientButton.setOnAction(e -> {
				Client client = new Client();
				client.setX(primaryStage.getX() - client.getScene().getWidth() - 10);
				client.setY(client.getY() + counter - 200);
				counter += client.getScene().getHeight() + 40;
			});
			
			serverButton.setOnAction(e -> {
				Server server = new Server();
				server.setX(primaryStage.getX() + primaryStage.getScene().getWidth() + 10);
			});
			
			primaryStage.setTitle("TTM4100 [Client/Server] chat");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(e -> System.exit(1));
			
			//TODO Remove after develop is done;
			developMode();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void developMode() {
		serverButton.fire();
		clientButton.fire();
		clientButton.fire();
		clientButton.fire();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
