package dependent.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class MosArtPreviewFrame extends JFrame {

	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -6382136493843240164L;

	private static MosArtPreviewFrame singleton;
	private BufferedImage image;
	private Graphics2D g2d;

	private MosArtPreviewFrame() {
		super("Preview");
	}

	public static MosArtPreviewFrame getInstance() {
		if (singleton == null) {
			singleton = new MosArtPreviewFrame();
		}

		return singleton;
	}

	public void init(int imageWidth, int imageHeight) {
		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();
		image = gConf.createCompatibleImage(imageWidth, imageHeight);
		g2d = image.createGraphics();
		
		this.setSize(this.getWidth(), this.getHeight() + this.getInsets().top);
		this.setResizable(false);  
	}

	public BufferedImage getImage() {
		return image;
	}

	public void drawImage(Image toDraw, int x, int y) {
		g2d.drawImage(toDraw, x, y, null);
		
		drawPreview();
	}

	public void showTarget(int x, int y, int width, int height) {
		g2d.setColor(Color.GREEN);
		g2d.drawRect(x, y, width, height);
		
		drawPreview();
	}

	public void diposeG2D() {
		g2d.dispose();
	}
	
	private void drawPreview() {
		BufferStrategy bf = this.getBufferStrategy();
		if (bf != null) {
			 Graphics g = bf.getDrawGraphics();
			 g.translate(0, this.getInsets().top);
			 g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			this.createBufferStrategy(2);
		}
	}
}
