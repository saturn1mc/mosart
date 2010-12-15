package dependent.workers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;

public class MosArtExtractor extends Thread {

	private final int MAX_THREAD = 50;

	private Stack<Image> scaledImages;
	private Stack<ITTrack> randomList;

	private ArrayList<ITTrack> selectedTracks;
	private int expectedImageCount;
	private int targetWidth;
	private int targetHeight;

	public MosArtExtractor(ArrayList<ITTrack> selectedTracks,
			int expectedImageCount, int targetWidth, int targetHeight) {

		scaledImages = new Stack<Image>();
		randomList = new Stack<ITTrack>();

		this.selectedTracks = selectedTracks;
		this.expectedImageCount = expectedImageCount;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public synchronized void putScaledImage(Image scaledImage) {
		scaledImages.add(scaledImage);
		notifyAll();
	}

	public synchronized Image getScaledImage() {
		while (scaledImages.empty()) {
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

	private int shuffleTracks() {
		int trackCount = selectedTracks.size();

		ArrayList<String> collectedAlbums = new ArrayList<String>();

		if (trackCount > 0) {
			for (ITTrack track : selectedTracks) {

				int t = 0;

				if (track.getArtwork().getCount() > 0) {

					String albumName = track.getAlbum();

					if (!collectedAlbums.contains(albumName)) {
						randomList.add(track);
						collectedAlbums.add(albumName);
					}
				}

				MosArtSupervisor.getInstance().reportProgress("Shuffling tracks...",
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
		int packetSize = Math.max((expectedImageCount / MAX_THREAD), 1)
				+ Math.min((expectedImageCount % MAX_THREAD), 1);
		int threadCount = 0;
		int leftTodo = expectedImageCount;

		while (leftTodo > 0) {

			ArrayList<ITTrack> tracks = new ArrayList<ITTrack>();

			for (int t = 0; t < Math.min(packetSize, leftTodo); t++) {

				if (randomList.empty()) {
					if (shuffleTracks() == 0) {
						return;
					}
				}

				tracks.add(randomList.pop());
			}

			threadCount++;
			new MosArtExtractorThread(this, threadCount, tracks,
					targetWidth, targetHeight).start();

			leftTodo -= packetSize;
		}
	}
}
