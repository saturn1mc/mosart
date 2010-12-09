package dependent.gui;

import independent.gui.Supervisor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import dependent.com.dt.iTunesController.iTunes;
import dependent.painter.MosaicPainter;

public class MosArt extends SwingWorker<File, String> {

	private MosaicPainter painter;
	private String targetFilename;

	public MosArt() {
		painter = null;
		targetFilename = null;
	}

	public void setTargetFilename(String targetFilename) throws MosArtException {
		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory() || !targetFile.canWrite()) {
			throw new MosArtException("Can't write to : '" + targetFilename
					+ "'");
		}

		this.targetFilename = targetFilename;
	}

	public void setMosaicProperties(int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) {

		if (painter == null) {
			painter = new MosaicPainter(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight);
		} else {
			painter.setProperties(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight);
		}

	}

	public File paint() throws IOException {

		File result = null;

		try {
			Supervisor.getInstance().reportMainProgress(
					"(1/3) Connecting to iTunes", 0.33f);

			iTunes itunes = new iTunes();

			Supervisor.getInstance().reportMainProgress(
					"(2/3) Generating Mosaic", 0.66f);
			BufferedImage mosaic = painter.createMosaic(itunes);

			Supervisor.getInstance().reportMainProgress(
					"(3/3) Saving work to " + targetFilename, 1);
			result = new File(targetFilename);
			ImageIO.write(mosaic, "PNG", result);

		} catch (Exception e) {
			Supervisor.getInstance().reportCrash(e.getMessage());
		}

		if (result != null) {
			Supervisor.getInstance().reportMainTaskFinished();
		} else {
			Supervisor.getInstance().reportCrash("Mosaic generation failed");
		}

		return result;
	}

	@Override
	protected File doInBackground() throws Exception {
		return paint();
	}
}
