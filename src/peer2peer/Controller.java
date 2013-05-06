/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer;

import gui.ChatGUI;
import gui.GUI;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectionPackages.DataObject;
import connectionPackages.DataProgramInterface;
import connectionPackages.GraphicsData;
import connectionPackages.TextData;
import connectionPackages.UserData;
import gui.ScreenshotDialog;

/**
 * 
 * @author Black
 */
public class Controller {

	private ArrayList<Integer> usedPorts = new ArrayList<Integer>();
	private ArrayList<ObjectConnection> connections = new ArrayList<ObjectConnection>();
	private ConnectionListener conListener;
	private GUI dieGUI;
    private ChatGUI chatGUI;
	private Image trayIcon = Toolkit.getDefaultToolkit()
			.createImage("Fehc.png");
	
	private static UserData profile;
	private ArrayList<ChatController> chats;
	private ChatController allChat;
	private ArrayList<FileTransferController> fileSends;
	private DataProgramInterface interpreter;

	private int standardPort;

	private static PHPController databaseConnector = new PHPController();
	private static final int PORT = 6112;

	public Controller() {
		init();
	}

	private void init() {
		profile = new UserData(System.getProperty("user.name"));
		setStandardPort(PORT);
        chatGUI = new ChatGUI(this);
        getChatGUI().setVisible(true);

		dieGUI = new GUI(this);
		conListener = new ConnectionListener(this);
		interpreter = new DataProgramInterface();

		getDatabaseConnector().uploadData(getProfile());
		startTimer();
		//connectOnline();
		buildTrayIcon();
		createAllChat();
	}

	public void createConnection(String ip, int aPort) {
		logMessage(Color.GREEN, "Controller:\t\tAufbau zu " + ip + ":" + aPort);
		if (checkIPAndPort(ip, aPort)) {
			new ConnectionBuilder(this, ip, aPort);
		}
	}

	public boolean newServerConnection(int port) {
		logMessage(Color.GREEN, "Controller:\t\tNeuer Server");
		getConnections().add(new ObjectConnection(port, this));
		return true;
	}

	public void newClientConnection(String ip, int port) {
		logMessage(Color.GREEN, "Controller:\t\tNeuer Client");
		getConnections().add(new ObjectConnection(ip, port, this));
	}

	public void sendBrodcast(DataObject aObj) {
		for (ObjectConnection con : getConnections()) {
			con.sendObject(aObj);
		}
	}

	public void connectOnline() {
		for (UserData aUser : getDatabaseConnector().loadAllUsers()) {
			if (checkIdentifierPortCombo(aUser)) {
				createConnection(aUser.getIp(), aUser.getPort());
			}
		}
	}

