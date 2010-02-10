package writers;

import gui.Supervisor;
import itc.ITCParser;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class MosaicPainter {

	private ITCParser parser;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private ImageIcon mosaic;

	public MosaicPainter() {
		this(0, 0, 0, 0);
	}

	public MosaicPainter(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight) {
		super();
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		this.parser = new ITCParser();
	}

	private Image handleITC(String filename, int targetWidth, int targetHeight)
			throws IOException {

		parser.parse(new File(filename));

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(parser
				.getImageData()));

		return image.getScaledInstance(targetWidth, targetHeight,
				Image.SCALE_SMOOTH);
	}

	public ImageIcon createMosaic(ArrayList<String> itcList)
			throws IOException {
		
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 1;
		
		Random random = new Random();
		
		mosaic = new ImageIcon(new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB));
		
		ArrayList<String> randomList = new ArrayList<String>(itcList);
		
		for(int i=0; i<mosaicWidth; i++){
			for(int j=0; j<mosaicHeight; j++){
				int randomIndex = random.nextInt(randomList.size());
				
				Image image = handleITC(randomList.get(randomIndex), tileWidth, tileHeight);
				mosaic.getImage().getGraphics().drawImage(image, tileX, tileY, mosaic.getImageObserver());
				
				float progress = ((float)done)/((float)mosaicHeight*(float)mosaicWidth);
				Supervisor.getInstance().reportProgress("Adding tile to (" + tileX + "," + tileY + ")", progress);
				
				randomList.remove(randomIndex);
				
				tileY += tileHeight;
				done++;
			}
			
			tileY = 0;
			tileX += tileWidth;
		}
		
		return mosaic;
	}
}
