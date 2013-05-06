package peer2peer;

import connectionPackages.RequestData;
import connectionPackages.UserData;
import gui.FilePanel;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Black
 * Date: 16.03.13
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
public class FileTransferController {

	private Controller ctrl;
	private FilePanel panel;
	private ChatController chatCtrl;
	private boolean onTransfer;

	public FileTransferController(Controller aCtrl, ChatController aChatCtrl ) {
		this.ctrl = aCtrl;
//		this.panel = aPanel;
		this.chatCtrl = aChatCtrl;
		this.onTransfer = false;
	}

	public void sendFile(File aFile) {
		for(UserData eachUser : getChatCtrl().getUserDataWithout()) {
			getCtrl().send(new RequestData(aFile.getAbsolutePath(), Controller.getProfile()), eachUser);
		}
	}

	public Controller getCtrl() {
		return ctrl;
	}

	public FilePanel getPanel() {
		return panel;
	}

	public ChatController getChatCtrl() {
		return chatCtrl;
	}

	public boolean isOnTransfer() {
		return onTransfer;
	}
}
