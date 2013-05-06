/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer;

import gui.ProgressBar;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Black
 */
public class FileTransfer extends Thread {

    private boolean mode;
    private boolean run = true;
    private boolean first = true;
    private DataInputStream in;
    private DataOutputStream out;
    private int bufferSize;
    private Connection con;
    private File fSend;
    private ProgressBar pgbBar;

    public FileTransfer(DataInputStream in, Connection c) {
        mode = false;
        this.in = in;
        con = c;
        init();
    }

    public FileTransfer(File f, DataOutputStream out, Connection c) {
        this.fSend = f;
        this.out = out;
        con = c;
        mode = true;
        init();
    }

    private void init() {
        pgbBar = new ProgressBar(this);
        bufferSize = 16384;
    }

    @Override
    public void run() {
        pgbBar.setVisible(true);
        if (mode) {  //Senden
            try {
                if (fSend.isDirectory()) {
                    out.writeInt(0);
                    sendDirectory(fSend);
                } else {
                    out.writeInt(1);
                    sendFile(fSend);
                }
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                //Empfangen
                if (in.readInt() == 0) {
                    empfangeDirectory();
                } else {
                    empfangeFile();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        con.receivedFile(fSend.getName());
        pgbBar.setVisible(false);
    }

    public void sendFile(File f) {
        InputStream fin = null;
        try {
            out.writeInt((int) f.length());
            String[] splitName = fSend.getAbsolutePath().split("\\\\");
            String fileName = splitName[splitName.length - 1];
            int index = f.getAbsolutePath().indexOf(fileName);
            f.getAbsolutePath().substring(index);
            out.writeUTF(f.getAbsolutePath().substring(index));
            fin = new FileInputStream(f);
            pgbBar.setMaxSize((int) f.length());
            pgbBar.setFileName(f.getName());
            con.logMessage(Color.YELLOW, "FileTransfer/sendFile:\tDateilänge: " + (int) f.length());
            byte[] file = new byte[bufferSize];
            int len = 0;
            long time = System.currentTimeMillis();
            for (int i = 0; (len = fin.read(file)) > 0 && run; i++) {
                pgbBar.increaseValue(len);
                if (System.currentTimeMillis() - time != 0) {
                    pgbBar.setSpeed((i * bufferSize) / (System.currentTimeMillis() - time) + " kB/s");
                }
                out.write(file, 0, len);
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void empfangeFile() {
        OutputStream fout = null;
        try {
            int lenght = in.readInt();
            String name = in.readUTF();
            if(first){
                fSend = new File(name);
                first = false;
            }
            con.logMessage(Color.WHITE, "FileTransfer/receiveFile:\tEmpfange Datei");
            File f = new File(name);
            f.createNewFile();
            fout = new FileOutputStream(f);
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            long time = System.currentTimeMillis();
            pgbBar.setFileName(f.getName());
            pgbBar.setMaxSize(lenght);
            while (f.length() < lenght && run) {
                if (lenght - f.length() < bufferSize) {
                    len = in.read(buffer, 0, (int) (lenght - f.length()));
                } else {
                    len = in.read(buffer);
                }
                pgbBar.increaseValue(len);
                if (System.currentTimeMillis() - time != 0) {
                    pgbBar.setSpeed(f.length() / (System.currentTimeMillis() - time) + " kB/s");
                }
                fout.write(buffer, 0, len);
                fout.flush();
            }
            long endTime;
            if (System.currentTimeMillis() - time == 0) {
                endTime = 1;
            } else {
                endTime = System.currentTimeMillis() - time;
            }
            long size = f.length();
            con.logMessage(Color.WHITE, "FileTransfer/receiveFile:\t" + size + "Byte empfangen in: " + (System.currentTimeMillis() - time) + "ms = " + size / endTime + "Byte/ms");
        } catch (IOException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendDirectory(File dir) {
        try {
            String[] splitName = fSend.getAbsolutePath().split("\\\\");
            String fileName = splitName[splitName.length - 1];
            int index = dir.getAbsolutePath().indexOf(fileName);
            dir.getAbsolutePath().substring(index);
            out.writeUTF(dir.getAbsolutePath().substring(index));
            out.writeInt(dir.list().length);
            for (File f : dir.listFiles()) {
                if (run) {
                    if (f.isDirectory()) {
                        out.writeInt(1);
                        sendDirectory(f);
                    } else {
                        out.writeInt(0);
                        sendFile(f);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void empfangeDirectory() {
        try {
            String path = in.readUTF();
            File f = new File(path);
            f.mkdirs();
            if (first) {
                fSend = new File(path);
                first = false;
            }
            int anz = in.readInt();
            for (int i = 0; i < anz && run; i++) {
                if (in.readInt() == 1) {
                    empfangeDirectory();
                } else {
                    empfangeFile();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        run = false;
    }
}
