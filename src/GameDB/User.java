package GameDB;

public class User {

	private String id;
	private String room;	
	
	public User(String id, String room) {
		super();
		this.id = id;
		this.room = room;
	}
	
	public User() {		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}	
	
}
