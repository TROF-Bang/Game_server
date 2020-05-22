package io_chating;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientExample extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("start");
		Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		//윈도우 창이 닫혀도 스레드는 살아있다.
		//primaryStage.setOnCloseRequest( e -> System.out.println("클라이언트 윈도우창 닫힘") );
		primaryStage.show();
	}
}