package painters;

import gui.Supervisor;
import itc.ITCArtwork;
import itc.ITCParser;
import itl.ITLCollection;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Stack;

import javax.imageio.ImageIO;

public class MosaicPainter {

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private ITLCollection collection;

	public MosaicPainter(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight) {
		super();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;
	}

	public void setProperties(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight) {

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;
	}

	public void setCollection(ITLCollection collection) {
		this.collection = collection;
	}

	private Image handleArtwork(ITCArtwork artwork, int targetWidth,
			int targetHeight) throws IOException {

		if (!artwork.isFullyParsed()) {
			Supervisor.getInstance().reportTask(
					"Completing : " + artwork.getSource());
			ITCParser.getInstance().completeArtwork(artwork);
		}

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(artwork
				.getImageData()));

		return image.getScaledInstance(targetWidth, targetHeight,
				Image.SCALE_SMOOTH);
	}

	public BufferedImage createMosaic() throws IOException {

		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 1;

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();
		BufferedImage mosaic = gConf.createCompatibleImage(imageWidth,
				imageHeight);
		Graphics2D g2d = mosaic.createGraphics();

		Stack<ITCArtwork> randomList = new Stack<ITCArtwork>();

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				if (randomList.isEmpty()) {
					randomList.addAll(collection.getArtworks());
					Collections.shuffle(randomList);
				}

				float progress = ((float) done)
				/ ((float) mosaicHeight * (float) mosaicWidth);

				Supervisor.getInstance().reportProgress(
				"Adding tile to (" + tileX + "," + tileY + ")",
				progress);
				
				Image image = handleArtwork(randomList.pop(),
						tileWidth, tileHeight);

				g2d.drawImage(image, tileX, tileY, null);


				tileY += tileHeight;
				done++;
			}

			tileX += tileWidth;
		}

		g2d.dispose();
		return mosaic;
	}
}
