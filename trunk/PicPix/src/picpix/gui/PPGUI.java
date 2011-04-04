package picpix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import picpix.tools.PPException;
import picpix.tools.PPSupervisor;
import picpix.workers.PPLauncher;

public class PPGUI extends JFrame {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = -1328087492843746960L;
	private static final int PATH_FIELD_WIDTH = 400;
	private static final int DIM_FIELD_WIDTH = 35;
	private static final int FIELD_HEIGHT = 25;
	private static final int TREE_WIDTH = 150;
	private static final int TREE_HEIGHT = 250;

	private static final int DEFAULT_IMG_DIM = 400;
	private static final int DEFAULT_TILE_COUNT = 20;

	private static enum DimInputs {
		IMG_HEIGHT, IMG_WIDTH, TILE_HEIGHT, TILE_WIDTH
	};

	private int mode;

	private PPFolderMirror libraryMirror;
	private PPLauncher worker;

	private JProgressBar mainProgressBar;
	private JProgressBar subProgressBar;

	private JTextField targetField;

	private JTextField imgFolderField;

	private JScrollPane treeView;

	private JPanel sourcePanel;
	private JTextField sourceField;
	private JButton sourceButton;

	private JTextField imgWidthField;
	private JTextField imgHeightField;
	private JTextField tileWidthField;
	private JTextField tileHeightField;

	private JButton launchButton;

	private PPGUI() {

		super("PicPix");

		buildLaunchButton();
		buildProgressBars();

		buildCenterPanel();
		buildEastPanel();
		buildSouthPanel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private void updateTree() {

		File imageFolder = new File(imgFolderField.getText());
		if (imageFolder != null && imageFolder.canRead()
				&& imageFolder.isDirectory()) {

			PPSupervisor.getInstance().lock();

			if (libraryMirror != null) {
				treeView.remove(libraryMirror.getTree());
			}

			PPSupervisor.getInstance().reportTask("Analysing folder");
			libraryMirror = new PPFolderMirror(imageFolder);

			treeView.setViewportView(libraryMirror.getTree());
			treeView.setPreferredSize(new Dimension(TREE_WIDTH, TREE_HEIGHT));

			PPSupervisor.getInstance().reset();
		}
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
		targetField.setText(System.getProperty("user.home") + File.separator
				+ "Mosaic.png"); // TODO replace with relative
		// path
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

	private JPanel buildImgFolderPanel() {
		JButton imgFolderButton = new JButton("...");

		MouseAdapter imgFolderMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setMultiSelectionEnabled(false);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					imgFolderField.setText(fc.getSelectedFile().getPath());

					new SwingWorker<Void, Void>() {
						protected Void doInBackground() throws Exception {
							updateTree();
							return null;
						};
					}.execute();
				}
			}
		};

		imgFolderButton.addMouseListener(imgFolderMouse);

		// Text field
		imgFolderField = new JTextField();
		imgFolderField.setText(System.getProperty("user.home") + File.separator
				+ "Pictures");
		Dimension fieldDim = new Dimension(PATH_FIELD_WIDTH, FIELD_HEIGHT);
		imgFolderField.setPreferredSize(fieldDim);
		imgFolderField.setMaximumSize(fieldDim);

