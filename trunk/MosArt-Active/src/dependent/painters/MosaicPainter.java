package dependent.painters;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import dependent.MosArtException;
import dependent.com.dt.iTunesController.iTunes;
import dependent.gui.Supervisor;
import dependent.workers.MosartArtExtractor;

public class MosaicPainter extends Thread {

	private iTunes itunes;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private BufferedImage mosaic;
	private String targetFilename;

	public MosaicPainter(iTunes itunes, String targetFilename, int imageWidth,
			int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		setProperties(itunes, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setProperties(iTunes itunes, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory()
				|| (targetFile.exists() && !targetFile.canWrite())) {
			throw new MosArtException("Can't write to : '" + targetFilename
					+ "'");
		}

		this.targetFilename = targetFilename;

		this.itunes = itunes;

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;
	}

	private void createMosaic() throws IOException {

		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		MosartArtExtractor extractor = new MosartArtExtractor(itunes,
				mosaicHeight * mosaicWidth, tileWidth, tileHeight);
		extractor.start();

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();
		mosaic = gConf.createCompatibleImage(imageWidth, imageHeight);
		Graphics2D g2d = mosaic.createGraphics();

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				Image image = extractor.getScaledImage();

				g2d.drawImage(image, tileX, tileY, null);

				tileY += tileHeight;

				float progress = ((float) ++done)
						/ ((float) mosaicHeight * (float) mosaicWidth);

				Supervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);
			}

			tileX += tileWidth;
		}

		g2d.dispose();

		Supervisor.getInstance().reportMainProgress(
				"(3/3) Saving work to " + targetFilename, 1);
		ImageIO.write(mosaic, "PNG", new File(targetFilename));

		Supervisor.getInstance().reportMainTaskFinished();
	}

	@Override
	public void run() {
		try {
			createMosaic();
		} catch (IOException e) {
			Supervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
