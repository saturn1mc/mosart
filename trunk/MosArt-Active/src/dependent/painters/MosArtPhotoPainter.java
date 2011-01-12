/**
 * 
 */
package dependent.painters;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dependent.MosArtColorTools;
import dependent.MosArtException;
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.gui.MosArtPreviewFrame;
import dependent.workers.MosArtColorExtractor;

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

	private BufferedImage getClosestArtworkFor(int[] RGB) {

		double minDelta = Double.POSITIVE_INFINITY;
		BufferedImage closest = null;

		for (MosArtArtworkRGB candidate : artworksRGB) {
			double dist = MosArtColorTools.distance(candidate.getRGB(), RGB);

			if (dist < minDelta) {
				closest = candidate.getArtwork();
				minDelta = dist;
			}
		}

		return closest;
	}

	public void paintPhoto() throws IOException {
		float wRatio = (float) source.getWidth() / (float) imageWidth;
		float hRatio = (float) source.getHeight() / (float) imageHeight;
		int tileWidth = (int) (Math.ceil((float) imageWidth
				/ (float) mosaicWidth));
		int tileHeight = (int) (Math.ceil((float) imageHeight
				/ (float) mosaicHeight));
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		MosArtColorExtractor colorExtractor = new MosArtColorExtractor(
				selectedTracks, tileWidth, tileHeight);
		colorExtractor.launch();

		artworksRGB = colorExtractor.getArtworksRGB();

		MosArtPreviewFrame.getInstance().init(imageWidth, imageHeight);
		MosArtPreviewFrame.getInstance().setVisible(true);
		MosArtPreviewFrame.getInstance().drawImage(
				source.getScaledInstance(imageWidth, imageHeight,
						Image.SCALE_SMOOTH), 0, 0);

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				int propW = (int) ((float) tileWidth * wRatio);
				int propH = (int) ((float) tileHeight * hRatio);
				int propX = (int) ((float) i * (float) propW);
				int propY = (int) ((float) j * (float) propH);

				MosArtPreviewFrame.getInstance().targetPreview(tileX, tileY,
						tileWidth, tileHeight);

				BufferedImage image = getClosestArtworkFor(MosArtColorTools
						.getAverageRGB(source, propX, propY, propW, propH));

				MosArtPreviewFrame.getInstance().drawImage(image, tileX, tileY);

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
		ImageIO.write(MosArtPreviewFrame.getInstance().getImage(), "PNG",
				new File(targetFilename));

		MosArtPreviewFrame.getInstance().diposeG2D();

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
