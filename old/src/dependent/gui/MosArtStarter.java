package dependent.gui;

import javax.swing.SwingWorker;

import dependent.com.dt.iTunesController.iTunes;

public class MosArtStarter extends SwingWorker<iTunes, String> {

	private iTunes iTunesStarter() {
		iTunes itunes = new iTunes();
		boolean available = false;
		
		while (!available) {
			try {
				itunes.getPlayerState() ;
				available = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return itunes;
	}

	@Override
	protected iTunes doInBackground() throws Exception {
		return iTunesStarter();
	}

}
