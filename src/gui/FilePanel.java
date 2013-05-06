package gui;

import peer2peer.Controller;
import peer2peer.FileTransfer;
import peer2peer.FileTransferController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Black
 * Date: 16.03.13
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class FilePanel extends JPanel {

	private JPanel requestPanel;
	private JPanel sendPanel;
	private JPanel receivePanel;
	private JLabel theNameLabel;

	public static final int REQUEST_LAYOUT = 1;
	public static final int RECEIVE_LAYOUT = 2;
	public static final int SEND_LAYOUT = 3;

	private FileTransferController ctrl;

	public FilePanel(FileTransferController aCtrl) {
		this.ctrl = aCtrl;
		setVisible(false);
		initSendPanel();
		initRequestPanel();
	}

	public void initSendPanel() {
		sendPanel = new JPanel();
		JProgressBar theProgBar = new JProgressBar();
		sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.Y_AXIS));
		sendPanel.add(theProgBar);

		add(requestPanel);
	}

	public void initRequestPanel() {
		requestPanel = new JPanel();
		JButton theAcceptButton = new JButton("Accept");
		JButton theDeclineButton = new JButton("Decline");
		theNameLabel = new JLabel("Test");
		JPanel theButtonPanel = new JPanel();
		theButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		theButtonPanel.add(theAcceptButton);
		theButtonPanel.add(theDeclineButton);
		requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
		requestPanel.add(theNameLabel);
		requestPanel.add(theButtonPanel);
	}

	public void initReceivePanel() {

	}

	public void setSendFile(File aFile) {
		ctrl.sendFile(aFile);
	}

	public void receiveFile() {

	}

	public void setPanelLayout(int aPanelLayout) {
		sendPanel.setVisible(false);
		receivePanel.setVisible(false);
		requestPanel.setVisible(false);
		switch(aPanelLayout) {
			case REQUEST_LAYOUT:
				requestPanel.setVisible(true);
				break;
			case RECEIVE_LAYOUT:
				receivePanel.setVisible(true);
				break;
			case SEND_LAYOUT:
				sendPanel.setVisible(true);
				break;
		}
	}
}
