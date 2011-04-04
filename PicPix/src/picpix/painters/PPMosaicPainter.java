package picpix.painters;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import picpix.gui.PPPreviewFrame;
import picpix.tools.PPException;
import picpix.tools.PPSupervisor;
import picpix.workers.PPExtractor;



public class PPMosaicPainter extends Thread {

	private ArrayList<String> selectedFiles;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private String targetFilename;

	public PPMosaicPainter(ArrayList<String> selectedFiles,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws PPException {

		setProperties(selectedFiles, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setProperties(ArrayList<String> selectedFiles,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws PPException {

		this.selectedFiles = selectedFiles;

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

	private void createMosaic() throws IOException {

		int tileWidth = (int)(Math.ceil((float)imageWidth / (float)mosaicWidth));
		int tileHeight = (int)(Math.ceil((float)imageHeight / (float)mosaicHeight));
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		PPExtractor extractor = new PPExtractor(selectedFiles,
				mosaicHeight * mosaicWidth, tileWidth, tileHeight);
		extractor.launch();

		PPPreviewFrame.getInstance().init(imageWidth, imageHeight);
		PPPreviewFrame.getInstance().setVisible(true);

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				Image image = extractor.popScaledImage();

				PPPreviewFrame.getInstance().drawImage(image, tileX, tileY);
				
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
		ImageIO.write(PPPreviewFrame.getInstance().getImage(), "PNG", new File(targetFilename));

		PPPreviewFrame.getInstance().diposeG2D();

		PPSupervisor.getInstance().reportMainTaskFinished();
	}

	@Override
	public void run() {
		try {
			createMosaic();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
