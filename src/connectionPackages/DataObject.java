package connectionPackages;

import java.io.Serializable;

public class DataObject implements Serializable{
    private UserData sender;

    public DataObject(UserData aUser) {
        this.sender = aUser;
    }

    public UserData getSender() {
        return sender;
    }
}
