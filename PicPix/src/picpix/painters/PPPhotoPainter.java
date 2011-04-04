/**
 * 
 */
package picpix.painters;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import picpix.gui.PPPreviewFrame;
import picpix.tools.PPColorTools;
import picpix.tools.PPException;
import picpix.tools.PPSupervisor;
import picpix.workers.PPColorExtractor;



/**
 * @author cmaurice2
 * 
 */
public class PPPhotoPainter extends Thread {

	private BufferedImage source;
	private ArrayList<String> selectedFiles;
	private ArrayList<PPImageRGB> artworksRGB;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private String targetFilename;

	public PPPhotoPainter(BufferedImage source,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		setProperties(source, selectedFiles, targetFilename, imageWidth,
				imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setProperties(BufferedImage source,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		this.selectedFiles = selectedFiles;
		this.source = source;
		this.artworksRGB = new ArrayList<PPImageRGB>();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory()
				|| (targetFile.exists() && !targetFile.canWrite())) {
			throw new PPException("Can't write to : '" + targetFilename
					+ "'");
		}

		this.targetFilename = targetFilename;
	}

	private BufferedImage getClosestArtworkFor(int[] RGB) {

		double minDelta = Double.POSITIVE_INFINITY;
		BufferedImage closest = null;

		for (PPImageRGB candidate : artworksRGB) {
			double dist = PPColorTools.distance(candidate.getRGB(), RGB);

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

		PPColorExtractor colorExtractor = new PPColorExtractor(
				selectedFiles, tileWidth, tileHeight);
		colorExtractor.launch();

		artworksRGB = colorExtractor.getArtworksRGB();

		PPPreviewFrame.getInstance().init(imageWidth, imageHeight);
		PPPreviewFrame.getInstance().setVisible(true);
		PPPreviewFrame.getInstance().drawImage(
				source.getScaledInstance(imageWidth, imageHeight,
						Image.SCALE_SMOOTH), 0, 0);

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				int propW = (int) ((float) tileWidth * wRatio);
				int propH = (int) ((float) tileHeight * hRatio);
				int propX = (int) ((float) i * (float) propW);
				int propY = (int) ((float) j * (float) propH);

				PPPreviewFrame.getInstance().targetPreview(tileX, tileY,
						tileWidth, tileHeight);

				BufferedImage image = getClosestArtworkFor(PPColorTools
						.getAverageRGB(source, propX, propY, propW, propH));

				PPPreviewFrame.getInstance().drawImage(image, tileX, tileY);
				image.flush();
				
				tileY += tileHeight;

				float progress = ((float) ++done)
						/ ((float) mosaicHeight * (float) mosaicWidth);

				PPSupervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);
			}

			tileX += tileWidth;
		}

		PPSupervisor.getInstance().reportMainProgress(
				"Saving work to " + targetFilename, 0.66f);
		ImageIO.write(PPPreviewFrame.getInstance().getImage(), "PNG",
				new File(targetFilename));

		PPPreviewFrame.getInstance().diposeG2D();

		PPSupervisor.getInstance().reportMainTaskFinished();
	}

	@Override
	public void run() {
		try {
			paintPhoto();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
