/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.util;

import java.util.Date;

import org.gcube.portlets.user.tdwx.client.model.grid.DataRowColumnConfig;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnConfigGenerator {

	public static DataRowColumnConfig<?> generateConfiguration(
			ColumnDefinition columnDefinition) {
		DataRowColumnConfig<?> columnConfig = null;

		SafeStyles safeStyle;
		SafeStylesBuilder ssb = new SafeStylesBuilder();

		switch (columnDefinition.getValueType()) {
		case BOOLEAN:
			columnConfig = ColumnConfigGenerator
					.<Boolean> create(columnDefinition);
			if (columnDefinition.getType() == ColumnType.VALIDATION) {

				columnConfig.setCellBoolean(new AbstractCell<Boolean>() {

					@Override
					public void render(Context context, Boolean value,
							SafeHtmlBuilder sb) {
						// int rowIndex = context.getIndex();
						// store.get(rowIndex);

						String style = "style='color: "
								+ (value ? "green" : "black") + "'";

						sb.appendHtmlConstant("<span "
								+ style
								+ " title='"
								+ new SafeHtmlBuilder().append(value)
										.toSafeHtml().asString()
								+ "'>"
								+ new SafeHtmlBuilder().append(value)
										.toSafeHtml().asString() + "</span>");
					}
				});
			}
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			ssb.textAlign(TextAlign.CENTER);
			break;
		case DATE:
			columnConfig = ColumnConfigGenerator
					.<Date> create(columnDefinition);
			columnConfig.setCellDate(new AbstractCell<Date>() {

				@Override
				public void render(
						com.google.gwt.cell.client.Cell.Context context,
						Date value, SafeHtmlBuilder sb) {

					DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");
					String v = String.valueOf(sdf.format(value));
					sb.appendEscaped(v);

				}
			});
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			ssb.textAlign(TextAlign.CENTER);
			break;
		case DOUBLE:
			columnConfig = ColumnConfigGenerator
					.<Double> create(columnDefinition);
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			ssb.textAlign(TextAlign.RIGHT);
			break;
		case INTEGER:
			columnConfig = ColumnConfigGenerator
					.<Integer> create(columnDefinition);
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			ssb.textAlign(TextAlign.RIGHT);
			break;
		case LONG:
			columnConfig = ColumnConfigGenerator
					.<Long> create(columnDefinition);
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			ssb.textAlign(TextAlign.RIGHT);
			break;
		case GEOMETRY:
			columnConfig = ColumnConfigGenerator
					.<String> create(columnDefinition);
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			break;
		case STRING:
			columnConfig = ColumnConfigGenerator
					.<String> create(columnDefinition);
			safeStyle = columnConfig.getColumnStyle();
			ssb.append(safeStyle);
			break;
		default:
			return null;
		}
		
		
		if(columnDefinition.getType()==null){
			
		} else {
			switch(columnDefinition.getType()){
			case COLUMNID:
				break;
			case DIMENSION:
				break;
			case CODE:
				break;
			case MEASURE:
				ssb.trustedBackgroundColor("#90CB8B");
				break;
			case SYSTEM:
				break;
			case TIMEDIMENSION:
				break;
			case USER:
				break;
			case VALIDATION:
				ssb.trustedBackgroundColor("#f6e681");
				break;
			case VIEWCOLUMN_OF_DIMENSION:
				ssb.trustedBackgroundColor("#c3e1fc");
				break;
			case VIEWCOLUMN_OF_TIMEDIMENSION:
				ssb.trustedBackgroundColor("#CFC3F7");
				break;
			default:
				break;
			
			}
		}
	
	
		columnConfig.setColumnStyle(ssb.toSafeStyles());

		columnConfig.setHidden(!columnDefinition.isVisible());
		SafeHtmlBuilder tooltipMessage = new SafeHtmlBuilder();
		String local = "";
		if (columnDefinition.getLocale() != null
				&& !columnDefinition.getLocale().isEmpty()) {
			local = " [" + columnDefinition.getLocale() + "] ";
		}

		tooltipMessage
				.appendHtmlConstant("<p align='Left'><bold style='font-weight:bold;'>"
						+ SafeHtmlUtils.htmlEscape(columnDefinition.getLabel())
						+ "</bold><BR>"
						+ "<em style='text-decoration:underline;'>"
						+ SafeHtmlUtils.htmlEscape(columnDefinition
								.getColumnTypeName())
						+ "</em><bold>"
						+ SafeHtmlUtils.htmlEscape(local)
						+ "</bold><BR>"
						+ "<em>"
						+ SafeHtmlUtils.htmlEscape(columnDefinition
								.getColumnDataType())
						+ "</em><BR>"
						+ SafeHtmlUtils.htmlEscape(columnDefinition
								.getTooltipMessage()) + "</p>");
		columnConfig.setToolTip(tooltipMessage.toSafeHtml());

		return columnConfig;
	}

	protected static <T> DataRowColumnConfig<T> create(
			ColumnDefinition columnDefinition) {
		return new DataRowColumnConfig<T>(columnDefinition);
	}

	public static void setEditor(GridInlineEditing<DataRow> editableGrid,
			ColumnConfig<DataRow, ?> columnConfig) {
		if (!(columnConfig instanceof DataRowColumnConfig<?>))
			throw new IllegalArgumentException(
					"Expected ColumnConfig type JSonValueColumnConfig");
		setEditor(editableGrid, (DataRowColumnConfig<?>) columnConfig);
	}

	@SuppressWarnings("unchecked")
	public static void setEditor(GridInlineEditing<DataRow> editableGrid,
			DataRowColumnConfig<?> columnConfig) {
		if (columnConfig.getDefinition().isEditable()) {
			switch (columnConfig.getDefinition().getValueType()) {
			case BOOLEAN:
				editableGrid.addEditor(
						(ColumnConfig<DataRow, Boolean>) columnConfig,
						new CheckBox());
				break;
			case DATE:
				editableGrid.addEditor(
						(ColumnConfig<DataRow, Date>) columnConfig,
						new DateField());
				break;
			case DOUBLE:
				editableGrid
						.addEditor(
								(ColumnConfig<DataRow, Double>) columnConfig,
								new NumberField<Double>(
										new NumberPropertyEditor.DoublePropertyEditor()));
				break;

			case INTEGER:
				editableGrid
						.addEditor(
								(ColumnConfig<DataRow, Integer>) columnConfig,
								new NumberField<Integer>(
										new NumberPropertyEditor.IntegerPropertyEditor()));
				break;
			case LONG:
				editableGrid.addEditor(
						(ColumnConfig<DataRow, Long>) columnConfig,
						new NumberField<Long>(
								new NumberPropertyEditor.LongPropertyEditor()));
				break;
			case GEOMETRY:
				editableGrid.addEditor(
						(ColumnConfig<DataRow, String>) columnConfig,
						new TextField());
				break;
			case STRING:
				editableGrid.addEditor(
						(ColumnConfig<DataRow, String>) columnConfig,
						new TextField());
				break;
			default:
				break;
			}
		}
	}

}
