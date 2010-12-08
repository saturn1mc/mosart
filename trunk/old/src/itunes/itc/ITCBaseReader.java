package itunes.itc;

import gui.Supervisor;

import java.io.File;
import java.util.ArrayList;

public class ITCBaseReader {
	private static final String CACHE_DIR = "Cache";
	private static final int MODE_CACHE = 0;

	private static final String DOWNLOAD_DIR = "Download";
	private static final int MODE_DOWNLOAD = 1;

	public static final String ITC_EXT = ".itc";
	public static final String ITC2_EXT = ".itc2";

	private ArrayList<String> itcList;
	private ArrayList<String> itcCacheList;
	private ArrayList<String> itcDownloadList;

	private static ITCBaseReader singleton;

	private ITCBaseReader() {
		super();
		itcList = new ArrayList<String>();
		itcCacheList = new ArrayList<String>();
		itcDownloadList = new ArrayList<String>();
	}

	public static ITCBaseReader getInstance() {
		if (singleton == null) {
			singleton = new ITCBaseReader();
		}

		return singleton;
	}

	public ArrayList<String> getITCs(String artworkDir) throws ITCException {
		File artworkDirectory = new File(artworkDir);
		itcList.clear();

		if (artworkDirectory.isDirectory()) {

			handleDirectory(new File(artworkDir + File.separator + CACHE_DIR),
					MODE_CACHE);
			handleDirectory(
					new File(artworkDir + File.separator + DOWNLOAD_DIR),
					MODE_DOWNLOAD);
		} else {
			throw new ITCException("'" + artworkDir + "'"
					+ " is not a directory");
		}

		return itcList;
	}

	private void handleDirectory(File directory, int mode) throws ITCException {

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				Supervisor.getInstance().reportTask(
						"Handling directory : " + file.getPath());
				handleDirectory(file, mode);
			} else {
				if (mode == MODE_CACHE) {
					handleCachedFile(file);
				} else if (mode == MODE_DOWNLOAD) {
					handleDownloadFile(file);
				} else {
					throw new ITCException("Unknown mode : " + mode);
				}
			}
		}
	}

	private void handleCachedFile(File file) {

		Supervisor.getInstance().reportTask(
				"Handling cached file : " + file.getPath());

		if (file.getPath().endsWith(ITC_EXT)
				|| file.getPath().endsWith(ITC2_EXT)) {
			itcList.add(file.getPath());
			itcCacheList.add(file.getPath());
		}
	}
	
	private void handleDownloadFile(File file) {

		Supervisor.getInstance().reportTask(
				"Handling download file : " + file.getPath());

		if (file.getPath().endsWith(ITC_EXT)
				|| file.getPath().endsWith(ITC2_EXT)) {
			
			// Adding to itcList only if no file with the same name already exists
			boolean found = false;
			for(String itc : itcList){
				if(itc.contains(file.getName())){
					found = true;
					break;
				}
			}
			
			if(!found){
				itcList.add(file.getPath());
			}
			
			itcDownloadList.add(file.getPath());
		}
	}
}
