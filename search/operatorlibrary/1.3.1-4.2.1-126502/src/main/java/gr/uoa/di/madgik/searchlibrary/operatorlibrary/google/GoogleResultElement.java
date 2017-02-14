package gr.uoa.di.madgik.searchlibrary.operatorlibrary.google;

/**
 * This class acts as a container for the external service to fill with results
 * 
 * @author UoA
 */
public class GoogleResultElement {
	
	private String key = null;
	private String payload = null;
	
	/**
	 * Constructor
	 */
	public GoogleResultElement() {}
	
	/**
	 * Constructor with arguments
	 * @param key key
	 * @param payload payload
	 */
	public GoogleResultElement(String key, String payload) {
		this.setKey(key);
		this.setPayload(payload);
	}
	
	/**
	 * @param key the key to set
	 */
	protected void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the key
	 */
	protected String getKey() {
		return key;
	}
	/**
	 * @param payload the payload to set
	 */
	protected void setPayload(String payload) {
		this.payload = payload;
	}
	/**
	 * @return the payload
	 */
	protected String getPayload() {
		return payload;
	}
	
	
}
