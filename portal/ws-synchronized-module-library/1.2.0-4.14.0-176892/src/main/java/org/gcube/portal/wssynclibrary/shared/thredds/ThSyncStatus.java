package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class ThSyncStatus.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public class ThSyncStatus implements Serializable{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5071064482243232229L;
	
	/** The descriptor. */
	private ThProcessDescriptor descriptor;
	
	/** The status. */
	private ThProcessStatus status;
	
	/**
	 * Instantiates a new th sync status.
	 */
	public ThSyncStatus() {
	}

	/**
	 * Instantiates a new th sync status.
	 *
	 * @param descriptor the descriptor
	 * @param status the status
	 */
	public ThSyncStatus(ThProcessDescriptor descriptor, ThProcessStatus status) {
		super();
		this.descriptor = descriptor;
		this.status = status;
	}

	/**
	 * Gets the descriptor.
	 *
	 * @return the descriptor
	 */
	public ThProcessDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Sets the descriptor.
	 *
	 * @param descriptor the new descriptor
	 */
	public void setDescriptor(ThProcessDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Gets the process status.
	 *
	 * @return the process status
	 */
	public ThProcessStatus getProcessStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(ThProcessStatus status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThSyncStatus [descriptor=");
		builder.append(descriptor);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

}
