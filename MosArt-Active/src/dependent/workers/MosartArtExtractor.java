package dependent.workers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import dependent.com.dt.iTunesController.ITTrack;
import dependent.com.dt.iTunesController.iTunes;
import dependent.gui.Supervisor;

public class MosartArtExtractor extends Thread {

	private final int MAX_THREAD = 100;

	private Stack<Image> scaledImages;
	private Stack<ITTrack> randomList;

	private iTunes itunes;
	private int expectedImageCount;
	private int targetWidth;
	private int targetHeight;

	public MosartArtExtractor(iTunes itunes, int expectedImageCount,
			int targetWidth, int targetHeight) {

		scaledImages = new Stack<Image>();
		randomList = new Stack<ITTrack>();

		this.itunes = itunes;
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

	private void gatherTracks() {
		int trackCount = itunes.getLibraryPlaylist().getTracks().getCount();

		ArrayList<String> collectedAlbums = new ArrayList<String>();

		if (trackCount > 0) {
			for (int t = 0; t < trackCount; t++) {

				ITTrack track = itunes.getLibraryPlaylist().getTracks()
						.getItem(t + 1);

				if (track.getArtwork().getCount() > 0) {

					String albumName = track.getAlbum();

					if (!collectedAlbums.contains(albumName)) {
						randomList.add(track);
						collectedAlbums.add(albumName);
					}
				}

				Supervisor.getInstance().reportProgress("Gathering tracks...",
						((float) (t + 1) / (float) trackCount));
			}

			Collections.shuffle(randomList);
		} else {
			Supervisor.getInstance().reportCrash("No tracks in iTunes");
		}
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
					gatherTracks();
				}

				tracks.add(randomList.pop());
			}

			threadCount++;
			new MosartArtExtractorThread(this, threadCount, tracks,
					targetWidth, targetHeight).start();

			leftTodo -= packetSize;
		}
	}
}
