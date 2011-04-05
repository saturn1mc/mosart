package picpix.workers;

import picpix.painters.PPPhotoPainter;

public class PPMatchWorker extends Thread {

	private PPPhotoPainter central;

	boolean killed;

	public PPMatchWorker(PPPhotoPainter central) {

		this.central = central;
	}

	public void kill() {
		killed = true;
	}

	@Override
	public void run() {
		while (!killed) {
			PPWorkerLoad load = central.getMatchWork();

			String file = central.getClosestArtworkFor(load.getRGB());

			central.putWork(new PPWorkerLoad(file, load.getTile()));
		}
	}
}
