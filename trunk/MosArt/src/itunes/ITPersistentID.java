package itunes;

public class ITPersistentID {

	private long persistentID;

	public ITPersistentID(long value) {
		this.persistentID = value;
	}
	
	public ITPersistentID(String value) {
		this(Long.parseLong(value));
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
	
	public Long longValue(){
		return persistentID;
	}
	
	public String toString(){
		return Long.toHexString(persistentID);
	}
}
