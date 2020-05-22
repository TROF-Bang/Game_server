package io_chating;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import GameDB.User;
import GameDB.UserDao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ServerController implements Initializable {

	@FXML TextArea txtDisplay;
	@FXML Button btnStartStop;
	
	///////////////////////////////////////////////
	ExecutorService executorService;
	ServerSocket serverSocket;
	private ArrayList<UserManager> connectedClients;
	private UserDao userDao;	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("1");
		connectedClients = new ArrayList<>();

		//이벤트 등록
		btnStartStop.setOnAction(event -> {
			if(btnStartStop.getText().equals("start")) {
				System.out.println("1");
				startServer();
			} else if(btnStartStop.getText().equals("stop")){
				stopServer();
			}
		});
		
	}
	
	public void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}	
		
	public void startServer() {
		executorService = Executors.newFixedThreadPool(30);
		
		try {
			userDao = new UserDao();
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost",7777));
//			manager = new RoomManager();
			Platform.runLater(()->{
				displayText("[서버시작]");
//				displayText("roomManager 생성 완료");
				btnStartStop.setText("stop");
				
			});
		} catch (Exception e) {
			if (!serverSocket.isClosed()) {
				stopServer();
				return;
			}
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						Socket socket = serverSocket.accept();
						String message = "[연결 수락: " + socket.getRemoteSocketAddress()+ ": " + Thread.currentThread().getName() + "]";
						Platform.runLater(()->displayText(message));						
						UserManager client = new UserManager(socket);
						connectedClients.add(client);
						Platform.runLater(()->displayText("[연결 개수: " + connectedClients.size() + "]"));
					}
				} catch (Exception e) {
					if (!serverSocket.isClosed()) {
						stopServer();
						return;
					}
				}
			}
		};
		executorService.submit(runnable);
	}
	
	public void stopServer() {
		try {
			Iterator<UserManager> iterator = connectedClients.iterator();
			while(iterator.hasNext()) {
				UserManager client = iterator.next();
				client.curClient.close();
				connectedClients.remove(client);
			}
			
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			
			Platform.runLater(()->{
				btnStartStop.setText("start");
				displayText("[서버멈춤]");
			});
		} catch (Exception e) {	}
	}
	
	public class UserManager {
		
		public Socket curClient;
		
		public PrintWriter out;
		
		public BufferedReader in;
		
		ClientInfoSeirialized info;
		
		Manager_Test manager_Test;
		
		public UserManager(Socket curClient) {
			this.curClient = curClient;
			info = new ClientInfoSeirialized();
			manager_Test = new Manager_Test();
			System.out.println("UserManager 생성");
			receive();
		}
		
		public UserManager(Socket curClient, String room) {
			this.curClient = curClient;
			info = new ClientInfoSeirialized();
			info.room = room;
			receive();
		}
		
		public void receive() {
			System.out.println("executorservice 시작");
			Runnable runnable = new Runnable() {
	
				@SuppressWarnings("unlikely-arg-type")
				@Override
				public void run() {

					try {
						out = new PrintWriter(curClient.getOutputStream(), true);						
						in = new BufferedReader(new InputStreamReader(curClient.getInputStream()));						
						info = (ClientInfoSeirialized) SerializeDeserialize.fromString(in.readLine());
						
						if(!userDao.select(info.id))
							userDao.insertUser(new User(info.id, info.room));
						
						checkRoomList();
						String data = SerializeDeserialize.toString(info);
						out.println(data);
						
						String serializedFromClient;
						while ((serializedFromClient = in.readLine()) != null) {
							System.out.println("while 시작");
							info = (ClientInfoSeirialized) SerializeDeserialize.fromString(serializedFromClient);
							System.out.println("info 정보 : " + info);
							System.out.println(info.id + "    " + info.room);							
							
							manager_Test.room = userDao.checkUser(new User(info.id, info.room));
							manager_Test.msg = checkMsg(info.msg);
							manager_Test.roomList = checkRoomList();
							System.out.println("room check " + manager_Test.room);
							System.out.println("msg check " + manager_Test.msg);
							System.out.println("roomList check " + manager_Test.roomList);
							data = SerializeDeserialize.toString(info);
							
							if(manager_Test.room) {								
								userDao.updateUser(info.id, info.room);
							
							} else if(manager_Test.msg) {
								
								if(info.room.equals("default room")) {
//									sendMessageAllCient(info.room + ": " + info.msg);
									sendMessageAllCient(data);									
									System.out.println("Server user.getroom" + info.room);
								} else 
									sendPrivateMsg(info.room, data);
										
								
							} else if(manager_Test.roomList) {
								System.out.println(info.roomList);
								out.println(data);
							}
							System.out.println("while 끝");
						}						
						
//						String room = in.readLine();
//						System.out.println("out data : " + out);
//						System.out.println("in data : " + in);
//						System.out.println("receive 밖");
//						System.out.println("receive room :" + room);
//						if(isLegalRoom(room)) {
//							System.out.println("receive 안");
//							Platform.runLater(() -> txtDisplay.appendText("Client " + room + " connected.\n"));
//							sendMessageAllCient("<Client " + room + " has entered>");
//							out.println("Welcome, " + room);
//							
//							info.room = room;
//							
//								String serializedFromClient;
//							while((serializedFromClient = in.readLine()) != null) {
//								System.out.println(serializedFromClient);
//								info = (ClientInfoSeirialized)SerializeDeserialize.fromString(serializedFromClient);
//								System.out.println("info 정보" + info);
//								if(info.room.equals("default room")) {
//									sendMessageAllCient(info.room + ": " + info.msg);
//									System.out.println("Server user.getroom" + info.room);
//								} else {
//									if(!sendPrivateMsg(info.room, info.msg)) {
//										out.println("<Couldn't send your message to " + info.room + ">");
//									} else {
//										out.println("<Sent: " + info.msg + " Only to: " + info.room + ">");
//									}
//								}
//							}
//							Platform.runLater(() -> txtDisplay.setText("Client " + info.room + " disconnected"));
//							sendMessageAllCient("<Client " + info.room + " disconnected>");
//						} else {
//							out.println("<Connection rejected because your name is 'all' or your name is already taken>");
//						}
					} catch (Exception e) {}
					  finally {
						synchronized (connectedClients) {
		                    connectedClients.remove(this);
		                }
						System.out.println("closeConnection");
						closeConnection();
					}
				
				}

			private boolean checkRoomList() throws SQLException {
				info.roomList = userDao.roomList();
				
				if(info.roomList.size() >= 2)
					return true;
				return false;
			}

				
//					out.println(roomList);
//					Runnable runnable = new Runnable() {
//						
//						@Override
//						public void run() {
//							synchronized (connectedClients) {
//								for (UserManager current : connectedClients) {
//									current.out.println(roomList);
//								}
//							}
//							
//						}
//					};
//					executorService.submit(runnable);
					
					
//					Runnable runnable = new Runnable() {
//						
//						@Override
//						public void run() {
//							synchronized (connectedClients) {
//				                for (UserManager current : connectedClients) {
//				                    if(current.info.room.equals("default room"))
//				                    	current.out.println(msg);
//				                }
//				            }					
//						}
//					};
//					executorService.submit(runnable);

		private boolean checkMsg(String msg) {
			System.out.println("msg " + msg);
				if(msg.equals(""))
					return false;
				else
					return true;
				}
				
		};
		
			executorService.submit(runnable);
			System.out.println("executorservice 끝");
		}
		
		private void sendPrivateMsg(String senderRoom, String msg) {
//            if (senderRoom.equals(senderRoom)) {
//            }
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					synchronized (connectedClients) {
						
						for (UserManager current : connectedClients) {
			               if(current.info.room.equals(senderRoom))
//			                  current.out.println(info.msg);
			                  current.out.println(msg);			                    	
						}
		            }					
				}
			};
			executorService.submit(runnable);
        }
		
		private void closeConnection() {
		    try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (curClient != null) {
					curClient.close();
				} 
			} catch (Exception e) {}			
		}

		private void sendMessageAllCient(String msg) {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					synchronized (connectedClients) {
						
						for (UserManager current : connectedClients) {
			               if(current.info.room.equals("default room"))
//			                  current.out.println(info.msg);
			                  current.out.println(msg);			                    	
						}
		            }					
				}
			};
			executorService.submit(runnable);
		}

//		@SuppressWarnings("unlikely-arg-type")
//		private boolean isLegalRoom(String room) {
//			synchronized (connectedClients) {
//				if(room.equals("default room")) {
//					return false;
//				}
//				for (UserManager userManager : connectedClients) {
//					if(userManager.info.room.equals(room)) {
//						return false;
//					}
//				}
//				return true;
//			}
//		}
	}
	
}