	public BufferedImage getScreenshot() {
		Robot robot;
		try {
			robot = new Robot();
			DisplayMode displayMode = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDisplayMode();
			Rectangle screen = new Rectangle(0, 0, displayMode.getWidth(),
					displayMode.getHeight());
			return robot.createScreenCapture(screen);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage getScreenshotArea() {
		ScreenshotDialog theDialog = new ScreenshotDialog(getChatGUI(), getScreenshot());
		theDialog.setVisible(true);
		return  theDialog.getImage();
	}

	public void takeScreenshot() {
		for (ObjectConnection con : getConnections()) {
			con.takeScreenshot();
		}
	}

	public void sendFile(File f, String name) {
//		getConnection(name).sendRequest(f);
	}

	public void sendOval(int xPos, int yPos, int size, Color c) {
		sendBrodcast(new GraphicsData(xPos, yPos, size, c, getProfile()));
	}

	public void drawOval(int xPos, int yPos, int size, Color c) {
		dieGUI.drawOval(xPos, yPos, size, c);
	}

	public void drawImage(BufferedImage img) {
		dieGUI.drawImage(img);
	}

	public void empfangeMessage(TextData aText, UserData aUser) {
        ChatController theChat = findChat(aText.getUsers());
        if (theChat != null) {
            theChat.writeInChat(aText);
        } else {
            createChat(aUser).writeInChat(aText);
        }

        //TODO unten stehende Einfügen
		/*if (!dieGUI.txtHasFocus()) {
			count++;
			dieGUI.setTitle("Fehc - (" + count + ") Neue Nachricht");
			dieGUI.toFront();
		}
		dieGUI.schreibeMessage(msg);*/
	}

	public void changeName(String name) {
		profile.setUsername(name);
		sendBrodcast(profile);
	}

	public void updateUser() {
		dieGUI.updateUser(getConnections());
        getChatGUI().updateUser(getConnectedUser());
		getAllChat().setUser(new HashSet<UserData>(getConnectedUser()));
	}

	public void logMessage(Color c, String message) {
		dieGUI.logMessage(c, message);
	}

	public void receivedFile(String path) {
		dieGUI.receiveFile(path);
	}

	public boolean checkIPAndPort(String ip, int aPort) {
		for (ObjectConnection c : getConnections()) {
			if (c.getProfile().getIp().contains(ip) && c.getProfile().getPort() == aPort) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param aUser
	 * @return true, wenn die Kombination noch nicht vorhanden ist.
	 */
	public boolean checkIdentifierPortCombo(UserData aUser) {
		if (aUser.getIp().contains(getProfile().getIp()) && aUser.getPort() == getProfile().getPort()) {
			return false;
		}
		for (ObjectConnection c : getConnections()) {
			if (c.getProfile().getIdentifier().contains(aUser.getIdentifier()) && c.getProfile().getPort() == aUser.getPort()) {
				return false;
			}
		}
		return true;
	}

	public void closeConnection(ObjectConnection con) {
		for (int i = 0; i < usedPorts.size(); i++) {
			if (usedPorts.get(i) == con.getProfile().getPort()) {
				usedPorts.set(i, 0);
			}
		}
		getConnections().remove(con);
		updateUser();
	}

	public static String getMacAddress() {
		String result = "";
		try {
			for (NetworkInterface ni : Collections.list(NetworkInterface
					.getNetworkInterfaces())) {
				byte[] hardwareAddress = ni.getHardwareAddress();

				if (hardwareAddress != null) {
					for (int i = 0; i < hardwareAddress.length; i++) {
						result += String.format((i == 0 ? "" : "-") + "%02X",
								hardwareAddress[i]);
					}
					if (!result.equalsIgnoreCase("")) {
						return result;
					}
				}
			}
		} catch (SocketException ex) {
			Logger.getLogger(Controller.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return result;
	}

	public static String getIPAddress() {
		InputStream is;
		int index = 0;
		String text = "";
		try {
			URL url = new URL("http://www.wieistmeineip.de/");
			is = url.openStream();
			text = new Scanner(is).useDelimiter("\\Z").next();
			index = text.lastIndexOf("<h1 class=\"ip\">");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.subSequence(index + 15, index + 27) + "";
	}

	public int getNextUnusedPort() {
		int port = getStandardPort() + 1;
		for (int p : usedPorts) {
			if (p >= port) {
				port = p + 1;
			}
		}
		return port;
	}

	public ObjectConnection getConnection(String name) {
		for (ObjectConnection c : getConnections()) {
			if (c.getProfile().getUsername().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}

	public int getStandardPort() {
		return standardPort;
	}

	public void increaseStandardPort() {
		standardPort++;
		getProfile().setStandardPort(standardPort);
	}

	public ArrayList<ObjectConnection> getConnections() {
		return connections;
	}

	public void buildTrayIcon() {
		try {
			SystemTray theSTI = SystemTray.getSystemTray();
			TrayIcon theTI = new TrayIcon(trayIcon, "Fehc-Torrent");
			theTI.setImageAutoSize(true);
			PopupMenu thePopUp = new PopupMenu();
			MenuItem theItem = new MenuItem();
			theItem.setLabel("GUI");
			theItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dieGUI.setVisible(true);
				}

			});
			thePopUp.add(theItem);
			theItem = new MenuItem();
			theItem.setLabel("Exit");
			theItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}

			});
			thePopUp.add(theItem);
			theTI.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dieGUI.setVisible(true);
				}

			});

			theTI.setPopupMenu(thePopUp);
			try {
				theSTI.add(theTI);
			} catch (AWTException e) {
				System.err.println(e.getMessage());
			}
		} catch (Exception ex) {

		}
	}
	
