/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GUI.java
 *
 * Created on 07.05.2012, 12:19:57
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import connectionPackages.TextData;

import peer2peer.Connection;
import peer2peer.Controller;
import peer2peer.ObjectConnection;

/**
 * 
 * @author Black
 */
public class GUI extends javax.swing.JFrame {

	private Controller ctrl;
	private JPanel drawOverPanel;
	private JPanel drawToolPanel;
	private JTextField sizeField;
	private JCheckBox verlaufBox;
	private JPanel drawPanel;
	private JPanel screenshotPanel;
	private JTextField timerText;
	private BufferedImage draw;
	private JColorChooser tcc;
	private Color color;
	private Timer ticker;
	private int count;

	private Color destination;

	private int size;

	/**
	 * Creates new form GUI
	 * 
	 * */
	public GUI(Controller ctrl) {
		this.ctrl = ctrl;
		tcc = new JColorChooser();
		screenshotPanel = new JPanel();
		drawOverPanel = new JPanel();
		drawToolPanel = new JPanel();
		drawPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(draw, 0, 0, null); // see javadoc for more info on
												// the parameters
			}
		};
		initComponents();
		init();
	}

	public void init() {
		ticker = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				ctrl.sendScreenshot(true);
				ticker.stop();
			}
		});
		color = Color.BLACK;
		size = 10;
//		txtName.setText(ctrl.getUserData().getUsername());
		DefaultCaret caret = (DefaultCaret) txaChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		txtMessage.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				setTitle("Fehc");
