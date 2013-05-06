package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import connectionPackages.ImageData;
import peer2peer.ChatController;

import connectionPackages.UserData;
import peer2peer.Controller;

public class ChatGUI extends JFrame{

    private JPanel superPanel;
    private JPanel chatPanel;

    private JMenuBar menu;

	private JTabbedPane tabPane;
	private ArrayList<ChatPanel> chatPanels;

    private JScrollPane usersListScroll;
	private JList<UserData> usersList;

    private Controller ctrl;
	
	public ChatGUI(Controller aCtrl) {
        initComponents();
        this.ctrl = aCtrl;
	}
	
	private void initComponents() {
        superPanel = new JPanel();
        superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.Y_AXIS));

        chatPanel = new JPanel();
        chatPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JMenuItem connectMenu = new JMenuItem("Connect");
        JMenuItem screenshotMenu = new JMenuItem("Screen");
		JMenuItem screenshotAreaMenu = new JMenuItem("Screen Area");
        screenshotMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
	            screenshotMenuAction();
            }
        });
		connectMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectMenuAction();
			}
		});
		screenshotAreaMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshotAreaMenuAction();
			}
		});
        menu = new JMenuBar();
		menu.add(screenshotMenu);
		menu.add(screenshotAreaMenu);
        menu.add(connectMenu);

		tabPane = new JTabbedPane();

		usersList = new JList<UserData>();
		usersList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				UserData user = (UserData) value;
				this.setText(user.getUsername());
				return this;
			}
		});

        usersList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
	            usersListAction(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        usersListScroll = new JScrollPane(usersList);
        usersListScroll.setPreferredSize(new Dimension(150, 360));

        chatPanel.add(tabPane);
        chatPanel.add(usersListScroll);
        superPanel.add(menu);
        superPanel.add(chatPanel);
		add(superPanel);
		setResizable(false);

        usersList.setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void windowClosing(WindowEvent e) {
				getCtrl().close();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void windowIconified(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void windowActivated(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				//To change body of implemented methods use File | Settings | File Templates.
			}
		});
		pack();
	}

	private void screenshotMenuAction() {
		ChatController theChatController = ((ChatPanel)tabPane.getSelectedComponent()).getChatController();
		theChatController.sendToChat(new ImageData(getCtrl().getScreenshot(), Controller.getProfile()));
	}

	private void connectMenuAction() {
		String theIp = (String) JOptionPane.showInputDialog(null, "Ip:", "Ip eingabe", JOptionPane.PLAIN_MESSAGE, null, null, null);
		ctrl.createConnection(theIp, getCtrl().getStandardPort());
	}

	private void screenshotAreaMenuAction() {
		ChatController theChatController = ((ChatPanel)tabPane.getSelectedComponent()).getChatController();
		theChatController.sendToChat(new ImageData(getCtrl().getScreenshotArea(), Controller.getProfile()));
	}

	private void usersListAction(MouseEvent e) {
		if (e.getClickCount() > 1) {
			ChatController theChat;
			if ((theChat = getCtrl().findChat(usersList.getSelectedValue())) != null) {
				usersList.setSelectedValue(theChat.getChatPanel(), true);
			} else {
				getCtrl().createChat(usersList.getSelectedValue()).getChatPanel();
			}
		}
	}

	public void addChatPanel(ChatPanel aPanel) {
		getChatPanels().add(aPanel);
		getTabPane().add(aPanel);
		pack();
	}

	private JTabbedPane getTabPane() {
		return tabPane;
	}

	private ArrayList<ChatPanel> getChatPanels() {
		if (chatPanels == null) {
			chatPanels = new ArrayList<ChatPanel>();
		}
		return chatPanels;
	}

    public Controller getCtrl() {
        return ctrl;
    }

    public void updateUser(ArrayList<UserData> aUserList) {
        usersList.setListData(aUserList.toArray(new UserData[0]));
    }
}
