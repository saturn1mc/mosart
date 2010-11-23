package itl;

import gui.Supervisor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class ITLParser {

	// States
	private static final int STATE_INITIAL = 0;
	private static final int STATE_HEADER = 1;
	private static final int STATE_LIBRARY = 2;
	private static final int STATE_TRACK = 3;
	private static final int STATE_FIELD_CONTENT = 4;
	private static final int STATE_IGNORE = 5;

	private static final char MARK_START = '<';
	private static final char MARK_STOP = '>';
	private static final char CLOSE_SIGN = '/';

	private static final String DICT = "dict";
	private static final String KEY = "key";
	private static final String INTG = "integer";
	private static final String STRG = "string";
	private static final String DATE = "date";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private BufferedReader reader;
	private char[] currentChar;
	private String currentField;
	private ITLSong currentSong;

	private int state;

	private static ITLParser singleton;

	private ITLParser() {
		// Nothing
		super();
	}

	public static ITLParser getInstance() {
		if (singleton == null) {
			singleton = new ITLParser();
		}

		return singleton;
	}

	public void parseITL(String iTunesLibrary, ITLCollection collection) {
		state = STATE_INITIAL;
		currentChar = new char[1];

		try {
			reader = new BufferedReader(new FileReader(iTunesLibrary));
			String tag = nextTag();

			while (tag != null) {

				switch (state) {
				case STATE_INITIAL:
					if (tag.equalsIgnoreCase(openTag(DICT))) {
						Supervisor.getInstance().reportTask("Reading header");
						state = STATE_HEADER;
					}

					break;

				case STATE_HEADER:

					if (tag.equalsIgnoreCase(openTag(DICT))) {
						Supervisor.getInstance().reportTask("Reading library");
						state = STATE_LIBRARY;
					}

					break;

				case STATE_LIBRARY:

					if (tag.equalsIgnoreCase(openTag(KEY))) {
						currentSong = new ITLSong();
					} else if (tag.equalsIgnoreCase(openTag(DICT))) {
						Supervisor.getInstance().reportTask("Reading track details");
						state = STATE_TRACK;
					} else if (tag.equalsIgnoreCase(closeTag(DICT))) {
						Supervisor.getInstance().reportTask("Finishing reading library");
						// End of library
						state = STATE_IGNORE;
					}

					break;

				case STATE_TRACK:

					if (tag.equalsIgnoreCase(openTag(KEY))) {
						currentField = getTagContent();
						state = STATE_FIELD_CONTENT;
					} else if (tag.equalsIgnoreCase(closeTag(DICT))) {
						Supervisor.getInstance().reportTask("Finished reading track '" + currentSong.getName() + "'");
						collection.add(currentSong);
						state = STATE_LIBRARY;
					}

					break;

				case STATE_FIELD_CONTENT:

					try {
						// Opening fields
						if (tag.equalsIgnoreCase(openTag(INTG))) {
							currentSong.set(currentField, getTagContent());
						} else if (tag.equalsIgnoreCase(openTag(STRG))) {
							currentSong.set(currentField, getTagContent());
						} else if (tag.equalsIgnoreCase(openTag(DATE))) {
							currentSong.set(currentField, getTagContent());
						}
						// Lone fields
						else if (tag.equalsIgnoreCase(loneTag(TRUE))) {
							currentSong.set(currentField, TRUE);
							state = STATE_TRACK;
						} else if (tag.equalsIgnoreCase(loneTag(FALSE))) {
							currentSong.set(currentField, FALSE);
							state = STATE_TRACK;
						}

						// Closing fields
						else if (tag.equalsIgnoreCase(closeTag(INTG))) {
							state = STATE_TRACK;
						} else if (tag.equalsIgnoreCase(closeTag(STRG))) {
							state = STATE_TRACK;
						} else if (tag.equalsIgnoreCase(closeTag(DATE))) {
							state = STATE_TRACK;
						}

					} catch (ParseException e) {
						e.printStackTrace();
					} catch (ITLException e) {
						e.printStackTrace();
					}

					break;

				case STATE_IGNORE:
					// Nothing
					break;

				default:
					// Nothing
					break;
				}

				// Read next tag
				tag = nextTag();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String openTag(String tagID) {
		String openTag = new String();
		openTag += MARK_START;
		openTag += tagID;
		openTag += MARK_STOP;
		return openTag;
	}

	private String closeTag(String tagID) {
		String closeTag = new String();
		closeTag += MARK_START;
		closeTag += CLOSE_SIGN;
		closeTag += tagID;
		closeTag += MARK_STOP;
		return closeTag;
	}

	private String loneTag(String tagID) {
		String loneTag = new String();
		loneTag += MARK_START;
		loneTag += tagID;
		loneTag += CLOSE_SIGN;
		loneTag += MARK_STOP;
		return loneTag;
	}

	private String nextTag() throws IOException {

		if (reader.ready()) {
			String tag = new String();

			while (currentChar[0] != MARK_START) {
				if (reader.read(currentChar) < 0) {
					break;
				}
			}

			tag += currentChar[0];

			while (currentChar[0] != MARK_STOP) {
				if (reader.read(currentChar) < 0) {
					break;
				}
				tag += currentChar[0];
			}

			return tag;
		}

		return null;
	}

	private String getTagContent() throws IOException {
		String content = new String();

		if (reader.read(currentChar) > 0) {

			while (currentChar[0] != MARK_START) {

				content += currentChar[0];

				if (reader.read(currentChar) < 0) {
					break;
				}
			}
		}

		return content;
	}
}
