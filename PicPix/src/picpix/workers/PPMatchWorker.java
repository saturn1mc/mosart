package picpix.workers;

import picpix.painters.PPPhotoPainter;
import picpix.tools.PPSupervisor;

public class PPMatchWorker extends Thread {

	private PPPhotoPainter central;

	boolean killed;

	public PPMatchWorker(PPPhotoPainter central) {
		this.central = central;
		this.killed = false;
	}

	public void kill() {
		killed = true;
	}

	@Override
	public void run() {
		while (!killed) {
			PPWorkerLoad load = central.getMatchWork();
			String file = central.getClosestArtworkFor(load.getRGB());

			PPSupervisor.getInstance().reportProgress(
					"Matching tile (" + load.getTile().x + ", "
							+ load.getTile().y + ")");

			central.putWork(new PPWorkerLoad(file, load.getTile()));
		}
	}
}
