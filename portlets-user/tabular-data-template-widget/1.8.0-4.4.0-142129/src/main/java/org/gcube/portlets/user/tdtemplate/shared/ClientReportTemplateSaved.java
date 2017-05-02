/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 4, 2014
 *
 */
public class ClientReportTemplateSaved implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1180988677372000038L;
	
	
	private boolean error = false;
	private List<TdColumnDefinition> listErrorColumn;
	private List<TdColumnDefinition> listColumns;
	
	/**
	 * 
	 */
	public ClientReportTemplateSaved() {
	}

	/**
	 * @param error
	 * @param listErrorColumn
	 */
	public ClientReportTemplateSaved(boolean error, List<TdColumnDefinition> listErrorColumn, List<TdColumnDefinition> columns) {
		this.error = error;
		this.listErrorColumn = listErrorColumn;
		this.listColumns = columns;
	}

	/**
	 * @return the listColumns
	 */
	public List<TdColumnDefinition> getListColumns() {
		return listColumns;
	}

	/**
	 * @param listColumns the listColumns to set
	 */
	public void setListColumns(List<TdColumnDefinition> listColumns) {
		this.listColumns = listColumns;
	}

	public boolean isError() {
		return error;
	}

	public List<TdColumnDefinition> getListErrorColumn() {
		return listErrorColumn;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setListErrorColumn(List<TdColumnDefinition> listErrorColumn) {
		this.listErrorColumn = listErrorColumn;
	}
	
	public void addValidColumn(TdColumnDefinition tdColumnDefinition){

		if(listColumns==null)
			listColumns = new ArrayList<TdColumnDefinition>();
		
		listColumns.add(tdColumnDefinition);
	}
	
	/**
	 * @param tdColumnDefinition
	 */
	public void addColumnError(TdColumnDefinition tdColumnDefinition) {
		
		if(listErrorColumn==null)
			listErrorColumn = new ArrayList<TdColumnDefinition>();
		
		listErrorColumn.add(tdColumnDefinition);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientReportTemplateSaved [error=");
		builder.append(error);
		builder.append(", listErrorColumn=");
		builder.append(listErrorColumn);
		builder.append(", listColumns=");
		builder.append(listColumns);
		builder.append("]");
		return builder.toString();
	}
}
