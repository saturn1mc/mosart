package dependent.painters;

import java.awt.image.BufferedImage;

public class MosArtArtworkRGB {
	private BufferedImage artwork;
	private int[] rgb;

	public MosArtArtworkRGB(BufferedImage artwork, int[] rgb) {
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