//				ctrl.resetCount();

			}
		});
		// this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Fehc.png")));
		this.setIconImage(Toolkit.getDefaultToolkit().createImage("Fehc.png"));

		drawOverPanel.setLayout(new FlowLayout());
		draw = new BufferedImage(jTabbedPane1.getWidth(),
				jTabbedPane1.getHeight(), BufferedImage.TYPE_INT_ARGB);

		drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				drawPanelMouseDragged(e);
				if (verlaufBox.isSelected()) {
					colorToDestination();
				}
			}
		});
		drawPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				drawPanelMouseClicked(e);
			}
		});
		drawPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				size += -e.getWheelRotation();
				sizeField.setText(size + "");
			}
		});
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(raisedbevel,
				loweredbevel);
		drawToolPanel.setBorder(compound);

		JButton btnNew = new JButton();
		btnNew.setText("Neues Feld");
		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawOval(-200, -200, 1000, Color.WHITE);
				ctrl.sendOval(-200, -200, 1000, Color.WHITE);
			}

		});

		sizeField = new JTextField("Zeichen Gr��e");
		sizeField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				try {
					size = Integer.parseInt(sizeField.getText());
					System.out.println(size);
				} catch (NumberFormatException e) {
					sizeField.setText(10 + "");
				}
			}
		});
		sizeField.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				size += -e.getWheelRotation();
				sizeField.setText(size + "");
			}
		});
		sizeField.setPreferredSize(new Dimension(95, 20));

		verlaufBox = new JCheckBox("Farbverlauf");

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				savePicture();
			}

		});

		drawToolPanel.add(btnNew);
		drawToolPanel.add(saveButton);
		drawToolPanel.add(sizeField);
		drawToolPanel.add(verlaufBox);

		drawToolPanel.setPreferredSize(new Dimension(
				drawOverPanel.getWidth() / 5 - 10,
				drawOverPanel.getHeight() - 10));
		drawPanel.setPreferredSize(new Dimension(
				drawOverPanel.getWidth() / 5 * 4 - 10, drawOverPanel
						.getHeight() - 10));
		drawOverPanel.add(drawPanel);
		drawOverPanel.add(drawToolPanel);

		JButton send = new JButton();
		send.setText("Send Screenshot");
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count = 4;
				startTimer(true);
			}
		});

		JButton take = new JButton();
		take.setText("Hole Screenshot");
		take.setEnabled(false);
		take.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ctrl.takeScreenshot();
			}
		});

		JButton sendPartial = new JButton();
		sendPartial.setText("Sende Bereich");
		sendPartial.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count = Integer.valueOf(timerText.getText());
				startTimer(false);
			}
		});

		timerText = new JTextField("5");
		timerText.setPreferredSize(new Dimension(100, 20));

		screenshotPanel.add(send);
		screenshotPanel.add(sendPartial);
		screenshotPanel.add(take);
		screenshotPanel.add(timerText);
	}

	@SuppressWarnings("static-access")
	protected void savePicture() {
		JFileChooser saveDialogue = new JFileChooser();
		if (saveDialogue.showSaveDialog(this) == saveDialogue.APPROVE_OPTION) {
			File f = saveDialogue.getSelectedFile();
			if (!f.exists()) {
				saveFile(f);
			} else {
				JOptionPane pane = new JOptionPane();
				if (pane.showConfirmDialog(
						this,
						"Datei ist bereits vorhanden.\nSoll die Datei ersetzt werden?",
						"Ersetzen", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.ERROR_MESSAGE) == JOptionPane.OK_OPTION) {
					saveFile(f);
				}
			}
		}
	}
	
	private void saveFile(File aFile){
		try {
			ImageIO.write(
					draw,
					aFile.getName().substring(
							aFile.getName().lastIndexOf(".") + 1), aFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startTimer(final boolean isFull) {
		System.out.println("test");
		ticker = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("test2");
				if (count <= 0) {
//					ctrl.sendScreenshot(isFull);
					ticker.stop();
				} else {
					count--;
					timerText.setText(count + "");
				}
			}

		});
		ticker.start();
	}

	public void colorToDestination() {
		int toleranz = 5;
		int multiplier = 4;

		if (destination == null) {
			destination = new Color((int) (Math.random() * 255),
					(int) (Math.random() * 255), (int) (Math.random() * 255));
		}
		int newRed = color.getRed();
		int newGreen = color.getGreen();
		int newBlue = color.getBlue();

		if (color.getRed() < destination.getRed() - toleranz
				|| color.getRed() > destination.getRed() + toleranz) {
			newRed = color.getRed() + (destination.getRed() - color.getRed())
					/ Math.abs(destination.getRed() - color.getRed())
					* multiplier;
		} else if (color.getGreen() < destination.getGreen() - toleranz
				|| color.getGreen() > destination.getGreen() + toleranz) {
			newGreen = color.getGreen()
					+ (destination.getGreen() - color.getGreen())
					/ Math.abs(destination.getGreen() - color.getGreen())
					* multiplier;
		} else if (color.getBlue() < destination.getBlue() - toleranz
				|| color.getBlue() > destination.getBlue() + toleranz) {
			newBlue = color.getBlue()
					+ (destination.getBlue() - color.getBlue())
					/ Math.abs(destination.getBlue() - color.getBlue())
					* multiplier;
		} else {
			destination = new Color((int) (Math.random() * 255),
					(int) (Math.random() * 255), (int) (Math.random() * 255));
		}
		color = new Color(newRed, newGreen, newBlue);
	}

	public void updateDrawPanel() {
		if (jTabbedPane1.getSelectedComponent().equals(drawOverPanel)) {
			drawPanel.getGraphics().drawImage(draw, 0, 0, this);
		}
	}

	public void drawOval(int xPos, int yPos, int size, Color c) {
		Graphics g = draw.getGraphics();
		g.setColor(c);
		g.fillOval(xPos, yPos, size, size);
		updateDrawPanel();
	}

	public void drawImage(BufferedImage img) {
		new PictureBox(img);
	}

	public boolean txtHasFocus() {
		return txtMessage.hasFocus();
	}

	public void schreibeMessage(String msg) {
		double time = System.currentTimeMillis();
		double sec = time / 1000;
		double min = sec / 60;
		double h = min / 60;
		txaChat.append((int) (h + 2) % 24 + ":" + (int) min % 60 + " " + msg
				+ "\n");
	}

	public void neueNachricht() {
		if (jTabbedPane1.getSelectedIndex() != 2) {
			jTabbedPane1.setForegroundAt(2, Color.ORANGE);
			super.setTitle("Fehc - Neue Nachricht");
			toFront();
		}
	}

	public void updateUser(ArrayList<ObjectConnection> users) {
		txaConnections.setText("");
		txaChatUser.setText("");
		int index = listTransfereUser.getSelectedIndex();
		listTransfereUser.removeAll();
		for (ObjectConnection c : users) {
			if (c.getRun()) {
				txaConnections.setText(c.getProfile().getUsername() + "\t\t" + c.getProfile().getIp() + "\n");
				txaChatUser.setText(c.getProfile().getUsername() + "\n");
				listTransfereUser.add(c.getProfile().getUsername());
			}
		}
		if (index >= 0) {
			listTransfereUser.select(index);
		} else {
			listTransfereUser.select(index + 1);
		}
	}

	private void sendMessage() {
		ctrl.sendBrodcast(new TextData(txtMessage.getText(), Controller.getProfile()));
//		schreibeMessage(ctrl.getUserData().getUsername() + ": " + txtMessage.getText());
		txtMessage.setText("");
	}

	public void logMessage(Color c, String message) {
		colorPaneLog.append(c, message + "\n");
	}

	public void receiveFile(String path) {
		listReceivedFiles.add(path);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jFileChooser1 = new JFileChooser();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new JPanel();
		txtIp = new JTextField();
		btnConnect = new JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		txaConnections = new javax.swing.JTextArea();
		txtName = new JTextField();
		btnChangeName = new JButton();
		jPanel2 = new JPanel();
		txtPath = new JTextField();
		btnOpen = new JButton();
		btnSendFile = new JButton();
		listTransfereUser = new java.awt.List();
		listReceivedFiles = new java.awt.List();
		btnGoTo = new JButton();
		jPanel3 = new JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		txaChat = new javax.swing.JTextArea();
		btnSendMessage = new JButton();
		txtMessage = new JTextField();
		jScrollPane3 = new javax.swing.JScrollPane();
		txaChatUser = new javax.swing.JTextArea();
		jPanel4 = new JPanel();
		jScrollPane4 = new javax.swing.JScrollPane();
		colorPaneLog = new gui.ColorPane();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Fehc");

		jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				jTabbedPane1StateChanged(evt);
			}
		});

		txtIp.setText("192.168.2.103");

		btnConnect.setText("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnConnectActionPerformed(evt);
			}
		});

		txaConnections.setColumns(20);
		txaConnections.setEditable(false);
		txaConnections.setRows(5);
		jScrollPane2.setViewportView(txaConnections);

		txtName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

		btnChangeName.setText("Change Name");
		btnChangeName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnChangeNameActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				txtName,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				140,
																				Short.MAX_VALUE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(36,
																				36,
																				36)
																		.addComponent(
																				btnConnect))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				txtIp,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				140,
																				Short.MAX_VALUE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(29,
																				29,
																				29)
																		.addComponent(
																				btnChangeName)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												414,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				txtName,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				33,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				btnChangeName)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				152,
																				Short.MAX_VALUE)
																		.addComponent(
																				txtIp,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				btnConnect))
														.addComponent(
																jScrollPane2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																273,
																Short.MAX_VALUE))
										.addContainerGap()));

		jTabbedPane1.addTab("Connection", jPanel1);

		txtPath.setText("C:/Users/Black/Documents/test.txt");

		btnOpen.setText("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnOpenActionPerformed(evt);
			}
		});

		btnSendFile.setText("Send");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnSendFileActionPerformed(evt);
			}
		});

		btnGoTo.setText("Go To");
		btnGoTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnGoToActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel2Layout
																										.createSequentialGroup()
																										.addComponent(
																												btnOpen,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												76,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												234,
																												Short.MAX_VALUE)
																										.addComponent(
																												btnSendFile,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												84,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								txtPath,
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								394,
																								Short.MAX_VALUE)))
														.addComponent(
																listReceivedFiles,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																404,
																Short.MAX_VALUE)
														.addComponent(
																btnGoTo,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																404,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												listTransfereUser,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												156,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																listTransfereUser,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																273,
																Short.MAX_VALUE)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				txtPath,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								btnOpen)
																						.addComponent(
																								btnSendFile))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				listReceivedFiles,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				178,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				13,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnGoTo)))
										.addContainerGap()));

		jTabbedPane1.addTab("Transfer", jPanel2);

		txaChat.setColumns(20);
		txaChat.setEditable(false);
		txaChat.setRows(5);
		txaChat.setWrapStyleWord(true);
		txaChat.setLineWrap(true);
		jScrollPane1.setViewportView(txaChat);

		btnSendMessage.setText("Sende Nachricht");
		btnSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnSendMessageActionPerformed(evt);
			}
		});

		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				txtMessageKeyPressed(evt);
			}
		});

		txaChatUser.setColumns(20);
		txaChatUser.setEditable(false);
		txaChatUser.setRows(5);
		jScrollPane3.setViewportView(txaChatUser);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				jPanel3Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								txtMessage,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								388,
																								Short.MAX_VALUE)
																						.addComponent(
																								jScrollPane1,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								388,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jScrollPane3,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				166,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addGap(117,
																				117,
																				117)
																		.addComponent(
																				btnSendMessage,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				206,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jScrollPane3,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																199,
																Short.MAX_VALUE)
														.addComponent(
																jScrollPane1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																199,
																Short.MAX_VALUE))
										.addGap(20, 20, 20)
										.addComponent(
												txtMessage,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnSendMessage)
										.addGap(16, 16, 16)));

		jTabbedPane1.addTab("Chat", jPanel3);

		colorPaneLog.setBackground(new Color(0, 0, 0));
		jScrollPane4.setViewportView(colorPaneLog);

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 580,
				Short.MAX_VALUE));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 295,
				Short.MAX_VALUE));

		jTabbedPane1.addTab("Draw", drawOverPanel);
		jTabbedPane1.addTab("Screenshot", screenshotPanel);
		jTabbedPane1.addTab("Log", jPanel4);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jTabbedPane1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 585,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jTabbedPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 323,
								Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	public void drawPanelMouseDragged(MouseEvent e) {
		drawOval(e.getX() - size / 2, e.getY() - size / 2, size, color);
		ctrl.sendOval(e.getX() - size / 2, e.getY() - size / 2, size, color);
	}

	public void drawPanelMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			Color theColor = tcc.showDialog(drawPanel, "Color Chooser",
					Color.BLACK);
			color = theColor == null ? color : theColor;
		}
	}

	private void btnConnectActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnConnectActionPerformed
