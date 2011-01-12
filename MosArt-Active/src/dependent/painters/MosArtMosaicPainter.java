package dependent.painters;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dependent.MosArtException;
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.gui.MosArtPreviewFrame;
import dependent.workers.MosArtExtractor;

public class MosArtMosaicPainter extends Thread {

	private ArrayList<ITTrack> selectedTracks;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private String targetFilename;

	public MosArtMosaicPainter(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		setProperties(selectedTracks, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setProperties(ArrayList<ITTrack> selectedTracks,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws MosArtException {

		this.selectedTracks = selectedTracks;

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

	private void createMosaic() throws IOException {

		int tileWidth = (int)(Math.ceil((float)imageWidth / (float)mosaicWidth));
		int tileHeight = (int)(Math.ceil((float)imageHeight / (float)mosaicHeight));
		int tileX = 0;
		int tileY = 0;
		int done = 0;

		MosArtExtractor extractor = new MosArtExtractor(selectedTracks,
				mosaicHeight * mosaicWidth, tileWidth, tileHeight);
		extractor.launch();

		MosArtPreviewFrame.getInstance().init(imageWidth, imageHeight);
		MosArtPreviewFrame.getInstance().setVisible(true);

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				Image image = extractor.popScaledImage();

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
		ImageIO.write(MosArtPreviewFrame.getInstance().getImage(), "PNG", new File(targetFilename));

		MosArtPreviewFrame.getInstance().diposeG2D();

		MosArtSupervisor.getInstance().reportMainTaskFinished();
	}

	@Override
	public void run() {
		try {
			createMosaic();
		} catch (IOException e) {
			MosArtSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
