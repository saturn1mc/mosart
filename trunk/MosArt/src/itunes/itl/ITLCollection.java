package itunes.itl;

import itc.ITCArtwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class ITLCollection {
	
	private ArrayList<ITCArtwork> artworks;
	private HashMap<String, TreeMap<Integer, ITLSong>> albums;
	
	private HashMap<String, ITLSong> songs;
	private HashMap<String, ITCArtwork> covers;
	

	public ITLCollection() {
		artworks = new ArrayList<ITCArtwork>();
		albums = new HashMap<String, TreeMap<Integer,ITLSong>>();
		songs = new HashMap<String, ITLSong>();
		covers = new HashMap<String, ITCArtwork>();
	}

	public void add(ITLSong song) {

		songs.put(song.getPersistentID(), song);

		if (song.getAlbum() != null) {
			TreeMap<Integer, ITLSong> album = albums.get(song.getAlbum());

			if (album == null) {
				album = new TreeMap<Integer, ITLSong>();
				albums.put(song.getAlbum(), album);
			}
			
			album.put(song.getTrackNumber(), song);
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
	
	public Set<String> getAlbums(){
		return albums.keySet();
	}
	
	public TreeMap<Integer, ITLSong> getAlbum(String albumName){
		return albums.get(albumName);
	}
	
	// Going to disappear once the new version is finalized
	public ArrayList<ITCArtwork> getArtworks(){
		return artworks;
	}
	// End disappearance
	
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