		KeyAdapter imgFolderFieldKey = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					updateTree();
				}
			}
		};
		
		imgFolderField.addKeyListener(imgFolderFieldKey);
		
		// Panel
		JPanel imgFolderPanel = new JPanel();
		imgFolderPanel.setLayout(new BoxLayout(imgFolderPanel,
				BoxLayout.LINE_AXIS));

		imgFolderPanel.add(new JLabel("Image folder : "));
		imgFolderPanel.add(imgFolderField);
		imgFolderPanel.add(imgFolderButton);

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));

		container.add(imgFolderPanel);

		return container;
	}

	private JPanel buildSourcePanel() {
		sourceButton = new JButton("...");

		MouseAdapter sourceMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (sourceButton.isEnabled()) {
					JFileChooser fc = new JFileChooser();
					fc.setDialogType(JFileChooser.OPEN_DIALOG);
					fc.setMultiSelectionEnabled(false);

					int returnVal = fc.showOpenDialog(null);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						sourceField.setText(fc.getSelectedFile().getPath());
						updateTargetDimension();
					}
				}
			}
		};

		sourceButton.addMouseListener(sourceMouse);

		// Text field
		sourceField = new JTextField();
		Dimension fieldDim = new Dimension(PATH_FIELD_WIDTH, FIELD_HEIGHT);
		sourceField.setPreferredSize(fieldDim);
		sourceField.setMaximumSize(fieldDim);
		
		KeyAdapter sourceFieldkey = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					updateTargetDimension();
				}
			}
		};

		// Source Panel
		sourcePanel = new JPanel();
		sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.LINE_AXIS));

		sourcePanel.add(new JLabel("Source : "));
		sourcePanel.add(sourceField);
		sourcePanel.add(sourceButton);

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));

		container.add(sourcePanel);

		return container;
	}
	
	private void updateTargetDimension(){
		try {
			BufferedImage source = ImageIO.read(new File(sourceField.getText()));
			imgWidthField.setText(Integer.toString(source.getWidth()));
			imgHeightField.setText(Integer.toString(source.getHeight()));	
		} catch (IOException e) {
			// Nothing
		}
	}

	private JButton buildLaunchButton() {
		launchButton = new JButton("Go!");

		MouseAdapter genMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (checking()) {

					try {
						PPSupervisor.getInstance().lock();

						PPGUI.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						launchWorker();
					} catch (PPException me) {
						JOptionPane.showMessageDialog(PPGUI.this,
								me.getMessage(), "Can't start",
								JOptionPane.ERROR_MESSAGE);

						PPSupervisor.getInstance().reset();

					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(PPGUI.this,
								ioe.getMessage(), "Can't start",
								JOptionPane.ERROR_MESSAGE);

						PPSupervisor.getInstance().reset();
					}
				} else {
					JOptionPane.showMessageDialog(PPGUI.this,
							"Please check inputs", "Can't start",
							JOptionPane.WARNING_MESSAGE);

					PPSupervisor.getInstance().reset();
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
		tWcountPanel.add(new JLabel("Images on width : "));
		tileWidthField = new JTextField();
		tileWidthField.setText(Integer.toString(DEFAULT_TILE_COUNT));
		tileWidthField.setPreferredSize(fieldDim);
		tileWidthField.setMaximumSize(fieldDim);
		tWcountPanel.add(tileWidthField);

		JPanel tHcountPanel = new JPanel();
		tHcountPanel
				.setLayout(new BoxLayout(tHcountPanel, BoxLayout.LINE_AXIS));
		tHcountPanel.add(new JLabel("Images on height : "));
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

	private JPanel buildModePanel() {
		// Mosaic mode button
		JRadioButton modeMosaic = new JRadioButton("Mosaic");

		MouseAdapter mosaicAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mode = PPLauncher.MOSAIC_MODE;
				sourceField.setEnabled(false);
				sourceButton.setEnabled(false);
				sourceField.setBackground(Color.GRAY);
			}
		};

		modeMosaic.addMouseListener(mosaicAdapter);

		// Photo mode button
		JRadioButton modePhoto = new JRadioButton("Photo");

		MouseAdapter photoAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mode = PPLauncher.PHOTO_MODE;
				sourceField.setEnabled(true);
				sourceButton.setEnabled(true);
				sourceField.setBackground(Color.WHITE);
			}
		};

		modePhoto.addMouseListener(photoAdapter);

		// Set default selection
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(modeMosaic);
		modeGroup.add(modePhoto);

		sourceField.setEnabled(false);
		sourceButton.setEnabled(false);
		sourceField.setBackground(Color.LIGHT_GRAY);

		modeMosaic.setSelected(true);
		mode = PPLauncher.MOSAIC_MODE;

		// Mode Panel
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.PAGE_AXIS));
		modePanel.add(modeMosaic);
		modePanel.add(modePhoto);

		return modePanel;
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
		// Target panel
		JPanel targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.PAGE_AXIS));

		targetPanel.add(buildTargetPanel());

		targetPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Target", TitledBorder.LEFT, TitledBorder.TOP));

		// Image folder panel
		JPanel imageFolderPanel = new JPanel();
		imageFolderPanel.setLayout(new BoxLayout(imageFolderPanel,
				BoxLayout.PAGE_AXIS));

		imageFolderPanel.add(buildImgFolderPanel());

		imageFolderPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Images", TitledBorder.LEFT, TitledBorder.TOP));

		// Source panel
		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.PAGE_AXIS));

		sourcePanel.add(buildSourcePanel());

		sourcePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Model", TitledBorder.LEFT, TitledBorder.TOP));

		// Dimension panel
		JPanel dimPanel = new JPanel();
		dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.PAGE_AXIS));

		dimPanel.add(buildDimensionsPanel());

		dimPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Dimensions", TitledBorder.LEFT, TitledBorder.TOP));

		// Center panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.add(targetPanel);
		centerPanel.add(imageFolderPanel);
		centerPanel.add(sourcePanel);
		centerPanel.add(dimPanel);

		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
	}

	private void buildEastPanel() {
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new FlowLayout());

		eastPanel.add(buildModePanel());

		eastPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mode",
				TitledBorder.LEFT, TitledBorder.TOP));

		this.getContentPane().add(eastPanel, BorderLayout.EAST);
	}

	private void buildWestPanel() {

		JPanel westPanel = new JPanel();
		westPanel.setLayout(new FlowLayout());

		treeView = new JScrollPane();
		updateTree();
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

	private void launchWorker() throws PPException, IOException {
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				BufferedImage source = null;

				if (mode == PPLauncher.PHOTO_MODE) {
					source = ImageIO.read(new File(sourceField.getText()));
				}

				if (worker == null) {
					worker = new PPLauncher(source, mode,
							libraryMirror.getSelectedFiles(),
							targetField.getText(),
							Integer.parseInt(imgWidthField.getText()),
							Integer.parseInt(imgHeightField.getText()),
							Integer.parseInt(tileWidthField.getText()),
							Integer.parseInt(tileHeightField.getText()));
				} else {
					worker.setPainterProperties(source, mode,
							libraryMirror.getSelectedFiles(),
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

		if (sourceField.isEnabled() && sourceField.getText().isEmpty()) {
			sourceField.setBackground(Color.RED);
			allFilled = false;
		} else if (sourceField.isEnabled()) {
			sourceField.setBackground(Color.WHITE);
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

	public PPFolderMirror getLibraryMirror() {
		return libraryMirror;
	}

	public synchronized String getTarget() {
		return targetField.getText();
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b == true) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					buildWestPanel();
					PPGUI.this.pack();
					PPGUI.this.repaint();
					PPGUI.this.setLocationRelativeTo(null);

					return null;
				}
			}.execute();
		}
	}

	public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				PPGUI gui = new PPGUI();
				PPSupervisor.getInstance().registerGUI(gui);
				gui.setVisible(true);
			}
		});
	}
}
