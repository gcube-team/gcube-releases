package org.gcube.portlets.user.tdwx.client.filter;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.tdwx.client.filter.text.TextFilter;
import org.gcube.portlets.user.tdwx.client.model.grid.DataRowColumnConfig;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;


/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class FiltersGenerator {
	public static ArrayList<Filter<DataRow, ?>> generate(
			ColumnModel<DataRow> columnModel) {
		ArrayList<Filter<DataRow, ?>> filters = new ArrayList<Filter<DataRow, ?>>();

		for (ColumnConfig<DataRow, ?> columnConfig : columnModel.getColumns()) {

			DataRowColumnConfig<?> dataRowColConfig = (DataRowColumnConfig<?>) columnConfig;

			if (dataRowColConfig != null) {
				if (dataRowColConfig.getDefinition() != null
						&& dataRowColConfig.getDefinition().getValueType() != null) {
					ValueType vt = dataRowColConfig.getDefinition()
							.getValueType();
					//Log.debug("DataRowColConfig: " + vt);
					switch (vt) {
					case BOOLEAN:
						@SuppressWarnings("unchecked")
						ExtendedBooleanFilter<DataRow> bFilt = new ExtendedBooleanFilter<DataRow>(
								(ValueProvider<DataRow, Boolean>) dataRowColConfig
										.getValueProvider());
						filters.add(bFilt);
						break;
					case DATE:
						@SuppressWarnings("unchecked")
						DateFilter<DataRow> dateFilt = new DateFilter<DataRow>(
								(ValueProvider<DataRow, Date>) dataRowColConfig
										.getValueProvider());
						filters.add(dateFilt);
						break;
					case DOUBLE:
						@SuppressWarnings("unchecked")
						NumericFilter<DataRow, Double> doubleFilt = new NumericFilter<DataRow, Double>(
								(ValueProvider<DataRow, Double>) dataRowColConfig
										.getValueProvider(),
								new NumberPropertyEditor.DoublePropertyEditor());
						filters.add(doubleFilt);
						break;
					case INTEGER:
						@SuppressWarnings("unchecked")
						NumericFilter<DataRow, Integer> integerFilt = new NumericFilter<DataRow, Integer>(
								(ValueProvider<DataRow, Integer>) dataRowColConfig
										.getValueProvider(),
								new NumberPropertyEditor.IntegerPropertyEditor());
						filters.add(integerFilt);
						break;
					case LONG:
						@SuppressWarnings("unchecked")
						NumericFilter<DataRow, Long> longFilt = new NumericFilter<DataRow, Long>(
								(ValueProvider<DataRow, Long>) dataRowColConfig
										.getValueProvider(),
								new NumberPropertyEditor.LongPropertyEditor());
						filters.add(longFilt);
						break;
					case STRING:
						@SuppressWarnings("unchecked")
						TextFilter<DataRow> stringFilt = new TextFilter<DataRow>(
								(ValueProvider<DataRow, String>) dataRowColConfig
										.getValueProvider());
						filters.add(stringFilt);
					case GEOMETRY:
						//TODO Filter for geometry type
						/*@SuppressWarnings("unchecked")
						StringFilter<DataRow> geometryFilt = new StringFilter<DataRow>(
								(ValueProvider<DataRow, String>) dataRowColConfig
										.getValueProvider());
						filters.add(geometryFilt);*/	
						break;
					default:
						break;

					}

				} else {
					Log.debug("DataRowColConfig: ValueType NULL");
				}
			} else {
				Log.debug("DataRowColConfig: NULL");
			}
		}

		return filters;

	}
}
