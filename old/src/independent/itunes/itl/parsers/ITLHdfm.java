package independent.itunes.itl.parsers;

/*
 *  titl - Tools for iTunes Libraries
 *  Copyright (C) 2008 Joseph Walton
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
import independent.itunes.ITUtil;
import independent.itunes.itl.ITLException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ITLHdfm {
	public final String version;
	public final int unknown;
	final byte[] headerRemainder;
	public final byte[] fileData;
	public Boolean compressContents;

	private ITLHdfm(String version, int unknown, byte[] headerRemainder,
			byte[] fileData, Boolean compressContents) {
		this.version = version;
		this.unknown = unknown;
		this.headerRemainder = headerRemainder;
		this.fileData = fileData;
		this.compressContents = compressContents;
	}

	// Byte Length Comment
	// -----------------------
	// 0 4 'hdfm'
	// 4 4 L = header length
	// 8 4 file length ?
	// 12 4 ?
	// 13 1 N = length of version string
	// 14 N application version string
	// 14+N L-N-17 ?
	public static ITLHdfm read(DataInput di, long fileLength) throws IOException,
			ITLException {
		int hdr = di.readInt();
		ITUtil.assertEquals(ITUtil.toString(hdr), "hdfm");

		int hl = di.readInt();

		int fl = di.readInt();
		if (fileLength != fl) {
			throw new IOException("Disk file is " + fileLength
					+ " but header claims " + fl);
		}

		int unknown = di.readInt();

		int vsl = di.readUnsignedByte();
		byte[] avs = new byte[vsl];
		di.readFully(avs);

		String version = new String(avs, "us-ascii");

		int consumed = vsl + 17;

		byte[] headerRemainder = new byte[hl - consumed];
		di.readFully(headerRemainder);

		consumed += headerRemainder.length;

		if (hl != consumed) {
			throw new IOException("Header claims to be " + hl
					+ " bytes but read " + consumed);
		}

		byte[] restOfFile = new byte[(int) fileLength - consumed];

		di.readFully(restOfFile);

		byte[] decrypted = new byte[restOfFile.length];

		/* Decrypt */
		decrypted = crypt(restOfFile, Cipher.DECRYPT_MODE);

		/* Unzip (aka inflate, decompress...) */
		byte[] inflated = inflate(decrypted);

		/*
		 * If inflate() returned the exact same array, that means the unzip
		 * failed, so we should assume that the compression shouldn't be used
		 * for this ITL file.
		 */
		boolean useCompression = !Arrays.equals(decrypted, inflated);

		return new ITLHdfm(version, unknown, headerRemainder, inflated,
				useCompression);
	}

	/**
	 * hdfm chunks occur inline in 10.0, for some reason.
	 * 
	 * @param di
	 * @param length
	 * @param consumed
	 * @return
	 * @throws IOException
	 * @throws ITLException
	 */
	public static ITLHdfm readInline(DataInput di, int length, int consumed)
			throws IOException, ITLException {
		int hl = di.readInt();

		if (hl != 0) {
			throw new IOException("Expected zero for inline HDFM length (was "
					+ hl + ")");
		}

		int fl = di.readInt();

		int unknown = di.readInt();

		int vsl = di.readUnsignedByte();
		byte[] avs = new byte[vsl];
		di.readFully(avs);

		String version = new String(avs, "us-ascii");

		consumed += vsl + 13;

		byte[] headerRemainder = new byte[length - consumed];
		di.readFully(headerRemainder);

		consumed += headerRemainder.length;

		if (consumed != length) {
			throw new IOException("Expected to read " + length
					+ " bytes but read " + consumed);
		}

		return new ITLHdfm(version, unknown, headerRemainder, null, false);
	}

	/**
	 * Obfuscation description from <a href=
	 * "http://search.cpan.org/src/BDFOY/Mac-iTunes-0.90/examples/crypt-rijndael.pl"
	 * >this sample</a>.
	 * 
	 * @param orig
	 * @param mode
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ITLException
	 */
	private static byte[] crypt(byte[] orig, int mode)
			throws UnsupportedEncodingException, ITLException {
		byte[] res = new byte[orig.length];

		/* Decrypt */
		try {
			byte[] rawKey = "BHUILuilfghuila3".getBytes("US-ASCII");
			
			SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cip = Cipher.getInstance("AES/ECB/NoPadding");
			cip.init(mode, skeySpec);

			int x = orig.length % 16;

			byte[] result = cip.doFinal(orig, 0, orig.length - x);
			System.arraycopy(result, 0, res, 0, result.length);
			System.arraycopy(orig, result.length, res, result.length, x);
			
		} catch (GeneralSecurityException gse) {
			if (mode == Cipher.DECRYPT_MODE) {
				throw new ITLException("Unable to decrypt library");
			} else if (mode == Cipher.ENCRYPT_MODE) {
				throw new ITLException("Unable to encrypt library");
			} else {
				throw new ITLException("Unable to perform operation");
			}
		}

		return res;
	}

	private static byte[] inflate(byte[] orig) throws ITLException {
		byte[] inflated = null;

		try {
			InflaterInputStream isInflater = new InflaterInputStream(
					new ByteArrayInputStream(orig), new Inflater());
			ByteArrayOutputStream osDecompressed = new ByteArrayOutputStream(
					orig.length);
			inflated = new byte[orig.length];
			int iDecompressed;
			while (true) {
				iDecompressed = isInflater.read(inflated, 0, orig.length);
				if (iDecompressed == -1)
					break;
				osDecompressed.write(inflated, 0, iDecompressed);
			}
			inflated = osDecompressed.toByteArray();
			osDecompressed.close();
			isInflater.close();
		} catch (ZipException ze) {
			// If a ZipException occurs, it's probably because "orig" isn't
			// actually compressed data,
			// because it's from an earlier version of iTunes.
			// So since there's nothing to decompress, just return the array
			// that was passed in, unchanged.
			return orig;
		} catch (IOException ioe) {
			throw new ITLException("Error when unzipping the file contents");
		}

		return inflated;
	}

	private static byte[] deflate(byte[] orig) throws ITLException {
		try {
			DeflaterInputStream isDeflater = new DeflaterInputStream(
					new ByteArrayInputStream(orig), new Deflater());
			ByteArrayOutputStream osCompressed = new ByteArrayOutputStream(
					orig.length);
			byte[] deflated = new byte[orig.length];
			int iCompressed;

			while (true) {
				iCompressed = isDeflater.read(deflated, 0, orig.length);
				if (iCompressed == -1)
					break;
				osCompressed.write(deflated, 0, iCompressed);
			}

			deflated = osCompressed.toByteArray();
			osCompressed.close();
			isDeflater.close();

			return deflated;
		} catch (IOException ioe) {
			throw new ITLException("Error when zipping the file contents");
		}
	}

	public void write(DataOutput o) throws IllegalArgumentException,
			IOException, ITLException {
		write(o, fileData);
	}

	public void write(DataOutput o, byte[] dat)
			throws IllegalArgumentException, IOException, ITLException {
		if (this.compressContents) {
			/*
			 * If the contents were zipped before, we should zip them now,
			 * before encrypting and then writing to the file
			 */
			dat = deflate(dat);
		}

		/* Write the header */
		byte[] ba = version.getBytes("us-ascii");

		assert ba.length < 256;

		o.writeInt(ITUtil.fromString("hdfm"));

		int hl = 17 + headerRemainder.length + ba.length;
		o.writeInt(hl);

		int fileLength = hl + dat.length;
		o.writeInt(fileLength);

		o.writeInt(unknown);

		o.writeByte(ba.length);
		o.write(ba);

		o.write(headerRemainder);

		/* Encode and write the data */
		byte[] encrypted = crypt(dat, Cipher.ENCRYPT_MODE);

		o.write(encrypted);
	}
}
