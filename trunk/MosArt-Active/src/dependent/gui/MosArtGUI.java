package dependent.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dependent.MosArtException;
import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.iTunes;
import dependent.workers.MosArtLauncher;

public class MosArtGUI extends JFrame {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = -1328087492843746960L;
	private static final int PATH_FIELD_WIDTH = 400;
	private static final int DIM_FIELD_WIDTH = 35;
	private static final int FIELD_HEIGHT = 25;
	private static final int TREE_WIDTH = 150;
	private static final int TREE_HEIGHT = 250;

	private static final int DEFAULT_IMG_DIM = 2500;
	private static final int DEFAULT_TILE_COUNT = 20;

	private static enum DimInputs {
		IMG_HEIGHT, IMG_WIDTH, TILE_HEIGHT, TILE_WIDTH
	};

	private iTunes itunes;
	private MosArtLibraryMirror libraryMirror;

	private MosArtLauncher worker;

	private JProgressBar mainProgressBar;
	private JProgressBar subProgressBar;

	private JTextField targetField;

	private JTextField imgWidthField;
	private JTextField imgHeightField;
	private JTextField tileWidthField;
	private JTextField tileHeightField;

	private JButton launchButton;

	private MosArtGUI() {

		super("MosArt");

		buildLaunchButton();
		buildProgressBars();

		buildCenterPanel();
		buildSouthPanel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
	}

	private void buildTreePanel(JScrollPane treeView) {

		libraryMirror = new MosArtLibraryMirror();

		MosArtSupervisor.getInstance().lock();

		int trackCount = itunes.getLibraryPlaylist().getTracks().getCount();

		for (int t = 0; t < trackCount; t++) {

			MosArtSupervisor.getInstance().reportProgress("Analysing library",
					((float) (t + 1) / (float) trackCount));

			ITTrack track = itunes.getLibraryPlaylist().getTracks()
					.getItem(t + 1);

			if (track.getArtwork().getCount() > 0) {
				libraryMirror.addTrack(track);
			}
		}

		treeView.setViewportView(libraryMirror.getLibraryTree());
		treeView.setPreferredSize(new Dimension(TREE_WIDTH, TREE_HEIGHT));

		MosArtSupervisor.getInstance().reset();
	}

