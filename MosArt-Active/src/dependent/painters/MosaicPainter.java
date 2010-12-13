package dependent.painters;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dependent.MosArtException;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.gui.Supervisor;
import dependent.workers.MosartArtExtractor;

public class MosaicPainter extends Thread {

	private ArrayList<ITTrack> selectedTracks;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private BufferedImage mosaic;
	private String targetFilename;

	public MosaicPainter(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		setProperties(selectedTracks, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setProperties(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		this.selectedTracks = selectedTracks;

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory()
				|| (targetFile.exists() && !targetFile.canWrite())) {
			throw new MosArtException("Can't write to : '" + targetFilename
					+ "'");
		}

		this.targetFilename = targetFilename;
	}

	private void createMosaic() throws IOException {

		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		MosartArtExtractor extractor = new MosartArtExtractor(selectedTracks,
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

		Supervisor.getInstance().reportMainProgress(
				"Saving work to " + targetFilename, 0.66f);
		ImageIO.write(mosaic, "PNG", new File(targetFilename));

		g2d.dispose();

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
