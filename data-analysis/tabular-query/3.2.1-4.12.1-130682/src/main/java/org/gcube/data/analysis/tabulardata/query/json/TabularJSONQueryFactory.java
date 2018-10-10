package org.gcube.data.analysis.tabulardata.query.json;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.TabularQueryFactory;

@Singleton
public class TabularJSONQueryFactory {
	
	TabularQueryFactory tabularQueryFactory;

	@Inject
	public TabularJSONQueryFactory(TabularQueryFactory tabularQueryFactory) {
		super();
		this.tabularQueryFactory = tabularQueryFactory;
	}

	public TabularJSONQuery get(TableId tableId) {
		return new TabularJSONQueryImpl(tabularQueryFactory.get(tableId));
	}

}
