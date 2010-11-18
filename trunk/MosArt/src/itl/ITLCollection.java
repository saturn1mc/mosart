package itl;

import java.util.HashMap;

public class ITLCollection {
	private HashMap<Integer, ITLSong> songs;
	private HashMap<String, ITLSong> albums;
	
	public ITLCollection() {
		songs = new HashMap<Integer, ITLSong>();
		albums = new HashMap<String, ITLSong>();
	}
	
	public void add(ITLSong song){
		songs.put(song.getID(), song);
		albums.put(song.getAlbum(), song);
	}
}
