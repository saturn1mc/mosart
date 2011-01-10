/**
 * 
 */
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
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.workers.MosArtExtractor;

/**
 * @author cmaurice2
 * 
 */
public class MosArtPhotoPainter extends Thread {

	private BufferedImage source;
	private ArrayList<ITTrack> selectedTracks;
	private ArrayList<MosArtArtworkRGB> artworksRGB;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private String targetFilename;

	public MosArtPhotoPainter(BufferedImage source,
			ArrayList<ITTrack> selectedTracks, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		setProperties(source, selectedTracks, targetFilename, imageWidth,
				imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setProperties(BufferedImage source,
			ArrayList<ITTrack> selectedTracks, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		this.selectedTracks = selectedTracks;
		this.source = source;
		this.artworksRGB = new ArrayList<MosArtArtworkRGB>();

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

	private int[] getRGB(BufferedImage image, int x, int y) {

		int rgb = image.getRGB(x, y);
		int r = (rgb & 0x00ff0000) >> 16;
		int g = (rgb & 0x0000ff00) >> 8;
		int b = (rgb & 0x000000ff);

		return new int[] { r, g, b };
	}

	private void addTo(int[] target, int[] source) {
		for (int i = 0; i < target.length; i++) {
			target[i] += source[i];
		}
	}

	private void scale(int[] target, float coeff) {
		for (int i = 0; i < target.length; i++) {
			target[i] = (int) (coeff * (float) target[i]);
		}
	}

	private int[] getAverageRGB(BufferedImage image, int x, int y, int squareW,
			int squareH) {
		int[] rgb = new int[] { 0, 0, 0 };

		for (int i = x; i < x + squareW; i++) {
			for (int j = y; j < y + squareH; j++) {
				addTo(rgb, getRGB(image, i, j));
			}
		}

		scale(rgb, (1.0f / (float) (squareW * squareH)));

		return rgb;
	}

	private int[] getAverageRGB(BufferedImage image) {
		return getAverageRGB(image, 0, 0, image.getWidth(), image.getHeight());
	}

	private double distance(int[] rgb1, int[] rgb2) {
		double dist2 = Math.pow(rgb1[0] - rgb2[0], 2)
				+ Math.pow(rgb1[1] - rgb2[1], 2)
				+ Math.pow(rgb1[2] - rgb2[2], 2);
		return Math.pow(dist2, 1.0d / 2.0d);
	}

	private BufferedImage getClosestArtworkFor(int[] RGB) {

		double minDelta = Double.POSITIVE_INFINITY;
		BufferedImage closest = null;

		for (MosArtArtworkRGB ad : artworksRGB) {
			double dist = distance(ad.getRGB(), RGB);

			if (dist < minDelta) {
				closest = ad.getArtwork();
				minDelta = dist;
			}
		}

		return closest;
	}

	private void analyzeArtwork() {
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		
		artworksRGB.clear();
		
		MosArtExtractor extractor = new MosArtExtractor(selectedTracks,
				selectedTracks.size(), tileWidth, tileHeight);
		extractor.start();

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();

		for (int i = 0; i < selectedTracks.size(); i++) {

			Image image = extractor.popScaledImage();

			BufferedImage artwork = gConf.createCompatibleImage(tileWidth,
					tileHeight);
			Graphics2D g2d = artwork.createGraphics();
			g2d.drawImage(image, 0, 0, null);

			artworksRGB.add(new MosArtArtworkRGB(artwork,
					getAverageRGB(artwork)));

			MosArtSupervisor.getInstance().reportProgress(
					"Analyzing artwork color",
					(float) i / (float) selectedTracks.size());
		}
	}

	public void paintPhoto() throws IOException {
		float wRatio = (float) source.getWidth() / (float) imageWidth;
		float hRatio = (float) source.getHeight() / (float) imageHeight;
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		analyzeArtwork();

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();
		BufferedImage mosaic = gConf.createCompatibleImage(imageWidth,
				imageHeight);
		Graphics2D g2d = mosaic.createGraphics();

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				int propW = (int) ((float) tileWidth * wRatio);
				int propH = (int) ((float) tileHeight * hRatio);
				int propX = (int) ((float) i * (float) propW);
				int propY = (int) ((float) j * (float) propH);

				BufferedImage image = getClosestArtworkFor(getAverageRGB(
						source, propX, propY, propW, propH));
				g2d.drawImage(image, tileX, tileY, null);

				tileY += tileHeight;

				float progress = ((float) ++done)
						/ ((float) mosaicHeight * (float) mosaicWidth);

				MosArtSupervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);
			}

			tileX += tileWidth;
		}

		MosArtSupervisor.getInstance().reportMainProgress(
				"Saving work to " + targetFilename, 0.66f);
		ImageIO.write(mosaic, "PNG", new File(targetFilename));

		g2d.dispose();

		MosArtSupervisor.getInstance().reportMainTaskFinished();
	}

	@Override
	public void run() {
		try {
			paintPhoto();
		} catch (IOException e) {
			MosArtSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
