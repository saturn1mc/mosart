package dependent.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dependent.com.dt.iTunesController.ITTrack;

public class MosArtLibraryMirror implements TreeSelectionListener {

	private DefaultMutableTreeNode rootNode;
	private JTree libraryTree;

	private HashMap<String, DefaultMutableTreeNode> nodesByGenre;
	private HashMap<String, DefaultMutableTreeNode> nodesByArtist;
	private HashMap<String, DefaultMutableTreeNode> nodesByAlbum;

	private HashMap<DefaultMutableTreeNode, ArrayList<ITTrack>> nodesTracks;

	private ArrayList<ITTrack> selectedTracks;

	public MosArtLibraryMirror() {
		rootNode = new DefaultMutableTreeNode("iTunes Library");

		libraryTree = new JTree(rootNode);
		libraryTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		libraryTree.addTreeSelectionListener(this);

		nodesByGenre = new HashMap<String, DefaultMutableTreeNode>();
		nodesByArtist = new HashMap<String, DefaultMutableTreeNode>();
		nodesByAlbum = new HashMap<String, DefaultMutableTreeNode>();

		nodesTracks = new HashMap<DefaultMutableTreeNode, ArrayList<ITTrack>>();

		selectedTracks = new ArrayList<ITTrack>();
	}

	public synchronized void addTrack(ITTrack track) {

		String album = track.getArtist() + track.getAlbum();
		DefaultMutableTreeNode albumNode = nodesByAlbum.get(album);

		if (albumNode == null) {
			String genre = track.getGenre();
			String artist = track.getArtist();

			// Handling genre node
			DefaultMutableTreeNode genreNode = nodesByGenre.get(genre);

			if (genreNode == null) {
				genreNode = new DefaultMutableTreeNode(genre);
				nodesByGenre.put(genre, genreNode);
			}

			rootNode.add(genreNode);

			// Handling artist node
			DefaultMutableTreeNode artistNode = nodesByGenre.get(artist);

			if (artistNode == null) {
				artistNode = new DefaultMutableTreeNode(artist);
				nodesByArtist.put(artist, artistNode);
			}

			genreNode.add(artistNode);

			// Handling album node
			albumNode = new DefaultMutableTreeNode(track.getAlbum());
			nodesByAlbum.put(album, albumNode);

			// Add track to root node list
			ArrayList<ITTrack> tracks = nodesTracks.get(rootNode);

			if (tracks == null) {
				tracks = new ArrayList<ITTrack>();
				nodesTracks.put(rootNode, tracks);
			}

			tracks.add(track);

			// Add track to genre node list
			tracks = nodesTracks.get(genreNode);

			if (tracks == null) {
				tracks = new ArrayList<ITTrack>();
				nodesTracks.put(genreNode, tracks);
			}

			tracks.add(track);

			// Add track to artist node list
			tracks = nodesTracks.get(artistNode);

			if (tracks == null) {
				tracks = new ArrayList<ITTrack>();
				nodesTracks.put(artistNode, tracks);
			}

			tracks.add(track);

			// Add track to album node list
			tracks = nodesTracks.get(albumNode);

			if (tracks == null) {
				tracks = new ArrayList<ITTrack>();
				nodesTracks.put(albumNode, tracks);
			}

			tracks.add(track);
		}
	}

	public synchronized ArrayList<ITTrack> getSelectedTracks() {

		if (selectedTracks.isEmpty()) {
			selectedTracks = nodesTracks.get(rootNode);
		}

		return selectedTracks;
	}
	
	public synchronized JTree getLibraryTree() {
		return libraryTree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

		HashSet<ITTrack> distinctTracks = new HashSet<ITTrack>();

		TreePath[] selection = libraryTree.getSelectionPaths();

		for (TreePath tp : selection) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
					.getLastPathComponent();

			if (node.isLeaf()) {
				distinctTracks.addAll(nodesTracks.get(node));
			} else {
				distinctTracks.addAll(nodesTracks.get(node.getLastChild()));
			}
		}

		selectedTracks.clear();
		selectedTracks.addAll(distinctTracks);
	}
}
