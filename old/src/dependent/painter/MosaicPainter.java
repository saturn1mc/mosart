package dependent.painter;

import independent.gui.Supervisor;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.iTunes;

public class MosaicPainter {
	private static final String TEMP_IMG = "TEMP" + File.separator
			+ "MOSART_TMP_IMG.png";

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
			track.getArtwork().getItem(1).SaveArtworkToFile(TEMP_IMG);
			BufferedImage image = ImageIO.read(new File(TEMP_IMG));
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
		int done = 1;

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

				if (randomList.size() == 0) {
					int trackCount = itunes.getLibraryPlaylist().getTracks()
							.getCount();

					if (trackCount > 0) {
						for (int t = 0; i < trackCount; i++) {
							randomList.add(itunes.getLibraryPlaylist()
									.getTracks().getItem(t + 1));
						}
					} else {
						Supervisor.getInstance().reportCrash(
								"No tracks in iTunes");
						return null;
					}
				}

				float progress = ((float) done)
						/ ((float) mosaicHeight * (float) mosaicWidth);

				Supervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);

				ITTrack currentTrack = randomList.pop();

				Image image = getScaledTrackArtwork(currentTrack, tileWidth,
						tileHeight);

				if (image != null) {
					g2d.drawImage(image, tileX, tileY, null);

					tileY += tileHeight;
					done++;
				}
			}

			tileX += tileWidth;
		}

		g2d.dispose();
		return mosaic;
	}
}
