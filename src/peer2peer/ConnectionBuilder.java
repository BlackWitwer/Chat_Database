/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Black
 */
public class ConnectionBuilder implements Runnable {

    private Thread th;
    private String ip;
    private Socket s;
    private Controller ctrl;
	private int port;

    public ConnectionBuilder(Controller ctrl, String ip, int aPort) {
        this.ip = ip;
        this.ctrl = ctrl;
	    this.port = aPort;

        ctrl.logMessage(Color.GREEN, "ConnectionBuilder/Run:\t: Suche Port");
        
        init();
        start();
        ctrl.logMessage(Color.GREEN, "Nach Start");
    }

    private void init() {
        th = new Thread(this);
        try {
            s = new Socket(ip, port);
        } catch (UnknownHostException ex) {
            ctrl.logMessage(Color.RED, "ConnectionBuilder:\t"+ ip + ": " + ex);
        } catch (IOException ex) {
            ctrl.logMessage(Color.RED, "ConnectionBuilder:\t"+ ip + ": " + ex);
        }
    }

    private void start() {
        th.start();
    }

    public void run() {
        try {
        	ctrl.logMessage(Color.GREEN, "Nach Start2");
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            int port = ctrl.getNextUnusedPort();
            boolean portFound = false;
            
            ctrl.logMessage(Color.GREEN, "ConnectionBuilder/Run:\t"+ ip + ": Suche Port");
            while (!portFound) {
                out.writeInt(port);
                if (in.readInt() == 1) {
                    ctrl.logMessage(Color.GREEN, "ConnectionBuilder/Run:\t"+ ip + ": Port found " + port);
                    ctrl.newClientConnection(ip, port);
                    ctrl.setUsedPorts(port);
                    portFound = true;
                } else {
                    port++;
                }
            }

        } catch (IOException ex) {
            ctrl.logMessage(Color.RED, "ConnectionBuilder:\t"+ ip + ": " + ex);
        } catch (NullPointerException ex){
        	ctrl.logMessage(Color.RED, "ConnectionBuilder:\t"+ ip + ": " + ex);
        }
    }
}
