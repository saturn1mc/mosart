package itl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ITLParser {

	// States
	private static final int STATE_INITIAL = 0;
	private static final int STATE_HEADER = 1;
	private static final int STATE_LIBRARY = 2;
	private static final int STATE_TRACK = 3;
	private static final int STATE_FIELD_CONTENT = 4;

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
	private ITLSong currentSong;

	private int state;
	private ArrayList<ITLSong> songs;

	public ITLParser() {
		state = STATE_INITIAL;
		songs = new ArrayList<ITLSong>();
		currentChar = new char[1];
	}

	public ArrayList<ITLSong> parseITL(String iTunesLibrary){
		try {
			reader = new BufferedReader(new FileReader(iTunesLibrary));
			String tag = nextTag();
			
			while(tag != null){
				
				switch (state) {
				case STATE_INITIAL :
					
					if(tag.equalsIgnoreCase(openTag(DICT))){
						state = STATE_HEADER;
					}
					
					break;

				case STATE_HEADER :
					
					if(tag.equalsIgnoreCase(openTag(DICT))){
						state = STATE_LIBRARY;
					}
					
					break;
					
				case STATE_LIBRARY :
					
					if(tag.equalsIgnoreCase(openTag(KEY))){
						currentSong = new ITLSong(getTagContent());
						songs.add(currentSong);
					}
					
					if(tag.equalsIgnoreCase(openTag(DICT))){
						state = STATE_TRACK;
						break;
					}
					
					break;
				
				case STATE_TRACK :
					
					if(tag.equalsIgnoreCase(openTag(KEY))){
						state = STATE_FIELD_CONTENT;
						break;
					}
					
					if(tag.equalsIgnoreCase(closeTag(DICT))){
						state = STATE_LIBRARY;
						break;
					}
					
					break;
					
				case STATE_FIELD_CONTENT :
					
					if(tag.equalsIgnoreCase(closeTag(INTG))){
						state = STATE_TRACK;
						break;
					}
					
					if(tag.equalsIgnoreCase(closeTag(STRG))){
						state = STATE_TRACK;
						break;
					}
					
					if(tag.equalsIgnoreCase(closeTag(DATE))){
						state = STATE_TRACK;
						break;
					}
					
					if(tag.equalsIgnoreCase(closeTag(TRUE))){
						state = STATE_TRACK;
						break;
					}
					
					if(tag.equalsIgnoreCase(closeTag(FALSE))){
						state = STATE_TRACK;
						break;
					}
					
					break;
					
				default:
					//Nothing
					break;
				}
				
				//Read next tag
				tag = nextTag();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return songs;
	}

	private String openTag(String tagID) {
		return MARK_START + tagID + MARK_STOP;
	}
	
	private String closeTag(String tagID) {
		return MARK_START + CLOSE_SIGN + tagID + MARK_STOP;
	}

	private String nextTag() throws IOException {

		if (reader.read(currentChar) > 0) {
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

		while (currentChar[0] != MARK_STOP) {

			content += currentChar[0];

			if (reader.read(currentChar) < 0) {
				break;
			}
		}

		return content;
	}

	public static void main(String[] args) {
		ITLParser spe = new ITLParser();
		for (ITLSong song : spe.parseITL("D:\\iTunes Music Library.xml")) {
			System.out.println(song);
		}
	}

}
