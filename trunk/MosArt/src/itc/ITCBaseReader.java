package itc;

import gui.Supervisor;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingWorker;

public class ITCBaseReader extends SwingWorker<ArrayList<String>, String> {
	public static final String ITC_EXT = ".itc";
	public static final String ITC2_EXT = ".itc2";

	private File artworkDirectory;
	private ArrayList<String> itcList;

	public ITCBaseReader(File artworkDirectory) {
		this.artworkDirectory = artworkDirectory;
		itcList = new ArrayList<String>();
	}
	
	public void setArtworkDirectory(File artDir){
		this.artworkDirectory = artDir;
	}

	public ArrayList<String> getITCs() {

		for (File file : artworkDirectory.listFiles()) {
			if (file.isDirectory()) {
				Supervisor.getInstance().reportTask("Handling directory : " + file.getPath());
				handleDirectory(file);
			} else {
				handleFile(file);
			}
		}

		return itcList;
	}

	private void handleDirectory(File directory) {

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				Supervisor.getInstance().reportTask("Handling directory : " + file.getPath());
				handleDirectory(file);
			} else {
				
				handleFile(file);
			}
		}
	}

	private void handleFile(File file) {
		
		Supervisor.getInstance().reportTask("Handling file : " + file.getPath());

		if (file.getPath().endsWith(ITC_EXT)
				|| file.getPath().endsWith(ITC2_EXT)) {
			itcList.add(file.getPath());
		}
	}
	
	
	@Override
	protected ArrayList<String> doInBackground() throws Exception {
		return getITCs();
	}
}
