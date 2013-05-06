package connectionPackages;

import java.awt.image.BufferedImage;

public class ImageData extends DataObject{

	private int imgData[];
	private int width;
	private int height;
	private int type;
	
	public ImageData(BufferedImage aImage, UserData aUser) {
        super(aUser);
		imgData = new int[aImage.getWidth()*aImage.getHeight()];
		for (int i = 0; i < aImage.getHeight(); i++) {
			for (int j = 0; j < aImage.getWidth(); j++) {
				imgData[aImage.getWidth()*i + j] = aImage.getRGB(j, i);
			}
		}
		width = aImage.getWidth();
		height = aImage.getHeight();
		type = aImage.getType();
	}
	
	public BufferedImage getImage() {
		BufferedImage theImage = new BufferedImage(width, height, type);
		for (int i = 0; i < theImage.getHeight(); i++) {
			for (int j = 0; j < theImage.getWidth(); j++) {
					theImage.setRGB(j, i, imgData[theImage.getWidth()*i + j]);
			}
		}
		return theImage;
	}
}
