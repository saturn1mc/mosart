package gui;

import itc.ITCBaseReader;
import itc.ITCParser;
import itl.ITLCollection;
import itl.ITLParser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import painters.MosaicPainter;

public class MosArt extends SwingWorker<File, String> {

	private static final String ARTWORK_DIR = "Album Artwork";
	private static final String ITL_XML = "iTunes Music Library.xml";

	private ITLCollection collection;
	private MosaicPainter painter;
	private String targetFilename;
	private String sourceDirectory;

	public MosArt() {
		collection = null;
		painter = null;
		targetFilename = null;
	}

	public void setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
	}

	public void setSourceDirectory(String sourceDir) throws MosArtException {

		// Test source directory
		File dir = new File(sourceDir);

		if (!dir.canRead()) {
			throw new MosArtException("Can't read : '" + dir.getPath() + "'");
		}

		if (!dir.isDirectory()) {
			throw new MosArtException("'" + dir.getPath()
					+ "' is not a directory");
		}

		// Test artwork directory
		dir = new File(sourceDir + File.separator + ARTWORK_DIR);

		if (!dir.canRead()) {
			throw new MosArtException("Can't find : '" + dir.getPath() + "'");
		}

		if (!dir.isDirectory()) {
			throw new MosArtException("'" + dir.getPath()
					+ "' is not a directory");
		}

		sourceDirectory = sourceDir;
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

	public void refreshCollection() throws IOException {

		if (sourceDirectory != null) {

			String library = sourceDirectory + File.separator
					+ File.separator + ITL_XML;

			if (collection == null) {
				collection = new ITLCollection();
			} else {
				collection.clear();
			}

			// Read ITL
			ITLParser.getInstance().parseITL(library, collection);
			
			//Read covers
			Supervisor.getInstance().reportMainProgress(
			"(2/4) Reading artworks");
			
			String artworkDir = sourceDirectory + File.separator + ARTWORK_DIR;
			ArrayList<String> itcList = ITCBaseReader.getInstance().getITCs(artworkDir);
	
			//Associate covers
			Supervisor.getInstance().reportTask("Associating artworks to tracks");
			int associated = 0;
			
			for(String itc : itcList){
				float progress = ((float)(associated++)) / ((float)itcList.size());
				File itcFile = new File(itc);
				Supervisor.getInstance().reportProgress("Associating '" + itcFile.getName() + "'", progress);
				collection.addArtwork(ITCParser.getInstance().getArtworkHead(itcFile));
			}
		}
	}

	public File paint() throws IOException {

		File result = null;
		
		// Read library && covers
		Supervisor.getInstance().reportMainProgress(
				"(1/4) Reading iTunes library");
		
		if (collection == null) {
			refreshCollection();
			// Refresh collection contains (2/4) Reading artworks
		}
		
		if (collection.getCovers() != null && collection.getCovers().size() > 0) {
			// Paint
			Supervisor.getInstance().reportMainProgress(
					"(3/4) Generating wallpaper");
			painter.setArtworkList(collection.getCoversList());
			BufferedImage mosaic = painter.createMosaic();

			// Save image
			Supervisor.getInstance().reportMainProgress(
					"(4/4) Saving work to " + targetFilename);
			result = new File(targetFilename);
			ImageIO.write(mosaic, "PNG", result);
		} else {
			if (collection.getCovers() == null) {
				Supervisor.getInstance().reportCrash(
						"Error during artwork directory analysis");
			} else if (collection.getCovers().size() <= 0) {
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
