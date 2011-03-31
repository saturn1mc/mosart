package picpix.painters;

import java.awt.image.BufferedImage;

public class PPImageRGB {
	private BufferedImage artwork;
	private int[] rgb;

	public PPImageRGB(BufferedImage artwork, int[] rgb) {
		super();
		this.artwork = artwork;
		this.rgb = rgb;
	}

	public BufferedImage getArtwork() {
		return artwork;
	}

	public int[] getRGB() {
		return rgb;
	}
}
