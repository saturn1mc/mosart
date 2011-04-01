package picpix.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class PPFolderMirror implements TreeSelectionListener {

	private PPMutableTreeNode rootNode;
	private JTree tree;
	private boolean enabled;

	HashMap<PPMutableTreeNode, ArrayList<File>> nodesLeaves;
	HashMap<PPMutableTreeNode, File> nodesFile;
	private ArrayList<File> selectedFiles;

	public PPFolderMirror(File imageFolder) {
		rootNode = new PPMutableTreeNode(imageFolder.getName());

		tree = new JTree(rootNode);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.setAutoscrolls(true);

		nodesLeaves = new HashMap<PPMutableTreeNode, ArrayList<File>>();
		nodesFile = new HashMap<PPMutableTreeNode, File>();

		selectedFiles = new ArrayList<File>();

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
		try {
			if (ImageIO.read(file) != null) {

				PPMutableTreeNode fileNode = new PPMutableTreeNode(
						file.getName());
				parentNode.add(fileNode);

				linkLeafToAllParent(file, parentNode);
				nodesFile.put(fileNode, file);
			}
		} catch (IOException e) {
			// Nothing
		}
	}

	private void linkLeafToAllParent(File file, PPMutableTreeNode parentNode) {

		if (parentNode != null) {
			ArrayList<File> files = nodesLeaves.get(parentNode);

			if (files == null) {
				files = new ArrayList<File>();
				nodesLeaves.put(parentNode, files);
			}

			files.add(file);

			linkLeafToAllParent(file,
					(PPMutableTreeNode) parentNode.getParent());
		}
	}

	public synchronized ArrayList<File> getSelectedFiles() {

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
	private void addAllLeaves(PPMutableTreeNode node, ArrayList<File> stack) {

		if (!node.isLeaf()) {
			Enumeration<PPMutableTreeNode> children = node.children();

			while (children.hasMoreElements()) {
				addAllLeaves(children.nextElement(), stack);
			}
		} else {
			File file = nodesFile.get(node);
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
