package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class MosArtGUI extends JFrame {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = -1328087492843746960L;
	private static final int PATH_FIELD_WIDTH = 400;
	private static final int DIM_FIELD_WIDTH = 30;
	private static final int FIELD_HEIGHT = 15;

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

	private JButton genButton;

	private JPanel buildSourcePanel() {

		// Button
		JButton sourceButton = new JButton("...");

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

		// Text field
		sourceField = new JTextField();
		sourceField.setPreferredSize(new Dimension(PATH_FIELD_WIDTH, FIELD_HEIGHT));

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
					worker.setTargetFilename(fc.getSelectedFile().getPath());
				}
			}
		};

		targetButton.addMouseListener(targetMouse);

		// Text field
		targetField = new JTextField();
		targetField.setPreferredSize(new Dimension(PATH_FIELD_WIDTH, FIELD_HEIGHT));

		// Panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		panel.add(new JLabel("Save in : "));
		panel.add(targetField);
		panel.add(targetButton);

		return panel;
	}

	private JButton buildGenButton() {
		genButton = new JButton("Go!");

		MouseAdapter genMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				worker.setMosaicProperties(Integer.parseInt(imgWidthField
						.getText()),
						Integer.parseInt(imgHeightField.getText()), Integer
								.parseInt(tileWidthField.getText()), Integer
								.parseInt(tileHeightField.getText()));

				genButton.setEnabled(false);
				worker.execute();
			}
		};

		genButton.addMouseListener(genMouse);

		return genButton;
	}

	private JPanel buildDimensionsPanel(){
		
		// Image dimensions 
		JPanel imgWpanel = new JPanel();
		imgWpanel.setLayout(new BoxLayout(imgWpanel, BoxLayout.LINE_AXIS));
		imgWpanel.add(new JLabel("Wallpaper width"));
		imgWidthField = new JTextField();
		imgWidthField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgWidthField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH, FIELD_HEIGHT));
		imgWpanel.add(imgWidthField);
		
		JPanel imgHpanel = new JPanel();
		imgHpanel.setLayout(new BoxLayout(imgHpanel, BoxLayout.LINE_AXIS));
		imgHpanel.add(new JLabel("Wallpaper height"));
		imgHeightField = new JTextField();
		imgHeightField.setText(Integer.toString(DEFAULT_IMG_DIM));
		imgHeightField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH, FIELD_HEIGHT));
		imgHpanel.add(imgHeightField);
		
		JPanel imgPanel = new JPanel();
		imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.PAGE_AXIS));
		imgPanel.add(imgWpanel);
		imgPanel.add(imgHpanel);
		
		// Tile counts
		JPanel tWcountPanel = new JPanel();
		tWcountPanel.setLayout(new BoxLayout(tWcountPanel, BoxLayout.LINE_AXIS));
		tWcountPanel.add(new JLabel("Covers on width"));
		tileWidthField = new JTextField();
		tileWidthField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileWidthField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH, FIELD_HEIGHT));
		tWcountPanel.add(tileWidthField);
		
		JPanel tHcountPanel = new JPanel();
		tHcountPanel.setLayout(new BoxLayout(tHcountPanel, BoxLayout.LINE_AXIS));
		tHcountPanel.add(new JLabel("Covers on height"));
		tileHeightField = new JTextField();
		tileHeightField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileHeightField.setPreferredSize(new Dimension(DIM_FIELD_WIDTH, FIELD_HEIGHT));
		tWcountPanel.add(tileHeightField);
		
		JPanel tilePanel = new JPanel();
		tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.PAGE_AXIS));
		tilePanel.add(tWcountPanel);
		tilePanel.add(tHcountPanel);
		
		// Dimensions panel
		JPanel dimensionsPanel = new JPanel();
		dimensionsPanel.setLayout(new BoxLayout(dimensionsPanel, BoxLayout.LINE_AXIS));
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

	private void buildWorker() {
		this.worker = new MosArt();
	}

	public JProgressBar getSubProgressBar() {
		return subProgressBar;
	}

	public JProgressBar getMainProgressBar() {
		return mainProgressBar;
	}

	public JButton getGenButton() {
		return genButton;
	}

	public String getTarget() {
		return targetField.getText();
	}

	public MosArtGUI() {

		buildWorker();

		buildGenButton();
		buildProgressBars();

		this.getContentPane().add(buildSourcePanel(), BorderLayout.CENTER);
		this.getContentPane().add(buildTargetPanel(), BorderLayout.CENTER);
		this.getContentPane().add(buildDimensionsPanel(), BorderLayout.EAST);
		this.getContentPane().add(mainProgressBar, BorderLayout.SOUTH);
		this.getContentPane().add(subProgressBar, BorderLayout.SOUTH);

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
