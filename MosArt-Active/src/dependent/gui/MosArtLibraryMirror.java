package dependent.gui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
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
		String genre = track.getGenre();
		String album = track.getArtist() + track.getAlbum(); 
	}
	
	public ArrayList<ITTrack> getSelectedTracks(){
		
		if(selectedTracks.isEmpty()){
			selectedTracks = nodesTracks.get(rootNode);
		}
		
		return selectedTracks;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub

	}
}
