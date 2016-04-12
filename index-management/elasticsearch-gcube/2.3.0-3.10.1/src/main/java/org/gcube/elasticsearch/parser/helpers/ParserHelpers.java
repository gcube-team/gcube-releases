package org.gcube.elasticsearch.parser.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;

public class ParserHelpers {

	private static Logger logger = LoggerFactory.getLogger(ParserHelpers.class);
	
	
	public static String translateField(String fieldID, RRadaptor adaptor)
	{
		String fieldName;
		if (adaptor != null)
			try {
				fieldName = adaptor.getFieldNameById(fieldID);
			} catch (Exception e) {
				logger.warn("Exception while translating fieldID "+fieldID);
				return fieldID;
			}
		else
			fieldName = fieldID;
		return fieldName;
	}
	
	public static boolean isBoolAnd(QueryBuilder qb) {
		if (qb instanceof BoolQueryBuilder) {
			try {
				Field f = qb.getClass().getDeclaredField("shouldClauses"); // NoSuchFieldException
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				ArrayList<QueryBuilder> shouldClauses = (ArrayList<QueryBuilder>) f.get(qb); // IllegalAccessException
				return shouldClauses.isEmpty();
			} catch (IllegalArgumentException e) {
				logger.error("Exception",e);
			} catch (IllegalAccessException e) {
				logger.error("Exception",e);
			} catch (NoSuchFieldException e) {
				logger.error("Exception",e);
			} catch (SecurityException e) {
				logger.error("Exception",e);
			}
		}
		return false;
	}
	
	public static boolean isBoolOr(QueryBuilder qb) {
		if (qb instanceof BoolQueryBuilder) {
			try {
				Field f = qb.getClass().getDeclaredField("mustClauses"); // NoSuchFieldException
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				ArrayList<QueryBuilder> mustClauses = (ArrayList<QueryBuilder>) f.get(qb); // IllegalAccessException
				f = qb.getClass().getDeclaredField("mustNotClauses"); // NoSuchFieldException
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				ArrayList<QueryBuilder> mustNotClauses = (ArrayList<QueryBuilder>) f.get(qb); // IllegalAccessException
				return mustClauses.isEmpty() && mustNotClauses.isEmpty();
			} catch (IllegalArgumentException e) {
				logger.error("Exception",e);
			} catch (IllegalAccessException e) {
				logger.error("Exception",e);
			} catch (NoSuchFieldException e) {
				logger.error("Exception",e);
			} catch (SecurityException e) {
				logger.error("Exception",e);
			}
		}
		return false;
	}
	
	private static String getQueryString(QueryStringQueryBuilder qb)
	{
		try {
			Field f = qb.getClass().getDeclaredField("queryString"); // NoSuchFieldException
			f.setAccessible(true);
			String queryString = (String) f.get(qb);
			return queryString;
		} catch (IllegalArgumentException e) {
			logger.error("Exception",e);
		} catch (IllegalAccessException e) {
			logger.error("Exception",e);
		} catch (NoSuchFieldException e) {
			logger.error("Exception",e);
		} catch (SecurityException e) {
			logger.error("Exception",e);
		}
		return null;
	}
	
	
	private static FilterBuilder getFilter(ConstantScoreQueryBuilder qb)
	{
		try {
			Field f = qb.getClass().getDeclaredField("filterBuilder"); // NoSuchFieldException
			f.setAccessible(true);
			FilterBuilder fb = (FilterBuilder) f.get(qb);
			return fb;
		} catch (IllegalArgumentException e) {
			logger.error("Exception",e);
		} catch (IllegalAccessException e) {
			logger.error("Exception",e);
		} catch (NoSuchFieldException e) {
			logger.error("Exception",e);
		} catch (SecurityException e) {
			logger.error("Exception",e);
		}
		return null;
	}
	
	public static QueryBuilder getCombinedQuery(QueryBuilder q1, QueryBuilder q2)
	{
		boolean isBoolq1 = isBoolAnd(q1);
		boolean isBoolq2 = isBoolAnd(q2);

		if(q1 instanceof QueryStringQueryBuilder && q2 instanceof QueryStringQueryBuilder)
		{
			return QueryBuilders.queryString(getQueryString((QueryStringQueryBuilder)q1) + " AND " + getQueryString((QueryStringQueryBuilder)q2));
		}
		else if(isBoolq1 && isBoolq2)
		{
			return ((BoolQueryBuilder)q1).must(q2);
		}
		else if (isBoolq1 && q2 instanceof QueryStringQueryBuilder)
		{
			return ((BoolQueryBuilder)q1).must(q2);
		}
		else if (isBoolq2 && q1 instanceof QueryStringQueryBuilder)
		{
			return ((BoolQueryBuilder)q2).must(q1);
		}
		else if (q1 instanceof ConstantScoreQueryBuilder)
		{
			return QueryBuilders.filteredQuery(q2, getFilter((ConstantScoreQueryBuilder)q1));
		}
		else if (q2 instanceof ConstantScoreQueryBuilder)
		{
			return QueryBuilders.filteredQuery(q1, getFilter((ConstantScoreQueryBuilder)q2));
		}
		else
		{
			return QueryBuilders.boolQuery().must(q1).must(q2);
		} 
	}
	
	
	public static QueryBuilder getCombinedOrQuery(QueryBuilder q1, QueryBuilder q2)
	{
		boolean isBoolOrq1 = isBoolOr(q1);
//		boolean isBoolOrq2 = isBoolOr(q2);
//		if (q1 instanceof QueryStringQueryBuilder && q2 instanceof QueryStringQueryBuilder)
//		{
//			return QueryBuilders.boolQuery().should(q1).should(q2).minimumNumberShouldMatch(1);
//		}
		if (isBoolOrq1)
		{
			return ((BoolQueryBuilder)q1).should(q2);
		}
		else
		{
			return QueryBuilders.boolQuery().should(q1).should(q2).minimumNumberShouldMatch(1);
		} 
	}

}
