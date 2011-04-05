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

	public PPWorkerLoad(String file, int[] rgb) {
		super();
		this.file = file;
		this.tile = null;
		this.rgb = rgb;
	}
	
	public PPWorkerLoad(Point tile, int[] rgb) {
		super();
		this.file = null;
		this.tile = tile;
		this.rgb = rgb;
	}

	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}

	public Point getTile() {
		return tile;
	}
	
	public void setTile(Point tile) {
		this.tile = tile;
	}

	public int[] getRGB() {
		return rgb;
	}
	
	public void setRGB(int[] rgb) {
		this.rgb = rgb;
	}
}
