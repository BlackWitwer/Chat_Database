package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ScreenshotDialog extends JDialog {

	private JPanel imagePanel;
	private final BufferedImage screenshot;
	private Point startPoint;
	private Point endPoint;

	public ScreenshotDialog(JFrame aFrame, BufferedImage aScreenshot) {
		super(aFrame, true);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setUndecorated(true);
		this.screenshot = aScreenshot;

		imagePanel = new JPanel() {

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(screenshot, 0, 0, this);
			}

		};
		
		imagePanel.addMouseMotionListener(new MouseMotionAdapter(){

			@Override
			public void mouseDragged(MouseEvent e) {
				drawOnPanel(e.getPoint());
			}			
		});
		
		imagePanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				startPoint = e.getPoint();	
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				endPoint = e.getPoint();
				closeFrame();
			}
			
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
					closeFrame();
				}
			}
		});
		imagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), "Bitte w�hle den zu fotografierenden Bereich:"));
		add(imagePanel);
	}
	
	private void drawOnPanel(Point anEndPoint){
		Graphics g = imagePanel.getGraphics();
		g.drawImage(screenshot, 0, 0, this);
		g.setColor(new Color(200, 200, 200, 100));
		g.fillRect((int) startPoint.getX(), (int) startPoint.getY(), (int) (anEndPoint.getX() - startPoint.getX()), (int) (anEndPoint.getY() - startPoint.getY()));
	}

	private void closeFrame() {
		this.setVisible(false);
		this.dispose();
	}
	
	private BufferedImage refactorScreenshot(){
		try {
			Robot robot = new Robot();
//			DisplayMode displayMode = GraphicsEnvironment
//					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
//					.getDisplayMode();
			Rectangle screen = new Rectangle((int)startPoint.getX(), (int)startPoint.getY(), (int)(endPoint.getX()-startPoint.getX()), (int)(endPoint.getY()-startPoint.getY()));
			return robot.createScreenCapture(screen);
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}		
		return null;
	}

	public BufferedImage getImage() {
		return refactorScreenshot();
	}
}
