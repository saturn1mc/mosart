package itl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Adapted from Travis example :
 * "Parsing the iTunes Library using a SAX parser (in Java)"
 * http://trav.is/thoughts/2010/03/30/parsing-itunes-library-using-sax-parser-java/
 * @author cmaurice2
 *
 */
public class ITLParser extends DefaultHandler {

	private static final String LIBRARY_FILE_PATH = "D:\\Mes Documents\\My Music\\iTunes\\iTunes Music Library.xml";
	private static final String KEY = "key";
	private static final String DICT = "dict";
	private static final String STR = "string";
	private static final String INTG = "integer";
	private static final String NAME = "Name";
	private static final String ARTIST = "Artist";
	private static final String ALBUM = "Album";
	private static final String PLAY_COUNT = "Play Count";
	private static final String PLAYLISTS = "Playlists";
	private static final String TRACKS = "Tracks";
	private static final String LOCATION = "Location";

	private List<ITLSong> tracks;
	private String stamp;

	private ITLSong song;

	boolean tracksFound = false;
	private String previousTag;
	private String previousTagVal;

	public ITLParser() {
		tracks = new ArrayList<ITLSong>();
	}

	public void runExample() {
		parseDocument();
		printData();
	}

	private void parseDocument() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(LIBRARY_FILE_PATH, this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void printData() {
		System.out.println("No of Tracks '" + tracks.size() + "'.");
		Iterator<ITLSong> it = tracks.iterator();
		while (it.hasNext()) {
			ITLSong song = it.next();
			System.out.println(song.getAlbum() + " - " + song.getName() + "-" + song.getLocation());
		}
	}

	// Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		stamp = "";

		if (tracksFound) {
			if (KEY.equals(previousTag) && DICT.equalsIgnoreCase(qName)) {
				// create a new instance of employee
				song = new ITLSong();
				tracks.add(song);
			}
		} else {
			if (KEY.equals(previousTag)
					&& TRACKS.equalsIgnoreCase(previousTagVal)
					&& DICT.equalsIgnoreCase(qName)) {
				tracksFound = true; // We are now inside the Tracks dict.
			}
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		stamp = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (tracksFound) {
			if (previousTagVal.equalsIgnoreCase(NAME) && qName.equals(STR)) {
				song.setName(stamp);
			} else if (previousTagVal.equalsIgnoreCase(ARTIST)
					&& qName.equals(STR)) {
				song.setArtist(stamp);
			} else if (previousTagVal.equalsIgnoreCase(ALBUM)
					&& qName.equals(STR)) {
				song.setAlbum(stamp);
			} else if (previousTagVal.equalsIgnoreCase(PLAY_COUNT)
					&& qName.equals(INTG)) {
				Integer value = Integer.parseInt(stamp);
				song.setPlayCount(value.intValue());
			}else if (previousTagVal.equalsIgnoreCase(LOCATION)
					&& qName.equals(STR)) {
				song.setLocation(stamp);
			}
			
			
			// Mark when we come to the end of the tracks dict.
			if (KEY.equals(qName) && PLAYLISTS.equalsIgnoreCase(stamp)) {
				tracksFound = false;
			}
		}

		// Keep track of the previous tag so we can track the context when we're
		// at the second tag in a key, value pair.
		previousTagVal = stamp;
		previousTag = qName;
	}

	public static void main(String[] args) {
		ITLParser spe = new ITLParser();
		spe.runExample();
	}

}
