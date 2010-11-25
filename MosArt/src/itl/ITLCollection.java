package itl;

import itc.ITCArtwork;

import java.util.ArrayList;
import java.util.Collection;
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
		covers.put(artwork.getTrackPersistentId(), artwork);
	}

	public HashMap<String, ITCArtwork> getCovers() {
		return covers;
	}

	public Collection<ITCArtwork> getCoversList() {
		return covers.values();
	}

	public void clear() {
		songs.clear();
		albums.clear();
		covers.clear();
	}
}
