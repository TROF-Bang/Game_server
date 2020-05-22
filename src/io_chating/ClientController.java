package io_chating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import GameUserPkg.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ClientController implements Initializable {

	@FXML TextArea txtDisplay;
	@FXML TextField txtInput;
	@FXML Button btnConn;
	@FXML Button btnSend;
	
	Socket socket;
	PrintWriter out = null;
	BufferedReader in = null;
	ClientInfoSeirialized info; 
	String name;
	User user;
	HashSet<String> roomList;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("initialize");
		txtDisplay.setEditable(false);
		//텍스트필드 키보드로 엔터키 칠때 
		txtInput.setOnAction( e -> send(txtInput.getText()));
		
		btnConn.setOnAction(e->{
			if(btnConn.getText().equals("start")) {
				startClient();
			} else if(btnConn.getText().equals("stop")){
				stopClient();
			}
		});		
		btnSend.setDisable(true);
		btnSend.setOnAction(e->send(txtInput.getText()));		
	}
	
	public void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}	
	
	public void startClient() {
		try {
			socket = new Socket("localhost",7777);	
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			info = new ClientInfoSeirialized();
//			user = new User();
			info.id = "" + (Math.random() * 10000);
			info.room = "default room";
			String serializedUser = SerializeDeserialize.toString(info);
			out.println(serializedUser);
			Platform.runLater(()->{
				displayText("[연결완료 : " + socket.getRemoteSocketAddress() + "]");
				btnSend.setDisable(false);
				btnConn.setText("stop");
			});
			
		} catch (Exception e) {
			Platform.runLater(()->displayText("[서버 통신 안됨]"));
			if (!socket.isClosed()) {
				stopClient();
			}
			return;
		}
		
//		try {
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				Platform.runLater(()->displayText("[받기완료] " + line));
//			} 
//		} catch (Exception e) {}
		
		receive();
	}
	
	public void stopClient() {
		try {
			Platform.runLater(()->{
				displayText("[연결 끊음]");
				btnConn.setText("start");
				btnSend.setDisable(true);
			});
			if(socket!=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {}
	}	
	
	public void receive() {
		Thread thread = new Thread(()->{
			try {
				String line;
				while((line = in.readLine()) != null) {
					info = (ClientInfoSeirialized) SerializeDeserialize.fromString(line);
					roomList = info.roomList;
//					System.out.println(info);
//					byte[] bytes = new byte[100];
//					InputStream is = socket.getInputStream();
//					
//					int readCount = is.read(bytes);
//					
//					if (readCount == -1) {
//						throw new IOException();
//					}
//					String data = new String(bytes, 0, readCount, "UTF-8");
//					String data = line;
					Platform.runLater(()->displayText("[받기완료] " + info.msg));

				}
			} catch (Exception e) {
				if (!socket.isClosed()) {
					Platform.runLater(()->displayText("[서버 통신 끊김]"));
					stopClient();
				}
			}
		});
		thread.start();
	}

	public void send(String data) {
		Thread thread = new Thread(()-> {
			try {
//				OutputStream os = socket.getOutputStream();
//				byte[] bytes = data.getBytes("UTF-8");
//				os.write(bytes);
//				os.flush();
				info.msg = txtInput.getText();
				String serializedUser = SerializeDeserialize.toString(info);				
				Platform.runLater(()->{
					if(!data.equals("")) {
						displayText("[보내기 완료]");
						displayText(txtInput.getText());
						txtInput.setText("");
					}
				});				
				out.println(serializedUser);
			} catch (Exception e) {
				if (!socket.isClosed()) {
					Platform.runLater(()->displayText("[서버 통신 끊김]"));
					stopClient();
				}
			}
		});
		thread.start();
	}
	
	public void send() {
		Thread thread = new Thread(() -> {
			try {
				String serializedUser = SerializeDeserialize.toString(info);
				out.println(serializedUser);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}); 
		thread.start();
	}

	@FXML
	public void creatRoom() throws Exception {
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(btnConn.getScene().getWindow());
		
		Parent parent = FXMLLoader.load(getClass().getResource("createRoom.fxml"));
		
		TextField field = (TextField)parent.lookup("#text");
		Button button = (Button)parent.lookup("#ok");
		
		button.setOnAction(e -> {
			try {
			info.room = field.getText();
			String serializedUser = SerializeDeserialize.toString(info);
			out.println(serializedUser);
			dialog.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		});
		
		Scene scene = new Scene(parent);
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
	}
	

	@SuppressWarnings("unchecked")
	@FXML
	public void choiceRoom() throws Exception {
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(btnConn.getScene().getWindow());
		
		Parent parent = FXMLLoader.load(getClass().getResource("room.fxml"));
		
		ListView<String> roomList = (ListView<String>)parent.lookup("#roomList");
//		List<GameRoom> room = ServerController.manager.allGetRoom();
		roomList.setItems(FXCollections.observableArrayList(this.roomList));
		Button choice = (Button)parent.lookup("#choice");
		choice.setOnAction(e -> {
//			int num = 0;
//			for (int i = 0; i < room.size(); i++) {
//				if(room.get(i).getRoomName() == roomList.getSelectionModel().getSelectedItem())
//					num = i;
//			}
//			roomList.getSelectionModel().getSelectedItem();
//			info.room = room.get(0).getRoomName();
//			out.println(info.room);
			try {
				
			info.room = roomList.getSelectionModel().getSelectedItem();
			String serializedUser = SerializeDeserialize.toString(info);
			out.println(serializedUser);
			dialog.close();
			
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		});
		
		Scene scene = new Scene(parent);
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
	}
}
