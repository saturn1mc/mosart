package itc;

import gui.Supervisor;

import java.io.File;
import java.util.ArrayList;

public class ITCBaseReader {
	public static final String ITC_EXT = ".itc";
	public static final String ITC2_EXT = ".itc2";

	private ArrayList<String> itcList;

	public ITCBaseReader() {
		itcList = new ArrayList<String>();
	}

	public ArrayList<String> getITCs(File artworkDirectory) {
		
		Supervisor.getInstance().reportTask("Looking for ITCs in : " + artworkDirectory.getPath());
		
		for (File file : artworkDirectory.listFiles()) {
			if (file.isDirectory()) {
				handleDirectory(file);
			} else {
				handleFile(file);
			}
		}

		return itcList;
	}

	private void handleDirectory(File directory) {
		
		Supervisor.getInstance().reportTask("Analyzing : " + directory.getPath());
		
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				handleDirectory(file);
			} else {
				handleFile(file);
			}
		}
	}

	private void handleFile(File file) {
		
		Supervisor.getInstance().reportTask("Analyzing : " + file.getPath());
		
		if (file.getPath().endsWith(ITC_EXT)
				|| file.getPath().endsWith(ITC2_EXT)) {
			itcList.add(file.getPath());
		}
	}
}
