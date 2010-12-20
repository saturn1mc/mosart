package dependent.workers;

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

	private MosArtMosaicPainter mosaicPainter;
	private MosArtPhotoPainter photoPainter;

	public MosArtLauncher(int mode, ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		setMosaicProperties(mode, selectedTracks, targetFilename, imageWidth,
				imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setMosaicProperties(int mode,
			ArrayList<ITTrack> selectedTracks, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

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
				photoPainter = new MosArtPhotoPainter(selectedTracks,
						targetFilename, imageWidth, imageHeight, mosaicWidth,
						mosaicHeight);
			} else {
				photoPainter.setProperties(selectedTracks, targetFilename,
						imageWidth, imageHeight, mosaicWidth, mosaicHeight);
			}
			break;
		}
	}

	private Void paint() throws IOException {

		try {
			MosArtSupervisor.getInstance().reportMainProgress(
					"Generating Mosaic", 0.33f);
			mosaicPainter.start();

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
