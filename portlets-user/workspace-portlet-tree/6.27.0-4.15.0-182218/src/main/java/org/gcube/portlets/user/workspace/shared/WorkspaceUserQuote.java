/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 31, 2014
 *
 */
public class WorkspaceUserQuote implements Serializable{
	
	private static final long serialVersionUID = -5363340286390074157L;
	
	
	private Long diskSpace;
	private String diskSpaceFormatted;
	private Long totalItems;
	
	public WorkspaceUserQuote(){
	}
	
	

	public WorkspaceUserQuote(Long diskSpace, String diskSpaceFormatted,
			Long totalItems) {
		this.diskSpace = diskSpace;
		this.diskSpaceFormatted = diskSpaceFormatted;
		this.totalItems = totalItems;
	}

	public Long getDiskSpace() {
		return diskSpace;
	}

	public String getDiskSpaceFormatted() {
		return diskSpaceFormatted;
	}

	public Long getTotalItems() {
		return totalItems;
	}

	public void setDiskSpace(Long diskSpace) {
		this.diskSpace = diskSpace;
	}

	public void setDiskSpaceFormatted(String diskSpaceFormatted) {
		this.diskSpaceFormatted = diskSpaceFormatted;
	}

	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceUserQuote [diskSpace=");
		builder.append(diskSpace);
		builder.append(", diskSpaceFormatted=");
		builder.append(diskSpaceFormatted);
		builder.append(", totalItems=");
		builder.append(totalItems);
		builder.append("]");
		return builder.toString();
	}

}