	private JPanel buildTargetPanel() {
		JButton targetButton = new JButton("...");

		MouseAdapter targetMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				fc.setMultiSelectionEnabled(false);

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetField.setText(fc.getSelectedFile().getPath());
				}
			}
		};

		targetButton.addMouseListener(targetMouse);

		// Text field
		targetField = new JTextField();
		targetField.setText("D:\\Mes Documents\\Mosaic.png"); // On my PC
		// targetField.setText("/Users/camille/Downloads/Mosaic.png"); // On my
		// Mac
		Dimension fieldDim = new Dimension(PATH_FIELD_WIDTH, FIELD_HEIGHT);
		targetField.setPreferredSize(fieldDim);
		targetField.setMaximumSize(fieldDim);

		// Panel
		JPanel targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.LINE_AXIS));

		targetPanel.add(new JLabel("Save in : "));
		targetPanel.add(targetField);
		targetPanel.add(targetButton);

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));

		container.add(targetPanel);

		return container;
	}

	private JButton buildLaunchButton() {
		launchButton = new JButton("Go!");

		MouseAdapter genMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (checking()) {

					try {
						MosArtSupervisor.getInstance().lock();

						MosArtGUI.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						launchWorker();
					} catch (MosArtException me) {
						JOptionPane.showMessageDialog(MosArtGUI.this,
								me.getMessage(), "Can't start",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(MosArtGUI.this,
								ioe.getMessage(), "Can't start",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(MosArtGUI.this,
							"Please check inputs", "Can't start",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		};

		launchButton.addMouseListener(genMouse);
		launchButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		return launchButton;
	}

	private JPanel buildDimensionsPanel() {

		Dimension fieldDim = new Dimension(DIM_FIELD_WIDTH, FIELD_HEIGHT);

		// Image dimensions
		JPanel imgWpanel = new JPanel();
		imgWpanel.setLayout(new BoxLayout(imgWpanel, BoxLayout.LINE_AXIS));
		imgWpanel.add(new JLabel("Wallpaper width : "));
		imgWidthField = new JTextField();
		imgWidthField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgWidthField.setPreferredSize(fieldDim);
		imgWidthField.setMaximumSize(fieldDim);
		imgWpanel.add(imgWidthField);

		JPanel imgHpanel = new JPanel();
		imgHpanel.setLayout(new BoxLayout(imgHpanel, BoxLayout.LINE_AXIS));
		imgHpanel.add(new JLabel("Wallpaper height : "));
		imgHeightField = new JTextField();
		imgHeightField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgHeightField.setPreferredSize(fieldDim);
		imgHeightField.setMaximumSize(fieldDim);
		imgHpanel.add(imgHeightField);

		JPanel imgPanel = new JPanel();
		imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.PAGE_AXIS));
		imgPanel.add(imgWpanel);
		imgPanel.add(imgHpanel);

		// Tile counts
		JPanel tWcountPanel = new JPanel();
		tWcountPanel
				.setLayout(new BoxLayout(tWcountPanel, BoxLayout.LINE_AXIS));
		tWcountPanel.add(new JLabel("Covers on width : "));
		tileWidthField = new JTextField();
		tileWidthField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileWidthField.setPreferredSize(fieldDim);
		tileWidthField.setMaximumSize(fieldDim);
		tWcountPanel.add(tileWidthField);

		JPanel tHcountPanel = new JPanel();
		tHcountPanel
				.setLayout(new BoxLayout(tHcountPanel, BoxLayout.LINE_AXIS));
		tHcountPanel.add(new JLabel("Covers on height : "));
		tileHeightField = new JTextField();
		tileHeightField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileHeightField.setPreferredSize(fieldDim);
		tileHeightField.setMaximumSize(fieldDim);
		tHcountPanel.add(tileHeightField);

		JPanel tilePanel = new JPanel();
		tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.PAGE_AXIS));
		tilePanel.add(tWcountPanel);
		tilePanel.add(tHcountPanel);

		// Dimensions panel
		JPanel dimensionsPanel = new JPanel();
		dimensionsPanel.setLayout(new BoxLayout(dimensionsPanel,
				BoxLayout.LINE_AXIS));
		dimensionsPanel.add(imgPanel);
		dimensionsPanel.add(tilePanel);

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));

		container.add(dimensionsPanel);

		return container;
	}

	private void buildProgressBars() {
		mainProgressBar = new JProgressBar(0, 100);
		mainProgressBar.setIndeterminate(false);
		mainProgressBar.setStringPainted(true);

		subProgressBar = new JProgressBar(0, 100);
		subProgressBar.setIndeterminate(false);
		subProgressBar.setStringPainted(true);
	}

	private void buildCenterPanel() {
		JPanel targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.PAGE_AXIS));

		targetPanel.add(buildTargetPanel());

		targetPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Target", TitledBorder.LEFT, TitledBorder.TOP));

		JPanel dimPanel = new JPanel();
		dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.PAGE_AXIS));

		dimPanel.add(buildDimensionsPanel());

		dimPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Dimensions", TitledBorder.LEFT, TitledBorder.TOP));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.add(targetPanel);
		centerPanel.add(dimPanel);

		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
	}

	private void buildWestPanel() {

		JPanel westPanel = new JPanel();
		westPanel.setLayout(new FlowLayout());

		JScrollPane treeView = new JScrollPane();
		buildTreePanel(treeView);
		westPanel.add(treeView);

		westPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Selection", TitledBorder.LEFT, TitledBorder.TOP));

		this.getContentPane().add(westPanel, BorderLayout.WEST);
	}

	private void buildSouthPanel() {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

		southPanel.add(launchButton);
		southPanel.add(mainProgressBar);
		southPanel.add(subProgressBar);

		southPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Creation", TitledBorder.LEFT, TitledBorder.TOP));

		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
	}

	private void launchWorker() throws MosArtException, IOException {

		final int mode = MosArtLauncher.PHOTO_MODE; // FOR TEST PURPOSE
		final BufferedImage source = ImageIO.read(new File(
				"D:\\Mes Documents\\seb.JPG"));

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (worker == null) {
					worker = new MosArtLauncher(source, mode,
							libraryMirror.getSelectedTracks(),
							targetField.getText(),
							Integer.parseInt(imgWidthField.getText()),
							Integer.parseInt(imgHeightField.getText()),
							Integer.parseInt(tileWidthField.getText()),
							Integer.parseInt(tileHeightField.getText()));
				} else {
					worker.setPainterProperties(source, mode,
							libraryMirror.getSelectedTracks(),
							targetField.getText(),
							Integer.parseInt(imgWidthField.getText()),
							Integer.parseInt(imgHeightField.getText()),
							Integer.parseInt(tileWidthField.getText()),
							Integer.parseInt(tileHeightField.getText()));
				}

				worker.execute();

				return null;
			}

		}.execute();
	}

	private boolean checking() {

		boolean allFilled = true;

		if (targetField.getText().isEmpty()) {
			targetField.setBackground(Color.RED);
			allFilled = false;
		} else {
			targetField.setBackground(Color.WHITE);
		}

		if (imgHeightField.getText().isEmpty()) {
			imgHeightField.setBackground(Color.RED);
			allFilled = false;
		} else {
			imgHeightField.setBackground(Color.WHITE);
		}

		if (imgWidthField.getText().isEmpty()) {
			imgWidthField.setBackground(Color.RED);
			allFilled = false;
		} else {
			imgWidthField.setBackground(Color.WHITE);
		}

		if (tileHeightField.getText().isEmpty()
				&& !tileHeightField.getText().isEmpty()) {
			tileHeightField.setBackground(Color.RED);
			allFilled = false;
		} else {
			tileHeightField.setBackground(Color.WHITE);
		}

		if (tileWidthField.getText().isEmpty()
				&& !tileWidthField.getText().isEmpty()) {
			tileWidthField.setBackground(Color.RED);
			allFilled = false;
		} else {
			tileWidthField.setBackground(Color.WHITE);
		}

		boolean allCorrect = true;

		if (allFilled) {
			// Dimensions
			DimInputs dimension = DimInputs.IMG_HEIGHT;

			try {
				dimension = DimInputs.IMG_HEIGHT;
				if (Integer.parseInt(imgHeightField.getText()) < 0) {
					imgHeightField.setBackground(Color.RED);
					allCorrect = false;
				} else {
					imgHeightField.setBackground(Color.WHITE);
				}

				dimension = DimInputs.IMG_WIDTH;
				if (Integer.parseInt(imgWidthField.getText()) < 0) {
					imgWidthField.setBackground(Color.RED);
					allCorrect = false;
				} else {
					imgWidthField.setBackground(Color.WHITE);
				}

				dimension = DimInputs.TILE_HEIGHT;
				if (Integer.parseInt(tileHeightField.getText()) < 0) {
					tileHeightField.setBackground(Color.RED);
					allCorrect = false;
				} else {
					tileHeightField.setBackground(Color.WHITE);
				}

				dimension = DimInputs.TILE_WIDTH;
				if (Integer.parseInt(tileWidthField.getText()) < 0) {
					tileWidthField.setBackground(Color.RED);
					allCorrect = false;
				} else {
					tileWidthField.setBackground(Color.WHITE);
				}
			} catch (NumberFormatException e) {

				switch (dimension) {
				case IMG_HEIGHT:
					imgHeightField.setBackground(Color.RED);
					break;

				case IMG_WIDTH:
					imgWidthField.setBackground(Color.RED);
					break;

				case TILE_HEIGHT:
					tileHeightField.setBackground(Color.RED);
					break;

				case TILE_WIDTH:
					tileWidthField.setBackground(Color.RED);
					break;
				}

				return false;
			}
		}

		return allFilled && allCorrect;
	}

	public synchronized JProgressBar getSubProgressBar() {
		return subProgressBar;
	}

	public synchronized JProgressBar getMainProgressBar() {
		return mainProgressBar;
	}

	public synchronized JButton getLaunchButton() {
		return launchButton;
	}

	public MosArtLibraryMirror getLibraryMirror() {
		return libraryMirror;
	}

	public synchronized String getTarget() {
		return targetField.getText();
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b == true && itunes == null) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					MosArtSupervisor.getInstance().reportTask(
							"Connecting to iTunes");

					MosArtSupervisor.getInstance().lock();
					itunes = new iTunes();
					MosArtSupervisor.getInstance().reset();

					buildWestPanel();
					MosArtGUI.this.pack();
					MosArtGUI.this.repaint();

					return null;
				}
			}.execute();
		}
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MosArtGUI gui = new MosArtGUI();
				MosArtSupervisor.getInstance().registerGUI(gui);
				gui.setVisible(true);
			}
		});
	}
}
