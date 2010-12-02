package itl;

import java.text.ParseException;

public class ITLSong {

	private final static String TRACK_ID = "Track ID";
	private final static String NAME = "Name";
	private final static String ARTIST = "Artist";
	private final static String ALBUM_ARTIST = "Album Artist";
	private final static String COMPOSER = "Composer";
	private final static String ALBUM = "Album";
	private final static String GROUPING = "Grouping";
	private final static String GENRE = "Genre";
	private final static String KIND = "Kind";
	private final static String SIZE = "Size";
	private final static String TOTAL_TIME = "Total Time";
	private final static String DISC_NUMBER = "Disc Number";
	private final static String DISC_COUNT = "Disc Count";
	private final static String TRACK_NUMBER = "Track Number";
	private final static String BPM = "BPM";
	private final static String TRACK_COUNT = "Track Count";
	private final static String YEAR = "Year";
	private final static String DATE_MODIFIED = "Date Modified";
	private final static String DATE_ADDED = "Date Added";
	private final static String BIT_RATE = "Bit Rate";
	private final static String SAMPLE_RATE = "Sample Rate";
	private final static String PART_GAPLESS = "Part Of Gapless Album";
	private final static String COMMENTS = "Comments";
	private final static String VOLUME_ADJUSTMENT = "Volume Adjustment";
	private final static String PLAY_COUNT = "Play Count";
	private final static String PLAY_DATE = "Play Date";
	private final static String PLAY_DATE_UTC = "Play Date UTC";
	private final static String SORT_ARTIST = "Sort Artist";
	private final static String SORT_NAME = "Sort Name";
	private final static String RELEASE_DATE = "Release Date";
	private final static String SKIP_COUNT = "Skip Count";
	private final static String SKIP_DATE = "Skip Date";
	private final static String ARTWORK_COUNT = "Artwork Count";
	private final static String SORT_ALBUM = "Sort Album";
	private final static String SORT_ALBUM_ARTIST = "Sort Album Artist";
	private final static String COMPILATION = "Compilation";
	private final static String PERSISTENT_ID = "Persistent ID";
	private final static String CLEAN = "Clean";
	private final static String EXPLICIT = "Explicit";
	private final static String DISABLED = "Disabled";
	private final static String TRACK_TYPE = "Track Type";
	private final static String HAS_VIDEO = "Has Video";
	private final static String HD = "HD";
	private final static String VIDEO_WIDTH = "Video Width";
	private final static String VIDEO_HEIGHT = "Video Height";
	private final static String MUSIC_VIDEO = "Music Video";
	private final static String MOVIE = "Movie";
	private final static String PROTECTED = "Protected";
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
	private String grouping;
	private String genre;
	private String kind;
	private int size;
	private int totalTime;
	private int discNumber;
	private int discCount;
	private int trackNumber;
	private int bpm;
	private int trackCount;
	private int year;
	private String dateModified;
	private String dateAdded;
	private String bitRate;
	private int sampleRate;
	private boolean gapless;
	private String comments;
	private int volumeAdjustment;
	private int playCount;
	private String playDate;
	private String dateUTC;
	private String sortArtist;
	private String sortName;
	private String releaseDate;
	private int skipCount;
	private String skipDate;
	private int artworkCount;
	private String sortAlbum;
	private String sortAlbumArtist;
	private boolean compilation;
	private String persistentID;
	private boolean clean;
	private boolean explicit;
	private boolean disabled;
	private String trackType;
	private boolean hasVideo;
	private boolean hd;
	private int videoWidth;
	private int videoHeight;
	private boolean musicVideo;
	private boolean movie;
	private boolean protect;
	private boolean purchased;
	private String location;
	private int fileFolderCount;
	private int libraryFolderCount;

	public ITLSong() {
		// Nothing
		super();
	}

