package dependent;

import java.awt.image.BufferedImage;

public class MosArtColorTools {
	private static int[] getRGB(BufferedImage image, int x, int y) {

		int rgb = image.getRGB(x, y);
		int r = (rgb & 0x00ff0000) >> 16;
		int g = (rgb & 0x0000ff00) >> 8;
		int b = (rgb & 0x000000ff);

		return new int[] { r, g, b };
	}

	private static void addTo(int[] target, int[] source) {
		for (int i = 0; i < target.length; i++) {
			target[i] += source[i];
		}
	}

	private static void scale(int[] target, float coeff) {
		for (int i = 0; i < target.length; i++) {
			target[i] = (int) (coeff * (float) target[i]);
		}
	}

	public static int[] getAverageRGB(BufferedImage image, int x, int y, int squareW,
			int squareH) {
		int[] rgb = new int[] { 0, 0, 0 };

		int xStop = Math.min(x + squareW, image.getWidth());
		int yStop = Math.min(y + squareH, image.getHeight());

		for (int i = x; i < xStop; i++) {
			for (int j = y; j < yStop; j++) {
				addTo(rgb, getRGB(image, i, j));
			}
		}

		scale(rgb, (1.0f / Math.max(((xStop - x) * (yStop - y)), 1)));

		return rgb;
	}

	public static int[] getAverageRGB(BufferedImage image) {
		return getAverageRGB(image, 0, 0, image.getWidth(), image.getHeight());
	}
	
	public static double distance(int[] rgb1, int[] rgb2) {
		double dist2 = Math.pow(rgb1[0] - rgb2[0], 2)
				+ Math.pow(rgb1[1] - rgb2[1], 2)
				+ Math.pow(rgb1[2] - rgb2[2], 2);
		return Math.pow(dist2, 1.0d / 2.0d);
	}
}
