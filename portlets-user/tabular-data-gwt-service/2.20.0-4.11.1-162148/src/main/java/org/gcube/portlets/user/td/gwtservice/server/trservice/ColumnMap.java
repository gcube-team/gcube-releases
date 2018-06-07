package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionColumnsMapping;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ColumnMap {
	private static Logger logger = LoggerFactory.getLogger(ColumnMap.class);

	public ColumnMap() {

	}

	public ArrayList<Map<String, Object>> genColumnMap(UnionSession unionSession)
			throws TDGWTServiceException {
		try {
			ArrayList<Map<String, Object>> composit = new ArrayList<Map<String, Object>>();
			logger.debug("UnionSession: " + unionSession);
			if (unionSession.getColumnsMatch() == null) {
				logger.debug("No columns match present");
				return composit;
			}
			TRId sourceTRId = unionSession.getTrId();
			TableId sourceTableId;
			if (sourceTRId.isViewTable()) {
				sourceTableId = new TableId(new Long(
						sourceTRId.getReferenceTargetTableId()));
			} else {
				sourceTableId = new TableId(new Long(sourceTRId.getTableId()));
			}

			TabResource unionTR = unionSession.getUnionTabularResource();
			TRId targetTRId = unionTR.getTrId();
			TableId targetTableId;
			if (targetTRId.isViewTable()) {
				targetTableId = new TableId(new Long(
						targetTRId.getReferenceTargetTableId()));
			} else {
				targetTableId = new TableId(new Long(targetTRId.getTableId()));
			}

			ArrayList<UnionColumnsMapping> columnMatch = unionSession
					.getColumnsMatch();
			ColumnData sourceColumn;
			ColumnData targetColumn;

			for (UnionColumnsMapping umap : columnMatch) {
				sourceColumn = umap.getSourceColumn();
				targetColumn = umap.getTargetColumn();
				//logger.debug("SourceColumn: " + sourceColumn);
				//logger.debug("TargetColumn: " + targetColumn);
				
				//On service source and target are reversed
				logger.debug("On service sourceColumn and targetColumn are reversed");
				logger.debug("Service SourceColumn: " + targetColumn);
				logger.debug("Service TargetColumn: " + sourceColumn);

				if (sourceColumn != null && targetColumn != null) {

					ColumnLocalId sourceColumnId = new ColumnLocalId(
							sourceColumn.getColumnId());
					ColumnReference sourceColumnRef = new ColumnReference(
							sourceTableId, sourceColumnId);

					ColumnLocalId targetColumnId = new ColumnLocalId(
							targetColumn.getColumnId());
					ColumnReference targetColumnRef = new ColumnReference(
							targetTableId, targetColumnId);

					Map<String, Object> colMap = new HashMap<String, Object>();
					colMap.put(Constants.PARAMETER_UNION_COMPOSITE_SOURCE,
							targetColumnRef);
					colMap.put(Constants.PARAMETER_UNION_COMPOSITE_TARGET,
							sourceColumnRef);
					composit.add(colMap);

				}
			}

			return composit;

		} catch (Throwable e) {
			logger.debug("Error in ColumnMap: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in columns map: "
					+ e.getLocalizedMessage());

		}
	}
	
	


}
