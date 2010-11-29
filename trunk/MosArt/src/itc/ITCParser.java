package itc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ITCParser {

	public static final int[] imageSignaturePNG = { 0x89, 0x50, 0x4E, 0x47 };
	public static final int[] imageSignatureJPEG = { 0xFF, 0xD8, 0xFF, 0xE0 };
	public static final int[] imageSignatureTIFF = { 0x49, 0x49, 0x2A };
	public static final int[] imageSignatureBMP = { 0x42, 0x4D };
	public static final int[] imageSignatureGIF = { 0x47, 0x49, 0x46 };

	private static ITCParser singleton;

	private ITCParser() {
		super();
		// Nothing
	}

	public static ITCParser getInstance() {
		if (singleton == null) {
			singleton = new ITCParser();
		}

		return singleton;
	}

	public static int[] getImagesignaturepng() {
		return imageSignaturePNG;
	}

	public static int[] getImagesignaturejpeg() {
		return imageSignatureJPEG;
	}
	
	  public String convertStringToHex(String str){
		  
		  char[] chars = str.toCharArray();
	 
		  StringBuffer hex = new StringBuffer();
		  for(int i = 0; i < chars.length; i++){
		    hex.append(Integer.toHexString((int)chars[i]));
		  }
	 
		  return hex.toString();
	  }
	 
	  public String convertHexToString(String hex){
	 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		  System.out.println("Decimal : " + temp.toString());
	 
		  return sb.toString();
	  }


	private int byteArrayToInt(final byte[] bytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream in = new DataInputStream(bis);
		return in.readInt();
	}

	private void readHeader(BufferedInputStream strm, ITCArtwork art)
			throws IOException {
		// Mark position
		strm.mark(Integer.MAX_VALUE);

		// Buffer
		byte[] bytes = new byte[4];

		// Self-describing header length
		strm.read(bytes);
		art.setHeaderLength(byteArrayToInt(bytes));

		// Itch
		strm.read(bytes);
		art.setItch(new String(bytes));

		// 16 bytes of disposable info
		strm.skip(16);

		// Artw
		strm.read(bytes);
		art.setArtw(new String(bytes));

		// Reset position to end of header (beginning of "null buffer")
		strm.reset();
		strm.skip(art.getHeaderLength());
	}

	private void readMetadata(BufferedInputStream strm, ITCArtwork art)
			throws IOException {
		// Mark position
		strm.mark(Integer.MAX_VALUE);

		// Buffer
		byte[] bytes = new byte[4];

		// 4 bytes of disposable info
		strm.skip(4);

		// Item
		strm.read(bytes);
		art.setItem(new String(bytes));

		// Read the entire length of the data header
		strm.read(bytes);
		art.setMetadataLength(byteArrayToInt(bytes));

		// 16 bytes of disposable info
		strm.skip(16);

		// If header length is 216 rather than 212, consume another 4 bytes
		// before getting more information
		if (art.getMetadataLength() == 216) {
			strm.skip(4);
		}

		// Get the library and track persistent Id's
		bytes = new byte[8];
		strm.read(bytes);
		art.setLibraryPersistentId(convertStringToHex(new String(bytes)));

		strm.read(bytes);		
		art.setTrackPersistentId(convertStringToHex(new String(bytes)));

		// Read the download/persistence indicator
		bytes = new byte[4];
		strm.read(bytes);
		art.setDownloadIndicator(new String(bytes)); // "down" or "locl"

		// Read the pseudo-file format
		strm.read(bytes);
		art.setFileFormatIndicator(new String(bytes));

		// 4 bytes of disposable info
		strm.skip(4);

		// Read width and height of image
		strm.read(bytes);
		art.setWidth(byteArrayToInt(bytes));

		strm.read(bytes);
		art.setHeight(byteArrayToInt(bytes));

		// Reset position to end of header (beginning of "null buffer")
		strm.reset();
		strm.skip(art.getMetadataLength());
	}

	private byte[] readImageData(BufferedInputStream strm, int imageBytesCount)
			throws IOException {
		
		byte[] imageData = new byte[imageBytesCount]; 
		strm.read(imageData);
		checkSignature(imageData);
		
		return imageData;
	}

	private boolean matchSignature(byte[] imagedata, int[] signature) {

		boolean match = false;

		if (imagedata != null) {
			int i = 0;
			if (imagedata.length >= signature.length) {
				match = true;

				for (i = 0; i < signature.length; i++) {
					if (imagedata[i] != (byte) signature[i]) {
						match = false;
						break;
					}
				}
			}
		}

		return match;
	}

	private int[] checkSignature(byte[] imagedata) {

		if (matchSignature(imagedata, imageSignatureJPEG)) {
			return imageSignatureJPEG;
		}

		if (matchSignature(imagedata, imageSignaturePNG)) {
			return imageSignaturePNG;
		}

		if (matchSignature(imagedata, imageSignatureBMP)) {
			return imageSignatureBMP;
		}

		if (matchSignature(imagedata, imageSignatureGIF)) {
			return imageSignatureGIF;
		}

		if (matchSignature(imagedata, imageSignatureTIFF)) {
			return imageSignatureTIFF;
		}

		return null;
	}

	public ITCArtwork getArtworkHead(File file) throws IOException {
		ITCArtwork artwork = new ITCArtwork(file);

		BufferedInputStream fistream = new BufferedInputStream(
				new FileInputStream(file.getPath()));

		// Read header
		readHeader(fistream, artwork);

		// Read metadata
		readMetadata(fistream, artwork);

		artwork.setFullyParsed(false);

		return artwork;
	}

	public void completeArtwork(ITCArtwork artwork) throws IOException {

		File file = new File(artwork.getSource());

		BufferedInputStream fistream = new BufferedInputStream(
				new FileInputStream(file.getPath()));

		// Skip head
		fistream.skip(artwork.getHeaderLength());

		// Skip metadata
		fistream.skip(artwork.getMetadataLength());

		// Read image data
		int imageBytesCount = (int) file.length() - artwork.getHeaderLength()
				- artwork.getMetadataLength();
		artwork.setImageData(readImageData(fistream, imageBytesCount));

		fistream.close();
		
		artwork.setFullyParsed(true);
	}

	public ITCArtwork getFullArtwork(File file) throws IOException {

		ITCArtwork artwork = new ITCArtwork(file);

		BufferedInputStream fistream = new BufferedInputStream(
				new FileInputStream(file.getPath()));

		// Read header
		readHeader(fistream, artwork);

		// Read metadata
		readMetadata(fistream, artwork);

		// Read image data
		int imageBytesCount = (int) file.length() - artwork.getHeaderLength()
				- artwork.getMetadataLength();
		artwork.setImageData(readImageData(fistream, imageBytesCount));

		fistream.close();

		artwork.setFullyParsed(true);

		return artwork;
	}
}
