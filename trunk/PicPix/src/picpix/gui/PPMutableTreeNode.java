package picpix.gui;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class PPMutableTreeNode extends DefaultMutableTreeNode {
	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -2659370229434592847L;

	private static final Comparator<MutableTreeNode> comparator = new Comparator<MutableTreeNode>() {

		@Override
		public int compare(MutableTreeNode o1, MutableTreeNode o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};

	public PPMutableTreeNode() {
		super();
	}

	public PPMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	public PPMutableTreeNode(Object userObject) {
		super(userObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insert(MutableTreeNode newChild, int childIndex) {
		// TODO Auto-generated method stub
		super.insert(newChild, childIndex);
		Collections.sort(this.children, comparator);
	}
}
