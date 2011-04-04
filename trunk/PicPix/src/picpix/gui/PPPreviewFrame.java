package picpix.gui;

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

public class PPPreviewFrame extends JFrame {

	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -6382136493843240164L;

	private static PPPreviewFrame singleton;
	private BufferedImage image;
	private Graphics2D g2d;

	private int prevWidth;
	private int prevHeight;

	private PPPreviewFrame() {
		super("Preview");
		setBackground(Color.BLACK);
	}

	public static PPPreviewFrame getInstance() {
		if (singleton == null) {
			singleton = new PPPreviewFrame();
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

	public synchronized void drawImage(Image toDraw, int x, int y) {
		g2d.drawImage(toDraw, x, y, null);
		drawPreview(toDraw, x, y);
	}

	public void diposeG2D() {
		g2d.dispose();
	}

	public synchronized void drawPreview(Image toDraw, int x, int y) {
		if (this.isVisible()) {
			BufferStrategy bf = getBufferStrategy();
			if (bf != null) {
				Graphics g = bf.getDrawGraphics();
				g.translate(0, getInsets().top);
				g.drawImage(toDraw, x, y, null);

				bf.show();
			}
		}
	}

	public synchronized void targetPreview(int x, int y, int width, int height) {
		if (this.isVisible()) {
			BufferStrategy bf = getBufferStrategy();
			if (bf != null) {
				Graphics g = bf.getDrawGraphics();
				g.translate(0, getInsets().top);
				g.setColor(Color.GREEN);
				g.drawRect(x, y, width, height);

				bf.show();
			}
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			this.createBufferStrategy(2);
			Dimension frameDim = new Dimension(prevWidth, prevHeight
					+ getInsets().top);

			this.setSize(frameDim);
			this.setPreferredSize(frameDim);
			this.setResizable(false);
		}
	}

	public BufferedImage getImage() {
		return image;
	}
}