	/**
	 * Set a Port as Used. Returns false if the port is already in use.
	 * @param aPort the Port which should set as used.
	 * @return false if the port is already in use, true otherwise.
	 */
	public boolean setUsedPorts(int aPort) {
		if(!usedPorts.contains(aPort)) {
			usedPorts.add(aPort);
			return true;
		}
		return false;
	}
	
	public void interpret(DataObject aObj, UserData aUser) {
		interpreter.progress(aObj, this, aUser);
	}

    public ArrayList<UserData> getConnectedUser() {
        ArrayList<UserData> theDatas = new ArrayList<UserData>();
        for (ObjectConnection eachCon : getConnections()) {
            theDatas.add(eachCon.getProfile());
        }
        return theDatas;
    }

    public ArrayList<ChatController> getChats() {
        if (chats == null) {
            chats = new ArrayList<ChatController>();
        }
        return chats;
    }

    public ChatController createChat(UserData aUser) {
        ChatController theChat = new ChatController(aUser, this, getChatGUI());
        getChats().add(theChat);
        getChatGUI().addChatPanel(theChat.getChatPanel());
//	    FileTransferController theFile = new FileTransferController(theChat.getChatPanel().getFilePanel(), this, theChat);
        return theChat;
    }

	private void createAllChat() {
		if (allChat == null) {
			allChat = new ChatController(null, this, getChatGUI());
			getChats().add(getAllChat());
			getChatGUI().addChatPanel(getAllChat().getChatPanel());
		}
	}

    public void send(DataObject aObj, UserData aUser) {
	    if (aObj instanceof TextData) {
	        getDatabaseConnector().uploadMessage((TextData) aObj, getProfile());
	    } else {
		    ObjectConnection theConnection;
		    if ((theConnection = getConnectionForUser(aUser)) != null) {
			    theConnection.sendObject(aObj);
		    }
	    }
    }
    
    public ObjectConnection getConnectionForUser(UserData aUser) {
        for (ObjectConnection eachCon : getConnections()) {
            if (eachCon.getProfile().equals(aUser)) {
                return eachCon;
            }
        }
        return null;
    }

    public ChatController findChat(HashSet<UserData> anUsers) {
        for (ChatController eachChat : getChats()) {
            if (eachChat.getUserData().equals(anUsers)) {
                return eachChat;
            }
        }
        return null;
    }

	public ChatController findChat(UserData aUser) {
		HashSet<UserData> theList = new HashSet<UserData>();
		theList.add(aUser);
		theList.add(Controller.getProfile());
		return findChat(theList);
	}
	
	public void loadNewMessages() {
		getDatabaseConnector().loadNewMessages();
	}
	
	public void startTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				loadNewMessages();
			}
			
			
		}, 1000, 5000);
	}

	public void close() {
		//TODO Muss das nachher noch rein?
//		getDatabaseConnector().deleteUser(getProfile());
	}

    public static UserData getProfile() {
        return profile;
    }

	public ChatGUI getChatGUI() {
		return chatGUI;
	}

	public ChatController getAllChat() {
		return allChat;
	}

	private static PHPController getDatabaseConnector() {
		return databaseConnector;
	}

	private void setStandardPort(int standardPort) {
		this.standardPort = standardPort;
		getProfile().setStandardPort(standardPort);
	}
}