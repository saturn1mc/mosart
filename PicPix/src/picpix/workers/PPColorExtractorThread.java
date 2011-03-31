package picpix.workers;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import picpix.painters.PPImageRGB;
import picpix.tools.PPColorTools;
import picpix.tools.PPSupervisor;

public class PPColorExtractorThread extends Thread {

	private static final String TEMP_IMG = "MOSART_TEMP" + File.separator
			+ "MOSART_TMP_IMG";

	private PPColorExtractor central;

	private ArrayList<File> files;
	private File tempFile;

	private int targetWidth;
	private int targetHeight;

	public PPColorExtractorThread(PPColorExtractor central, int id,
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

			ArrayList<PPImageRGB> aRGBs = new ArrayList<PPImageRGB>();

			GraphicsEnvironment gEnv = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
			GraphicsConfiguration gConf = gDevice.getDefaultConfiguration();

			for (File file : files) {
				Image scaledImage = extractScaledfileArtwork(file,
						targetWidth, targetHeight);

				BufferedImage artwork = gConf.createCompatibleImage(
						targetWidth, targetHeight);
				Graphics2D g2d = artwork.createGraphics();
				g2d.drawImage(scaledImage, 0, 0, null);

				aRGBs.add(new PPImageRGB(artwork, PPColorTools
						.getAverageRGB(artwork)));
			}

			central.putArtworkRGB(aRGBs);

			tempFile.delete();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
