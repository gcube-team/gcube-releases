package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class SubTree extends BaseModelData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FolderModel parent;
	private List<FileModel> children;
	
	public SubTree(){
		
	}
	public SubTree(FolderModel parent, List<FileModel> children) {
		this.parent = parent;
		this.children = children;
	}

	public FolderModel getParent() {
		return parent;
	}

	public List<FileModel> getChildren() {
		return children;
	}
	
	public String getParentId(){
		return this.parent.getIdentifier();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubTree [parent=");
		builder.append(parent);
		builder.append(", children=");
		builder.append(children!=null?children.size():"null");
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
