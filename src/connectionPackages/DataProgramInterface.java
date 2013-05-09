package connectionPackages;

import peer2peer.Controller;

/**
 * Stellt den Interpreter zwischen Sendepacketen und Programm
 * @author Black
 *
 */
public class DataProgramInterface {
	
	public void progress(DataObject aObj, Controller aController, UserData aUser) {
		if (aObj instanceof UserData) {
			
		} else if (aObj instanceof TextData) {
			aController.empfangeMessage((TextData) aObj);
		} else if (aObj instanceof RequestData) {
			
		} else if (aObj instanceof GraphicsData) {
			GraphicsData theObj = (GraphicsData)aObj;
			aController.drawOval(theObj.getxPos(), theObj.getYPos(), theObj.getSize(), theObj.getColor());
		} else if (aObj instanceof ImageData) {
			aController.drawImage(((ImageData)aObj).getImage());
		}
	}

}
