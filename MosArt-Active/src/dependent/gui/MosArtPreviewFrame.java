package dependent.gui;

import java.awt.Color;
import java.awt.Dimension;
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

	private int prevWidth;
	private int prevHeight;
	
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
		
		prevWidth = imageWidth;
		prevHeight = imageHeight;
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
		if (this.isVisible()) {

			new Thread() {
				@Override
				public void run() {
					BufferStrategy bf = MosArtPreviewFrame.this
							.getBufferStrategy();
					if (bf != null) {
						Graphics g = bf.getDrawGraphics();
						g.translate(0, MosArtPreviewFrame.this.getInsets().top);
						g.clearRect(0, 0, image.getWidth(), image.getHeight());
						g.drawImage(image, 0, 0, image.getWidth(),
								image.getHeight(), null);
					}
				}
			}.start();
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			this.createBufferStrategy(2);
			Dimension frameDim = new Dimension(prevWidth, prevHeight
					+ this.getInsets().top);

			this.setSize(frameDim);
			this.setPreferredSize(frameDim);
			this.setResizable(false);
		}
	}
}
