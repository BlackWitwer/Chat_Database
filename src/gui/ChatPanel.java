package gui;

import connectionPackages.TextData;
import peer2peer.ChatController;
import peer2peer.Controller;
import peer2peer.FileTransferController;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

public class ChatPanel extends JPanel{

	private ChatController ctrlChat;
	
	private JTextArea chat;
	private JTextField eingabe;
	private FilePanel filePanel;

	public ChatPanel(ChatController aCtrlChat) {
		this.ctrlChat = aCtrlChat;
		initComponents();
	}

	public ChatPanel(ChatController aCtrlChat, FilePanel aFilePanel) {
		this.ctrlChat = aCtrlChat;
		filePanel = aFilePanel;
		initComponents();
	}

	private void initComponents() {
		JPanel eingabePanel = new JPanel();
		JButton senden = new JButton("Senden");

		chat = new JTextArea();
		chat.setPreferredSize(new Dimension(380, 300));
		chat.setEditable(false);
//		DropTargetListener dropTargetListener =
//				new DropTargetListener() {
//
//					// Die Maus betritt die Komponente mit
//					// einem Objekt
//					public void dragEnter(DropTargetDragEvent e) {
//						setPanelVisibility(true);
//					}
//
//					// Die Komponente wird verlassen
//					public void dragExit(DropTargetEvent e) {
//						setPanelVisibility(false);
//					}
//
//					// Die Maus bewegt sich über die Komponente
//					public void dragOver(DropTargetDragEvent e) {}
//
//					public void drop(DropTargetDropEvent e) {
//						try {
//							Transferable tr = e.getTransferable();
//							DataFlavor[] flavors = tr.getTransferDataFlavors();
//							for (int i = 0; i < flavors.length; i++)
//								if (flavors[i].isFlavorJavaFileListType()) {
//									// Zunächst annehmen
//									e.acceptDrop (e.getDropAction());
//									List files = (List)tr.getTransferData(flavors[i]);
//									if (filePanel != null) {
//										filePanel.setSendFile((File) files.get(0));
//									}
//									e.dropComplete(true);
//									return;
//								}
//						} catch (Throwable t) { t.printStackTrace(); }
//						// Ein Problem ist aufgetreten
//						e.rejectDrop();
//					}
//
//					// Jemand hat die Art des Drops (Move, Copy, Link)
//					// geändert
//					public void dropActionChanged(
//							DropTargetDragEvent e) {}
//				};
//		DropTarget dropTarget = new DropTarget(
//				chat, dropTargetListener);


		eingabe = new JTextField();
		eingabe.setPreferredSize(new Dimension(200, 30));
		eingabe.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {

			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {

			}
		});

		senden.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}
		});
//		senden.addMouseListener(new MouseListener() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				//To change body of implemented methods use File | Settings | File Templates.
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				//To change body of implemented methods use File | Settings | File Templates.
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				//To change body of implemented methods use File | Settings | File Templates.
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				int theX = (int)(Math.random() * 200);
//				int theY = (int)(Math.random() * 200);
//				senden.setLocation(theX, theY);
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				//To change body of implemented methods use File | Settings | File Templates.
//			}
//		});

		eingabePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
		eingabePanel.add(eingabe);
		eingabePanel.add(senden);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(chat);
//		add(filePanel);
		add(eingabePanel);
	}

	private void sendMessage() {
		String theMessage = eingabe.getText();
		getChatController().sendToChat(new TextData(theMessage, Controller.getProfile()));
		writeMessage(new TextData(theMessage, Controller.getProfile()));
        eingabe.setText("");
	}
	
	public ChatController getChatController() {
		return ctrlChat;
	}

    public void writeMessage(TextData aText) {
        double time = System.currentTimeMillis();
        double sec = time / 1000;
        double min = sec / 60;
        double h = min / 60;
        String theTimestamp = (int) (h + 2) % 24 + ":" + (int) min % 60 + " ";
        chat.append(theTimestamp + aText.getSender().getUsername() + ": " + aText.getText() + "\n");
    }

//	public void setPanelVisibility(boolean aFlag) {
//		if(filePanel == null) return;
//		filePanel.setVisible(aFlag);
//		if (aFlag) {
//			chat.setPreferredSize(new Dimension(380, 240));
//		} else {
//			chat.setPreferredSize(new Dimension(380, 300));
//		}
//	}

//	public FilePanel getFilePanel() {
//		return filePanel;
//	}
}
