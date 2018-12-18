package org.gcube.data.analysis.tabulardata.query.json;

import java.sql.Date;
import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.query.TabularQuery;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularJSONQueryImpl implements TabularJSONQuery {

	private Logger log = LoggerFactory.getLogger(TabularJSONQueryImpl.class);

	private TabularQuery tabularQuery;

	public TabularJSONQueryImpl(TabularQuery tabularQuery) {
		super();
		this.tabularQuery = tabularQuery;
	}

	@Override
	public TabularJSONQuery setFilter(QueryFilter filter) {
		tabularQuery.setFilter(filter);
		return this;
	}
	
	@Override
	public TabularJSONQuery setSelection(QuerySelect selection) {
		tabularQuery.setSelection(selection);
		return this;
	}

	@Override
	public TabularJSONQuery setGrouping(QueryGroup grouping) {
		tabularQuery.setGrouping(grouping);
		return this;
	}

	@Override
	public TabularJSONQuery setOrdering(QueryOrder ordering) {
		tabularQuery.setOrdering(ordering);
		return this;
	}

	@Override
	public int getTotalTuples() {
		return tabularQuery.getTotalTuples();
	}

	@Override
	public String getPage(QueryPage page) {
		Iterator<Object[]> queryResult = tabularQuery.getPage(page);
		return generateJSONFromData(queryResult);

	}

	@Override
	public String getAll() {
		Iterator<Object[]> queryResult = tabularQuery.getAll();
		return generateJSONFromData(queryResult);
	}

	private String generateJSONFromData(Iterator<Object[]> dataIterator) {
		JSONObject json = new JSONObject();
		JSONArray jsonRows = new JSONArray();
		while (dataIterator.hasNext()) {
			Object[] row = dataIterator.next();
			Object[] modifiedRow = new Object[row.length];
			
			for (int i = 0; i<row.length; i++){
				if (row[i] instanceof Date ){
					modifiedRow[i] = ((Date) row[i]).getTime();
				} else if (row[i] instanceof PGgeometry )
						modifiedRow[i] = ((PGgeometry) row[i]).getValue();
					else modifiedRow[i] = row[i];
			}
			
			jsonRows.put(modifiedRow);
		}
		try {
			json.put("rows", jsonRows);
		} catch (JSONException e) {
			log.error("Unable to produce JSON document.", e);
			throw new RuntimeException("Error occured with serialization of table content. Check server log.");
		}
		return json.toString();
	}

	

}
