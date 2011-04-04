package picpix.workers;

import java.util.ArrayList;

import picpix.tools.PPImageRGB;
import picpix.tools.PPSupervisor;



public class PPColorExtractor {

	private static final int MAX_THREAD = 1;

	private ArrayList<PPImageRGB> artworksRGB;

	private ArrayList<String> selectedFiles;
	private int targetWidth;
	private int targetHeight;

	public PPColorExtractor(ArrayList<String> selectedFiles,
			int targetWidth, int targetHeight) {

		artworksRGB = new ArrayList<PPImageRGB>();

		this.selectedFiles = selectedFiles;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public synchronized void putArtworkRGB(ArrayList<PPImageRGB> aRGBs) {
		artworksRGB.addAll(aRGBs);
		
		PPSupervisor.getInstance().reportProgress(
				"Analyzing artwork color",
				(float) artworksRGB.size() / (float) selectedFiles.size());
		
		notifyAll();
	}

	public synchronized ArrayList<PPImageRGB> getArtworksRGB() {
		while (artworksRGB.size() < selectedFiles.size()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return artworksRGB;
	}

	public void launch() {
		int packetSize = (int) Math
				.ceil(((float) selectedFiles.size() / (float) MAX_THREAD));
		int threadCount = 0;
		int done = 0;

		while (done < selectedFiles.size()) {

			ArrayList<String> tracks = new ArrayList<String>();

			tracks.addAll(selectedFiles.subList(done,
					Math.min(done + packetSize, selectedFiles.size())));

			new PPColorWorker(this, threadCount, tracks,
					targetWidth, targetHeight).start();

			threadCount++;
			done += packetSize;
		}
	}
}
