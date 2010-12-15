package dependent;

import java.awt.Cursor;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import dependent.gui.MosArtGUI;

public class MosArtSupervisor {

	private static MosArtSupervisor singleton = null;
	private MosArtGUI gui;

	private MosArtSupervisor() {
		gui = null;
	}

	public synchronized static MosArtSupervisor getInstance() {
		if (singleton == null) {
			singleton = new MosArtSupervisor();
		}

		return singleton;
	}

	public synchronized void registerGUI(MosArtGUI gui) {
		this.gui = gui;
	}

	public synchronized void lock() {
		if(gui != null){
			gui.getLaunchButton().setEnabled(false);
		}
	}
	
	public synchronized void reset() {
		if (gui != null) {
			gui.getMainProgressBar().setIndeterminate(false);
			gui.getMainProgressBar().setString("Ready!");
			gui.getMainProgressBar().setValue(
					gui.getMainProgressBar().getMinimum());

			gui.getSubProgressBar().setIndeterminate(false);
			gui.getSubProgressBar().setString("Ready!");
			gui.getSubProgressBar().setValue(
					gui.getSubProgressBar().getMinimum());

			gui.getLaunchButton().setEnabled(true);
		}
	}

	public synchronized void reportMainTaskFinished() {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getMainProgressBar().setIndeterminate(false);
					gui.getMainProgressBar().setValue(
							gui.getMainProgressBar().getMaximum());
					gui.getMainProgressBar().setString("Done");

					gui.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					gui.getLaunchButton().setEnabled(true);

					JOptionPane.showMessageDialog(gui, "Wallpaper saved to : "
							+ gui.getTarget(), "Done",
							JOptionPane.INFORMATION_MESSAGE);

					return null;
				}
			}.execute();
		} else {
			System.out.println("Wallpaper saved");
		}
	}

	public synchronized void reportCrash(final String reason) {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getMainProgressBar().setString(null);
					gui.getMainProgressBar().setIndeterminate(false);
					gui.getMainProgressBar().setValue(
							gui.getMainProgressBar().getMinimum());
					gui.getMainProgressBar().setString("Error");

					gui.getSubProgressBar().setString(null);
					gui.getSubProgressBar().setIndeterminate(false);
					gui.getSubProgressBar().setValue(
							gui.getMainProgressBar().getMinimum());
					gui.getSubProgressBar().setString("Error");

					gui.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					gui.getLaunchButton().setEnabled(true);

					JOptionPane.showMessageDialog(gui, reason, "Error",
							JOptionPane.ERROR_MESSAGE);

					return null;
				}

			}.execute();
		} else {
			System.out.println("Program crashed : " + reason);
		}
	}

	public synchronized void reportMainTask(final String task) {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getMainProgressBar().setString(null);
					gui.getMainProgressBar().setIndeterminate(true);
					gui.getMainProgressBar().setString(task);

					return null;
				}
			}.execute();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportMainProgress(final String message,
			final float progress) {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getMainProgressBar().setString(null);
					gui.getMainProgressBar().setIndeterminate(false);
					gui.getMainProgressBar().setString(message);
					gui.getMainProgressBar().setValue((int) (100f * progress));

					return null;
				}
			}.execute();
		} else {
			System.out.println(Math.round(progress * 100f) + "% - " + message);
		}
	}

	public synchronized void reportTask(final String task) {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getSubProgressBar().setString(null);
					gui.getSubProgressBar().setIndeterminate(true);
					gui.getSubProgressBar().setString(task);

					return null;
				}
			}.execute();
		} else {
			System.out.println(task);
		}
	}

	public synchronized void reportProgress(final String message,
			final float progress) {
		if (gui != null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					gui.getSubProgressBar().setString(null);
					gui.getSubProgressBar().setIndeterminate(false);
					gui.getSubProgressBar().setString(
							Math.round(progress * 100f) + "% - " + message);
					gui.getSubProgressBar().setValue((int) (100f * progress));

					return null;
				}
			}.execute();
		} else {
			System.out.println(Math.round(progress * 100f) + "% - " + message);
		}
	}
}
