/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 17, 2014
 *
 */
public class TrashOperationContent extends TrashContent{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3089944053784656200L;
	
	private List<String> listTrashIds;
	private WorkspaceTrashOperation operation;

	/**
	 * 
	 */
	public TrashOperationContent() {
	}

	/**
	 * @param trashContent
	 * @param listErrors
	 */
	public TrashOperationContent(WorkspaceTrashOperation operation, List<String> listTrashIds) {
		this.listTrashIds = listTrashIds;
		this.operation = operation;
	}

	public void setListTrashIds(List<String> listTrashIds) {
		this.listTrashIds = listTrashIds;
	}

	public WorkspaceTrashOperation getOperation() {
		return operation;
	}

	public void setOperation(WorkspaceTrashOperation operation) {
		this.operation = operation;
	}

	public List<String> getListTrashIds() {
		return listTrashIds;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrashOperationContent [listTrashIds=");
		builder.append(listTrashIds);
		builder.append(", operation=");
		builder.append(operation);
		builder.append(", trashContent=");
		builder.append(trashContent);
		builder.append(", listErrors=");
		builder.append(listErrors);
		builder.append("]");
		return builder.toString();
	}

	
}
