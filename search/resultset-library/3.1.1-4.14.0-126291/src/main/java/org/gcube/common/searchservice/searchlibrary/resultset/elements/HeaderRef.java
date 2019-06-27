package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Placeholder for information on a header part
 * 
 * @author UoA
 */
public class HeaderRef {
	/**
	 * Whether or not this header is the head of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 */
	private String isHead=null;
	/**
	 * The name of the file that holds this header
	 */
	private String localName=null;
	/**
	 * The name of the file holding the previous header
	 */
	private String prev=null;
	/**
	 * The name of the file holding the next header
	 */
	private String next=null;
	
	/**
	 * Creates a new {@link HeaderRef}
	 * 
	 * @param isHead Indicates if this is the head
	 * @param localName The name of the file to hold the header
	 * @param prev The previous header in the chain
	 * @param next The next header in the chain
	 */
	public HeaderRef(String isHead,String localName,String prev,String next){
		this.isHead=isHead;
		this.localName=localName;
		this.prev=prev;
		this.next=next;
	}

	/**
	 * Creates a new {@link HeaderRef}
	 * 
	 * @param localName The name of the file to hold the header 
	 * @param prev The previous header in the chain
	 */
	public HeaderRef(String localName,String prev){
		this.isHead="no";
		this.localName=localName;
		this.prev=prev;
		this.next="no";
	}

	/**
	 * Retrieves the is Head property
	 * 
	 * @return Teh ishead property
	 */
	public String getIsHead() {
		return isHead;
	}

	/**
	 * Sets the isHead propety
	 * 
	 * @param isHead The value to set
	 */
	public void setIsHead(String isHead) {
		this.isHead = isHead;
	}

	/**
	 * Retrieves the name of the file that holds this header
	 * 
	 * @return The file name
	 */
	public String getLocalName() {
		return localName;
	}

	/**
	 * Sets the name of the file that holds this header
	 * 
	 * @param localName The name of the file
	 */
	public void setLocalName(String localName) {
		this.localName = localName;
	}

	/**
	 * Retrieves the name of the file that holds the next header
	 * 
	 * @return The name of the file
	 */
	public String getNext() {
		return next;
	}

	/**
	 * Sets the name of the file that holds the next header
	 * 
	 * @param next The name of the file
	 */
	public void setNext(String next) {
		this.next = next;
	}

	/**
	 * Retrieves the name of the file that holds the previous header
	 * 
	 * @return The name of the file
	 */
	public String getPrev() {
		return prev;
	}

	/**
	 * Sets the name of the file that holds the previous header
	 * 
	 * @param prev The name of the file
	 */
	public void setPrev(String prev) {
		this.prev = prev;
	}
}
