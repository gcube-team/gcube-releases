/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client.utils;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnTypeCodeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeStore;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class ComboColumnTypeUtils {
	
	
	public static ComboBox<ColumnDataTypeElement> createComboAttributeType(){
		
		// comboAttributeType
		ColumnDataTypeProperties propsAttributeType = GWT.create(ColumnDataTypeProperties.class);
		ListStore<ColumnDataTypeElement> storeComboAttributeType = new ListStore<ColumnDataTypeElement>(propsAttributeType.id());
		storeComboAttributeType.addAll(ColumnDataTypeStore.getAttributeType());

		ComboBox<ColumnDataTypeElement> comboAttributeType = new ComboBox<ColumnDataTypeElement>(storeComboAttributeType, propsAttributeType.label());

		comboAttributeType.setEmptyText("Select an attribute type...");
		comboAttributeType.setWidth(191);
		comboAttributeType.setTypeAhead(true);
		comboAttributeType.setTriggerAction(TriggerAction.ALL);
		
		return comboAttributeType;

	}
	
	public static ComboBox<ColumnDataTypeElement> createComboMeausureType(){
		
		ColumnDataTypeProperties propsMeasureType = GWT.create(ColumnDataTypeProperties.class);
		ListStore<ColumnDataTypeElement> storeComboMeasureType = new ListStore<ColumnDataTypeElement>(propsMeasureType.id());
		storeComboMeasureType.addAll(ColumnDataTypeStore.getMeasureType());

		ComboBox<ColumnDataTypeElement> comboMeasureType = new ComboBox<ColumnDataTypeElement>(storeComboMeasureType, propsMeasureType.label());

		comboMeasureType.setEmptyText("Select a measure type...");
		comboMeasureType.setWidth(191);
		comboMeasureType.setTypeAhead(true);
		comboMeasureType.setTriggerAction(TriggerAction.ALL);

		
		return comboMeasureType;
	}
	
	public static ComboBox<ColumnTypeCodeElement> createComboColumType(TRId trId){

		ColumnTypeCodeProperties propsColumnTypeCode = GWT.create(ColumnTypeCodeProperties.class);
		ListStore<ColumnTypeCodeElement> storeComboTypeCode = new ListStore<ColumnTypeCodeElement>(propsColumnTypeCode.id());
		ArrayList<ColumnTypeCodeElement> columnTypes = ColumnTypeCodeStore.getColumnTypeCodes(trId);
		ArrayList<ColumnTypeCodeElement> skipped = new ArrayList<ColumnTypeCodeElement>(columnTypes.size());
		for (ColumnTypeCodeElement columnTypeCodeElement : columnTypes) {
			
			if((!columnTypeCodeElement.getCode().equals(ColumnTypeCode.DIMENSION)) && (!columnTypeCodeElement.getCode().equals(ColumnTypeCode.TIMEDIMENSION)))
				skipped.add(columnTypeCodeElement);
		}
			
		storeComboTypeCode.addAll(skipped);

		ComboBox<ColumnTypeCodeElement> comboColumnTypeCode = new ComboBox<ColumnTypeCodeElement>(storeComboTypeCode, propsColumnTypeCode.label());
		Log.trace("ComboColumnTypeCode created");

		comboColumnTypeCode.setEmptyText("Select a column type...");
		comboColumnTypeCode.setWidth(191);
		comboColumnTypeCode.setTypeAhead(true);
		comboColumnTypeCode.setTriggerAction(TriggerAction.ALL);
		
		return comboColumnTypeCode;
	}

}
