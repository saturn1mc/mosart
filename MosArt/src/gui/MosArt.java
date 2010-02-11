package gui;

import itc.ITCBaseReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import writers.MosaicPainter;

public class MosArt extends SwingWorker<File, String> {

	public static final int TASK_COUNT = 3;
	
	private ITCBaseReader baseReader;
	private MosaicPainter painter;
	private String targetFilename;

	public MosArt() {
		baseReader = null;
		painter = null;
		targetFilename = null;
	}

	public void setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
	}

	public void setSourceDirectory(File sourceDir) {
		if (baseReader == null) {
			baseReader = new ITCBaseReader(sourceDir);
		} else {
			baseReader.setArtworkDirectory(sourceDir);
		}
	}

	public void setMosaicProperties(int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) {

		if (painter == null) {
			painter = new MosaicPainter(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight, null);
		} else {
			painter.setProperties(imageWidth, imageHeight, mosaicWidth,
					mosaicHeight, null);
		}

	}
	
	public ITCBaseReader getBaseReader() {
		return baseReader;
	}
	
	public MosaicPainter getPainter() {
		return painter;
	}

	@Override
	protected File doInBackground() throws Exception {
		// Read base
		publish("(1/3) Reading artwork directories");
		baseReader.execute();

		// Paint
		publish("(2/3) Generating wallpaper");
		painter.setITCList(baseReader.get());
		painter.execute();

		// Save image
		publish("(3/3) Saving work to " + targetFilename);
		File result = new File(targetFilename);
		ImageIO.write((BufferedImage) painter.get().getImage(), "PNG", result);

		return result;
	}
	
	@Override
	protected void process(List<String> chunks) {
		for(String task : chunks){
			Supervisor.getInstance().reportMainProgress(task);
		}
	}
	
	@Override
	protected void done() {
		Supervisor.getInstance().reportMainTaskFinished();
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {

		// MosArt aa = new MosArt();
		//		
		// aa.chooseTargetFile("D:\\Mes Documents\\test.png");
		//		
		// File dir = aa.chooseSourceDir();
		//
		// if (dir != null) {
		// try {
		//				
		// aa.setMosaicProperties(2650, 2650, 20, 20,
		// aa.getBaseReader().getITCs());
		// aa.getPainter().execute();
		//				
		// ImageIcon image = aa.getPainter().get();
		//				
		// Supervisor.getInstance().reportTask("Writing image...");
		//				
		// ImageIO.write((BufferedImage)image.getImage(), "PNG", new
		// File(aa.getTargetFilename()));
		// JOptionPane.showMessageDialog(null, "Wallpaper available in : " +
		// aa.getTargetFilename(), "Done !", JOptionPane.INFORMATION_MESSAGE);
		//				
		// Supervisor.getInstance().reportTask("Image saved");
		//				
		// JFrame frame = new JFrame("Result");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//				
		// JLabel label = new JLabel(image);
		// frame.getContentPane().add(label);
		//				
		// frame.pack();
		// frame.setLocationRelativeTo(null);
		// frame.setVisible(true);
		//				
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
//	}

}
