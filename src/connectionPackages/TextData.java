package connectionPackages;

import java.util.ArrayList;
import java.util.HashSet;

public class TextData extends DataObject{

	private String text;
	private String datum;
    private HashSet<UserData> users;
    
	public TextData(String aText, String aDatum, UserData aUser) {
        super(aUser);
		text = aText;
	}

	public TextData (String aText, UserData aUser) {
		this(aText, null, aUser);
	}
	
	public String getText() {
		return text;
	}

    public HashSet<UserData> getUsers() {
        return users;
    }

    public void setUsers(HashSet<UserData> users) {
        this.users = users;
    }
}
