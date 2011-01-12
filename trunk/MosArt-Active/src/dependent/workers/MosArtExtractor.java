package dependent.workers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;

public class MosArtExtractor extends Thread {

	private static final int MAX_THREAD = 100;

	private LinkedList<Image> scaledImages;
	private LinkedList<ITTrack> randomList;

	private ArrayList<ITTrack> selectedTracks;
	private int expectedImageCount;
	private int targetWidth;
	private int targetHeight;

	public MosArtExtractor(ArrayList<ITTrack> selectedTracks,
			int expectedImageCount, int targetWidth, int targetHeight) {

		scaledImages = new LinkedList<Image>();
		randomList = new LinkedList<ITTrack>();

		this.selectedTracks = selectedTracks;
		this.expectedImageCount = expectedImageCount;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public synchronized void putScaledImage(Image scaledImage) {
		scaledImages.add(scaledImage);
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

		notifyAll();

		return scaledImage;
	}

	public synchronized Image getScaledImage(int index) {
		if (index >= 0) {

			if (index >= scaledImages.size()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Image scaledImage = scaledImages.get(index);

			notifyAll();

			return scaledImage;
		} else {
			return null;
		}
	}

	private int shuffleTracks() {
		int trackCount = selectedTracks.size();

		if (trackCount > 0) {
			for (ITTrack track : selectedTracks) {

				int t = 0;

				if (track.getArtwork().getCount() > 0) {
					randomList.add(track);
				}

				MosArtSupervisor.getInstance().reportProgress(
						"Shuffling tracks...",
						((float) (t + 1) / (float) trackCount));
			}

			Collections.shuffle(randomList);
		} else {
			MosArtSupervisor.getInstance().reportCrash("No tracks selected");
		}

		return randomList.size();
	}

	@Override
	public void run() {
		int packetSize = (int)Math.ceil(((float)expectedImageCount / (float)MAX_THREAD));
		int threadCount = 0;
		int leftTodo = expectedImageCount;

		while (leftTodo > 0) {

			ArrayList<ITTrack> tracks = new ArrayList<ITTrack>();

			for (int t = 0; t < Math.min(packetSize, leftTodo); t++) {

				if (randomList.isEmpty()) {
					if (shuffleTracks() == 0) {
						return;
					}
				}

				tracks.add(randomList.pop());
			}

			threadCount++;
			new MosArtExtractorThread(this, threadCount, tracks, targetWidth,
					targetHeight).start();

			leftTodo -= packetSize;
		}
	}
}
