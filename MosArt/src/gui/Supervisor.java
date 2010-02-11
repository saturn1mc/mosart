package gui;

import javax.swing.JOptionPane;

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

	public synchronized void registerGUI(MosArtGUI gui) {
		this.gui = gui;
	}

	public void reportMainTaskFinished() {
		if (gui != null) {
			JOptionPane.showMessageDialog(gui, "Wallpaper saved to : "
					+ gui.getTarget(), "Done", JOptionPane.INFORMATION_MESSAGE);

			gui.getGenButton().setEnabled(true);
		}
	}

	public synchronized void reportMainProgress(String task) {
		if (gui != null) {
			gui.getGlobalProgressBar().setIndeterminate(true);
			gui.getGlobalProgressBar().setString(task);
			gui.getGlobalProgressBar().repaint();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportTask(String task) {
		if (gui != null) {
			gui.getProgressBar().setIndeterminate(true);
			gui.getProgressBar().setString(task);
			gui.getProgressBar().repaint();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportProgress(String message, float progress) {
		if (gui != null) {
			gui.getProgressBar().setIndeterminate(false);
			gui.getProgressBar()
					.setString((progress * 100f) + "% - " + message);
			gui.getProgressBar().setValue(100 * (int) progress);
			gui.getProgressBar().repaint();
		} else {
			System.out.println((progress * 100f) + "% - " + message);
		}
	}
}
