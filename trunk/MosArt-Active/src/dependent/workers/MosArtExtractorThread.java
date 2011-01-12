package dependent.workers;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;

public class MosArtExtractorThread extends Thread {

	private static final String TEMP_IMG = "MOSART_TEMP" + File.separator
			+ "MOSART_TMP_IMG";

	private MosArtExtractor central;
	
	private ArrayList<ITTrack> tracks;
	private File tempFile;

	private int targetWidth;
	private int targetHeight;

	public MosArtExtractorThread(MosArtExtractor central, int id,
			ArrayList<ITTrack> tracks, int targetWidth, int targetHeight) {

		this.central = central;
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

			ArrayList<Image> sImgs = new ArrayList<Image>();

			for (ITTrack track : tracks) {
				Image scaledImage = extractScaledTrackArtwork(track,
						targetWidth, targetHeight);

				sImgs.add(scaledImage);
			}
			
			central.putScaledImages(sImgs);

			tempFile.delete();
		} catch (IOException e) {
			MosArtSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
