/**
 * 
 */
package picpix.painters;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import picpix.gui.PPPreviewFrame;
import picpix.tools.PPColorTools;
import picpix.tools.PPException;
import picpix.tools.PPSupervisor;
import picpix.workers.PPColorWorker;
import picpix.workers.PPMatchWorker;
import picpix.workers.PPMosaicWorker;
import picpix.workers.PPWorkerLoad;



/**
 * @author cmaurice2
 * 
 */
public class PPPhotoPainter extends Thread implements PPPainter{

	private static final int MAX_THREAD = 2;
	
	private BufferedImage source;
	private ArrayList<String> selectedFiles;

	private LinkedList<String> analysisLoad;
	private LinkedList<PPWorkerLoad> analysisResults;
	private LinkedList<PPWorkerLoad> matchLoad;
	private LinkedList<PPWorkerLoad> workLoad;
	
	private LinkedList<PPColorWorker> colorWorkers;
	private LinkedList<PPMatchWorker> matchWorkers;
	private LinkedList<PPMosaicWorker> mosaicWorkers;
	
	private int imageWidth;
	private int imageHeight;

	private int mosaicWidth;
	private int mosaicHeight;
	
	private int done;
	private int analyzed;
	
	private boolean analysisFinished;
	private boolean matchFinished;

	private String targetFilename;

	public PPPhotoPainter(BufferedImage source,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		setProperties(source, selectedFiles, targetFilename, imageWidth,
				imageHeight, mosaicWidth, mosaicHeight);
	}

