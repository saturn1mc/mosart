package itl;

import itc.ITCArtwork;

import java.util.ArrayList;
import java.util.HashMap;

public class ITLCollection {
	
	private ArrayList<ITCArtwork> artworks;
	private HashMap<String, ArrayList<ITLSong>> albums;
	
	private HashMap<String, ITLSong> songs;
	private HashMap<String, ITCArtwork> covers;
	

	public ITLCollection() {
		artworks = new ArrayList<ITCArtwork>();
		albums = new HashMap<String, ArrayList<ITLSong>>();
		songs = new HashMap<String, ITLSong>();
		covers = new HashMap<String, ITCArtwork>();
	}

	public void add(ITLSong song) {

		songs.put(song.getPersistentID(), song);

		if (song.getAlbum() != null) {
			ArrayList<ITLSong> album = albums.get(song.getAlbum());

			if (album == null) {
				album = new ArrayList<ITLSong>();
				albums.put(song.getAlbum(), album);
			}
			
			album.add(song);
		}
	}

	public void addArtwork(ITCArtwork artwork) {
		
		String[] split = artwork.getFilename().split(ITCArtwork.FILENAME_SEPARATOR);
		
		artworks.add(artwork);
		covers.put(split[ITCArtwork.TRACK_PID_POS], artwork);
	}

	public HashMap<String, ITCArtwork> getCovers() {
		return covers;
	}

	public HashMap<String, ITLSong> getSongs() {
		return songs;
	}
	
	// A VIRER
	public ArrayList<ITCArtwork> getArtworks(){
		return artworks;
	}
	// FIN A VIRER
	
	public ITCArtwork getArtwork(String trackPersistendID){
		return covers.get(trackPersistendID);
	}
	
	public ITLSong getSong(String persistendID){
		return songs.get(persistendID);
	}

	public void clear() {
		artworks.clear();
		albums.clear();
		songs.clear();
		covers.clear();
	}
}
