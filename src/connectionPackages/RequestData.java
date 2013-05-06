package connectionPackages;

import java.io.File;

public class RequestData extends DataObject{
	
	private String filePath;
    private boolean active;
    
    public RequestData(String aFilePath, UserData aUser){
        super(aUser);
        active = true;
        this.filePath = aFilePath;
    }
    
    public void deactivate(){
        active = false;
    }
    
    public boolean getActive(){
        return active;
    }
}
