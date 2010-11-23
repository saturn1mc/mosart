package itl;

import itc.ITCArtwork;

import java.util.ArrayList;
import java.util.HashMap;

public class ITLCollection {
	private HashMap<String, ArrayList<ITLSong>> albums;
	private HashMap<String, ITLSong> songs;
	private HashMap<String, ITCArtwork> covers;

	public ITLCollection() {
		songs = new HashMap<String, ITLSong>();
		albums = new HashMap<String, ArrayList<ITLSong>>();
		covers = new HashMap<String, ITCArtwork>();
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

	public void addArtwork(ITCArtwork artwork) {
		covers.put(artwork.getTrackPersistentId(), artwork);
	}

	public HashMap<String, ITCArtwork> getCovers() {
		return covers;
	}
	
	public ArrayList<ITCArtwork> getCoversList(){
		return (ArrayList<ITCArtwork>) covers.values();
	}
	
	public void clear() {
		songs.clear();
		albums.clear();
		covers.clear();
	}
}
