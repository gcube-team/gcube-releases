/**
 * 
 */
package org.gcube.portlets.user.tdwx.shared.model;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableId implements Serializable {
	
	private static final long serialVersionUID = -1694977332913609912L;
	
	protected String dataSourceFactoryId;
	protected String tableKey;
	
	public TableId(){}
	
	/**
	 * @param dataSourceFactoryId
	 * @param tableKey
	 */
	public TableId(String dataSourceFactoryId, String tableKey) {
		this.dataSourceFactoryId = dataSourceFactoryId;
		this.tableKey = tableKey;
	}

	/**
	 * @return the dataSourceFactoryId
	 */
	public String getDataSourceFactoryId() {
		return dataSourceFactoryId;
	}

	/**
	 * @return the tableKey
	 */
	public String getTableKey() {
		return tableKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableId [dataSourceFactoryId=");
		builder.append(dataSourceFactoryId);
		builder.append(", tableKey=");
		builder.append(tableKey);
		builder.append("]");
		return builder.toString();
	}

}
