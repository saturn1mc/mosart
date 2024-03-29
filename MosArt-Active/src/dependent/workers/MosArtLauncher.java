package dependent.workers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import dependent.MosArtException;
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.painters.MosArtMosaicPainter;
import dependent.painters.MosArtPhotoPainter;

public class MosArtLauncher extends SwingWorker<Void, String> {

	public static final int MOSAIC_MODE = 0;
	public static final int PHOTO_MODE = 1;

	private int mode;
	private MosArtMosaicPainter mosaicPainter;
	private MosArtPhotoPainter photoPainter;

	public MosArtLauncher(BufferedImage source, int mode,
			ArrayList<ITTrack> selectedTracks, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		setPainterProperties(source, mode, selectedTracks, targetFilename,
				imageWidth, imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setPainterProperties(BufferedImage source, int mode,
			ArrayList<ITTrack> selectedTracks, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		this.mode = mode;

		switch (mode) {
		case MOSAIC_MODE:
			if (mosaicPainter == null) {
				mosaicPainter = new MosArtMosaicPainter(selectedTracks,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			} else {
				mosaicPainter.setProperties(selectedTracks, targetFilename,
						imageWidth, imageHeight, mosaicWidth, mosaicHeight);
			}
			break;

		case PHOTO_MODE:
			if (photoPainter == null) {
				photoPainter = new MosArtPhotoPainter(source, selectedTracks,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			} else {
				photoPainter.setProperties(source, selectedTracks,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			}
			break;
		}
	}

	private Void paint() throws IOException {

		try {
			MosArtSupervisor.getInstance().reportMainProgress(
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
			MosArtSupervisor.getInstance().reportCrash(e.getMessage());
		}

		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		return paint();
	}
}
