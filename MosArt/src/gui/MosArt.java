package gui;

import itc.ITCBaseReader;
import itl.ITLCollection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import painters.MosaicPainter;

public class MosArt extends SwingWorker<File, String> {

	private ITLCollection collection;
	private ITCBaseReader baseReader;
	private MosaicPainter painter;
	private String targetFilename;

	public MosArt() {
		collection = new ITLCollection();
		baseReader = null;
		painter = null;
		targetFilename = null;
	}

	public void setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
	}

	public void setSourceDirectory(File sourceDir) {
		if (baseReader == null) {
			baseReader = new ITCBaseReader(sourceDir);
		} else {
			baseReader.setArtworkDirectory(sourceDir);
		}
	}

	public void setMosaicProperties(int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) {

		if (painter == null) {
			painter = new MosaicPainter(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight, null);
		} else {
			painter.setProperties(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight, null);
		}

	}

	public File paint() throws IOException {

		File result = null;

		// Read base
		Supervisor.getInstance().reportMainProgress(
				"(1/3) Reading artwork directories");
		ArrayList<String> itcList = baseReader.getITCs();

		if (itcList != null && itcList.size() > 0) {
			// Paint
			Supervisor.getInstance().reportMainProgress(
					"(2/3) Generating wallpaper");
			painter.setITCList(itcList);
			ImageIcon mosaic = painter.createMosaic();

			// Save image
			Supervisor.getInstance().reportMainProgress(
					"(3/3) Saving work to " + targetFilename);
			result = new File(targetFilename);
			ImageIO.write((BufferedImage) mosaic.getImage(), "PNG", result);
		} else {
			if (itcList == null) {
				Supervisor.getInstance().reportCrash(
						"Error during artwork directory analysis");
			} else if (itcList.size() == 0) {
				Supervisor.getInstance().reportCrash("No iTunes covers found");
			}
		}

		if (result != null) {
			Supervisor.getInstance().reportMainTaskFinished();
		}

		return result;
	}

	@Override
	protected File doInBackground() throws Exception {
		return paint();
	}
}
