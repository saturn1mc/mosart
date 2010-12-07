package gui;

import itunes.ITPersistentID;
import itunes.itc.ITCArtwork;
import itunes.itc.ITCParser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MosArtGUI extends JFrame {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = -1328087492843746960L;
	private static final int PATH_FIELD_WIDTH = 400;
	private static final int DIM_FIELD_WIDTH = 30;
	private static final int FIELD_HEIGHT = 10;

	private static final int DEFAULT_IMG_DIM = 3000;
	private static final int DEFAULT_TILE_COUNT = 30;

	private MosArt worker;

	private String iTunesDir = "D:\\Mes Documents\\My Music\\iTunes"; // On my
																		// PC
	// private String iTunesDir = "/Users/camille/Music/iTunes"; //On my Mac

	private JProgressBar mainProgressBar;
	private JProgressBar subProgressBar;

	private JTextField sourceField;
	private JTextField targetField;

	private JTextField imgWidthField;
	private JTextField imgHeightField;
	private JTextField tileWidthField;
	private JTextField tileHeightField;

	private enum DimInputs {
		IMG_HEIGHT, IMG_WIDTH, TILE_HEIGHT, TILE_WIDTH
	};

	private JButton launchButton;

	private JPanel buildSourcePanel() {

		// Button
		JButton sourceButton = new JButton("...");

		MouseAdapter sourceMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setMultiSelectionEnabled(false);

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					sourceField.setText(fc.getSelectedFile().getPath());
				}
			}
		};

		sourceButton.addMouseListener(sourceMouse);
		sourceButton.setAlignmentX(RIGHT_ALIGNMENT);

		// Text field
		sourceField = new JTextField();
		sourceField.setPreferredSize(new Dimension(PATH_FIELD_WIDTH,
				FIELD_HEIGHT));

		sourceField.setAlignmentX(RIGHT_ALIGNMENT);

		// Panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		panel.add(new JLabel("iTunes Music directory : "));
		sourceField.setText(iTunesDir);
		panel.add(sourceField);
		panel.add(sourceButton);

		return panel;
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
		// targetField.setText("/Users/camille/Downloads/Mosaic.png"); //On my
		// Mac
		targetField.setPreferredSize(new Dimension(PATH_FIELD_WIDTH,
				FIELD_HEIGHT));

		// Panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		panel.add(new JLabel("Save in : "));
		panel.add(targetField);
		panel.add(targetButton);

		return panel;
	}

	private JButton buildLaunchButton() {
		launchButton = new JButton("Go!");

		MouseAdapter genMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (checking()) {
					buildWorker();
					launchButton.setEnabled(false);
					MosArtGUI.this.setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));
					worker.execute();
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

		// Image dimensions
		JPanel imgWpanel = new JPanel();
		imgWpanel.setLayout(new BoxLayout(imgWpanel, BoxLayout.LINE_AXIS));
		imgWpanel.add(new JLabel("Wallpaper width : "));
		imgWidthField = new JTextField();
		imgWidthField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgWidthField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH,
				FIELD_HEIGHT));
		imgWpanel.add(imgWidthField);

		JPanel imgHpanel = new JPanel();
		imgHpanel.setLayout(new BoxLayout(imgHpanel, BoxLayout.LINE_AXIS));
		imgHpanel.add(new JLabel("Wallpaper height : "));
		imgHeightField = new JTextField();
		imgHeightField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgHeightField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH,
				FIELD_HEIGHT));
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
		tileWidthField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH,
				FIELD_HEIGHT));
		tWcountPanel.add(tileWidthField);

		JPanel tHcountPanel = new JPanel();
		tHcountPanel
				.setLayout(new BoxLayout(tHcountPanel, BoxLayout.LINE_AXIS));
		tHcountPanel.add(new JLabel("Covers on height : "));
		tileHeightField = new JTextField();
		tileHeightField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileHeightField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH,
				FIELD_HEIGHT));
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

		return dimensionsPanel;
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
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

		centerPanel.add(buildSourcePanel());
		centerPanel.add(buildTargetPanel());

		centerPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Paths", TitledBorder.LEFT, TitledBorder.TOP));

		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
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

	private void buildEastPanel() {
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));

		eastPanel.add(buildDimensionsPanel());

		eastPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Dimensions", TitledBorder.LEFT, TitledBorder.TOP));

		this.getContentPane().add(eastPanel, BorderLayout.EAST);
	}

	private void buildWorker() {
		if (worker == null) {
			this.worker = new MosArt();
		}
		try {

			this.worker.setMosaicProperties(
					Integer.parseInt(imgWidthField.getText()),
					Integer.parseInt(imgHeightField.getText()),
					Integer.parseInt(tileWidthField.getText()),
					Integer.parseInt(tileHeightField.getText()));

			this.worker.setTargetFilename(targetField.getText());

			this.worker.setSourceDirectory(sourceField.getText());

		} catch (MosArtException e) {
			JOptionPane.showMessageDialog(MosArtGUI.this, e.getCause(),
					"Can't start", JOptionPane.ERROR_MESSAGE);
		}

	}

	private boolean checking() {

		boolean allFilled = true;

		if (sourceField.getText().isEmpty()) {
			sourceField.setBackground(Color.RED);
			allFilled = false;
		} else {
			sourceField.setBackground(Color.WHITE);
		}

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

	public synchronized String getTarget() {
		return targetField.getText();
	}

	public void showArtwork(String itc) throws IOException {
		ITCArtwork art = ITCParser.getInstance().getFullArtwork(new File(itc));
		ImageIcon icon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(
				art.getImageData())));
		JLabel label = new JLabel(icon);

		JDialog dialog = new JDialog();
		dialog.getContentPane().add(label);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public MosArtGUI() {

		super("MosArt");

		buildLaunchButton();
		buildProgressBars();

		buildCenterPanel();
		buildSouthPanel();
		buildEastPanel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MosArtGUI gui = new MosArtGUI();
				
				///TEST
				
				ITPersistentID tpid = new ITPersistentID("1C649B6833CF2E40");
				ITPersistentID apid = new ITPersistentID("AAD8BA43C0D64900");
				
				System.out.println(tpid.getHighBits());
				System.out.println(apid.getHighBits());
				
				System.out.println(tpid.getHighBits());
				System.out.println(apid.getHighBits());
				
				System.out.println(tpid.getLowBits());
				System.out.println(apid.getLowBits());
				
				///END TEST
				
				Supervisor.getInstance().registerGUI(gui);
				gui.setVisible(true);
			}
		});
	}
}
