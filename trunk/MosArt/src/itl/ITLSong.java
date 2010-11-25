package itl;

import java.text.ParseException;

public class ITLSong {
	
	private final static String TRACK_ID = "Track ID";
	private final static String NAME = "Name";
	private final static String ARTIST = "Artist";
	private final static String ALBUM_ARTIST = "Album Artist";
	private final static String COMPOSER = "Composer";
	private final static String ALBUM = "Album";
	private final static String GENRE = "Genre";
	private final static String KIND = "Kind";
	private final static String SIZE = "Size";
	private final static String TOTAL_TIME = "Total Time";
	private final static String DISC_NUMBER = "Disc Number";
	private final static String DISC_COUNT = "Disc Count";
	private final static String TRACK_NUMBER = "Track Number";
	private final static String TRACK_COUNT = "Track Count";
	private final static String YEAR = "Year";
	private final static String DATE_MODIFIED = "Date Modified";
	private final static String DATE_ADDED = "Date Added";
	private final static String BIT_RATE = "Bit Rate";
	private final static String SAMPLE_RATE = "Sample Rate";
	private final static String PART_GAPLESS = "Part Of Gapless Album";
	private final static String PLAY_COUNT = "Play Count";
	private final static String PLAY_DATE = "Play Date";
	private final static String PLAY_DATE_UTC = "Play Date UTC";
	private final static String RELEASE_DATE = "Release Date";
	private final static String SKIP_COUNT = "Skip Count";
	private final static String SKIP_DATE = "Skip Date";
	private final static String ARTWORK_COUNT = "Artwork Count";
	private final static String PERSISTENT_ID = "Persistent ID";
	private final static String TRACK_TYPE = "Track Type";
	private final static String PURCHASED = "Purchased";
	private final static String LOCATION = "Location";
	private final static String FILE_FOLDER_COUNT = "File Folder Count";
	private final static String LIBRARY_FOLDER_COUNT = "Library Folder Count";
	
	
	private int id;
	private String name;
	private String artist;
	private String albumArtist;
	private String composer;
	private String album;
	private String genre;
	private String kind;
	private int size;
	private int totalTime;
	private int discNumber;
	private int discCount;
	private int trackNumber;
	private int trackCount;
	private int year;
	private String dateModified;
	private String dateAdded;
	private String bitRate;
	private int sampleRate;
	private boolean gapless;
	private int playCount;
	private String playDate;
	private String dateUTC;
	private String releaseDate;
	private int skipCount;
	private String skipDate;
	private int artworkCount;
	private String persistentID;
	private String trackType;
	private boolean purchased;
	private String location;
	private int fileFolderCount;
	private int libraryFolderCount;
	
	public ITLSong(){
		//Nothing
		super();
	}
	
	public void set(String field, String value) throws ParseException, ITLException{
		if(field.equalsIgnoreCase(TRACK_ID)){
			id = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(NAME)){
			name = value;
		}
		else if(field.equalsIgnoreCase(ARTIST)){
			artist = value;
		}
		else if(field.equalsIgnoreCase(ALBUM_ARTIST)){
			albumArtist = value;
		}
		else if(field.equalsIgnoreCase(COMPOSER)){
			composer = value;
		}
		else if(field.equalsIgnoreCase(ALBUM)){
			album = value;
		}
		else if(field.equalsIgnoreCase(GENRE)){
			genre = value;
		}
		else if(field.equalsIgnoreCase(KIND)){
			kind = value;
		}
		else if(field.equalsIgnoreCase(SIZE)){
			size = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(TOTAL_TIME)){
			totalTime = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(DISC_NUMBER)){
			discNumber = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(DISC_COUNT)){
			discCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(TRACK_NUMBER)){
			trackNumber = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(TRACK_COUNT)){
			trackCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(YEAR)){
			year = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(DATE_MODIFIED)){
			dateModified = value;
		}
		else if(field.equalsIgnoreCase(DATE_ADDED)){
			dateAdded = value;
		}
		else if(field.equalsIgnoreCase(BIT_RATE)){
			bitRate = value;
		}
		else if(field.equalsIgnoreCase(SAMPLE_RATE)){
			sampleRate = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(PART_GAPLESS)){
			gapless = Boolean.parseBoolean(value);
		}
		else if(field.equalsIgnoreCase(PLAY_COUNT)){
			playCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(PLAY_DATE)){
			playDate = value;
		}
		else if(field.equalsIgnoreCase(PLAY_DATE_UTC)){
			dateUTC = value;
		}
		else if(field.equalsIgnoreCase(RELEASE_DATE)){
			releaseDate = value;
		}
		else if(field.equalsIgnoreCase(PERSISTENT_ID)){
			persistentID = value;
		}
		else if(field.equalsIgnoreCase(SKIP_COUNT)){
			skipCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(SKIP_DATE)){
			skipDate = value;
		}
		else if(field.equalsIgnoreCase(ARTWORK_COUNT)){
			artworkCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(TRACK_TYPE)){
			trackType = value;
		}
		else if(field.equalsIgnoreCase(PURCHASED)){
			purchased = Boolean.parseBoolean(value);
		}
		else if(field.equalsIgnoreCase(LOCATION)){
			location = value;
		}
		else if(field.equalsIgnoreCase(FILE_FOLDER_COUNT)){
			fileFolderCount = Integer.parseInt(value);
		}
		else if(field.equalsIgnoreCase(LIBRARY_FOLDER_COUNT)){
			libraryFolderCount = Integer.parseInt(value);
		}
		else{
			//TODO remove comment
			//throw new ITLException("Field '" + field + "' unknown");
		}
	}
	
	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public String getComposer() {
		return composer;
	}
	
	public String getAlbum() {
		return album;
	}

	public String getGenre() {
		return genre;
	}

	public String getKind() {
		return kind;
	}

	public int getSize() {
		return size;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public int getDiscNumber() {
		return discNumber;
	}

	public int getDiscCount() {
		return discCount;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public int getTrackCount() {
		return trackCount;
	}

	public int getYear() {
		return year;
	}

	public String getDateModified() {
		return dateModified;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public String getBitRate() {
		return bitRate;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public boolean isGapless() {
		return gapless;
	}

	public int getPlayCount() {
		return playCount;
	}

	public String getPlayDate() {
		return playDate;
	}

	public String getPlayDateUTC() {
		return dateUTC;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getDateUTC() {
		return dateUTC;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public String getSkipDate() {
		return skipDate;
	}

	public int getArtworkCount() {
		return artworkCount;
	}

	public String getPersistentID() {
		return persistentID;
	}

	public String getTrackType() {
		return trackType;
	}

	public boolean getPurchased() {
		return purchased;
	}

	public String getLocation() {
		return location;
	}

	public int getFileFolderCount() {
		return fileFolderCount;
	}

	public int getLibraryFolderCount() {
		return libraryFolderCount;
	}

	@Override
	public String toString() {
		return this.id + " : " + this.name + " - " + this.album + " - " + this.artist;
	}
}
