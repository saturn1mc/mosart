package picpix.workers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import picpix.painters.PPMosaicPainter;
import picpix.painters.PPPhotoPainter;
import picpix.tools.PPException;
import picpix.tools.PPSupervisor;



public class PPLauncher extends SwingWorker<Void, String> {

	public static final int MOSAIC_MODE = 0;
	public static final int PHOTO_MODE = 1;

	private int mode;
	private PPMosaicPainter mosaicPainter;
	private PPPhotoPainter photoPainter;

	public PPLauncher(BufferedImage source, int mode,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		setPainterProperties(source, mode, selectedFiles, targetFilename,
				imageWidth, imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setPainterProperties(BufferedImage source, int mode,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		this.mode = mode;

		switch (mode) {
		case MOSAIC_MODE:
			if (mosaicPainter == null) {
				mosaicPainter = new PPMosaicPainter(selectedFiles,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			} else {
				mosaicPainter.setProperties(selectedFiles, targetFilename,
						imageWidth, imageHeight, mosaicWidth, mosaicHeight);
			}
			break;

		case PHOTO_MODE:
			if (photoPainter == null) {
				photoPainter = new PPPhotoPainter(source, selectedFiles,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			} else {
				photoPainter.setProperties(source, selectedFiles,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			}
			break;
		}
	}

	private Void paint() throws IOException {

		try {
			PPSupervisor.getInstance().reportMainProgress(
					"Generating Mosaic", 0.33f);

			switch (mode) {
			case MOSAIC_MODE:
				mosaicPainter.start();
				break;
			case PHOTO_MODE:
				photoPainter.start();
				break;
			}

		} catch (Exception e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}

		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		return paint();
	}
}
