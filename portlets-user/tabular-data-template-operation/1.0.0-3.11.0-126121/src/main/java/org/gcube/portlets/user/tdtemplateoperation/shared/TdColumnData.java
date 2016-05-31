/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 24, 2015
 */
public class TdColumnData extends TdBaseData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3902407210350820550L;
	
	private ServerObjectId serverId; 
	private String dataTypeName;
	private String name;
	private String periodType;

//	private String id = super.id;
//	private String label = super.label;

	private boolean isBaseColumn = true;
	
	/**
	 * 
	 */
	public TdColumnData() {
	}
	
	/**
	 * @param serverId
	 * @param id
	 * @param name
	 * @param label
	 * @param dataTypeName
	 */
	public TdColumnData(ServerObjectId serverId, String id, String name, String label, String dataTypeName, boolean isBaseColumn) {
		super(id, label);
		this.serverId = serverId;
		this.name = name;
		this.dataTypeName = dataTypeName;
		this.isBaseColumn = isBaseColumn;
	}

	/**
	 * @return the isBaseColumn
	 */
	public boolean isBaseColumn() {
		return isBaseColumn;
	}

	/**
	 * @param isBaseColumn the isBaseColumn to set
	 */
	public void setBaseColumn(boolean isBaseColumn) {
		this.isBaseColumn = isBaseColumn;
	}


	

	/**
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}

	/**
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	/**
	 * @return the serverId
	 */
	public ServerObjectId getServerId() {
		return serverId;
	}

	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(ServerObjectId serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the dataTypeName
	 */
	public String getDataTypeName() {
		return dataTypeName;
	}

	/**
	 * @param dataTypeName the dataTypeName to set
	 */
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdColumnData [serverId=");
		builder.append(serverId);
		builder.append(", dataTypeName=");
		builder.append(dataTypeName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", periodType=");
		builder.append(periodType);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
