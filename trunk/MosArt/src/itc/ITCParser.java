package itc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ITCParser {

	public static final int[] imageSignaturePNG = { 0x89, 0x50, 0x4E, 0x47 };
	public static final int[] imageSignatureJPEG = { 0xFF, 0xD8, 0xFF, 0xE0 };
	public static final int[] imageSignatureTIFF = { 0x49, 0x49, 0x2A };
	public static final int[] imageSignatureBMP = { 0x42, 0x4D };
	public static final int[] imageSignatureGIF = { 0x47, 0x49, 0x46 };
	
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
	
	public ITCParser() {
		super();
	}
	
	public int getHeaderLength() {
		return headerLength;
	}

	public int getMetadataLength() {
		return metadataLength;
	}

	public String getItch() {
		return itch;
	}

	public String getArtw() {
		return artw;
	}

	public String getItem() {
		return item;
	}

	public String getLibraryPersistentId() {
		return libraryPersistentId;
	}

	public String getTrackPersistentId() {
		return trackPersistentId;
	}

	public String getDownloadIndicator() {
		return downloadIndicator;
	}

	public String getFileFormatIndicator() {
		return fileFormatIndicator;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public static int[] getImagesignaturepng() {
		return imageSignaturePNG;
	}

	public static int[] getImagesignaturejpeg() {
		return imageSignatureJPEG;
	}

	private int byteArrayToInt(final byte[] bytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream in = new DataInputStream(bis);
		return in.readInt();
	}

	private void readHeader(BufferedInputStream strm) throws IOException {
		// Mark position
		strm.mark(Integer.MAX_VALUE);

		// Buffer
		byte[] bytes = new byte[4];

		// Self-describing header length
		strm.read(bytes);
		this.headerLength = byteArrayToInt(bytes);

		// Itch
		strm.read(bytes);
		this.itch = new String(bytes);

		// 16 bytes of disposable info
		strm.skip(16);

		// Artw
		strm.read(bytes);
		this.artw = new String(bytes);

		// Reset position to end of header (beginning of "null buffer")
		strm.reset();
		strm.skip(this.headerLength);
	}

	public void readMetadata(BufferedInputStream strm) throws IOException {
		// Mark position
		strm.mark(Integer.MAX_VALUE);
		
		// Buffer
		byte[] bytes = new byte[4];

		// 4 bytes of disposable info
		strm.skip(4);

		// Item
		strm.read(bytes);
		this.item = new String(bytes);

		// Read the entire length of the data header
		strm.read(bytes);
		this.metadataLength = byteArrayToInt(bytes);

		// 16 bytes of disposable info
		strm.skip(16);

		// If header length is 216 rather than 212, consume another 4 bytes
		// before getting more information
		if (this.metadataLength == 216) {
			strm.skip(4);
		}

		// Get the library and track persistent Id's
		bytes = new byte[8];
		strm.read(bytes);
		this.libraryPersistentId = Arrays.toString(bytes);

		strm.read(bytes);
		this.trackPersistentId = Arrays.toString(bytes);

		// Read the download/persistence indicator
		bytes = new byte[4];
		strm.read(bytes);
		this.downloadIndicator = new String(bytes); // "down" or "locl"

		// Read the pseudo-file format
		strm.read(bytes);
		this.fileFormatIndicator = new String(bytes);

		// 4 bytes of disposable info
		strm.skip(4);

		// Read width and height of image
		strm.read(bytes);
		this.width = byteArrayToInt(bytes);

		strm.read(bytes);
		this.height = byteArrayToInt(bytes);

		// Reset position to end of header (beginning of "null buffer")
		strm.reset();
		strm.skip(this.metadataLength);
	}

	private void readImageData(BufferedInputStream strm) throws IOException {
		strm.read(imageData);
		checkSignature(imageData);
	}

	private boolean matchSignature(byte[] imagedata, int[] signature){
		
		boolean match = false;
		
		if (imagedata != null) {
			int i = 0;
			if (imagedata.length >= signature.length) {
				match = true;
				
				for (i = 0; i < signature.length; i++) {
					if (imagedata[i] != (byte)signature[i]) {
						match = false;
						break;
					}
				}
			}
		}
		
		return match;
	}
	
	private int[] checkSignature(byte[] imagedata) {		
		
		if(matchSignature(imagedata, imageSignatureJPEG)){
			return imageSignatureJPEG;
		}
		
		if(matchSignature(imagedata, imageSignaturePNG)){
			return imageSignaturePNG;
		}
		
		if(matchSignature(imagedata, imageSignatureBMP)){
			return imageSignatureBMP;
		}
		
		if(matchSignature(imagedata, imageSignatureGIF)){
			return imageSignatureGIF;
		}
		
		if(matchSignature(imagedata, imageSignatureTIFF)){
			return imageSignatureTIFF;
		}
		
		return null;
	}

	public void parse(File file) throws IOException {
		BufferedInputStream fistream = new BufferedInputStream(
				new FileInputStream(file.getPath()));

		readHeader(fistream);
		readMetadata(fistream);

		int imageBytesCount = (int) file.length() - headerLength - metadataLength;
		imageData = new byte[imageBytesCount];
		readImageData(fistream);

		fistream.close();
	}
}
