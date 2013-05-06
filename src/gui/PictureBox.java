package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PictureBox extends JFrame{
	
	private JPanel panel;
	private Image img;
	
	public PictureBox(final Image img){
		this.img = img;
		final double theFactor = (double)img.getHeight(this) / (double)img.getWidth(this);
		setSize(img.getWidth(this), img.getHeight(this));
		panel = new JPanel(){
			public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(img.getScaledInstance(this.getWidth(), (int)(this.getWidth()*theFactor), Image.SCALE_AREA_AVERAGING), 0, 0, null); // see javadoc for more info on the parameters            
		    }
		};
		add(panel);
		setVisible(true);
	}
}
