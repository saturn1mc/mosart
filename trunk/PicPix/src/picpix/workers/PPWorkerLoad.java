package picpix.workers;

import java.awt.Point;

public class PPWorkerLoad {
	private String file;
	private Point tile;
	private int[] rgb;

	public PPWorkerLoad(String file, Point tile) {
		super();
		this.file = file;
		this.tile = tile;
		this.rgb = null;
	}

	public PPWorkerLoad(String file, Point tile, int[] rgb) {
		super();
		this.file = file;
		this.tile = tile;
		this.rgb = rgb;
	}

	public String getFile() {
		return file;
	}

	public Point getTile() {
		return tile;
	}

	public int[] getRGB() {
		return rgb;
	}
}
