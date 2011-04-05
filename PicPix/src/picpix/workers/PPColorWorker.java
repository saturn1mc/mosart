package picpix.workers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import picpix.painters.PPPhotoPainter;
import picpix.tools.PPColorTools;
import picpix.tools.PPSupervisor;

public class PPColorWorker extends Thread {

	private PPPhotoPainter central;

	boolean killed;

	public PPColorWorker(PPPhotoPainter central) {
		this.central = central;
		this.killed = false;
	}

	public void kill() {
		killed = true;
	}

	@Override
	public void run() {
		try {
			while (!killed) {
				String file = central.getAnalysisWork();
				BufferedImage image = ImageIO.read(new File(file));

				central.putAnalysis(new PPWorkerLoad(file, PPColorTools
						.getAverageRGB(image)));

				image.flush();
			}
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
