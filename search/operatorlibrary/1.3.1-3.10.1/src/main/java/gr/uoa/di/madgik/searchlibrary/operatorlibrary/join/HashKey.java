package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

/**
 * The key used in the hash table
 * 
 * @author UoA
 */
public class HashKey {
	/**
	 * The key
	 */
	private byte []key=null;
	
	/**
	 * Creates a new {@link HashKey}
	 * 
	 * @param key The key to set
	 */
	public HashKey(byte []key){
		this.key=key;
	}

	/**
	 * Retrieves the hash key
	 * 
	 * @return The hash key
	 */
	public String getKey() {
		return new String(key);
	}

	/**
	 * Sets the hash key
	 * 
	 * @param key The hash key
	 */
	public void setKey(byte[] key) {
		this.key = key;
	}
}
