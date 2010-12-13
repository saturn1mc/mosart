package dependent.workers;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import dependent.MosArtException;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.ITTrackCollection;
import dependent.gui.Supervisor;
import dependent.painters.MosaicPainter;

public class MosArtLauncher extends SwingWorker<Void, String> {

	private MosaicPainter painter;

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
			Supervisor.getInstance().reportProgress(
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
			painter = new MosaicPainter(selectedTracks, targetFilename,
					imageWidth, imageHeight, mosaicWidth, mosaicHeight);
		} else {
			painter.setProperties(selectedTracks, targetFilename, imageWidth,
					imageHeight, mosaicWidth, mosaicHeight);
		}

	}

	private Void paint() throws IOException {

		try {
			Supervisor.getInstance().reportMainProgress("Generating Mosaic",
					0.33f);
			painter.start();

		} catch (Exception e) {
			Supervisor.getInstance().reportCrash(e.getMessage());
		}

		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		return paint();
	}
}
