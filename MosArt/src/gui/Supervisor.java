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
			gui.getMainProgressBar().setIndeterminate(false);
			gui.getMainProgressBar().setValue(
					gui.getMainProgressBar().getMaximum());
			gui.getMainProgressBar().setString("Done");
			gui.getMainProgressBar().repaint();

			gui.getLaunchButton().setEnabled(true);
			
			JOptionPane.showMessageDialog(gui, "Wallpaper saved to : "
					+ gui.getTarget(), "Done", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public synchronized void reportMainProgress(String task) {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(true);
			gui.getMainProgressBar().setString(task);
			gui.getMainProgressBar().repaint();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportTask(String task) {
		if (gui != null) {
			gui.getSubProgressBar().setIndeterminate(true);
			gui.getSubProgressBar().setString(task);
			gui.getSubProgressBar().repaint();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportProgress(String message, float progress) {
		if (gui != null) {
			gui.getSubProgressBar().setIndeterminate(false);
			gui.getSubProgressBar()
					.setString((progress * 100f) + "% - " + message);
			gui.getSubProgressBar().setValue((int)(100f * progress));
			gui.getSubProgressBar().repaint();
		} else {
			System.out.println((progress * 100f) + "% - " + message);
		}
	}
}
