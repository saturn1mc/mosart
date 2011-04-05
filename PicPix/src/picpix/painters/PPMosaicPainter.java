package picpix.painters;

import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import picpix.gui.PPPreviewFrame;
import picpix.tools.PPException;
import picpix.tools.PPSupervisor;
import picpix.workers.PPMosaicWorker;
import picpix.workers.PPWorkerLoad;

public class PPMosaicPainter extends Thread implements PPPainter {

	private static final int MAX_THREAD = 20;

	private ArrayList<String> selectedFiles;
	private LinkedList<PPWorkerLoad> workLoad;
	private LinkedList<PPMosaicWorker> workers;

	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;

	private int done;

	private String targetFilename;

	public PPMosaicPainter(ArrayList<String> selectedFiles,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws PPException {

		setProperties(selectedFiles, targetFilename, imageWidth, imageHeight,
				mosaicWidth, mosaicHeight);
	}

	public void setProperties(ArrayList<String> selectedFiles,
			String targetFilename, int imageWidth, int imageHeight,
			int mosaicWidth, int mosaicHeight) throws PPException {

		this.selectedFiles = selectedFiles;
		this.workLoad = new LinkedList<PPWorkerLoad>();
		this.workers = new LinkedList<PPMosaicWorker>();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory()
				|| (targetFile.exists() && !targetFile.canWrite())) {
			throw new PPException("Can't write to : '" + targetFilename + "'");
		}

		this.targetFilename = targetFilename;
	}

	public synchronized void drawTile(Image image, int x, int y) {
		PPPreviewFrame.getInstance().drawImage(image, x, y);
		PPSupervisor.getInstance().reportProgress(
				"Drawing tile (" + x + ", " + y + ")",
				((float) done) / ((float) mosaicHeight * (float) mosaicWidth));
		done++;
		notifyAll();
	}

	public synchronized void watchProgress() throws IOException {
		while (!workLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		stopWorkers();

		PPSupervisor.getInstance().reportMainProgress(
				"Saving work to " + targetFilename, 0.66f);

		ImageIO.write(PPPreviewFrame.getInstance().getImage(), "PNG", new File(
				targetFilename));

		PPPreviewFrame.getInstance().diposeG2D();

		PPSupervisor.getInstance().reportMainTaskFinished();
	}

	public synchronized PPWorkerLoad getWork() {
		while (workLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return workLoad.pop();
	}

	public synchronized void putWork(PPWorkerLoad load) {
		workLoad.add(load);
		notifyAll();
	}

	private void launchWorkers(int tileWidth, int tileHeight) {
		for (int i = 0; i < MAX_THREAD; i++) {
			PPMosaicWorker worker = new PPMosaicWorker(this, tileWidth,
					tileHeight);
			workers.add(worker);
			worker.start();
		}
	}

	private synchronized void stopWorkers() {
		PPSupervisor.getInstance().reportTask("Interrupting threads");

		for (PPMosaicWorker worker : workers) {
			worker.kill();
		}

		notifyAll();
	}

	private void createMosaic() throws IOException {

		int tileWidth = (int) (Math.ceil((float) imageWidth
				/ (float) mosaicWidth));
		int tileHeight = (int) (Math.ceil((float) imageHeight
				/ (float) mosaicHeight));
		int tileX = 0;
		int tileY = 0;
		int index = 0;
		done = 0;

		PPPreviewFrame.getInstance().init(imageWidth, imageHeight);
		PPPreviewFrame.getInstance().setVisible(true);

		launchWorkers(tileWidth, tileHeight);

		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				putWork(new PPWorkerLoad(selectedFiles.get(index
						% selectedFiles.size()), new Point(tileX, tileY)));

				index++;
				tileY += tileHeight;

			}

			tileX += tileWidth;
		}

		watchProgress();
	}

	@Override
	public void run() {
		try {
			createMosaic();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
