package GameUserPkg;

import java.io.Serializable;

public class User implements Serializable {

	private final static long serialVersionUID = 1;
	String room;
	String msg;
	String recipient;
	
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	
}
