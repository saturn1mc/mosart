package gui;

import javax.swing.JOptionPane;

public class Supervisor {

	private static Supervisor singleton = null;
	private MosArtGUI gui;

	private Supervisor() {
		gui = null;
	}

	public synchronized static Supervisor getInstance() {
		if (singleton == null) {
			singleton = new Supervisor();
		}

		return singleton;
	}

	public synchronized void registerGUI(MosArtGUI gui) {
		this.gui = gui;
	}

	public synchronized void reportMainTaskFinished() {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(false);
			gui.getMainProgressBar().setValue(
					gui.getMainProgressBar().getMaximum());
			gui.getMainProgressBar().setString("Done");

			gui.getLaunchButton().setEnabled(true);

			JOptionPane.showMessageDialog(gui,
					"Wallpaper saved to : " + gui.getTarget(), "Done",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			System.out.println("Wallpaper saved");
		}
	}

	public synchronized void reportCrash(String reason) {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(false);
			gui.getMainProgressBar().setValue(
					gui.getMainProgressBar().getMinimum());
			gui.getMainProgressBar().setString("Error");

			gui.getSubProgressBar().setIndeterminate(false);
			gui.getSubProgressBar().setValue(
					gui.getMainProgressBar().getMinimum());
			gui.getSubProgressBar().setString("Error");

			gui.getLaunchButton().setEnabled(true);

			JOptionPane.showMessageDialog(gui, reason, "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			System.out.println("Program crashed : " + reason);
		}
	}

	public synchronized void reportMainTask(String task) {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(true);
			gui.getMainProgressBar().setString(task);
		} else {
			System.out.println(task);
		}
	}
	
	public synchronized void reportMainProgress(String message, float progress) {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(false);
			gui.getMainProgressBar().setString(message);
			gui.getMainProgressBar().setValue((int) (100f * progress));
		} else {
			System.out.println(Math.round(progress * 100f) + "% - " + message);
		}
	}

	public synchronized void reportTask(String task) {
		if (gui != null) {
				gui.getSubProgressBar().setIndeterminate(true);
				gui.getSubProgressBar().setString(task);
		} else {
			System.out.println(task);
		}
	}
	
	public synchronized void reportProgress(String message, float progress) {
		if (gui != null) {
			gui.getSubProgressBar().setIndeterminate(false);
			gui.getSubProgressBar().setString(
					Math.round(progress * 100f) + "% - " + message);
			gui.getSubProgressBar().setValue((int) (100f * progress));
		} else {
			System.out.println(Math.round(progress * 100f) + "% - " + message);
		}
	}
}
