package picpix.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class PPFolderMirror implements TreeSelectionListener {

	private PPMutableTreeNode rootNode;
	private JTree tree;
	private boolean enabled;

	HashMap<PPMutableTreeNode, ArrayList<String>> nodesLeaves;
	HashMap<PPMutableTreeNode, String> nodesFile;
	private ArrayList<String> selectedFiles;

	public PPFolderMirror(File imageFolder) {
		rootNode = new PPMutableTreeNode(imageFolder.getName());

		tree = new JTree(rootNode);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.setAutoscrolls(true);

		nodesLeaves = new HashMap<PPMutableTreeNode, ArrayList<String>>();
		nodesFile = new HashMap<PPMutableTreeNode, String>();

		selectedFiles = new ArrayList<String>();

		enabled = true;

		parseFolder(imageFolder);

		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	private void parseFolder(File imageFolder) {
		for (File f : imageFolder.listFiles()) {
			if (f.isDirectory()) {
				handleFolder(f, rootNode);
			} else {
				handleFile(f, rootNode);
			}
		}
	}

	private void handleFolder(File folder, PPMutableTreeNode parentNode) {

		PPMutableTreeNode folderNode = new PPMutableTreeNode(folder.getName());
		parentNode.add(folderNode);

		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				handleFolder(f, folderNode);
			} else {
				handleFile(f, folderNode);
			}
		}
	}

	private void handleFile(File file, PPMutableTreeNode parentNode) {
		if (isImage(file) && file.canRead()) {

			PPMutableTreeNode fileNode = new PPMutableTreeNode(file.getName());
			parentNode.add(fileNode);

			linkLeafToAllParent(file, parentNode);
			nodesFile.put(fileNode, file.getAbsolutePath());
		}
	}

	private boolean isImage(File file) {
		if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
				|| file.getName().endsWith(".jpeg")
				|| file.getName().endsWith(".bmp")) {
			return true;
		} else {
			return false;
		}
	}

	private void linkLeafToAllParent(File file, PPMutableTreeNode parentNode) {

		if (parentNode != null) {
			ArrayList<String> files = nodesLeaves.get(parentNode);

			if (files == null) {
				files = new ArrayList<String>();
				nodesLeaves.put(parentNode, files);
			}

			files.add(file.getAbsolutePath());

			linkLeafToAllParent(file,
					(PPMutableTreeNode) parentNode.getParent());
		}
	}

	public synchronized ArrayList<String> getSelectedFiles() {

		if (selectedFiles.isEmpty()) {
			selectedFiles = nodesLeaves.get(rootNode);
		}

		return selectedFiles;
	}

	public synchronized JTree getTree() {
		return tree;
	}

	public synchronized boolean isEnabled() {
		return enabled;
	}

	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@SuppressWarnings("unchecked")
	private void addAllLeaves(PPMutableTreeNode node, ArrayList<String> stack) {

		if (!node.isLeaf()) {
			Enumeration<PPMutableTreeNode> children = node.children();

			while (children.hasMoreElements()) {
				addAllLeaves(children.nextElement(), stack);
			}
		} else {
			String file = nodesFile.get(node);
			if (file != null) {
				stack.add(file);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (isEnabled()) {
			selectedFiles.clear();

			TreePath[] selection = tree.getSelectionPaths();

			if (selection != null) {
				for (TreePath tp : selection) {
					PPMutableTreeNode node = (PPMutableTreeNode) tp
							.getLastPathComponent();

					if (!node.isLeaf()) {
						addAllLeaves(node, selectedFiles);
					} else {
						selectedFiles.add(nodesFile.get(node));
					}
				}
			}
		}
	}
}
