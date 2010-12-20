package dependent.painters;

import java.awt.image.BufferedImage;

public class MosArtArtworkDistance {
	private BufferedImage artwork;
	private double distance;

	public MosArtArtworkDistance(BufferedImage artwork, double distance) {
		super();
		this.artwork = artwork;
		this.distance = distance;
	}

	public BufferedImage getArtwork() {
		return artwork;
	}

	public double getDistance() {
		return distance;
	}
}
