package painters;

import gui.Supervisor;
import itc.ITCParser;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

public class MosaicPainter extends SwingWorker<ImageIcon, String> {

	private ITCParser parser;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private ArrayList<String> imageList;

	private ImageIcon mosaic;

	@SuppressWarnings("unused")
	private MosaicPainter() {
		this(0, 0, 0, 0, null);
	}

	public MosaicPainter(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight, ArrayList<String> imageList) {
		super();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		this.imageList = imageList;

		this.parser = new ITCParser();
	}

	public void setProperties(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight, ArrayList<String> imageList) {

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		this.imageList = imageList;
	}
	
	public void setITCList(ArrayList<String> imageList){
		this.imageList = imageList;
	}

	private Image handleITC(String filename, int targetWidth, int targetHeight)
			throws IOException {

		parser.parse(new File(filename));

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(parser
				.getImageData()));

		return image.getScaledInstance(targetWidth, targetHeight,
				Image.SCALE_SMOOTH);
	}

	public ImageIcon createMosaic() throws IOException {
		
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 1;
		int index = 0;

		mosaic = new ImageIcon(new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB));

		ArrayList<String> randomList = new ArrayList<String>(imageList);
		Collections.shuffle(randomList);

		for (int i = 0; i < mosaicWidth; i++) {
			
			tileY = 0;
			
			for (int j = 0; j < mosaicHeight; j++) {

				Image image = handleITC(randomList.get(index), tileWidth,
						tileHeight);
				
				mosaic.getImage().getGraphics().drawImage(image, tileX, tileY,
						mosaic.getImageObserver());

				float progress = ((float) done)
						/ ((float) mosaicHeight * (float) mosaicWidth);
				
				Supervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);

				index = ((index+1)%randomList.size());
				tileY += tileHeight;
				done++;
			}
			
			tileX += tileWidth;
		}

		return mosaic;
	}

	@Override
	protected ImageIcon doInBackground() throws Exception {
		return createMosaic();
	}
}
