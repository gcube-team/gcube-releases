package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;

/**
 * Implements the realtionship for Dimension and TimeDimension columns
 * 
 * @author Giancarlo Panichi
 * 
 */
public class RelationshipData implements Serializable {

	private static final long serialVersionUID = 897893891284145975L;

	
	Long targetTableId;
	String targetColumnId;
	
	public RelationshipData() {

	}

	public RelationshipData(Long targetTableId, String targetColumnId) {
		this.targetColumnId=targetColumnId;
		this.targetTableId=targetTableId;
	}

	public String getTargetColumnId() {
		return targetColumnId;
	}

	public void setTargetColumnId(String targetColumnId) {
		this.targetColumnId = targetColumnId;
	}

	public Long getTargetTableId() {
		return targetTableId;
	}

	public void setTargetTableId(Long targetTableId) {
		this.targetTableId = targetTableId;
	}

	@Override
	public String toString() {
		return "RelationshipData [targetTableId=" + targetTableId
				+ ", targetColumnId=" + targetColumnId + "]";
	}

	

}
