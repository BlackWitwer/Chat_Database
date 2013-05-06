package peer2peer;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import connectionPackages.DataObject;
import connectionPackages.ImageData;
import connectionPackages.UserData;

public class ObjectConnection implements Runnable {

	private ServerSocket ss;
	private Socket s;
	private UserData profile;
	private Thread th;
	private Controller ctrl;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean run = true;
	private boolean communicationOn = true;
	private int connectionPort;

	public ObjectConnection(int port, Controller ctrl) {
		try {
			ss = new ServerSocket(port);
			s = ss.accept();
			this.ctrl = ctrl;
			init();
			start();
			// sendConnections();
		} catch (IOException ex) {
			getCtrl().logMessage(Color.GREEN, "Connection/Serverkonstruktor:\t" + ex);
		}
	}

	public ObjectConnection(String ip, int port, Controller ctrl) {
		try {
			s = new Socket(ip, port);
			this.ctrl = ctrl;
			init();
			start();
			// sendConnections();
		} catch (UnknownHostException ex) {
			ctrl.logMessage(Color.GREEN, "Connection/Clientkonstruktor:\t" + ex);
		} catch (IOException ex) {
			ctrl.logMessage(Color.GREEN, "Connection/Clientkonstruktor:\t" + ex);
		}
	}

	private void init() {
		try {
			out = new ObjectOutputStream(s.getOutputStream());
			in = new ObjectInputStream(s.getInputStream());
			th = new Thread(this);
			out.writeObject(Controller.getProfile());
		} catch (IOException ex) {
			getCtrl().logMessage(Color.WHITE, "Connection/init:\t" + ex);
		}
	}

	private void start() {
		th.start();
	}

	@Override
	public void run() {
		try {
			setProfile((UserData)in.readObject());
			getCtrl().updateUser();
		} catch (IOException ex) {
			getCtrl().logMessage(Color.WHITE, "Connection/run:\t" + ex);
		} catch (ClassNotFoundException ex) {
			getCtrl().logMessage(Color.WHITE, "Connection/run:\t" + ex);
		}
		while (run) {
			if (communicationOn) {
				try {
					getCtrl().interpret((DataObject)in.readObject(), getProfile());
				} catch (IOException ex) {
					getCtrl().logMessage(Color.WHITE, "Connection/ThreadEnde:\t" + ex);
					if (ex.getMessage().contains("Connection reset")) {
						closeConnection();
					}
				}  catch (ClassNotFoundException ex) {
					getCtrl().logMessage(Color.WHITE, "Connection/ThreadEnde:\t" + ex);
				}
//				try {
//					int option = in.readInt();
//
//					switch (option) {
//					case 1:
////						try {
////							 FileTransfer trans = new FileTransfer(in, this);
////							 trans.start();
////							 trans.join();
////						} catch (InterruptedException ex) {
////							Logger.getLogger(Connection.class.getName()).log(
////									Level.SEVERE, null, ex);
////						}
//						break;
//
//					case 2:
//						ctrl.logMessage(Color.WHITE,
//								"Connection/run:\tNeue Chat Nachricht von "
//										+ this.name);
//						java.awt.Toolkit.getDefaultToolkit().beep();
//						ctrl.empfangeMessage(name + ": " + in.readUTF());
//						break;
//
//					case 3:
//						String tempName = in.readUTF();
//						ctrl.logMessage(Color.BLUE, "Connection/run:\t"
//								+ this.name + " ändert sein Name in "
//								+ tempName);
//						this.name = tempName;
//						ctrl.updateUser();
//						break;
//
//					case 4:
//						String ip = in.readUTF();
//						ctrl.createConnection(ip);
//						break;
//
//					case 5:
//						String name = in.readUTF();
//						ctrl.logMessage(Color.YELLOW,
//								"Connection/run:\tEmpfange Request für "
//										+ name);
//						dataRequest(name);
//						break;
//
//					case 6:
//						if (in.readInt() == 1 && request.getActive()) {
//							sendFile(request.getFile());
//						}
//						request.deactivate();
//						break;
//
//					case 7:
//						String[] paras = in.readUTF().split(":");
//						ctrl.drawOval(Integer.parseInt(paras[0]),
//								Integer.parseInt(paras[1]),
//								Integer.parseInt(paras[2]),
//								new Color(Integer.parseInt(paras[3])));
//						break;
//
//					case 8:
//						ctrl.drawImage(receiveImage());
//						break;
//
//					case 9:
//						ctrl.logMessage(Color.WHITE,
//								"Connection:\tScreenshot request");
//						ctrl.sendScreenshot(true);
//						break;
//					}
//
//				} catch (IOException ex) {
//					if (ex.getMessage().contains("IOException")) {
//						ctrl.logMessage(Color.WHITE, "Connection/ThreadEnde:\t"
//								+ ex);
//					} else {
//						ctrl.logMessage(Color.RED, "Connection/ThreadEnde:\t"
//								+ ex);
//						closeConnection();
//					}
//				}
			}
		}
	}

