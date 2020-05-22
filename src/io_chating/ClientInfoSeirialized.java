package io_chating;

import java.io.Serializable;
import java.util.HashSet;


public class ClientInfoSeirialized implements Serializable {

    private final static long serialVersionUID = 1;//Adding a serialVersionUID
    //to the class protects against a problem when new fields being added

    String id;
    
    String room; //Client's name

    String msg; //The message that the client wants to send

    HashSet<String> roomList;
    
    public ClientInfoSeirialized() {
		roomList = new HashSet<String>();
	}
    
//    String recipient; //Holds other client's name to send a private message
    //or 'all' to send a message to everyone

//    boolean showOnline; //If it's true server returns a string with all the 
    //online user

}
