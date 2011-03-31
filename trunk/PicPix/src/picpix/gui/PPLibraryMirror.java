package picpix.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class PPLibraryMirror implements TreeSelectionListener {

	private PPMutableTreeNode rootNode;
	private JTree libraryTree;
	private boolean enabled;
	
	HashMap<PPMutableTreeNode, ArrayList<File>> nodesFileChildren;
	private ArrayList<File> selectedFiles;

	public PPLibraryMirror(File imageFolder) {
		rootNode = new PPMutableTreeNode("Image Folder");

		libraryTree = new JTree(rootNode);
		libraryTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		libraryTree.setShowsRootHandles(true);
		libraryTree.addTreeSelectionListener(this);

		nodesFileChildren = new HashMap<PPMutableTreeNode, ArrayList<File>>();

		selectedFiles = new ArrayList<File>();

		enabled = true;
		
		parseFolder(imageFolder);
	}

	private void parseFolder(File imageFolder){
		for(File f : imageFolder.listFiles()){
			if(f.isDirectory()){
				handleFolder(f, rootNode);
			}
			else{
				handleFile(f, rootNode);
			}
		}
	}
	
	private void handleFolder(File folder, PPMutableTreeNode parentNode){
		
		PPMutableTreeNode folderNode = new PPMutableTreeNode(folder.getName());
		parentNode.add(folderNode);
		
		for(File f : folder.listFiles()){
			if(f.isDirectory()){
				handleFolder(f, folderNode);
			}
			else{
				handleFile(f, folderNode);
			}
		}
	}
	
	private void handleFile(File file, PPMutableTreeNode parentNode){
		PPMutableTreeNode fileNode = new PPMutableTreeNode(file.getName());
		parentNode.add(fileNode);
		
		addFileAsChild(fileNode, file);
	}
	
	private void addFileAsChild(PPMutableTreeNode node, File file){
		ArrayList<File> files = nodesFileChildren.get(node);
		
		if(files == null){
			files = new ArrayList<File>();
			nodesFileChildren.put(node, files);
		}
		
		files.add(file);
		
		if(node.getParent() != null){
			addFileAsChild((PPMutableTreeNode)node.getParent(), file);
		}
	}

	public synchronized ArrayList<File> getSelectedFiles() {

		if (selectedFiles.isEmpty()) {
			selectedFiles = nodesFileChildren.get(rootNode);
		}

		return selectedFiles;
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
	private void addAllLeaves(PPMutableTreeNode node,
			ArrayList<PPMutableTreeNode> stack) {
		if (!node.isLeaf()) {
			Enumeration<PPMutableTreeNode> children = node.children();

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
			HashSet<File> distinctTracks = new HashSet<File>();

			TreePath[] selection = libraryTree.getSelectionPaths();

			if (selection != null) {
				for (TreePath tp : selection) {
					PPMutableTreeNode node = (PPMutableTreeNode) tp
							.getLastPathComponent();

					if (node.isLeaf()) {
						distinctTracks.addAll(nodesFileChildren.get(node));
					} else {
						ArrayList<PPMutableTreeNode> leaves = new ArrayList<PPMutableTreeNode>();
						addAllLeaves(node, leaves);

						for (PPMutableTreeNode leaf : leaves) {
							distinctTracks.addAll(nodesFileChildren.get(leaf));
						}
					}
				}

				selectedFiles.clear();
				selectedFiles.addAll(distinctTracks);
			}
		}
	}
}
