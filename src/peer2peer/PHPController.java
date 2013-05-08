package peer2peer;
import connectionPackages.TextData;
import connectionPackages.UserData;

import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Black
 * Date: 24.03.13
 * Time: 02:07
 * To change this template use File | Settings | File Templates.
 */
public class PHPController {

	private String indexUrl = "http://fail-and-die.comlu.com/ChatBackend.php";
	private URL url;

	private static final String OPTION = "Option";
	private static final String OPTION_UPLOAD = "UPLOAD";
	private static final String OPTION_LOAD = "LOAD";
	private static final String OPTION_UPDATE = "UPDATE";
	private static final String OPTION_DELETE = "DELETE";
	private static final String OPTION_UPMESSAGE = "UPMESSAGE";
	private static final String OPTION_LOADMESSAGE = "LOADMESSAGES";

	public PHPController() {
		init();
	}

	private void init() {
		try {
			url = new URL(getIndexUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void uploadData(UserData aUserData) {
		try {
			String val = "";
			val = addPara(val, OPTION_UPLOAD, OPTION);
			val = addPara(val, aUserData.getIdentifier(), "identifier");
			val = addPara(val, aUserData.getUsername(), "nickname");
			//val = addPara(val, aUserData.getIp(), "ip");
			//val = addPara(val, String.valueOf(aUserData.getPort()), "port");

			System.out.print(uploadStatement(val));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<TextData> loadNewMessages() {
		try {
			String val = "";
			val = addPara(val, OPTION_LOADMESSAGE, OPTION);
			val = addPara(val, Controller.getProfile().getIdentifier(), "identifier");
			String result = uploadStatement(val);
			System.out.println("Result: \t" + result.substring(0, result.indexOf(";<!--")));
			return parseTextDatasFromString(result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<TextData> parseTextDatasFromString(String aResult) {
		//74a371de8770829073c56aa88b037ad7§U§Manu§2§74a371de8770829073c56aa88b037ad7§U§Manu§T§§U§§T§§2§Hallo§2§§1§
		ArrayList<TextData> theDatas = new ArrayList<TextData>();
		for (String eachObject : aResult.split("$1$")) {
			String[] theParas = eachObject.split("$2$");
			String[] theVerfasser = theParas[0].split("§U§");
			UserData theVerfasserUsTheUserData = new UserData(theVerfasser[1], theVerfasser[0]);

			HashSet<UserData> theEmpfaengerList = new HashSet<UserData>();
			for (String eachEmpfaenger : theParas[1].split("§T§")) {
				String[] theEmpfaenger = eachEmpfaenger.split("§U§");
				theEmpfaengerList.add(new UserData(theEmpfaenger[1], theEmpfaenger[0]));
			}

			String theMessage = theParas[3];

			TextData theMessageData = new TextData(theMessage, theVerfasserUsTheUserData);
			theMessageData.setUsers(theEmpfaengerList);

			theDatas.add(theMessageData);
		}
		return theDatas;
	}

	public ArrayList<UserData> loadAllUsers() {
		try {
			String val = "";
			val = addPara(val, OPTION_LOAD, OPTION);
			String result = uploadStatement(val);
			return parseUserFromString(result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void uploadMessage(TextData aTextData, UserData aVerfasser) {
		try {
			String theEmpfaenger = "";
			for (UserData eachUser : aTextData.getUsers()) {
				if (eachUser != null) {
					theEmpfaenger = theEmpfaenger + eachUser.getIdentifier() + ";";
				}
			}
			
			String val = "";
			val = addPara(val, OPTION_UPMESSAGE, OPTION);
			val = addPara(val, aVerfasser.getIdentifier(), "identifier");
			val = addPara(val, aTextData.getText(), "message");
			val = addPara(val, theEmpfaenger, "empfaenger");
			
			uploadStatement(val);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteUser(UserData aUser) {
		try {
			String val = "";
			val = addPara(val, OPTION_DELETE, OPTION);
			val = addPara(val, aUser.getIdentifier(), "identifier");
			uploadStatement(val);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String uploadStatement(String aStatement) {
		try {
			URLConnection conn = getUrl().openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(aStatement);
			wr.flush();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String theAnswer = "";
			String line;
			while ((line = rd.readLine()) != null) {
				theAnswer += ";" + line;
			}
			return theAnswer;
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Controller.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (MalformedURLException ex) {
		} catch (IOException ex) {
		}
		return null;
	}

	private String addPara(String aVal, String aPara, String aParaName) throws UnsupportedEncodingException {
		aVal += "&" + URLEncoder.encode(aParaName, "UTF-8") + "="
				+ URLEncoder.encode(aPara, "UTF-8");
		return aVal;
	}

	private String getIndexUrl() {
		return indexUrl;
	}

	private URL getUrl() {
		return url;
	}

	private ArrayList<UserData> parseUserFromString(String aVal) {
		ArrayList<UserData> theUsers = new ArrayList<UserData>();

		for (String aUserString : aVal.split(";")) {
			String[] theParas = aUserString.split(":");
			if (theParas.length == 3 && theParas[0].length() == 32) {
				theUsers.add(new UserData(theParas[0], theParas[1], Integer.parseInt(theParas[2])));
			}
		}
		return theUsers;
	}
}
