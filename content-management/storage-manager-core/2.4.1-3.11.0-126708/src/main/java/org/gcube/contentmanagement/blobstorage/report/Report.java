package org.gcube.contentmanagement.blobstorage.report;
/**
 * Generic interface for accounting report
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
@Deprecated
public interface Report {	
	/**
	 * Set generic properties of report
	 * @param resourceType
	 * @param consumerId
	 * @param resourceOwner
	 * @param resourceScope
	 * @return
	 */
	public void init(String consumerId, String resourceScope);
	/**
	 * set start time of the operation
	 * @return
	 */
	public void timeUpdate();
	
	/**
	 * Set end time of operation and other specific properties
	 * @return
	 */
	public void ultimate(String owner, String uri, String operation, String size );
	
	/**
	 * send report
	 * @return
	 */
	public void send();

}
