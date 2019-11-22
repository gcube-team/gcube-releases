package org.gcube.portlets.user.tdwx.datasource.td.filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Soundex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.portlets.user.tdwx.datasource.td.TDXDataSource;
import org.gcube.portlets.user.tdwx.datasource.td.map.DataTypeMap;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.shared.FilterInformation;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class FiltersBuilder {

	protected ArrayList<FilterInformation> filters;
	protected ArrayList<StaticFilterInformation> staticFilters;
	protected TableDefinition tableDefinition;

	protected Table serviceTable;
	protected Logger logger = LoggerFactory.getLogger(TDXDataSource.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 * @param filters
	 * @param tableDefinition
	 */
	public FiltersBuilder(ArrayList<FilterInformation> filters,
			ArrayList<StaticFilterInformation> staticFilters,
			TableDefinition tableDefinition, Table serviceTable) {
		this.filters = filters;
		this.staticFilters = staticFilters;
		this.tableDefinition = tableDefinition;
		this.serviceTable = serviceTable;
	}

	/**
	 * 
	 * @return
	 * @throws DataSourceXException
	 */
	protected Expression createExpression() throws DataSourceXException {
		try {

			Expression exp = null;

			ArrayList<Expression> andExp = new ArrayList<Expression>();

			if (filters != null) {
				for (FilterInformation filter : filters) {
					switch (filter.getFilterType()) {
					case "string":
						if (tableDefinition.getColumns().get(
								filter.getFilterField()) == null) {
							logger.error("The specified filter column \""
									+ filter.getFilterField()
									+ "\" don't exists");

							throw new DataSourceXException(
									"The specified filter column \""
											+ filter.getFilterField()
											+ "\" don't exists");
						} else {
							ColumnDefinition columnDefinition = tableDefinition
									.getColumns().get(filter.getFilterField());
							Column column = serviceTable
									.getColumnByName(columnDefinition.getId());
							Expression ex = getExpressionText(column, filter);
							if (ex != null) {
								andExp.add(ex);
							}

						}
						break;
					case "numeric":
						if (tableDefinition.getColumns().get(
								filter.getFilterField()) == null) {
							logger.error("The specified filter column \""
									+ filter.getFilterField()
									+ "\" don't exists");

							throw new DataSourceXException(
									"The specified filter column \""
											+ filter.getFilterField()
											+ "\" don't exists");
						} else {
							ColumnDefinition columnDefinition = tableDefinition
									.getColumns().get(filter.getFilterField());
							Column column = serviceTable
									.getColumnByName(columnDefinition.getId());

							Expression ex = getExpressionNumeric(column, filter);
							if (ex != null) {
								andExp.add(ex);
							}
						}
						break;
					case "boolean":
						if (tableDefinition.getColumns().get(
								filter.getFilterField()) == null) {
							logger.error("The specified filter column \""
									+ filter.getFilterField()
									+ "\" don't exists");

							throw new DataSourceXException(
									"The specified filter column \""
											+ filter.getFilterField()
											+ "\" don't exists");
						} else {
							ColumnDefinition columnDefinition = tableDefinition
									.getColumns().get(filter.getFilterField());
							Column column = serviceTable
									.getColumnByName(columnDefinition.getId());

							Equals contains = new Equals(new ColumnReference(
									serviceTable.getId(), column.getLocalId()),
									new TDBoolean(new Boolean(filter
											.getFilterValue())));
							andExp.add(contains);
						}

						break;

					case "date":
						if (tableDefinition.getColumns().get(
								filter.getFilterField()) == null) {
							logger.error("The specified filter column \""
									+ filter.getFilterField()
									+ "\" don't exists");

							throw new DataSourceXException(
									"The specified filter column \""
											+ filter.getFilterField()
											+ "\" don't exists");
						} else {
							ColumnDefinition columnDefinition = tableDefinition
									.getColumns().get(filter.getFilterField());
							Column column = serviceTable
									.getColumnByName(columnDefinition.getId());
							Expression ex = getExpressionDate(column, filter);
							if (ex != null) {
								andExp.add(ex);
							}
						}

						break;
					default:
						break;

					}
				}
			}

			if (staticFilters != null) {
				for (StaticFilterInformation staticFilter : staticFilters) {
					if (tableDefinition.getColumns().get(
							staticFilter.getColumnName()) == null) {
						logger.error("Static Filter not applicable, the specified column \""
								+ staticFilter.getColumnName()
								+ "\" don't exists");

						throw new DataSourceXException(
								"Static Filter not applicable, the specified column \""
										+ staticFilter.getColumnName()
										+ "\" don't exists");
					} else {
						ColumnDefinition columnDefinition = tableDefinition
								.getColumns().get(staticFilter.getColumnName());
						Column column = serviceTable
								.getColumnById(new ColumnLocalId(
										columnDefinition.getColumnLocalId()));
						Expression e = createStaticExpression(staticFilter,
								serviceTable.getId(), column);
						if (e != null) {
							andExp.add(e);
						}
					}
				}
			}

			if (andExp.size() > 0) {
				if (andExp.size() == 1) {
					exp = andExp.get(0);
				} else {
					And andE = new And(andExp);
					exp = andE;
				}
			}

			logger.debug("Filter Expression created: " + exp);

			return exp;

		} catch (Throwable e) {
			logger.error("Create expression failed. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new DataSourceXException("Create expression failed. "
					+ e.getLocalizedMessage());
		}

	}

	private Expression createStaticExpression(
			StaticFilterInformation staticFilter, TableId id, Column column)
			throws DataSourceXException {
		try {
			Expression exp = null;
			DataType dataType = column.getDataType();
			ValueType valueType = DataTypeMap.getValueType(dataType);
			switch (valueType) {
			case BOOLEAN:
				TDBoolean bool = new TDBoolean(new Boolean(
						staticFilter.getFilterValue()));
				exp = new Equals(new ColumnReference(id, column.getLocalId()),
						bool);
				break;
			case DATE:
				try {
					Date dd = sdf.parse(staticFilter.getFilterValue());
					TDDate tdDate = new TDDate(dd);
					exp = new Equals(new ColumnReference(id,
							column.getLocalId()), tdDate);
				} catch (ParseException e) {
					logger.error("Static Filter has not valid value type: "
							+ staticFilter);
					e.printStackTrace();
				}
				break;
			case DOUBLE:
				TDNumeric doublenum = new TDNumeric(new Double(
						staticFilter.getFilterValue()));
				exp = new Equals(new ColumnReference(id, column.getLocalId()),
						doublenum);
				break;
			case INTEGER:
				TDInteger intnum = new TDInteger(new Integer(
						staticFilter.getFilterValue()));
				exp = new Equals(new ColumnReference(id, column.getLocalId()),
						intnum);
				break;
			case LONG:
				TDInteger longnum = new TDInteger(new Integer(
						staticFilter.getFilterValue()));
				exp = new Equals(new ColumnReference(id, column.getLocalId()),
						longnum);
				break;
			case STRING:
				exp = new TextContains(new ColumnReference(id,
						column.getLocalId()), new TDText(
						staticFilter.getFilterValue()));
				break;
			case GEOMETRY:
				exp = new TextContains(new ColumnReference(id,
						column.getLocalId()), new TDText(
						staticFilter.getFilterValue()));
				break;
			default:
				break;

			}

			return exp;
		} catch (Throwable e) {
			logger.error("Create Static Filter expression failed :"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new DataSourceXException(
					"Create Static Filter expression failed :"
							+ e.getLocalizedMessage());
		}
	}

	protected Expression getExpressionDate(Column column,
			FilterInformation filter) throws DataSourceXException {
		// [{ "filterField":"kqljyp" ,"filterType":"date"
		// ,"filterComparison":"on" ,"filterValue": "1399370400000" }]
		Expression exp = null;

		Date dd = new Date();
		Long day = null;

		try {
			day = new Long(filter.getFilterValue());
		} catch (NumberFormatException e) {
			logger.error("No valid filterValue for date: "
					+ filter.getFilterValue());
			throw new DataSourceXException("No valid filter value");
		}

		dd.setTime(day);
		TDDate tdDate = new TDDate(dd);

		switch (filter.getFilterComparison()) {
		case "on":
			exp = new Equals(new ColumnReference(serviceTable.getId(),
					column.getLocalId()), tdDate);
			break;
		case "after":
			exp = new GreaterThan(new ColumnReference(serviceTable.getId(),
					column.getLocalId()), tdDate);
			break;
		case "before":
			exp = new LessThan(new ColumnReference(serviceTable.getId(),
					column.getLocalId()), tdDate);
			break;
		default:
			break;

		}
		return exp;
	}

	protected Expression getExpressionText(Column column,
			FilterInformation filter) {
		Expression exp = null;


		String comparison=filter.getFilterComparison();
		
		if(comparison!=null&& comparison.isEmpty()){
			return exp;
		}
		
		switch(comparison){
		case "contains":
			TextContains contains = new TextContains(
					new ColumnReference(serviceTable.getId(),
							column.getLocalId()), new TDText(
							filter.getFilterValue()));
			exp=contains;
			break;
		case "begins":
			TextBeginsWith begins = new TextBeginsWith(
					new ColumnReference(serviceTable.getId(),
							column.getLocalId()), new TDText(
							filter.getFilterValue()));
			exp=begins;
			break;	
		case "ends":
			TextEndsWith ends = new TextEndsWith(
					new ColumnReference(serviceTable.getId(),
							column.getLocalId()), new TDText(
							filter.getFilterValue()));
			exp=ends;
			break;		
		case "soundex":
			Soundex soundexCol= new Soundex(new ColumnReference(serviceTable.getId(),
							column.getLocalId()));
			Soundex soundexVal= new Soundex(new TDText(
							filter.getFilterValue()));
			Equals eq=new Equals(soundexCol, soundexVal);
			exp=eq;
		default:
			break;
		
		}
		
		return exp;
	}

	protected Expression getExpressionNumeric(Column column,
			FilterInformation filter) {
		Expression exp = null;

		ValueType vt = DataTypeMap.getValueType(column.getDataType());

		switch (vt) {
		case DOUBLE:
			TDNumeric floatnum = new TDNumeric(new Double(
					filter.getFilterValue()));
			if (filter.getFilterComparison().compareTo("eq") == 0) {
				exp = new Equals(new ColumnReference(serviceTable.getId(),
						column.getLocalId()), floatnum);
			} else {
				if (filter.getFilterComparison().compareTo("gt") == 0) {
					exp = new GreaterThan(new ColumnReference(
							serviceTable.getId(), column.getLocalId()),
							floatnum);
				} else {
					if (filter.getFilterComparison().compareTo("lt") == 0) {
						exp = new LessThan(new ColumnReference(
								serviceTable.getId(), column.getLocalId()),
								floatnum);
					} else {

					}

				}

			}
			break;
		case INTEGER:
			TDInteger intnum = new TDInteger(new Integer(
					filter.getFilterValue()));
			if (filter.getFilterComparison().compareTo("eq") == 0) {
				exp = new Equals(new ColumnReference(serviceTable.getId(),
						column.getLocalId()), intnum);
			} else {
				if (filter.getFilterComparison().compareTo("gt") == 0) {
					exp = new GreaterThan(new ColumnReference(
							serviceTable.getId(), column.getLocalId()), intnum);
				} else {
					if (filter.getFilterComparison().compareTo("lt") == 0) {
						exp = new LessThan(new ColumnReference(
								serviceTable.getId(), column.getLocalId()),
								intnum);
					} else {

					}
				}
			}
			break;
		case LONG:
			TDInteger longnum = new TDInteger(new Integer(
					filter.getFilterValue()));
			if (filter.getFilterComparison().compareTo("eq") == 0) {
				exp = new Equals(new ColumnReference(serviceTable.getId(),
						column.getLocalId()), longnum);
			} else {
				if (filter.getFilterComparison().compareTo("gt") == 0) {
					exp = new GreaterThan(new ColumnReference(
							serviceTable.getId(), column.getLocalId()), longnum);
				} else {
					if (filter.getFilterComparison().compareTo("lt") == 0) {
						exp = new LessThan(new ColumnReference(
								serviceTable.getId(), column.getLocalId()),
								longnum);
					} else {

					}
				}
			}
			break;
		default:
			break;

		}
		return exp;
	}

	/**
	 * 
	 * @return
	 * @throws DataSourceXException
	 */
	public QueryFilter createQueryFilter() throws DataSourceXException {
		QueryFilter queryFilter = null;
		Expression exp = createExpression();
		if (exp != null) {
			queryFilter = new QueryFilter(exp);
		}
		logger.debug("QueryFilter: " + queryFilter);
		return queryFilter;
	}

}
