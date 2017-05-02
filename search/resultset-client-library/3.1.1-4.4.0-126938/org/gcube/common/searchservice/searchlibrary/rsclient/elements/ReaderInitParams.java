package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

/**
 * Initialization parameters
 * 
 * @author UoA
 */
public class ReaderInitParams {
	/**
	 * The locator
	 */
	public RSLocator Locator;
	/**
	 * localize the reader
	 */
	public boolean Localize;
	/**
	 * The resource type
	 */
	public RSResourceType ResourceType;
	/**
	 * Keep top orr not
	 */
	public boolean KeepTop;
	/**
	 * The number of records
	 */
	public int Count;
	
	/**
	 * creates a new instance
	 * 
	 * @param locator the locator
	 * @param localize localize or not
	 * @param resourceType the resource type
	 * @param keepTop keep top or not
	 * @param count the number of records in the keep top
	 */
	public ReaderInitParams(RSLocator locator, boolean localize, RSResourceType resourceType, boolean keepTop, int count){
		this.Localize=localize;
		this.Locator=locator;
		this.ResourceType=resourceType;
		this.KeepTop=keepTop;
		this.Count=count;
	}
}
