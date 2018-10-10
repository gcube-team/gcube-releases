/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 17, 2014
 *
 */
public class TrashContent implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7428752149892396573L;
	
	
	List<FileTrashedModel> trashContent;
	List<FileTrashedModel> listErrors;
	
	/**
	 * 
	 */
	public TrashContent() {
	}

	/**
	 * @param trashContent
	 * @param listErrors
	 */
	public TrashContent(List<FileTrashedModel> trashContent, List<FileTrashedModel> listErrors) {
		this.trashContent = trashContent;
		this.listErrors = listErrors;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrashOperationResult [trashContent=");
		builder.append(trashContent);
		builder.append(", listErrors=");
		builder.append(listErrors);
		builder.append("]");
		return builder.toString();
	}

	public List<FileTrashedModel> getTrashContent() {
		return trashContent;
	}

	public void setTrashContent(List<FileTrashedModel> trashContent) {
		this.trashContent = trashContent;
	}

	public List<FileTrashedModel> getListErrors() {
		return listErrors;
	}

	public void setListErrors(List<FileTrashedModel> listErrors) {
		this.listErrors = listErrors;
	}
}
