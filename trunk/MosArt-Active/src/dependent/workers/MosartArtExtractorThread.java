package dependent.workers;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dependent.com.dt.iTunesController.ITTrack;
import dependent.gui.Supervisor;

public class MosartArtExtractorThread extends Thread {

	private static final String TEMP_IMG = "MOSART_TEMP" + File.separator
			+ "MOSART_TMP_IMG";

	private int id;
	private MosartArtExtractor central;
	
	private ArrayList<ITTrack> tracks;
	private File tempFile;

	private int targetWidth;
	private int targetHeight;

	public MosartArtExtractorThread(MosartArtExtractor central, int id, ArrayList<ITTrack> tracks,
			int targetWidth, int targetHeight) {
		
		this.central = central;
		this.id = id;
		this.tracks = tracks;

		tempFile = new File(TEMP_IMG + "_" + id);
		tempFile.getParentFile().mkdirs();

		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	private Image extractScaledTrackArtwork(ITTrack track, int targetWidth,
			int targetHeight) throws IOException {

		Image artwork = null;
		int artCount = track.getArtwork().getCount();

		if (artCount != 0) {
			track.getArtwork().getItem(artCount)
					.SaveArtworkToFile(tempFile.getAbsolutePath());
			BufferedImage image = ImageIO.read(tempFile);
			artwork = image.getScaledInstance(targetWidth, targetHeight,
					Image.SCALE_SMOOTH);
		}

		return artwork;
	}

	@Override
	public void run() {
		try {
			for (ITTrack track : tracks) {
				Image scaledImage = extractScaledTrackArtwork(track,
						targetWidth, targetHeight);
				//TODO
				
			}
		} catch (IOException e) {
			Supervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
