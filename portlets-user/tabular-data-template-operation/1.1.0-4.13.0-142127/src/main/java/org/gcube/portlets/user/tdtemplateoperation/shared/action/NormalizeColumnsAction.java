/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;


/**
 * The Class NormalizeColumnsAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 17, 2015
 */
public class NormalizeColumnsAction implements TabularDataAction, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8373371052786653952L;

	/**
	 * 
	 */
	public static final String NORMALIZE_COLUMNS = "Normalize Column/s";

	private ServerObjectId serverObjectId;

	private List<TdColumnData> columns;

	private String normalizeLabel = "";

	private String quantityLabel = "";
	
	/**
	 * Instantiates a new normalize columns action.
	 */
	public NormalizeColumnsAction() {
	}
	
	/**
	 * Instantiates a new group time column action.
	 *
	 * @param serverObjectId the server object id
	 * @param columns the columns
	 * @param normalizeLabel the normalize label
	 * @param quantityLabel the quantity label
	 */
	public NormalizeColumnsAction(ServerObjectId serverObjectId, List<TdColumnData> columns, String normalizeLabel, String quantityLabel){
		this.serverObjectId = serverObjectId;
		this.columns = columns;
		this.normalizeLabel = normalizeLabel;
		this.quantityLabel = quantityLabel;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getId()
	 */
	@Override
	public String getId() {
		return NormalizeColumnsAction.class.getSimpleName();
	}
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getDescription()
	 */
	@Override
	public String getDescription() {
		return NORMALIZE_COLUMNS;
	}
	
	
	/**
	 * Gets the server object id.
	 *
	 * @return the serverObjectId
	 */
	public ServerObjectId getServerObjectId() {
		return serverObjectId;
	}
	
	/**
	 * Sets the server object id.
	 *
	 * @param serverObjectId the serverObjectId to set
	 */
	public void setServerObjectId(ServerObjectId serverObjectId) {
		this.serverObjectId = serverObjectId;
	}
	
	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public List<TdColumnData> getColumns() {
		return columns;
	}
	
	/**
	 * Sets the columns.
	 *
	 * @param columns the columns to set
	 */
	public void setColumns(List<TdColumnData> columns) {
		this.columns = columns;
	}
	
	/**
	 * Gets the normalize label.
	 *
	 * @return the normalizeLabel
	 */
	public String getNormalizeLabel() {
		return normalizeLabel;
	}
	
	/**
	 * Sets the normalize label.
	 *
	 * @param normalizeLabel the normalizeLabel to set
	 */
	public void setNormalizeLabel(String normalizeLabel) {
		this.normalizeLabel = normalizeLabel;
	}
	
	/**
	 * Gets the quantity label.
	 *
	 * @return the quantityLabel
	 */
	public String getQuantityLabel() {
		return quantityLabel;
	}
	
	/**
	 * Sets the quantity label.
	 *
	 * @param quantityLabel the quantityLabel to set
	 */
	public void setQuantityLabel(String quantityLabel) {
		this.quantityLabel = quantityLabel;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NormalizeColumnsAction [serverObjectId=");
		builder.append(serverObjectId);
		builder.append(", columns=");
		builder.append(columns);
		builder.append(", normalizeLabel=");
		builder.append(normalizeLabel);
		builder.append(", quantityLabel=");
		builder.append(quantityLabel);
		builder.append("]");
		return builder.toString();
	}
	
	
}
