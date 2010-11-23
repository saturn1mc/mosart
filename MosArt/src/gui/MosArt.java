package gui;

import itc.ITCBaseReader;
import itl.ITLCollection;
import itl.ITLParser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import painters.MosaicPainter;

public class MosArt extends SwingWorker<File, String> {

	private static final String ARTWORK_DIR = "Album Artwork";
	private static final String ITL_XML = "iTunes Music Library.xml";
	
	private ITLCollection collection;
	private ITCBaseReader baseReader;
	private MosaicPainter painter;
	private String targetFilename;
	private String sourceDirectory;

	public MosArt() {
		collection = new ITLCollection();
		baseReader = null;
		painter = null;
		targetFilename = null;
	}

	public void setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
	}

	public void setSourceDirectory(String sourceDir) throws MosArtException {
		
		sourceDirectory = sourceDir;
		
		//Test source directory
		File dir = new File(sourceDir);
		
		if(!dir.canRead()){
			throw new MosArtException("Can't read : '" + dir.getPath() + "'");
		}
		
		if(!dir.isDirectory()){
			throw new MosArtException("'" + dir.getPath() + "' is not a directory");
		}
		
		//Test artwork directory
		dir = new File(sourceDir + File.separator + ARTWORK_DIR);
		
		if(!dir.canRead()){
			throw new MosArtException("Can't find : '" + dir.getPath() + "'");
		}
		
		if(!dir.isDirectory()){
			throw new MosArtException("'" + dir.getPath() + "' is not a directory");
		}
		
		//Create ITC base reader
		if (baseReader == null) {
			baseReader = new ITCBaseReader(dir);
		} else {
			baseReader.setArtworkDirectory(dir);
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
	
	public ITLCollection refreshCollection(){
		String library = sourceDirectory + File.separator + ARTWORK_DIR + File.separator + ITL_XML;	
		
		if(collection == null){
			collection = new ITLCollection();
		}
		else{
			collection.clear();
		}
		
		ITLParser.getInstance().parseITL(library, collection);
		
		return collection;
	}

	private File paint() throws IOException {

		File result = null;
		// Read library		
		Supervisor.getInstance().reportMainProgress(
		"(1/4) Reading iTunes library");
		if(collection == null){
			refreshCollection();
		}
		
		// Read base
		Supervisor.getInstance().reportMainProgress(
				"(2/4) Reading artwork directories");
		ArrayList<String> itcList = baseReader.getITCs();

		if (itcList != null && itcList.size() > 0) {
			// Paint
			Supervisor.getInstance().reportMainProgress(
					"(3/4) Generating wallpaper");
			painter.setITCList(itcList);
			ImageIcon mosaic = painter.createMosaic();

			// Save image
			Supervisor.getInstance().reportMainProgress(
					"(4/4) Saving work to " + targetFilename);
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
