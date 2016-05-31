package org.gcube.portlets.admin.wfdocslibrary.shared;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
/**
 * <code> Step </code> class
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class Step implements Serializable {
	private int left;
	private int top;
	private String label;
	private String description;
	private Map<WfRole, ArrayList<PermissionType>> permissions;

	public Step() {}

	public Step(String label, Map<WfRole, ArrayList<PermissionType>> permissions) {
		this.label = label;
		this.permissions = permissions;
	}

	public Step(int left, int top, String label,Map<WfRole, ArrayList<PermissionType>> permissions) {
		super();
		this.left = left;
		this.top = top;
		this.label = label;
		this.permissions = permissions;
	}
	
	public Step(int left, int top, String label, String description,
			Map<WfRole, ArrayList<PermissionType>> permissions) {
		super();
		this.left = left;
		this.top = top;
		this.label = label;
		this.description = description;
		this.permissions = permissions;
	}

	/**
	 * constructor to be used in template mode
	 * 
	 * @param left
	 * @param top
	 * @param label
	 */
	public Step(int left, int top, String label, String description) {
		super();
		this.left = left;
		this.top = top;
		this.description = description;
		this.label = label;
		this.permissions = null;
	}

	public String getLabel() {	return label;	}
	public void setLabel(String label) { this.label = label;	}
	public Map<WfRole, ArrayList<PermissionType>> getPermissions() {	return permissions;	}
	public void setPermissions(Map<WfRole, ArrayList<PermissionType>> permissions) {	this.permissions = permissions;	}
	public int getLeft() {	return left;	}
	public void setLeft(int left) {	this.left = left;	}
	public int getTop() {	return top;	}
	public void setTop(int top) {	this.top = top;	}
	public String toString() {	return this.label;	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