	public void sendObject(DataObject aObject) {
		try {
			out.writeObject(aObject);
		} catch (IOException ex) {
			getCtrl().logMessage(Color.WHITE, "Connection/sendMessage:\t" + ex);
		}
	}

	public void sendFile(File f) {
		// try {
		// out.writeInt(1);
		// FileTransfer trans = new FileTransfer(f, out, this);
		// trans.start();
		// trans.join();
		// } catch (IOException ex) {
		// ctrl.logMessage(Color.YELLOW, "Connection/SendFile:\t" + ex);
		// } catch (Exception ex) {
		// System.out.println("Test: " + ex);
		// }
	}

	public void sendScreenshot(BufferedImage anImage) {
		sendObject(new ImageData(anImage, Controller.getProfile()));
		getCtrl().logMessage(Color.WHITE, "Connection:\tScreenshot send");
	}

	public void takeScreenshot() {
		try {
			out.writeInt(9);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			getCtrl().logMessage(
					Color.RED,
					"Connection/Close:\tSchließe: "
							+ s.getRemoteSocketAddress());
			run = false;
			in.close();
			out.close();
			s.close();
			try {
				ss.close();
			} catch (NullPointerException ex) {
				getCtrl().logMessage(Color.WHITE, "Connection/Close:\t" + ex);
			}
			getCtrl().closeConnection(this);
		} catch (IOException ex) {
			getCtrl().logMessage(Color.WHITE, "Conneciton/Close:\t" + ex);
		}
	}
//
//	public boolean sendRequest(File f) {
//		try {
//			request = new Request(f);
//			out.writeInt(5);
//			out.writeUTF(f.getName());
//		} catch (Exception ex) {
//			ctrl.logMessage(Color.YELLOW,
//					"Connection/requestAnswer:\tRequest Failed:" + ex);
//		}
//		return false;
//	}

//	public void dataRequest(String name) {
//		// dieMsgBox = new MsgBox(this);
//		// dieMsgBox.setFileName(name);
//	}

//	public void requestAnswer(boolean pB) {
//		try {
//			if (pB) {
//				out.writeInt(6);
//				out.writeInt(1);
//			} else {
//				out.writeInt(6);
//				out.writeInt(0);
//			}
//
//		} catch (Exception ex) {
//			ctrl.logMessage(Color.YELLOW,
//					"Connection/requestAnswer:\tRequest Failed:" + ex);
//		}
//		dieMsgBox.setVisible(false);
//	}

	public void logMessage(Color c, String message) {
		getCtrl().logMessage(c, message);
	}

	public void receivedFile(String path) {
		getCtrl().receivedFile(path);
	}

//	public BufferedImage receiveImage() {
//		int lenght;
//		setCommunication(false);
//		try {
//			lenght = in.readInt();
//			int read = 0;
//			int maxRead = 100;
//			byte[] data = new byte[lenght];
//			while (in.read(data, read, maxRead) != -1 && read < lenght) {
//				System.out.println("read: " + read + " " + maxRead);
//				read += maxRead;
//				if (read + maxRead > lenght) {
//					maxRead = lenght - read;
//				}
//			}
//			InputStream bIn = new ByteArrayInputStream(data);
//			return ImageIO.read(bIn);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			setCommunication(true);
//		}
//		return null;
//	}

	@SuppressWarnings("unused")
	private void sendConnections() {
		// for (Connection c : ctrl.getConnections()) {
		// sendMessage(4, c.getIp());
		// }
	}

	public void setCommunication(boolean com) {
		communicationOn = com;
	}

	public void deactivateCommunication() {
		communicationOn = false;
	}

	public boolean getRun() {
		return run;
	}

	/**
	 * @return the profile
	 */
	public UserData getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(UserData profile) {
		this.profile = profile;
	}

	public Controller getCtrl() {
		return ctrl;
	}
}
