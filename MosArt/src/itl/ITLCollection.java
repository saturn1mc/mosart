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
		System.out.println("Adding : " + song.getID() + ", " + song.getAlbum() + ", " + song.getName());
		songs.put(song.getID(), song);
		albums.put(song.getAlbum(), song);
	}
}
