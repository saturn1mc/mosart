package picpix.workers;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import picpix.tools.PPSupervisor;

public class PPExtractorThread extends Thread {

	private static final String TEMP_IMG = "MOSART_TEMP" + File.separator
			+ "MOSART_TMP_IMG";

	private PPExtractor central;

	private ArrayList<File> files;
	private File tempFile;

	private int targetWidth;
	private int targetHeight;

	public PPExtractorThread(PPExtractor central, int id,
			ArrayList<File> files, int targetWidth, int targetHeight) {

		this.central = central;
		this.files = files;

		tempFile = new File(TEMP_IMG + "_" + id);
		tempFile.getParentFile().mkdirs();

		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	private Image extractScaledfileArtwork(File file, int targetWidth,
			int targetHeight) throws IOException {

		BufferedImage image = ImageIO.read(file);

		return image.getScaledInstance(targetWidth, targetHeight,
				Image.SCALE_SMOOTH);
	}

	@Override
	public void run() {
		try {

			ArrayList<Image> sImgs = new ArrayList<Image>();

			for (File file : files) {
				Image scaledImage = extractScaledfileArtwork(file, targetWidth,
						targetHeight);

				sImgs.add(scaledImage);
			}

			central.putScaledImages(sImgs);

			tempFile.delete();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
