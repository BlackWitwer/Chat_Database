package peer2peer;

import connectionPackages.DataObject;
import connectionPackages.TextData;
import connectionPackages.UserData;
import gui.ChatGUI;
import gui.ChatPanel;
import gui.FilePanel;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatController {

    private HashSet<UserData> userDatas;
    private Controller ctrl;
    private ChatGUI gui;
    private ChatPanel chatPanel;

	public ChatController(HashSet<UserData> aUser, Controller aCtrl, ChatGUI aGUI) {
        this(aUser, aCtrl, aGUI, null);
	}

	public ChatController(HashSet<UserData> aUser, Controller aCtrl, ChatGUI aGUI, FilePanel aFilePanel) {
		aUser.add(Controller.getProfile());
		setUser(aUser);
		this.ctrl = aCtrl;
		this.gui = aGUI;
		this.chatPanel = new ChatPanel(this, aFilePanel);
	}
	
	public void sendToChat(DataObject aObj) {
        if (aObj instanceof TextData) {
            ((TextData) aObj).setUsers(getUserData());
            getCtrl().send(aObj, null);
        } else {
        	for (UserData eachUser : getUserDataWithout()) {
        		getCtrl().send(aObj, eachUser);
        	}
        }
	}

    public void addUser(UserData aUser) {
        getUserData().add(aUser);
    }

	public void setUser(HashSet<UserData> aUserDatas) {
		this.userDatas = aUserDatas;
	}

    public HashSet<UserData> getUserData() {
        if (userDatas == null) {
            userDatas = new HashSet<UserData>();
        }
        return userDatas;
    }

    public HashSet<UserData> getUserDataWithout() {
	    HashSet<UserData> theUserData = (HashSet<UserData>) getUserData().clone();
	    //TODO wieder einfügen wenn nicht mehr auf einem Pc getestet werden muss
        theUserData.remove(Controller.getProfile());
        return theUserData;
    }

    public Controller getCtrl() {
        return ctrl;
    }

    public void writeInChat(TextData aText) {
        chatPanel.writeMessage(aText);
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }
}
