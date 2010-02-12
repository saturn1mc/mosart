package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	private static final int DEFAULT_IMG_DIM = 800;
	private static final int DEFAULT_TILE_COUNT = 5;

	private MosArt worker;

	private JProgressBar mainProgressBar;
	private JProgressBar subProgressBar;

	private JTextField sourceField;
	private JTextField targetField;

	private JTextField imgWidthField;
	private JTextField imgHeightField;
	private JTextField tileWidthField;
	private JTextField tileHeightField;

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

		panel.add(new JLabel("Artwork directory : "));
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
				buildWorker();
				launchButton.setEnabled(false);
				worker.execute();
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
		mainProgressBar = new JProgressBar(0, 3);
		mainProgressBar.setStringPainted(true);

		subProgressBar = new JProgressBar(0, 100);
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
		this.worker = new MosArt();

		this.worker.setMosaicProperties(Integer.parseInt(imgWidthField
				.getText()), Integer.parseInt(imgHeightField.getText()),
				Integer.parseInt(tileWidthField.getText()), Integer
						.parseInt(tileHeightField.getText()));

		this.worker.setTargetFilename(targetField.getText());
		this.worker.setSourceDirectory(new File(sourceField.getText()));
	}

	public JProgressBar getSubProgressBar() {
		return subProgressBar;
	}

	public JProgressBar getMainProgressBar() {
		return mainProgressBar;
	}

	public JButton getLaunchButton() {
		return launchButton;
	}

	public String getTarget() {
		return targetField.getText();
	}

	public MosArtGUI() {

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
				Supervisor.getInstance().registerGUI(gui);
				gui.setVisible(true);
			}
		});
	}
}
