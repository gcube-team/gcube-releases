/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.grid;

import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.DataRow;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DataRowColumnConfig<N> extends ColumnConfig<DataRow, N> {

	protected ColumnDefinition definition;
	
	public DataRowColumnConfig(ColumnDefinition definition) {
		super(new DataRowValueProvider<N>(definition.getKey()), definition.getWidth(), definition.getLabel());
		this.definition = definition;
	}

	/**
	 * @return the definition
	 */
	public ColumnDefinition getDefinition() {
		return definition;
	}
}