	public void setProperties(BufferedImage source,
			ArrayList<String> selectedFiles, String targetFilename,
			int imageWidth, int imageHeight, int mosaicWidth, int mosaicHeight)
			throws PPException {

		this.selectedFiles = selectedFiles;
		this.source = source;
		
		this.analysisLoad = new LinkedList<String>();
		
		this.analysisResults = new LinkedList<PPWorkerLoad>();
		this.matchLoad = new LinkedList<PPWorkerLoad>();
		this.workLoad = new LinkedList<PPWorkerLoad>();
		
		this.colorWorkers = new LinkedList<PPColorWorker>();
		this.matchWorkers = new LinkedList<PPMatchWorker>();
		this.mosaicWorkers = new LinkedList<PPMosaicWorker>();

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mosaicWidth = mosaicWidth;
		this.mosaicHeight = mosaicHeight;

		File targetFile = new File(targetFilename);

		if (targetFile.isDirectory()
				|| (targetFile.exists() && !targetFile.canWrite())) {
			throw new PPException("Can't write to : '" + targetFilename
					+ "'");
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
		while (!analysisFinished || !matchFinished || !workLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		stopMosaicWorkers();
		
		PPSupervisor.getInstance().reportMainProgress(
				"Saving work to " + targetFilename, 0.66f);
		
		ImageIO.write(PPPreviewFrame.getInstance().getImage(), "PNG",
				new File(targetFilename));

		PPPreviewFrame.getInstance().diposeG2D();

		PPSupervisor.getInstance().reportMainTaskFinished();
	}
	
	public synchronized void watchMatchProgress() throws IOException {
		while (!analysisFinished || !matchLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		matchFinished = true;
		notifyAll();
		
		stopMatchWorkers();
	}
	
	public synchronized void watchAnalysisProgress() {
		while (analyzed < selectedFiles.size()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		analysisFinished = true;
		notifyAll();
		
		stopColorWorkers();
	}

	public synchronized PPWorkerLoad getWork() {
		while (!analysisFinished || workLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return workLoad.pop();
	}
	
	public synchronized PPWorkerLoad getMatchWork() {
		while (!analysisFinished || matchLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		notifyAll();
		
		return matchLoad.pop();
	}
	
	public synchronized String getAnalysisWork() {
		while (analysisLoad.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return analysisLoad.pop();
	}

	public synchronized void putWork(PPWorkerLoad load) {
		workLoad.add(load);
		notifyAll();
	}
	
	public synchronized void putAnalysis(PPWorkerLoad load) {
		analysisResults.add(load);
		analyzed++;
		
		notifyAll();
		
		PPSupervisor.getInstance().reportProgress("Analysing color schemes", ((float)analyzed/(float)selectedFiles.size()));
	}
	
	private synchronized void launchColorWorkers() {
		analysisFinished = false;
		
		for (int i = 0; i < MAX_THREAD; i++) {
			PPColorWorker worker = new PPColorWorker(this);
			colorWorkers.add(worker);
			worker.start();
		}
	}
	
	private synchronized void launchMatchWorkers() {
		
		matchFinished = false;
		
		for (int i = 0; i < MAX_THREAD; i++) {
			PPMatchWorker worker = new PPMatchWorker(this);
			matchWorkers.add(worker);
			worker.start();
		}
	}
	
	private synchronized void launchMosaicWorkers(int tileWidth, int tileHeight) {
		for (int i = 0; i < MAX_THREAD; i++) {
			PPMosaicWorker worker = new PPMosaicWorker(this, tileWidth,
					tileHeight);
			mosaicWorkers.add(worker);
			worker.start();
		}
	}
	
	private synchronized void stopColorWorkers() {
		PPSupervisor.getInstance().reportTask("Interrupting color threads");

		for (PPColorWorker worker : colorWorkers) {
			worker.kill();
		}

		notifyAll();
	}
	
	private synchronized void stopMatchWorkers() {
		PPSupervisor.getInstance().reportTask("Interrupting match threads");

		for (PPMatchWorker worker : matchWorkers) {
			worker.kill();
		}

		notifyAll();
	}
	
	private synchronized void stopMosaicWorkers() {
		PPSupervisor.getInstance().reportTask("Interrupting mosaic threads");

		for (PPMosaicWorker worker : mosaicWorkers) {
			worker.kill();
		}

		notifyAll();
	}
	
	public String getClosestArtworkFor(int[] RGB) {

		double minDelta = Double.POSITIVE_INFINITY;
		String closest = null;

		for (PPWorkerLoad candidate : analysisResults) {
			double dist = PPColorTools.distance(candidate.getRGB(), RGB);

			if (dist < minDelta) {
				closest = candidate.getFile();
				minDelta = dist;
			}
		}

		return closest;
	}

	public void paintPhoto() throws IOException {
		float wRatio = (float) source.getWidth() / (float) imageWidth;
		float hRatio = (float) source.getHeight() / (float) imageHeight;
		int tileWidth = (int) (Math.ceil((float) imageWidth
				/ (float) mosaicWidth));
		int tileHeight = (int) (Math.ceil((float) imageHeight
				/ (float) mosaicHeight));
		int tileX = 0;
		int tileY = 0;
		
		done = 0;
		analysisLoad.addAll(selectedFiles);
		
		PPPreviewFrame.getInstance().init(imageWidth, imageHeight);
		PPPreviewFrame.getInstance().setVisible(true);
		PPPreviewFrame.getInstance().drawPreview(
				source.getScaledInstance(imageWidth, imageHeight,
						Image.SCALE_FAST), 0, 0);
		
		for (int i = 0; i < mosaicWidth; i++) {

			tileY = 0;

			for (int j = 0; j < mosaicHeight; j++) {

				int propW = (int) ((float) tileWidth * wRatio);
				int propH = (int) ((float) tileHeight * hRatio);
				int propX = (int) ((float) i * (float) propW);
				int propY = (int) ((float) j * (float) propH);

				PPPreviewFrame.getInstance().targetPreview(tileX, tileY,
						tileWidth, tileHeight);

				PPSupervisor.getInstance().reportProgress("Splitting source", (float)((i*j)+1)/((float) (mosaicWidth*mosaicHeight)));
				matchLoad.add(new PPWorkerLoad(new Point(tileX, tileY), PPColorTools.getAverageRGB(source, propX, propY, propW, propH)));
				
				tileY += tileHeight;
			}

			tileX += tileWidth;
		}
		
		launchColorWorkers();
		watchAnalysisProgress();
		
		launchMatchWorkers();
		launchMosaicWorkers(tileWidth, tileHeight);
		
		watchMatchProgress();
		watchProgress();
	}

	@Override
	public void run() {
		try {
			paintPhoto();
		} catch (IOException e) {
			PPSupervisor.getInstance().reportCrash(e.getMessage());
		}
	}
}
