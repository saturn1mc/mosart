package xml;

import javax.swing.SwingWorker;

public class ITLParser extends SwingWorker<Void, Void>{

	public static final String ROOT_NODE_NAME = "plist";
	public static final String DICT_NODE_NAME = "dict";
	
	@Override
	protected Void doInBackground() throws Exception {
		// Got to find a way to parse iTunes library XML
		return null;
	}
	
	
}
