package itc;

public class ITCArtwork {
	
	private String source;
	private boolean fullyParsed;
	
	private int headerLength;
	private int metadataLength;

	private String itch;
	private String artw;
	private String item;

	private String libraryPersistentId;
	private String trackPersistentId;

	private String downloadIndicator;
	private String fileFormatIndicator;

	private int width;
	private int height;

	private byte[] imageData;
	
	public ITCArtwork(String source){
		this.source = source;
		fullyParsed = false;
		
		headerLength = 0;
		metadataLength = 0;
		itch = null;
		artw = null;
		item = null;
		
		libraryPersistentId = null;
		trackPersistentId = null;
		
		downloadIndicator = null;
		fileFormatIndicator = null;
		
		width = 0;
		height = 0;
		
		imageData = null;
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

	public String getLibraryPersistentId() {
		return libraryPersistentId;
	}

	public void setLibraryPersistentId(String libraryPersistentId) {
		this.libraryPersistentId = libraryPersistentId;
	}

	public String getTrackPersistentId() {
		return trackPersistentId;
	}

	public void setTrackPersistentId(String trackPersistentId) {
		this.trackPersistentId = trackPersistentId;
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
