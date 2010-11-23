package itc;

import gui.Supervisor;

import java.io.File;
import java.util.ArrayList;

public class ITCBaseReader {
	public static final String ITC_EXT = ".itc";
	public static final String ITC2_EXT = ".itc2";

	private ArrayList<String> itcList;
	
	private static ITCBaseReader singleton;

	private ITCBaseReader() {
		super();
		itcList = new ArrayList<String>();
	}

	public static ITCBaseReader getInstance() {
		if (singleton == null) {
			singleton = new ITCBaseReader();
		}

		return singleton;
	}
	
	public ArrayList<String> getITCs(String artworkDir) {
		File artworkDirectory =  new File(artworkDir);
		
		itcList.clear();
		
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
}