	public void set(String field, String value) throws ParseException,
			ITLException {
		if (field.equalsIgnoreCase(TRACK_ID)) {
			id = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(NAME)) {
			name = value;
		} else if (field.equalsIgnoreCase(ARTIST)) {
			artist = value;
		} else if (field.equalsIgnoreCase(ALBUM_ARTIST)) {
			albumArtist = value;
		} else if (field.equalsIgnoreCase(COMPOSER)) {
			composer = value;
		} else if (field.equalsIgnoreCase(ALBUM)) {
			album = value;
		} else if (field.equalsIgnoreCase(GROUPING)) {
			grouping = value;
		} else if (field.equalsIgnoreCase(GENRE)) {
			genre = value;
		} else if (field.equalsIgnoreCase(KIND)) {
			kind = value;
		} else if (field.equalsIgnoreCase(SIZE)) {
			size = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(TOTAL_TIME)) {
			totalTime = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(DISC_NUMBER)) {
			discNumber = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(DISC_COUNT)) {
			discCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(TRACK_NUMBER)) {
			trackNumber = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(BPM)) {
			bpm = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(TRACK_COUNT)) {
			trackCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(YEAR)) {
			year = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(DATE_MODIFIED)) {
			dateModified = value;
		} else if (field.equalsIgnoreCase(DATE_ADDED)) {
			dateAdded = value;
		} else if (field.equalsIgnoreCase(BIT_RATE)) {
			bitRate = value;
		} else if (field.equalsIgnoreCase(SAMPLE_RATE)) {
			sampleRate = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(PART_GAPLESS)) {
			gapless = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(COMMENTS)) {
			comments = value;
		} else if (field.equalsIgnoreCase(VOLUME_ADJUSTMENT)) {
			volumeAdjustment = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(PLAY_COUNT)) {
			playCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(PLAY_DATE)) {
			playDate = value;
		} else if (field.equalsIgnoreCase(PLAY_DATE_UTC)) {
			dateUTC = value;
		} else if (field.equalsIgnoreCase(SORT_ARTIST)) {
			sortArtist = value;
		} else if (field.equalsIgnoreCase(SORT_NAME)) {
			sortName = value;
		} else if (field.equalsIgnoreCase(RELEASE_DATE)) {
			releaseDate = value;
		} else if (field.equalsIgnoreCase(PERSISTENT_ID)) {
			persistentID = value;
		} else if (field.equalsIgnoreCase(CLEAN)) {
			clean = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(EXPLICIT)) {
			explicit = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(DISABLED)) {
			disabled = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(SKIP_COUNT)) {
			skipCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(SKIP_DATE)) {
			skipDate = value;
		} else if (field.equalsIgnoreCase(ARTWORK_COUNT)) {
			artworkCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(SORT_ALBUM)) {
			sortAlbum = value;
		} else if (field.equalsIgnoreCase(SORT_ALBUM_ARTIST)) {
			sortAlbumArtist = value;
		} else if (field.equalsIgnoreCase(COMPILATION)) {
			compilation = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(TRACK_TYPE)) {
			trackType = value;
		} else if (field.equalsIgnoreCase(HAS_VIDEO)) {
			hasVideo = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(HD)) {
			hd = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(VIDEO_WIDTH)) {
			videoWidth = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(VIDEO_HEIGHT)) {
			videoHeight = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(MUSIC_VIDEO)) {
			musicVideo = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(MOVIE)) {
			movie = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(PROTECTED)) {
			protect = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(PURCHASED)) {
			purchased = Boolean.parseBoolean(value);
		} else if (field.equalsIgnoreCase(LOCATION)) {
			location = value;
		} else if (field.equalsIgnoreCase(FILE_FOLDER_COUNT)) {
			fileFolderCount = Integer.parseInt(value);
		} else if (field.equalsIgnoreCase(LIBRARY_FOLDER_COUNT)) {
			libraryFolderCount = Integer.parseInt(value);
		} else {
			throw new ITLException("Field '" + field + "' unknown");
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

	public String getGrouping() {
		return grouping;
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

	public int getBpm() {
		return bpm;
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

	public String getComments() {
		return comments;
	}

	public int getVolumeAdjustment() {
		return volumeAdjustment;
	}

	public int getPlayCount() {
		return playCount;
	}

	public String getPlayDate() {
		return playDate;
	}

	public String getDateUTC() {
		return dateUTC;
	}

	public String getSortArtist() {
		return sortArtist;
	}

	public String getSortName() {
		return sortName;
	}

	public String getReleaseDate() {
		return releaseDate;
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

	public String getSortAlbum() {
		return sortAlbum;
	}

	public String getSortAlbumArtist() {
		return sortAlbumArtist;
	}

	public boolean isCompilation() {
		return compilation;
	}

	public String getPersistentID() {
		return persistentID;
	}

	public boolean isClean() {
		return clean;
	}

	public boolean isExplicit() {
		return explicit;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public String getTrackType() {
		return trackType;
	}

	public boolean isHasVideo() {
		return hasVideo;
	}

	public boolean isHd() {
		return hd;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public boolean isMusicVideo() {
		return musicVideo;
	}

	public boolean isMovie() {
		return movie;
	}

	public boolean isProtect() {
		return protect;
	}

	public boolean isPurchased() {
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
		return this.id + " : " + this.name + " - " + this.album + " - "
				+ this.artist;
	}
}
