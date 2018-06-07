package org.gcube.data.analysis.tabulardata.query;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@Singleton
public class TabularQueryFactory {

	private TabularQueryUtils tabularQueryUtils;

	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private CubeManager cubeManager;

	@Inject
	public TabularQueryFactory(TabularQueryUtils tabularQueryUtils, SQLExpressionEvaluatorFactory evaluatorFactory, CubeManager cubeManager) {
		this.tabularQueryUtils = tabularQueryUtils;
		this.evaluatorFactory = evaluatorFactory;
		this.cubeManager = cubeManager;
	}

	public TabularQuery get(Table table) {
		return new TabularQueryImpl(tabularQueryUtils, evaluatorFactory, table);
	}
	
	public TabularQuery get(TableId tableId){
		Table table;
		try {
			table = cubeManager.getTable(tableId);
		} catch (NoSuchTableException e) {
			throw new IllegalArgumentException("Unable to create query, a table with the given id does not exists.",e);
		}
		return new TabularQueryImpl(tabularQueryUtils, evaluatorFactory, table);
	}

}
