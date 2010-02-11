package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MosArtGUI extends JFrame {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = -1328087492843746960L;

	private MosArt worker;

	private JProgressBar globalProgressBar;
	private JProgressBar progressBar;

	private JButton sourceButton;
	private JButton targetButton;
	private JButton genButton;

	private void buildSourceButton() {
		sourceButton = new JButton("...");

		MouseAdapter sourceMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// A VIRER
				fc
						.setCurrentDirectory(new File(
								"D:\\Mes Documents\\My Music\\iTunes\\Album Artwork\\Download"));
				//

				fc.setMultiSelectionEnabled(false);

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					worker.setSourceDirectory(fc.getSelectedFile());
				}
			}
		};

		sourceButton.addMouseListener(sourceMouse);
	}

	private void buildTargetButton() {
		targetButton = new JButton("...");

		MouseAdapter targetMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				fc.setMultiSelectionEnabled(false);

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					worker.setTargetFilename(fc.getSelectedFile().getPath());
				}
			}
		};

		targetButton.addMouseListener(targetMouse);
	}

	private void buildGenButton() {
		genButton = new JButton("Go!");

		MouseAdapter genMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				// TODO CHANGER AVEC LES PREFS UTILISATEUR
				worker.setMosaicProperties(800, 600, 4, 4);
				//
				
				genButton.setEnabled(false);
				worker.execute();
			}
		};

		genButton.addMouseListener(genMouse);
	}

	private void buildProgressBars() {
		globalProgressBar = new JProgressBar(0, 3);
		globalProgressBar.setStringPainted(true);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
	}

	private void buildWorker() {
		this.worker = new MosArt();
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JProgressBar getGlobalProgressBar() {
		return globalProgressBar;
	}

	public JButton getGenButton() {
		return genButton;
	}

	public String getTarget() {
		// TODO a changer
		return "TODO recuperer target dans un champ utilisateur";
	}

	public MosArtGUI() {

		buildWorker();
		buildSourceButton();
		buildTargetButton();
		buildGenButton();
		buildProgressBars();

		// A virer
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(sourceButton);
		panel.add(targetButton);
		panel.add(genButton);
		panel.add(globalProgressBar);
		panel.add(progressBar);
		this.getContentPane().add(panel);
		//

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MosArtGUI gui = new MosArtGUI();
				Supervisor.getInstance().registerGUI(gui);
				gui.setVisible(true);
			}
		});
	}
}
