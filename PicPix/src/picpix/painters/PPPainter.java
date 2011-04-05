package picpix.painters;

import java.awt.Image;

import picpix.workers.PPWorkerLoad;

public abstract interface PPPainter {
	public PPWorkerLoad getWork();
	public void drawTile(Image image, int x, int y);
}
