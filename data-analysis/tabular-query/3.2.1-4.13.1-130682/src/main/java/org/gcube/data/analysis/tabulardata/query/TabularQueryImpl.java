package org.gcube.data.analysis.tabulardata.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.query.sql.SQLResultSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularQueryImpl implements TabularQuery {

	private static Logger log = LoggerFactory.getLogger(TabularQueryImpl.class);

	//private static final int COUNT_LIMIT = 100000;
	
	private TabularQueryUtils queryUtils;
	private Table table;

	//Query params
	private QuerySelect select = null;
	private QueryFilter filter = null;
	private QueryOrder ordering = null;
	private QueryGroup grouping = null;
	private QueryPage page;

	private QueryBuilder queryBuilder = new QueryBuilder();

	private SQLExpressionEvaluatorFactory evaluatorFactory;

	public TabularQueryImpl(TabularQueryUtils queryUtils, SQLExpressionEvaluatorFactory evaluatorFactory, Table table) {
		super();
		this.queryUtils = queryUtils;
		this.evaluatorFactory = evaluatorFactory;
		this.table = table;
	}

	@Override
	public TabularQuery setFilter(QueryFilter filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public TabularQuery setOrdering(QueryOrder ordering) {
		this.ordering = ordering;
		return this;
	}

	/**
	 * @param grouping the grouping to set
	 */
	public TabularQuery setGrouping(QueryGroup grouping) {
		this.grouping = grouping;
		return this;
	}

	/**
	 * @param select the select to set
	 */
	public TabularQuery setSelection(QuerySelect selection) {
		this.select = selection;
		return this;
	}

	@Override
	public int getTotalTuples() {
		return executeCountOnTotalTouple();
	}
	
	private int executeCountOnTotalTouple(){
		String sql = queryBuilder.buildCountTuplesQuery();
		ResultSet rs = queryUtils.executeSQLQuery(sql);
		int totalTuples = parseGetTotalTuplesQueryResult(rs); 
		return totalTuples;
	}

	@Override
	public Iterator<Object[]> getPage(QueryPage page) {
		this.page = page;
		return executeQuery();
	}

	@Override
	public Iterator<Object[]> getAll() {
		this.page=null;
		return executeQuery();
	}

	private Iterator<Object[]> executeQuery(){
		String query = queryBuilder.buildQuery();

		return new SQLResultSetIterator(queryUtils.executeSQLQuery(query), table);
	}



	//	private void checkIfOrderingColumnExists() {
	//		if (ordering==null)return;
	//		try {
	//			table.getColumnById(ordering.getOrderingColumnId());
	//		} catch (Exception e) {
	//			String msg = String.format("Provided ordering column name '%s' is not valid within table %s.",
	//					ordering.getOrderingColumnId(), table);
	//			log.error(msg);
	//			throw new IllegalArgumentException(msg);
	//		}
	//	}

	@Override
	public String toString() {
		return queryBuilder.buildQuery();
	}

	private int parseGetTotalTuplesQueryResult(ResultSet rs) {
		try {
			rs.next();
			int result = rs.getInt(1);
			DbUtils.closeQuietly(rs);
			return result;
		} catch (SQLException e) {
			log.error("An error occurred while querying the database.", e);
			throw new RuntimeException("An error occurred while querying the database. Check server logs");
		}
	}

	private class QueryBuilder {

		private String buildQuery() {
			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append(getQuerySelectPart());
			queryBuilder.append(getQueryFilterPart());
			queryBuilder.append(getQueryGroupingPart());
			queryBuilder.append(getQueryOrderPart());
			queryBuilder.append(getLimitQueryPart());
			queryBuilder.append(";");

			return queryBuilder.toString();
		}

		private String getQueryGroupingPart() {
			if (grouping==null || grouping.getColumns().isEmpty()) return "";
			return String.format("GROUP BY %s ", buildColumnCommaSeparatedList(grouping.getColumns()));
		}

		private String getQuerySelectPart() {
			String columns = "";
			if (select!=null)
				columns = buildQueryColumnCommaSeparatedList(select.getColumns());
			else columns = buildColumnNameCommaSeparatedList(table.getColumns());
			return String.format("SELECT %s FROM %s ", columns, table.getName());
		}

		private String getQueryFilterPart() {
			if (filter==null) return "";
			String whereCondition;
			try {
				whereCondition = evaluatorFactory.getEvaluator(filter.getFilterExpression()).evaluate();
			} catch (EvaluatorException e) {
				log.warn("Unable to evaluate expression, skipping WHERE clause.",e);
				return "";
			}
			return String.format(" WHERE %s ", whereCondition);
		}

		private String getQueryOrderPart() {
			if (ordering==null) return "";
			Column orderingColumn;
			try {
				orderingColumn = table.getColumnById(ordering.getOrderingColumnId());
			} catch (NoSuchColumnException e) {
				String msg = String.format("Provided ordering column name '%s' does not exists within table %s.",
						ordering.getOrderingColumnId(), table);
				log.warn(msg + "\nSkipping ordering clause.");
				return "";
			}
			return String.format(" ORDER BY %s %s ", orderingColumn.getName(), ordering
					.getOrderingDirection().getSQLKeyword());
		}

		private String getLimitQueryPart() {
			if (page==null) return "";
			return String.format(" LIMIT %s OFFSET %s ", page.getPageSize(), page.getOffset());
		}

		private String buildQueryColumnCommaSeparatedList(Collection<QueryColumn> columns) {
			StringBuilder columnNames = new StringBuilder();
			int i =0;
			System.out.println("size of select columns is "+columns.size());
			for (QueryColumn c : columns) {
				Column col = table.getColumnById(c.getColumnLocalId());

				switch (c.getFunction()) {
				case COUNT:
					columnNames.append("count(").append(col.getName()).append(")");
					break;
				case MAX:
					columnNames.append("max(").append(col.getName()).append(")");
					break;
				case MIN:
					columnNames.append("min(").append(col.getName()).append(")");
					break;
				case AVERAGE:
					columnNames.append("avg(").append(col.getName()).append(")");
					break;
				default:
					columnNames.append(col.getName());
					break;
				}
				if (++i < columns.size())
					columnNames.append(",");
			}
			return columnNames.toString();
		}

		private String buildColumnCommaSeparatedList(Collection<ColumnLocalId> columns) {
			StringBuilder columnNames = new StringBuilder();
			int i =0;
			for (ColumnLocalId cId : columns) {
				Column c = table.getColumnById(cId);
				columnNames.append(c.getName());
				if (++i < columns.size())
					columnNames.append(",");
			}
			return columnNames.toString();
		}

		private String buildColumnNameCommaSeparatedList(Collection<Column> columns) {
			StringBuilder columnNames = new StringBuilder();
			int i =0;
			for (Column c : columns) {
				columnNames.append(c.getName());
				if (++i < columns.size())
					columnNames.append(",");
			}
			return columnNames.toString();
		}

		private String buildCountTuplesQuery() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(buildSelectCountFirstPart());
			stringBuilder.append(getQueryFilterPart());
			stringBuilder.append(";");
			return stringBuilder.toString();

		}

		private String buildSelectCountFirstPart(){
			return String.format("SELECT COUNT(*) FROM %s ", table.getName());
		}

	}

}
