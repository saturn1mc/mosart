package dependent.workers;

import java.util.ArrayList;

import dependent.MosArtSupervisor;
import dependent.com.dt.iTunesController.ITTrack;
import dependent.painters.MosArtArtworkRGB;

public class MosArtColorExtractor {

	private static final int MAX_THREAD = 1000;

	private ArrayList<MosArtArtworkRGB> artworksRGB;

	private ArrayList<ITTrack> selectedTracks;
	private int targetWidth;
	private int targetHeight;

	public MosArtColorExtractor(ArrayList<ITTrack> selectedTracks,
			int targetWidth, int targetHeight) {

		artworksRGB = new ArrayList<MosArtArtworkRGB>();

		this.selectedTracks = selectedTracks;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public synchronized void putArtworkRGB(ArrayList<MosArtArtworkRGB> aRGBs) {
		artworksRGB.addAll(aRGBs);
		
		MosArtSupervisor.getInstance().reportProgress(
				"Analyzing artwork color",
				(float) artworksRGB.size() / (float) selectedTracks.size());
		
		notifyAll();
	}

	public synchronized ArrayList<MosArtArtworkRGB> getArtworksRGB() {
		while (artworksRGB.size() < selectedTracks.size()) {
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
				.ceil(((float) selectedTracks.size() / (float) MAX_THREAD));
		int threadCount = 0;
		int done = 0;

		while (done < selectedTracks.size()) {

			ArrayList<ITTrack> tracks = new ArrayList<ITTrack>();

			tracks.addAll(selectedTracks.subList(done,
					Math.min(done + packetSize, selectedTracks.size())));

			new MosArtColorExtractorThread(this, threadCount, tracks,
					targetWidth, targetHeight).start();

			threadCount++;
			done += packetSize;
		}
	}
}
