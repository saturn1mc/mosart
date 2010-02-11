package gui;

public class Supervisor {

	private static Supervisor singleton = null;
	private MosArtGUI gui;

	private Supervisor() {
		gui = null;
	}

	public static Supervisor getInstance() {
		if (singleton == null) {
			singleton = new Supervisor();
		}

		return singleton;
	}

	public void registerGUI(MosArtGUI gui) {
		this.gui = gui;
	}

	public void reportMainProgress(String task) {
		if (gui != null) {
			gui.getGlobalProgressBar().setIndeterminate(true);
			gui.getGlobalProgressBar().setString(task);
		} else {
			System.out.println(task);
		}
	} 
	
	public void reportTask(String task) {
		if (gui != null) {
			gui.getProgressBar().setIndeterminate(true);
			gui.getProgressBar().setString(task);
		} else {
			System.out.println(task);
		}
	}

	public void reportProgress(String message, float progress) {
		if (gui != null) {
			gui.getProgressBar().setIndeterminate(false);
			gui.getProgressBar().setString((progress * 100f) + "% - " + message);
			gui.getProgressBar().setValue(100*(int)progress);
		} else {
			System.out.println((progress * 100f) + "% - " + message);
		}
	}
}
