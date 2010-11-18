package itl;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ITLSong {
	
	private final static String TRACK_ID = "Track ID";
	private final static String NAME = "Name";
	private final static String ARTIST = "Artist";
	private final static String ALBUM_ARTIST = "Album Artist";
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
	private final static String PERSISTENT_ID = "Persistent ID";
	private final static String TRACK_TYPE = "Track Type";
	private final static String PURCHASED = "Purchased";
	
	private String id;
	private String name;
	private String artist;
	private String albumArtist;
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
	private Date dateModified;
	private Date dateAdded;
	private String bitRate;
	private int sampleRate;
	private boolean gapless;
	private int playCount;
	private Date playDate;
	private Date dateUTC;
	private Date releaseDate;
	private String persistentID;
	private String trackType;
	private boolean purchased;
	private String location;
	private String fileFolderCount;
	private String libraryFolderCount;
	
	public ITLSong(String id){
		this.id = id;
	}
	
	public void set(String field, String value) throws ParseException, ITLException{
		if(field.equalsIgnoreCase(TRACK_ID)){
			id = value;
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
			dateModified = DateFormat.getDateInstance().parse(value);
		}
		else if(field.equalsIgnoreCase(DATE_ADDED)){
			dateAdded = DateFormat.getDateInstance().parse(value);
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
			playDate = DateFormat.getDateInstance().parse(value);
		}
		else if(field.equalsIgnoreCase(PLAY_DATE_UTC)){
			dateUTC = DateFormat.getDateInstance().parse(value);
		}
		else if(field.equalsIgnoreCase(RELEASE_DATE)){
			releaseDate = DateFormat.getDateInstance().parse(value);
		}
		else if(field.equalsIgnoreCase(PERSISTENT_ID)){
			persistentID = value;
		}
		else if(field.equalsIgnoreCase(TRACK_TYPE)){
			trackType = value;
		}
		else if(field.equalsIgnoreCase(PURCHASED)){
			purchased = Boolean.parseBoolean(value);
		}
		else{
			throw new ITLException("Field '" + value + "' unknown");
		}
		
		
	}
	
	public String getId() {
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

	public Date getDateModified() {
		return dateModified;
	}

	public Date getDateAdded() {
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

	public Date getPlayDate() {
		return playDate;
	}

	public Date getPlayDateUTC() {
		return dateUTC;
	}

	public Date getReleaseDate() {
		return releaseDate;
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

	public String getFileFolderCount() {
		return fileFolderCount;
	}

	public String getLibraryFolderCount() {
		return libraryFolderCount;
	}

	@Override
	public String toString() {
		return this.id + " : " + this.name + " - " + this.album + " - " + this.artist;
	}
}
