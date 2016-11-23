/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.grid;

import java.util.Date;

import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 * @param <N>
 */
public class DataRowColumnConfig<N> extends ColumnConfig<DataRow, N> {
	
	protected ColumnDefinition definition;

	public DataRowColumnConfig(ColumnDefinition definition) {
		super(new DataRowValueProvider<N>(definition.getKey()), definition
				.getWidth(), definition.getLabel());
		this.definition = definition;
	}

	/**
	 * @return the definition
	 */
	public ColumnDefinition getDefinition() {
		return definition;
	}

	@SuppressWarnings("unchecked")
	public void setCellBoolean(AbstractCell<Boolean> cell) {
		Cell<N> cell2 = (Cell<N>) cell;
		super.setCell(cell2);
	}
	
	@SuppressWarnings("unchecked")
	public void setCellDate(AbstractCell<Date> cell) {
		Cell<N> cell2 = (Cell<N>) cell;
		super.setCell(cell2);
	}
	
	@SuppressWarnings("unchecked")
	public void setCellAbstract(AbstractCell<?> cell) {
		Cell<N> cell2 = (Cell<N>) cell;
		super.setCell(cell2);
	}
	
	
	public Cell<N> getCell() {
		return super.getCell();
	}

	
}
