package org.gcube.portlets.user.tdwx.shared;

import java.io.Serializable;

import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnsReorderingConfig implements Serializable {

	private static final long serialVersionUID = -5161869285744817591L;

	private int columnIndex;
	private ColumnDefinition columnDefinition;

	public ColumnsReorderingConfig() {
		super();
	}

	/**
	 * 
	 * @param columnIndex
	 * @param columnDefinition
	 */
	public ColumnsReorderingConfig(int columnIndex,
			ColumnDefinition columnDefinition) {
		this.columnIndex = columnIndex;
		this.columnDefinition = columnDefinition;

	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public ColumnDefinition getColumnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(ColumnDefinition columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	@Override
	public String toString() {
		return "ColumnsReorderingConfig [columnIndex=" + columnIndex
				+ ", columnDefinition=" + columnDefinition + "]";
	}

}
