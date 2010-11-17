package itl;

public class ITLSong {
	private String name;
	private String artist;
	private String album;
	private String location;
	private int playCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artistName) {
		this.artist = artistName;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String albumName) {
		this.album = albumName;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
