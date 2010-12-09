package itunes;

import java.math.BigInteger;

public class ITPersistentID {

	private long persistentID;

	public ITPersistentID(long value) {
		this.persistentID = value;
	}

	public ITPersistentID(String value) {
		this.persistentID = new BigInteger(value, 16).longValue();
	}

	public ITPersistentID(int high, int low) {
		this.persistentID = (long) ((((long) high << 32) & 0xFFFFFFFF00000000L) + ((long) low & 0x00000000FFFFFFFFL));
	}

	public int getLowBits() {
		return (int) (persistentID & 0xFFFFFFFF);
	}

	public int getHighBits() {
		return (int) (persistentID >> 32);
	}

	public Long longValue() {
		return persistentID;
	}

	public boolean equals(String value) {
		long lvalue = new BigInteger(value, 16).longValue();
		return this.persistentID == lvalue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ITPersistentID) {
			return ((ITPersistentID) obj).persistentID == this.persistentID;
		} else {
			return false;
		}
	}

	public String toString() {
		return Long.toHexString(persistentID);
	}
}
