/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Black
 */
public class ConnectionListener implements Runnable {

    private Thread th;
    private ServerSocket ss;
    private Socket s;
    private Controller ctrl;

    public ConnectionListener(Controller ctrl) {
        this.ctrl = ctrl;
        init();
        start();
    }

    private void init() {
        th = new Thread(this);
    }

    private void start() {
        th.start();
    }

    @SuppressWarnings("static-access")
	public void run() {
        while (true) {
        	do {
		        try {
			        ss = new ServerSocket(ctrl.getStandardPort());
		        } catch (IOException ex) {
			        ctrl.increaseStandardPort();
			        ctrl.logMessage(Color.RED, "ConnectionListener:\t" + ex);
		        }
	        } while (ss == null);

            DataInputStream in;
            DataOutputStream out;
            boolean portFound = false;

            try {
                ctrl.logMessage(Color.GREEN, "ConnecitonListener:\tWarte auf Verbindung");
                s = ss.accept();
                ctrl.logMessage(Color.GREEN, "ConnectionListener:\tVerbindung gefunden");
                portFound = false;
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());

                while (!portFound) {
                    int port = in.readInt();
                    if (ctrl.setUsedPorts(port)) {
                        portFound = true;
                        out.writeInt(1);
                        ctrl.logMessage(Color.GREEN, "ConnectionListener:\tBaue Verbindung auf");
                        if (ctrl.newServerConnection(port)) {
                            ctrl.logMessage(Color.GREEN, "ConnectionListener:\tPort: " + port);
                            in.close();
                            out.close();
                            s.close();
                        }
                    } else {
                        out.writeInt(0);
                    }
                }
                ss.close();
            } catch (IOException ex) {
                ctrl.logMessage(Color.RED, "ConnectionListener:\t" + ex);
            }

            try {
                th.sleep(50);
            } catch (InterruptedException ex) {
                ctrl.logMessage(Color.RED, "ConnectionListener:\t" + ex);
            }
        }
    }
}
