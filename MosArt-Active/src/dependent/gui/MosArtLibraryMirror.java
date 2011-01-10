package dependent.gui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dependent.com.dt.iTunesController.ITTrack;

public class MosArtLibraryMirror implements TreeSelectionListener {

	private MosArtMutableTreeNode rootNode;
	private JTree libraryTree;
	private boolean enabled;

	private HashMap<String, MosArtMutableTreeNode> nodesByGenre;
	private HashMap<String, MosArtMutableTreeNode> nodesByArtist;
	private HashMap<String, MosArtMutableTreeNode> nodesByAlbum;

	private HashMap<MosArtMutableTreeNode, ArrayList<ITTrack>> nodesTracks;

	private ArrayList<ITTrack> selectedTracks;

	public MosArtLibraryMirror() {
		rootNode = new MosArtMutableTreeNode("iTunes Library");

		libraryTree = new JTree(rootNode);
		libraryTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		libraryTree.setShowsRootHandles(true);
		libraryTree.addTreeSelectionListener(this);

		nodesByGenre = new HashMap<String, MosArtMutableTreeNode>();
		nodesByArtist = new HashMap<String, MosArtMutableTreeNode>();
		nodesByAlbum = new HashMap<String, MosArtMutableTreeNode>();

		nodesTracks = new HashMap<MosArtMutableTreeNode, ArrayList<ITTrack>>();

		selectedTracks = new ArrayList<ITTrack>();

		enabled = true;
	}

	public synchronized void addTrack(ITTrack track) {

		String album = track.getAlbum();
		MosArtMutableTreeNode albumNode = nodesByAlbum.get(album);

		if (albumNode == null) {
			String genre = track.getGenre();
			String artist = track.getArtist();

			// Handling genre node
			MosArtMutableTreeNode genreNode = nodesByGenre.get(genre);

			if (genreNode == null) {
				genreNode = new MosArtMutableTreeNode(genre);
				nodesByGenre.put(genre, genreNode);
			}

			rootNode.add(genreNode);

			// Handling artist node
			MosArtMutableTreeNode artistNode = nodesByArtist.get(artist);

			if (artistNode == null) {
				artistNode = new MosArtMutableTreeNode(artist);
				nodesByArtist.put(artist, artistNode);
			}

			genreNode.add(artistNode);

			// Handling album node
			albumNode = new MosArtMutableTreeNode(album);
			nodesByAlbum.put(album, albumNode);
			artistNode.add(albumNode);

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

	public synchronized boolean isEnabled() {
		return enabled;
	}

	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@SuppressWarnings("unchecked")
	private void addAllLeaves(MosArtMutableTreeNode node,
			ArrayList<MosArtMutableTreeNode> stack) {
		if (!node.isLeaf()) {
			Enumeration<MosArtMutableTreeNode> children = node.children();

			while (children.hasMoreElements()) {
				addAllLeaves(children.nextElement(), stack);
			}
		} else {
			stack.add(node);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (isEnabled()) {
			HashSet<ITTrack> distinctTracks = new HashSet<ITTrack>();

			TreePath[] selection = libraryTree.getSelectionPaths();

			if (selection != null) {
				for (TreePath tp : selection) {
					MosArtMutableTreeNode node = (MosArtMutableTreeNode) tp
							.getLastPathComponent();

					if (node.isLeaf()) {
						distinctTracks.addAll(nodesTracks.get(node));
					} else {
						ArrayList<MosArtMutableTreeNode> leaves = new ArrayList<MosArtMutableTreeNode>();
						addAllLeaves(node, leaves);

						for (MosArtMutableTreeNode leaf : leaves) {
							distinctTracks.addAll(nodesTracks.get(leaf));
						}
					}
				}

				selectedTracks.clear();
				selectedTracks.addAll(distinctTracks);
			}
		}
	}
}
