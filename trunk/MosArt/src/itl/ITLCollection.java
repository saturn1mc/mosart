package itl;

import java.util.ArrayList;
import java.util.HashMap;

public class ITLCollection {
	private HashMap<String, ArrayList<ITLSong>> albums;
	private HashMap<String, ITLSong> songs;
	private HashMap<String, String> covers;

	public ITLCollection() {
		songs = new HashMap<String, ITLSong>();
		albums = new HashMap<String, ArrayList<ITLSong>>();
		covers = new HashMap<String, String>();
	}

	public void add(ITLSong song) {
		songs.put(song.getPersistentID(), song);

		ArrayList<ITLSong> songs = albums.get(song.getAlbum());
		
		if(songs == null){
			songs = new ArrayList<ITLSong>();
			albums.put(song.getAlbum(), songs);
		}
		
		songs.add(song);
	}

	public void addArtwork(String persistentID, String artworkPath) {
		covers.put(persistentID, artworkPath);
	}

	public HashMap<String, String> getCovers() {
		return covers;
	}

	public ArrayList<String> getCoversList() {
		return (ArrayList<String>) covers.values();
	}

	public void clear() {
		songs.clear();
		albums.clear();
		covers.clear();
	}
}
