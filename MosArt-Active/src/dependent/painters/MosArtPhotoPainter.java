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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

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
	private LinkedList<BufferedImage> sortedArtworks;

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
		this.sortedArtworks = new LinkedList<BufferedImage>();

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
			target[i] *= coeff;
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

		scale(rgb, (1.0f / (squareW * squareH)));

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

		double minDist = Double.POSITIVE_INFINITY;
		BufferedImage closest = null;

		for (BufferedImage image : sortedArtworks) {
			double dist = distance(getAverageRGB(image), RGB);

			if (dist > minDist) {
				break;
			} else {
				minDist = dist;
				closest = image;
			}
		}

		return closest;
	}

	private void sortArtwork() {
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;

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

			sortedArtworks.add(artwork);
			MosArtSupervisor.getInstance().reportProgress("Getting artwork",
					(float) i / (float) selectedTracks.size());
		}

		MosArtSupervisor.getInstance().reportTask(
				"Sorting artworks by average color");

		Comparator<BufferedImage> imageComparator = new Comparator<BufferedImage>() {
			@Override
			public int compare(BufferedImage o1, BufferedImage o2) {
				int[] white = { 0, 0, 0 };
				double dist1 = distance(white, getAverageRGB(o1));
				double dist2 = distance(white, getAverageRGB(o2));
				double diff = dist1 - dist2;

				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}

			}
		};

		Collections.sort(sortedArtworks, imageComparator);

		MosArtSupervisor.getInstance().reportTask("Artworks sorted !");
	}

	public void paintPhoto() throws IOException {
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		sortArtwork();

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

				BufferedImage image = getClosestArtworkFor(getAverageRGB(
						source, i * tileWidth, j * tileHeight, tileWidth,
						tileHeight));

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