//		ctrl.createConnection(txtIp.getText());
	}// GEN-LAST:event_btnConnectActionPerformed

	private void btnSendMessageActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnSendMessageActionPerformed
		sendMessage();
	}// GEN-LAST:event_btnSendMessageActionPerformed

	private void btnOpenActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnOpenActionPerformed
		jFileChooser1.showOpenDialog(this);
		txtPath.setText(jFileChooser1.getSelectedFile().getAbsolutePath());
	}// GEN-LAST:event_btnOpenActionPerformed

	private void btnSendFileActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnSendFileActionPerformed
		try {
			File f = new File(txtPath.getText());
			ctrl.sendFile(f, listTransfereUser.getSelectedItem());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}// GEN-LAST:event_btnSendFileActionPerformed

	private void btnChangeNameActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnChangeNameActionPerformed
		if (txtName.getText().equalsIgnoreCase("")) {
			txtName.setText("DefaultUser");
		}
		ctrl.changeName(txtName.getText());
	}// GEN-LAST:event_btnChangeNameActionPerformed

	private void btnGoToActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnGoToActionPerformed
		try {
			Runtime.getRuntime().exec(
					"explorer.exe " + new File("").getAbsolutePath());
		} catch (IOException ex) {
			Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
		}
	}// GEN-LAST:event_btnGoToActionPerformed

	private void txtMessageKeyPressed(KeyEvent evt) {// GEN-FIRST:event_txtMessageKeyPressed
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMessage();
		}
	}// GEN-LAST:event_txtMessageKeyPressed

	private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jTabbedPane1StateChanged
		if (jTabbedPane1.getSelectedIndex() == 2) {
			jTabbedPane1.setForegroundAt(2, Color.BLACK);
			super.setTitle("Fehc");
		}
	}// GEN-LAST:event_jTabbedPane1StateChanged
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private JButton btnChangeName;
	private JButton btnConnect;
	private JButton btnGoTo;
	private JButton btnOpen;
	private JButton btnSendFile;
	private JButton btnSendMessage;
	private gui.ColorPane colorPaneLog;
	private JFileChooser jFileChooser1;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JPanel jPanel4;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JTabbedPane jTabbedPane1;
	private java.awt.List listReceivedFiles;
	private java.awt.List listTransfereUser;
	private javax.swing.JTextArea txaChat;
	private javax.swing.JTextArea txaChatUser;
	private javax.swing.JTextArea txaConnections;
	private JTextField txtIp;
	private JTextField txtMessage;
	private JTextField txtName;
	private JTextField txtPath;
	// End of variables declaration//GEN-END:variables
}
