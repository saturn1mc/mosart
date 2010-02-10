package gui;

import itc.ITCBaseReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
		painter = new MosaicPainter();
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
	
	public File chooseAlbumArtDirectory() {
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
			int mosaicHeight) {
		
		painter = new MosaicPainter(imageWidth, imageHeight, mosaicWidth, mosaicHeight);
		
	}
	
	public void chooseTargetFile(String targetFilename){
		this.targetFilename = targetFilename;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MosArt aa = new MosArt();

		aa.setMosaicProperties(2650, 1024, 20, 20);
		aa.chooseTargetFile("D:\\Mes Documents\\test.png");
		
		File dir = aa.chooseAlbumArtDirectory();

		if (dir != null) {
			try {
				ImageIcon image = aa.getPainter().createMosaic(aa.getBaseReader().getITCs(dir));
				
				ImageIO.write((BufferedImage)image.getImage(), "PNG", new File(aa.getTargetFilename()));
				JOptionPane.showMessageDialog(null, "Wallpaper available in : " + aa.getTargetFilename(), "Done !", JOptionPane.INFORMATION_MESSAGE);
				
//				JFrame frame = new JFrame("Result");
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				
//				JLabel label = new JLabel(image);
//				frame.getContentPane().add(label);
//				
//				frame.pack();
//				frame.setLocationRelativeTo(null);
//				frame.setVisible(true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
