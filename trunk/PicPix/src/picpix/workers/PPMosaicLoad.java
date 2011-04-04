package picpix.workers;

import java.awt.Point;

public class PPMosaicLoad {
	private String file;
	private Point tile;
	
	public PPMosaicLoad(String file, Point tile) {
		super();
		this.file = file;
		this.tile = tile;
	}
	
	public String getFile() {
		return file;
	}
	
	public Point getTile() {
		return tile;
	}
}
