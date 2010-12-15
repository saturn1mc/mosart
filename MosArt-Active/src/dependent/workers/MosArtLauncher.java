package dependent.workers;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import dependent.MosArtException;
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.ITTrackCollection;
import dependent.painters.MosArtMosaicPainter;

public class MosArtLauncher extends SwingWorker<Void, String> {

	private MosArtMosaicPainter painter;

	public MosArtLauncher(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		setMosaicProperties(selectedTracks, targetFilename, imageWidth,
				imageHeight, mosaicWidth, mosaicHeight);
	}

	public MosArtLauncher(ITTrackCollection selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		ArrayList<ITTrack> tracks = new ArrayList<ITTrack>();

		int trackCount = selectedTracks.getCount();

		for (int i = 0; i < trackCount; i++) {
			tracks.add(selectedTracks.getItem(i + 1));
			MosArtSupervisor.getInstance().reportProgress(
					"Listing selected tracks...",
					((float) (i + 1) / (float) trackCount));
		}

		setMosaicProperties(tracks, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setMosaicProperties(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		if (painter == null) {
			painter = new MosArtMosaicPainter(selectedTracks, targetFilename,
					imageWidth, imageHeight, mosaicWidth, mosaicHeight);
		} else {
			painter.setProperties(selectedTracks, targetFilename, imageWidth,
					imageHeight, mosaicWidth, mosaicHeight);
		}

	}

	private Void paint() throws IOException {

		try {
			MosArtSupervisor.getInstance().reportMainProgress("Generating Mosaic",
					0.33f);
			painter.start();

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
