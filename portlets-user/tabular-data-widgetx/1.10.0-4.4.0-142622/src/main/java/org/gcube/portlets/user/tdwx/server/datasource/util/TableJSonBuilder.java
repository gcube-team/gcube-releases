/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.datasource.util;

import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TableJSonBuilder extends GridJSonBuilder {

	protected TableDefinition tableDefinition;

	public TableJSonBuilder(TableDefinition tableDefinition) {
		super();
		this.tableDefinition = tableDefinition;
	}

	public void startRows() {
		super.startRows(tableDefinition.getJsonRowsField());
	}

	public void endRow() {
		if (fieldsCount != tableDefinition.getColumns().size())
			throw new IllegalStateException("Expected "
					+ tableDefinition.getColumns().size() + " fields, added "
					+ fieldsCount);
		super.endRow();
	}

	public void setTotalLength(int length) {
		super.setTotalLength(tableDefinition.getJsonTotalLengthField(), length);
	}

	public void setOffset(int offset) {
		super.setOffset(tableDefinition.getJsonOffsetField(), offset);
	}

}
