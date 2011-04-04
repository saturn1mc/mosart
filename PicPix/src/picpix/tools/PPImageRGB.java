package picpix.tools;

import java.awt.image.BufferedImage;

public class PPImageRGB {
	private BufferedImage image;
	private int[] rgb;

	public PPImageRGB(BufferedImage artwork, int[] rgb) {
		super();
		this.image = artwork;
		this.rgb = rgb;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int[] getRGB() {
		return rgb;
	}
}
