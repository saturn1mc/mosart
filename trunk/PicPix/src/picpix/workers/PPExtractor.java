package picpix.workers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import picpix.tools.PPSupervisor;



public class PPExtractor {

	private static final int MAX_THREAD = 100;

	private LinkedList<Image> scaledImages;
	private LinkedList<String> randomList;

	private ArrayList<String> selectedfiles;
	private int expectedImageCount;
	private int targetWidth;
	private int targetHeight;

	public PPExtractor(ArrayList<String> selectedfiles,
			int expectedImageCount, int targetWidth, int targetHeight) {

		scaledImages = new LinkedList<Image>();
		randomList = new LinkedList<String>();

		this.selectedfiles = selectedfiles;
		this.expectedImageCount = expectedImageCount;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public synchronized void putScaledImages(ArrayList<Image> sImgs) {
		scaledImages.addAll(sImgs);
		notifyAll();
	}

	public synchronized Image popScaledImage() {
		while (scaledImages.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Image scaledImage = scaledImages.pop();

		return scaledImage;
	}

	private int shuffleFiles() {
		int fileCount = selectedfiles.size();

		if (fileCount > 0) {
			for (String file : selectedfiles) {

				int t = 0;

				randomList.add(file);

				PPSupervisor.getInstance().reportProgress(
						"Shuffling files...",
						((float) (t + 1) / (float) fileCount));
			}

			Collections.shuffle(randomList);
		} else {
			PPSupervisor.getInstance().reportCrash("No files selected");
		}

		return randomList.size();
	}

	public void launch() {
		int packetSize = (int) Math
				.ceil(((float) expectedImageCount / (float) MAX_THREAD));
		int threadCount = 0;
		int leftTodo = expectedImageCount;

		while (leftTodo > 0) {

			ArrayList<String> files = new ArrayList<String>();

			for (int t = 0; t < Math.min(packetSize, leftTodo); t++) {

				if (randomList.isEmpty()) {
					if (shuffleFiles() == 0) {
						return;
					}
				}

				files.add(randomList.pop());
			}

			threadCount++;
			new PPExtractorThread(this, threadCount, files, targetWidth,
					targetHeight).start();

			leftTodo -= packetSize;
		}
	}
}
