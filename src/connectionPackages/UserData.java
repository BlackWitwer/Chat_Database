package connectionPackages;

import peer2peer.Controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserData extends DataObject{

	private String username;
    private String identifier;
	private String ip;
	private int standardPort;

	public UserData(String aUsername, String aIdentifier, String aIp, int aPort, UserData aUser) {
		super(aUser);
		this.username = aUsername;
		this.identifier = aIdentifier;
		this.ip = aIp;
		this.standardPort = aPort;
	}
	
	public UserData(String aUsername, String aIdentifier) {
		this(aUsername, aIdentifier, null, 0, null);
	}
	
	public UserData(String aUsername) {
		this(aUsername, createIdentifier());
	}

	public static String createIdentifier() {
		try {
			String sDaten = Controller.getMacAddress();
			MessageDigest md = null;
			md = MessageDigest.getInstance("MD5");
			StringBuffer sbMD5SUM = new StringBuffer();
			md.update(sDaten.getBytes());
			byte[] digest = md.digest();
			for (byte d : digest) {
				sbMD5SUM.append(Integer.toHexString((d & 0xFF) | 0x100).toLowerCase().substring(1, 3));
			}
			return sbMD5SUM.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String aUsername) {
		this.username = aUsername;
	}

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
	    if (obj == null) {
		    return false;
	    }
        return identifier.equalsIgnoreCase(((UserData)obj).getIdentifier());
    }

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return standardPort;
	}

	public void setStandardPort(int standardPort) {
		this.standardPort = standardPort;
	}
}
