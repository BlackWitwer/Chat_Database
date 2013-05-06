package connectionPackages;

import java.awt.Color;

public class GraphicsData extends DataObject{
	
	private int xPos;
	private int yPos;
	private int size;
	private Color color;
	
	public GraphicsData(int aXPos, int aYPos, int aSize, Color aColor, UserData aUser) {
        super(aUser);
		this.xPos = aXPos;
		this.yPos = aYPos;
		this.size = aSize;
		this.color = aColor;
	}

	public int getxPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public int getSize() {
		return size;
	}

	public Color getColor() {
		return color;
	}	
}
