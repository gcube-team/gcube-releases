/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean.output;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ErrorResource extends Resource implements Serializable {

	private static final long serialVersionUID = -5452797429958563962L;
	private String stackTrace;

	public ErrorResource() {
		super();
		this.setResourceType(ResourceType.ERROR);
	}

	public ErrorResource(String stackTrace) {
		super();
		this.stackTrace = stackTrace;
		this.setResourceType(ResourceType.ERROR);
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	@Override
	public String toString() {
		return "ErrorResource [getResourceId()=" + getResourceId()
				+ ", getDescription()=" + getDescription() + ", getName()="
				+ getName() + ", getResourceType()=" + getResourceType()
				+ ", stackTrace=" + stackTrace + "]";
	}

	

}
