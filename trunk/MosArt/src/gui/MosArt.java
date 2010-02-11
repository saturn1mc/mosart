package gui;

import itc.ITCBaseReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import writers.MosaicPainter;

public class MosArt {

	private ITCBaseReader baseReader;
	private MosaicPainter painter;
	private String targetFilename;
	
	public MosArt() {
		baseReader = new ITCBaseReader();
		painter = null;
	}
	
	public ITCBaseReader getBaseReader() {
		return baseReader;
	}
	
	public MosaicPainter getPainter() {
		return painter;
	}

	public String getTargetFilename() {
		return targetFilename;
	}
	
	public File chooseSourceDir() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		//A VIRER
		fc.setCurrentDirectory(new File("D:\\Mes Documents\\My Music\\iTunes\\Album Artwork\\Download"));
		//
		
		fc.setMultiSelectionEnabled(false);

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else {
			return null;
		}
	}
	
	public void setMosaicProperties(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight, ArrayList<String> itcList) {
		
		painter = new MosaicPainter(imageWidth, imageHeight, mosaicWidth, mosaicHeight, itcList);
		
	}
	
	public void chooseTargetFile(String targetFilename){
		this.targetFilename = targetFilename;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MosArt aa = new MosArt();
		
		aa.chooseTargetFile("D:\\Mes Documents\\test.png");
		
		File dir = aa.chooseSourceDir();

		if (dir != null) {
			try {
				
				aa.setMosaicProperties(2650, 2650, 20, 20, aa.getBaseReader().getITCs(dir));
				aa.getPainter().execute();
				
				ImageIcon image = aa.getPainter().get();
				
				Supervisor.getInstance().reportTask("Writing image...");
				
				ImageIO.write((BufferedImage)image.getImage(), "PNG", new File(aa.getTargetFilename()));
				JOptionPane.showMessageDialog(null, "Wallpaper available in : " + aa.getTargetFilename(), "Done !", JOptionPane.INFORMATION_MESSAGE);
				
				Supervisor.getInstance().reportTask("Image saved");
				
//				JFrame frame = new JFrame("Result");
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				
//				JLabel label = new JLabel(image);
//				frame.getContentPane().add(label);
//				
//				frame.pack();
//				frame.setLocationRelativeTo(null);
//				frame.setVisible(true);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
