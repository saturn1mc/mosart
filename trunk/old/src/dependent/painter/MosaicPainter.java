package dependent.painter;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Stack;

import javax.imageio.ImageIO;

import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.iTunes;
import dependent.gui.Supervisor;

public class MosaicPainter {

	private static final String TEMP_IMG = "MOSART_TEMP" + File.separator
			+ "MOSART_TMP_IMG";

	private File tempFile;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

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

	private Image getScaledTrackArtwork(ITTrack track, int targetWidth,
			int targetHeight) throws IOException {

		Image artwork = null;
		int artCount = track.getArtwork().getCount();

		if (artCount != 0) {
			track.getArtwork().getItem(artCount)
					.SaveArtworkToFile(tempFile.getAbsolutePath());
			BufferedImage image = ImageIO.read(tempFile);
			artwork = image.getScaledInstance(targetWidth, targetHeight,
					Image.SCALE_SMOOTH);
		}

		return artwork;
	}

	public BufferedImage createMosaic(iTunes itunes) throws IOException {

		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		tempFile = new File(TEMP_IMG);
		tempFile.getParentFile().mkdirs();

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();
		BufferedImage mosaic = gConf.createCompatibleImage(imageWidth,
				imageHeight);
		Graphics2D g2d = mosaic.createGraphics();

		Stack<ITTrack> randomList = new Stack<ITTrack>();

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				Image image = null;

				while (image == null) {

					Supervisor.getInstance().reportTask(
							"Looking for a track with an artwork");

					if (randomList.size() == 0) {
						int trackCount = itunes.getLibraryPlaylist()
								.getTracks().getCount();

						if (trackCount > 0) {
							for (int t = 0; t < trackCount; t++) {
								randomList.add(itunes.getLibraryPlaylist()
										.getTracks().getItem(t + 1));

								Supervisor.getInstance().reportProgress(
										"Randomized artworks picking",
										((float) (t + 1) / (float) trackCount));
							}

							Collections.shuffle(randomList);
						} else {
							Supervisor.getInstance().reportCrash(
									"No tracks in iTunes");
							return null;
						}
					}

					ITTrack currentTrack = randomList.pop();

					image = getScaledTrackArtwork(currentTrack, tileWidth,
							tileHeight);

				}

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
		return mosaic;
	}
}
