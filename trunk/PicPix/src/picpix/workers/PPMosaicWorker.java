package picpix.workers;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import picpix.painters.PPPainter;
import picpix.tools.PPSupervisor;

public class PPMosaicWorker extends Thread {

	private PPPainter central;

	boolean killed;
	
	private int targetWidth;
	private int targetHeight;


	public PPMosaicWorker(PPPainter central, int targetWidth,
			int targetHeight) {

		this.central = central;

		this.killed = false;
		
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}
	
	public void kill(){
		killed = true;
	}

	@Override
	public void run() {
		try {
			while (!killed) {
				PPWorkerLoad load = central.getWork();
				BufferedImage image = ImageIO.read(new File(load.getFile()));

				
				central.drawTile(image.getScaledInstance(targetWidth, targetHeight,
								Image.SCALE_FAST), load.getTile().x,
						load.getTile().y);

				image.flush();
			}
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
