package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author massi
 *
 */
public class ClientCloudReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private DeployStatus status;
	
	/**
	 * 
	 */
	private List<DeployStatus> itemsStatuses;
	/**
	 * 
	 */
	public ClientCloudReport() {
		super();
	}

	/**
	 * 
	 * @param globalstatus
	 * @param itemsStatuses
	 */
	public ClientCloudReport(DeployStatus globalstatus,
			List<DeployStatus> itemsStatuses) {
		super();
		this.status = globalstatus;
		this.itemsStatuses = itemsStatuses;
	}

	public DeployStatus getStatus() {
		return status;
	}

	public void setStatus(DeployStatus status) {
		this.status = status;
	}

	public List<DeployStatus> getItemsStatuses() {
		return itemsStatuses;
	}

	public void setItemsStatuses(List<DeployStatus> itemsStatuses) {
		this.itemsStatuses = itemsStatuses;
	}
	
	
}
