package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistTargetColumn;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ExtractCodelistOperationMap {
	private static Logger logger = LoggerFactory
			.getLogger(ExtractCodelistOperationMap.class);

	public ExtractCodelistOperationMap() {

	}

	public ArrayList<Map<String, Object>> genMap(
			ExtractCodelistSession extractCodelistSession) {
		logger.debug(extractCodelistSession.toString());
		ArrayList<Map<String, Object>> composit = new ArrayList<Map<String, Object>>();

		TRId trId = extractCodelistSession.getTrId();
		TableId tableId;
		if (trId.isViewTable()) {
			tableId = new TableId(new Long(trId.getReferenceTargetTableId()));
		} else {
			tableId = new TableId(new Long(trId.getTableId()));
		}

		ArrayList<ExtractCodelistTargetColumn> targetColumns = extractCodelistSession
				.getTargetColumns();
		for (ExtractCodelistTargetColumn target : targetColumns) {
			HashMap<String, Object> mapCol = new HashMap<String, Object>();

			ColumnData sourceColumn = target.getSourceColumn();
			ColumnLocalId columnSourceId = new ColumnLocalId(
					sourceColumn.getColumnId());
			ColumnReference source = new ColumnReference(tableId,
					columnSourceId);
			if (target.isNewColumn()) {
				HashMap<String, Object> column_definition = new HashMap<String, Object>();
				ColumnMockUp defNewColumn = target.getDefColumn();
				ColumnTypeCode columnTypeCode = defNewColumn.getColumnType();
				ColumnType columnType = ColumnTypeCodeMap
						.getColumnType(columnTypeCode);
				column_definition.put(
						Constants.PARAMETER_EXTRACT_CODELIST_COLUMN_TYPE,
						columnType);
				
				ArrayList<ColumnMetadata> metadata=new ArrayList<ColumnMetadata>();
				
				if (columnTypeCode == ColumnTypeCode.CODENAME) {
					DataLocaleMetadata dataLocaleMetadata = new DataLocaleMetadata(
							defNewColumn.getLocaleName());
					metadata.add(dataLocaleMetadata);
				}
				if(defNewColumn.getLabel()!=null&&!defNewColumn.getLabel().isEmpty()){
					String label=defNewColumn.getLabel();
					List<LocalizedText> texts=new ArrayList<LocalizedText>();
					texts.add(new ImmutableLocalizedText(label));
					NamesMetadata nameMetadata=new NamesMetadata(texts);
					metadata.add(nameMetadata);
				}

				if(metadata.size()>0){
				column_definition.put(
						Constants.PARAMETER_EXTRACT_CODELIST_METADATA,
						metadata);
				}
				
				TDText defaultValue = new TDText(defNewColumn.getDefaultValue());
				column_definition.put(
						Constants.PARAMETER_EXTRACT_CODELIST_DEFAULT,
						defaultValue);

				mapCol.put(Constants.PARAMETER_EXTRACT_CODELIST_SOURCE, source);
				mapCol.put(
						Constants.PARAMETER_EXTRACT_CODELIST_COLUMN_DEFINITION,
						column_definition);
				composit.add(mapCol);

			} else {
				ColumnData targetColumn = target.getTargetColumn();
				TRId codelistTRId = target.getCodelist();
				TableId codelistTableId;
				if (codelistTRId.isViewTable()) {
					codelistTableId = new TableId(new Long(
							codelistTRId.getReferenceTargetTableId()));
				} else {
					codelistTableId = new TableId(new Long(
							codelistTRId.getTableId()));
				}
				ColumnLocalId targetCodeColumnId = new ColumnLocalId(
						targetColumn.getColumnId());
				ColumnReference targetCodeColumn = new ColumnReference(
						codelistTableId, targetCodeColumnId);
				mapCol.put(Constants.PARAMETER_EXTRACT_CODELIST_SOURCE, source);
				mapCol.put(
						Constants.PARAMETER_EXTRACT_CODELIST_TARGET_CODE_COLUMN,
						targetCodeColumn);
				composit.add(mapCol);
			}
		}

		logger.debug("ExtractCodelist Operation Map: "+composit.toString());
		return composit;
	}
}
