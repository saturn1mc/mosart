package independent.itunes.itl;

import independent.itunes.itc.ITCArtwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class ITLCollection {
	
	private ArrayList<ITCArtwork> artworks;
	private HashMap<String, TreeMap<Integer, ITLSong>> albums;
	
	private HashMap<Long, ITLSong> songs;
	private HashMap<Long, ITCArtwork> covers;
	

	public ITLCollection() {
		artworks = new ArrayList<ITCArtwork>();
		albums = new HashMap<String, TreeMap<Integer,ITLSong>>();
		songs = new HashMap<Long, ITLSong>();
		covers = new HashMap<Long, ITCArtwork>();
	}

	public void add(ITLSong song) {

		songs.put(song.getPersistentID().longValue(), song);

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
		artworks.add(artwork);
		covers.put(artwork.getTrackPersistentId().longValue(), artwork);
	}

	public HashMap<Long, ITCArtwork> getCovers() {
		return covers;
	}

	public HashMap<Long, ITLSong> getSongs() {
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
	
	public ITCArtwork getArtwork(long trackPersistendID){
		return covers.get(trackPersistendID);
	}
	
	public ITLSong getSong(long trackPersistendID){
		return songs.get(trackPersistendID);
	}

	public void clear() {
		artworks.clear();
		albums.clear();
		songs.clear();
		covers.clear();
	}
}
