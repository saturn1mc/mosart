package independent.itunes.itc;

import independent.itunes.ITPersistentID;

import java.io.File;

public class ITCArtwork {
	
	public static final String FILENAME_SEPARATOR = "-";
	public static final int LIBRARY_PID_POS = 0;
	public static final int TRACK_PID_POS = 1;
	
	private String source;
	private boolean fullyParsed;
	
	private int headerLength;
	private int metadataLength;

	private String itch;
	private String artw;
	private String item;

	private ITPersistentID libraryPersistentId;
	private ITPersistentID trackPersistentId;

	private String downloadIndicator;
	private String fileFormatIndicator;

	private int width;
	private int height;

	private byte[] imageData;
	
	public ITCArtwork(File source){
		super();
		this.source = source.getPath();
		fullyParsed = false;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isFullyParsed() {
		return fullyParsed;
	}

	public void setFullyParsed(boolean fullyParsed) {
		this.fullyParsed = fullyParsed;
	}

	public int getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(int headerLength) {
		this.headerLength = headerLength;
	}

	public int getMetadataLength() {
		return metadataLength;
	}

	public void setMetadataLength(int metadataLength) {
		this.metadataLength = metadataLength;
	}

	public String getItch() {
		return itch;
	}

	public void setItch(String itch) {
		this.itch = itch;
	}

	public String getArtw() {
		return artw;
	}

	public void setArtw(String artw) {
		this.artw = artw;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public ITPersistentID getLibraryPersistentId() {
		return libraryPersistentId;
	}

	public void setLibraryPersistentId(long value) {
		this.libraryPersistentId = new ITPersistentID(value);
	}

	public ITPersistentID getTrackPersistentId() {
		return trackPersistentId;
	}

	public void setTrackPersistentId(long value) {
		this.trackPersistentId = new ITPersistentID(value);
	}

	public String getDownloadIndicator() {
		return downloadIndicator;
	}

	public void setDownloadIndicator(String downloadIndicator) {
		this.downloadIndicator = downloadIndicator;
	}

	public String getFileFormatIndicator() {
		return fileFormatIndicator;
	}

	public void setFileFormatIndicator(String fileFormatIndicator) {
		this.fileFormatIndicator = fileFormatIndicator;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void clearImageData(){
		imageData = null;
		fullyParsed = false;
	}
	
	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
}
