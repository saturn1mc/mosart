package painters;

import gui.Supervisor;
import itc.ITCArtwork;
import itc.ITCParser;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import javax.imageio.ImageIO;

public class MosaicPainter {

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private Collection<ITCArtwork> artworks;

	@SuppressWarnings("unused")
	private MosaicPainter() {
		this(0, 0, 0, 0, null);
	}

	public MosaicPainter(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight, ArrayList<ITCArtwork> artworks) {
		super();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		this.artworks = artworks;
	}

	public void setProperties(int imageWidth, int imageHeight, int mosaicWidth,
			int mosaicHeight, ArrayList<ITCArtwork> artworks) {

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		this.artworks = artworks;
	}
	
	public void setArtworkList(Collection<ITCArtwork> artworks){
		this.artworks = artworks;
	}

	private Image handleArtwork(ITCArtwork artwork, int targetWidth, int targetHeight)
			throws IOException {

		if(!artwork.isFullyParsed()){
			Supervisor.getInstance().reportTask("Completing : " + artwork.getSource());
			ITCParser.getInstance().completeArtwork(artwork);
		}

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(artwork.getImageData()));

		//artwork.clearImageData();
		
		return image.getScaledInstance(targetWidth, targetHeight,
				Image.SCALE_SMOOTH);
	}

	public BufferedImage createMosaic() throws IOException {
		
		int tileWidth = imageWidth / mosaicWidth;
		int tileHeight = imageHeight / mosaicHeight;
		int tileX = 0;
		int tileY = 0;
		int done = 1;

		BufferedImage mosaic = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		Stack<ITCArtwork> randomList = new Stack<ITCArtwork>();

		for (int i = 0; i < mosaicWidth; i++) {
			
			tileY = 0;
			
			for (int j = 0; j < mosaicHeight; j++) {

				if(randomList.isEmpty()){
					randomList.addAll(artworks);
					Collections.shuffle(randomList);
				}
				
				Image image = handleArtwork(randomList.pop(), tileWidth,
						tileHeight);
				
				mosaic.getGraphics().drawImage(image, tileX, tileY,
						null);

				float progress = ((float) done)
						/ ((float) mosaicHeight * (float) mosaicWidth);
				
				Supervisor.getInstance().reportProgress(
						"Adding tile to (" + tileX + "," + tileY + ")",
						progress);

				tileY += tileHeight;
				done++;
			}
			
			tileX += tileWidth;
		}

		return mosaic;
	}
}
