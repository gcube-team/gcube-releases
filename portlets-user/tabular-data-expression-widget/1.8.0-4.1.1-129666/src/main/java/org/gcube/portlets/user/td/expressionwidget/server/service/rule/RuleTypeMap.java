package org.gcube.portlets.user.td.expressionwidget.server.service.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.rules.types.BaseColumnRuleType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.DimensionColumnRuleType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleColumnType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleTableType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDBaseColumnRuleType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDDimensionColumnRuleType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleColumnType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleTableType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.DimensionReferenceData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleTypeMap {

	public static RuleType map(TDRuleType tdRuleType) {
		if (tdRuleType == null) {
			return null;
		}

		if (TDRuleColumnType.class.isAssignableFrom(tdRuleType.getClass())) {

			if (tdRuleType instanceof TDBaseColumnRuleType) {
				TDBaseColumnRuleType tdBaseColumnRuleType = (TDBaseColumnRuleType) tdRuleType;
				BaseColumnRuleType baseColumnRuleType = new BaseColumnRuleType(
						ColumnDataTypeMap.map(tdBaseColumnRuleType
								.getColumnDataType()));
				return baseColumnRuleType;

			}

			if (tdRuleType instanceof TDDimensionColumnRuleType) {
				TDDimensionColumnRuleType tdDimensionColumnRuleType = (TDDimensionColumnRuleType) tdRuleType;

				TableId tableId = new TableId(
						Long.valueOf(tdDimensionColumnRuleType
								.getDimensionReferenceData().getTableId()));
				ColumnLocalId columnId = new ColumnLocalId(
						tdDimensionColumnRuleType.getDimensionReferenceData()
								.getColumnId());

				DimensionReference dimensionReference = new DimensionReference(
						tableId, columnId);

				DimensionColumnRuleType dimensionColumnRuleType = new DimensionColumnRuleType(
						dimensionReference);

				return dimensionColumnRuleType;

			}
		} else {
			if (tdRuleType instanceof TDRuleTableType) {
				TDRuleTableType tdRuleTableType = (TDRuleTableType) tdRuleType;
				Map<String, Class<? extends DataType>> ruleColumnPlaceHolderDescriptorMap = 
					new HashMap<String, Class<? extends DataType>>();

				ArrayList<RuleColumnPlaceHolderDescriptor> descriptors = tdRuleTableType.getRuleColumnPlaceHolderDescriptors();
				for (RuleColumnPlaceHolderDescriptor  ruleColumnPlaceHolderDescriptor:descriptors) {
					Class<? extends DataType> dataType=ColumnDataTypeMap.mapToDataTypeClass(ruleColumnPlaceHolderDescriptor.getColumnDataType());
					ruleColumnPlaceHolderDescriptorMap.put(ruleColumnPlaceHolderDescriptor.getLabel(), dataType);
				}
				RuleTableType ruleTableType = new RuleTableType(
						ruleColumnPlaceHolderDescriptorMap);
				return ruleTableType;
			}
		}

		return null;

	}

	public static TDRuleType map(RuleType ruleType) {
		if (ruleType == null) {
			return null;
		}

		if (RuleColumnType.class.isAssignableFrom(ruleType.getClass())) {

			if (ruleType instanceof BaseColumnRuleType) {
				BaseColumnRuleType baseColumnRuleType = (BaseColumnRuleType) ruleType;
				TDBaseColumnRuleType tdBaseColumnRuleType = new TDBaseColumnRuleType(
						ColumnDataTypeMap.map(baseColumnRuleType
								.getInternalType()));
				return tdBaseColumnRuleType;

			}

			if (ruleType instanceof DimensionColumnRuleType) {
				DimensionColumnRuleType dimensionColumnRuleType = (DimensionColumnRuleType) ruleType;

				DimensionReference dimensionReference = (DimensionReference) dimensionColumnRuleType
						.getInternalType();

				DimensionReferenceData dimensionReferenceData = new DimensionReferenceData(
						String.valueOf(dimensionReference.getTableId()
								.getValue()), dimensionReference.getColumnId()
								.getValue());

				TDDimensionColumnRuleType tdDimensionColumnRuleType = new TDDimensionColumnRuleType(
						dimensionReferenceData);

				return tdDimensionColumnRuleType;

			}

		} else {
			if (ruleType instanceof RuleTableType) {
				RuleTableType ruleTableType = (RuleTableType) ruleType;
				Map<String, Class<? extends DataType>> ruleColumnPlaceHolderDescriptorMap = ruleTableType
						.getInternalType();

				ArrayList<RuleColumnPlaceHolderDescriptor> descriptors = new ArrayList<RuleColumnPlaceHolderDescriptor>();
				for (Map.Entry<String, Class<? extends DataType>> entry : ruleColumnPlaceHolderDescriptorMap
						.entrySet()) {
					String id = entry.getKey();
					Class<? extends DataType> dataType = entry.getValue();
					ColumnDataType columnDataType = ColumnDataTypeMap
							.mapFromDataTypeClass(dataType);
					RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor = new RuleColumnPlaceHolderDescriptor(id,
							id, columnDataType);
					descriptors.add(ruleColumnPlaceHolderDescriptor);
				}
				TDRuleTableType tdRuleTableType = new TDRuleTableType(
						descriptors);
				return tdRuleTableType;
			}

		}

		return null;

	}

}
