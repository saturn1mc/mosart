package independent.itunes.itl.parsers;

import independent.itunes.itl.ITLException;

/*
 *  titl - Tools for iTunes Libraries
 *  Copyright (C) 2008, 2010 Joseph Walton
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * An unknown hohm type was encountered. This exception includes the contents to
 * give a clue to the data.
 * 
 * @author joe
 */
public class ITLUnknownHohmException extends ITLException {
	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = 124687070168710869L;
	private final int hohmType;
	private final byte[] contents;

	public ITLUnknownHohmException(int hohmType, byte[] contents) {
		super(null);
		this.hohmType = hohmType;
		this.contents = contents;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Unknown type: ");
		sb.append(hohmType);
		sb.append(String.format(" (0x%X)", hohmType));

		int len = Math.min(contents.length, 64);

		/* Hex */
		sb.append('\n');
		sb.append("Hex:");
		for (int i = 0; i < len; i++) {
			sb.append(' ');
			sb.append(String.format("0x%02X", contents[i] & 0xFF));
		}

		if (len < contents.length) {
			sb.append(" ...");
		}

		len = Math.min(contents.length, 96);

		/* ASCII */
		sb.append('\n');
		sb.append("ASCII: ");
		for (int i = 0; i < len; i++) {
			/* Only show printable ASCII characters */
			int c = contents[i] & 0xFF;
			if ((c >= 0x20) && (c <= 0x7E)) {
				sb.append((char) c);
			} else {
				sb.append('.');
			}
		}

		if (len < contents.length) {
			sb.append(" ...");
		}

		return sb.toString();
	}
}
