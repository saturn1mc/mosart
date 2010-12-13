package dependent.workers;

import java.io.IOException;

import javax.swing.SwingWorker;

import dependent.MosArtException;
import dependent.com.dt.iTunesController.iTunes;
import dependent.gui.Supervisor;
import dependent.painters.MosaicPainter;

public class MosArtLauncher extends SwingWorker<Void, String> {

	private iTunes itunes;
	private MosaicPainter painter;

	public MosArtLauncher(String targetFilename, int imageWidth,
			int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {
		
		Supervisor.getInstance().reportMainProgress(
				"(1/3) Connecting to iTunes", 0.33f);
		itunes = new iTunes();

		setMosaicProperties(targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setMosaicProperties(String targetFilename, int imageWidth,
			int imageHeight, int mosaicWidth, int mosaicHeight)
			throws MosArtException {

		if (painter == null) {
			painter = new MosaicPainter(itunes, targetFilename, imageWidth,
					imageHeight, mosaicWidth, mosaicHeight);
		} else {
			painter.setProperties(itunes, targetFilename, imageWidth,
					imageHeight, mosaicWidth, mosaicHeight);
		}

	}

	private Void paint() throws IOException {

		try {
			Supervisor.getInstance().reportMainProgress(
					"(2/3) Generating Mosaic", 0.66f);
			painter.start(); // Contains (3/3)

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
