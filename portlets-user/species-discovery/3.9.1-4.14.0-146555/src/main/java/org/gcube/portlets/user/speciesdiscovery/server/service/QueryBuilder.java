package org.gcube.portlets.user.speciesdiscovery.server.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.dataaccess.spql.ParserException;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.portlets.user.speciesdiscovery.shared.Coordinate;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;

public class QueryBuilder {
	
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	protected static Logger logger = Logger.getLogger(QueryBuilder.class);
	
	public static String buildQuery(String searchTerm, SearchType searchType, SearchFilters searchFilters){
		logger.trace("query building...");
		
		StringBuilder query = new StringBuilder();

		query.append("SEARCH BY ");
		
		//ADD search type;
		switch (searchType) {
			case BY_SCIENTIFIC_NAME: query.append("SN "); break;
			case BY_COMMON_NAME: query.append("CN "); break;
			default: break;
		}
		
		//ADD search term;
		query.append('\'');
		query.append(searchTerm);
		query.append("' ");
		
		//ADDED BY FRANCESCO 18/07/2013
		//ADD UNFOLD
		if (searchType == SearchType.BY_SCIENTIFIC_NAME && searchFilters.getListDataSourcesForUnfold()!=null && searchFilters.getListDataSourcesForUnfold().size()>0){
			
			query.append("UNFOLD WITH ");
			
			Iterator<DataSourceModel> dsIterator = searchFilters.getListDataSourcesForUnfold().iterator();
			while(dsIterator.hasNext()) {
				DataSourceModel ds = dsIterator.next();
				query.append(ds.getId());
				if (dsIterator.hasNext()) query.append(", ");
				else query.append(" ");
			}		
		}
		
		
		if (searchType == SearchType.BY_COMMON_NAME) query.append("RESOLVE ");

		
		//ADDED BY FRANCESCO 17/07/2013
		//ADD EXPAND sources;
		if (searchFilters.getListDataSourcesForSynonyms()!=null && searchFilters.getListDataSourcesForSynonyms().size()>0) {
			query.append("EXPAND WITH ");
			
			Iterator<DataSourceModel> dsIterator = searchFilters.getListDataSourcesForSynonyms().iterator();
			while(dsIterator.hasNext()) {
				DataSourceModel ds = dsIterator.next();
				query.append(ds.getId());
				if (dsIterator.hasNext()) query.append(", ");
				else query.append(" ");
			}		
		}
		
		//ADD data sources;
		if (searchFilters.getListDataSources()!=null && searchFilters.getListDataSources().size()>0) {
			query.append("IN ");
			
			Iterator<DataSourceModel> dsIterator = searchFilters.getListDataSources().iterator();
			while(dsIterator.hasNext()) {
				DataSourceModel ds = dsIterator.next();
				query.append(ds.getId());
				if (dsIterator.hasNext()) query.append(", ");
				else query.append(" ");
			}		
		}

		List<String> conditions = createFilterProperties(searchFilters);

		//ADD filters
		if(conditions.size()>0){
			query.append("WHERE ");
			
			Iterator<String> conditionsIterator = conditions.iterator();
			while (conditionsIterator.hasNext()) {
				String condition = conditionsIterator.next();
				query.append(condition);
				
				if (conditionsIterator.hasNext()) query.append(" AND ");
				else  query.append(" ");
			}
		}

		//ADD return type
		query.append("RETURN ");
		if (searchFilters.getResultType()!=null) { 
			switch (searchFilters.getResultType()) {
				case RESULTITEM: query.append("Product HAVING xpath(\"//product[type='Occurrence' and count>0]\")"); break;
				case TAXONOMYITEM: query.append("Taxon"); break; 
			}
		} else {
			query.append("Product HAVING xpath(\"//product[type='Occurrence' and count>0]\")");
		}
		
		String builtQuery = query.toString();
		
//		System.out.println("built query: "+builtQuery);
		
		logger.trace("built query: "+builtQuery);
		
		//FIXME TEST
		try {
			SPQLQueryParser.parse(builtQuery);
		} catch (ParserException e) {
			logger.error("Parsing error: ",e);
//			System.out.println("error parsing");
//			e.printStackTrace();
		}

		return builtQuery;

	}

	protected static List<String> createFilterProperties(SearchFilters searchFilters)
	{
		List<String> conditions = new ArrayList<String>();
		
		if (searchFilters.getUpperBound()!=null) {
			Coordinate coordinate = searchFilters.getUpperBound();
			conditions.add("coordinate <= " + coordinate.getLatitude() + " , "+coordinate.getLongitude());
		}
		
		if (searchFilters.getLowerBound()!=null) {
			Coordinate coordinate = searchFilters.getLowerBound();
			conditions.add("coordinate >= " + coordinate.getLatitude() + " , "+coordinate.getLongitude());
		}

		if (searchFilters.getFromDate()!=null) {
			conditions.add("eventDate >= '" + DATE_FORMAT.format(searchFilters.getFromDate())+"'");
		}
		
		if (searchFilters.getToDate()!=null) {
			conditions.add("eventDate <= '" + DATE_FORMAT.format(searchFilters.getToDate())+"'");
		}

		return conditions;
	}
}
