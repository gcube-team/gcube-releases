/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.util;

import java.util.Date;

import org.gcube.portlets.user.tdw.client.model.grid.DataRowColumnConfig;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.DataRow;

import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ColumnConfigGenerator {

	public static DataRowColumnConfig<?> generateConfiguration(ColumnDefinition columnDefinition)
	{
		DataRowColumnConfig<?> columnConfig = null;
		switch (columnDefinition.getValueType()) {
			case BOOLEAN: columnConfig = ColumnConfigGenerator.<Boolean>create(columnDefinition); break;
			case DATE: columnConfig = ColumnConfigGenerator.<Date>create(columnDefinition); break;
			case DOUBLE: columnConfig = ColumnConfigGenerator.<Double>create(columnDefinition); break;
			case FLOAT: columnConfig = ColumnConfigGenerator.<Float>create(columnDefinition); break;
			case INTEGER: columnConfig = ColumnConfigGenerator.<Integer>create(columnDefinition); break;
			case LONG: columnConfig = ColumnConfigGenerator.<Long>create(columnDefinition); break;
			case STRING: {

				columnConfig = ColumnConfigGenerator.<String>create(columnDefinition);
				/*column.setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
					@Override
					public SafeHtml render(String object) {
						return SafeHtmlUtils.fromString(object);
					}
				})
				);*/

			} break;
			default: return null;
		}
		
		columnConfig.setHidden(!columnDefinition.isVisible());
		
		return columnConfig;
	}

	protected static <T> DataRowColumnConfig<T> create(ColumnDefinition columnDefinition)
	{
		return new DataRowColumnConfig<T>(columnDefinition);
	}
	
	public static void setEditor(AbstractGridEditing<DataRow> editableGrid, ColumnConfig<DataRow,?> columnConfig)
	{
		if (!(columnConfig instanceof DataRowColumnConfig<?>)) throw new IllegalArgumentException("Expected ColumnConfig type JSonValueColumnConfig");
		setEditor(editableGrid, (DataRowColumnConfig<?>)columnConfig);
	}

	@SuppressWarnings("unchecked")
	public static void setEditor(AbstractGridEditing<DataRow> editableGrid, DataRowColumnConfig<?> columnConfig)
	{
		if (columnConfig.getDefinition().isEditable()) {
			switch (columnConfig.getDefinition().getValueType()) {
				case BOOLEAN: editableGrid.addEditor((ColumnConfig<DataRow, Boolean>) columnConfig, new CheckBox()); break;
				case DATE: editableGrid.addEditor((ColumnConfig<DataRow, Date>) columnConfig, new DateField()); break;
				case DOUBLE: editableGrid.addEditor((ColumnConfig<DataRow, Double>) columnConfig, new NumberField<Double>(new NumberPropertyEditor.DoublePropertyEditor())); break;
				case FLOAT: editableGrid.addEditor((ColumnConfig<DataRow, Float>) columnConfig, new NumberField<Float>(new NumberPropertyEditor.FloatPropertyEditor())); break;
				case INTEGER: editableGrid.addEditor((ColumnConfig<DataRow, Integer>) columnConfig, new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor())); break;
				case LONG: editableGrid.addEditor((ColumnConfig<DataRow, Long>) columnConfig, new NumberField<Long>(new NumberPropertyEditor.LongPropertyEditor())); break;
				case STRING: editableGrid.addEditor((ColumnConfig<DataRow, String>) columnConfig, new TextField()); break;
				default: break;
			}
		}
	}


}
