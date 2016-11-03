package org.gcube.search.sru.db;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.ModifierSet;


public class CqlToSql {

	private static final Logger logger = LoggerFactory
			.getLogger(CqlToSql.class);
	
	List<String> projections = new ArrayList<String>();
	Set<String> tables = new HashSet<String>();
	List<String> columns = new ArrayList<String>();
	
	String sqlQuery;
	
	String whereClause;
	
	Integer limit;
	Integer offset = 0;
	
	String cqlQuery;
	
	String defaultTable;
	
	
	public static void main(String[] args) throws Exception {
		String cql = "books.title == \"mytitle\" or books.author == \"myauthor2\"";
//		cql = "dc.title any fish";
//		cql = "title=\"mytitle\"";
		
		Integer limit = 5;
		Integer offset = null;
		
//		List<String> projections = new ArrayList<String>();
//		projections.add("books.title");
//		projections.add("books.author");
//		
//		Set<String> tables = new HashSet<String>();
//		tables.add("books");
		
		CqlToSql cts = new CqlToSql(cql, limit, offset,"");
		
		//cts.projections = projections;
//		cts.tables = tables;
		//cts.whereClause = cql;
		
		cts.parseQuery();
		
		logger.info("sql : " + cts.getSqlQuery());
		logger.info("sql : " + cts.getSqlCountQuery());
		
		
	}
	
	public CqlToSql(String cqlQuery, Integer limit, Integer offset, String defaultTable) {
		this.cqlQuery = cqlQuery;
		this.limit = limit;
		this.offset = offset;
		this.defaultTable = defaultTable;
	}
	
	public CqlToSql(String cqlQuery, String defaultTable) {
		this(cqlQuery, null, null, defaultTable);
	}
	
	
	private String getSelectCountClause(){
		return "SELECT COUNT(*)";
	}
	
	private String getSelectClause(){
		if (projections == null || this.projections.size() == 0)
			return "SELECT *";
		else {
			return "SELECT " + StringUtils.join(this.projections, ", ");
		}
	}
	
	private String getFromClause() {
		if (this.tables == null || this.tables.size() == 0)
			return "FROM " + this.defaultTable;
		else {
			return "FROM " + StringUtils.join(this.tables, ", "); 
		}
	}
	
	private String getWhereClause() {
		return "WHERE " + whereClause;
	}
	
	private String getLimitClause(){
		if (this.limit == null)
			return "";
		else {
			return "LIMIT " + (this.offset != null ? this.offset : "0") + ", " +  this.limit;
		}
	}
	
	public String getSqlQuery() {
		String sql = 
				this.getSelectClause() + " " +
				this.getFromClause() + " " +
				this.getWhereClause() + " " +
				this.getLimitClause();
		return sql;
	}
	
	public String getSqlCountQuery() {
		String sql = 
				this.getSelectCountClause() + " " +
				this.getFromClause() + " " +
				this.getWhereClause();
		return sql;
	}
	
	
	
	public void parseQuery() throws Exception {
		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(this.cqlQuery);
		this.whereClause = processNode(head);
		
		logger.info("tables      : " + tables);
		logger.info("projections : " + projections);
		logger.info("columns     : " + columns);
	}
	
	
	private String processNode(GCQLNode node) throws Exception{
		
		//cases for the possible node types
		if(node instanceof GCQLProjectNode)
			return processNode((GCQLProjectNode)node);
		if(node instanceof GCQLAndNode)
			return processNode((GCQLAndNode)node);
		if(node instanceof GCQLNotNode)
			return processNode((GCQLNotNode)node);
		if(node instanceof GCQLOrNode)
			return processNode((GCQLOrNode)node);
		if(node instanceof GCQLTermNode)
			return processNode((GCQLTermNode)node);
		
		throw new Exception("This node class is not supported: " + node.getClass().toString());
	}
	
	
	private String processNode(GCQLProjectNode node) throws Exception{
		//add all the projections in the projected fields
		Vector<ModifierSet> nodeProjections = node.getProjectIndexes();
		for(ModifierSet projection : nodeProjections)
		{
			//check if this projection is the wildcard
			if(projection.getBase().equals("*")) {
				
				projections.clear();
				//projectedFields.put(IndexType.WILDCARD, IndexType.WILDCARD);
				return processNode(node.subtree);
			}
			
			//get the field label for this field id
			
//			String fieldLabel = adaptor.getFieldNameById(projection.getBase());
//			String projField = findPresentable(fieldLabel);
			String projField = projection.getBase();
			
			if(projField == null)
			{
				continue;
			}
			projections.add(projField);
		}
		//return the lucene query of the subtree
		return processNode(node.subtree);
	}
	
	private String processNode(GCQLAndNode node) throws Exception{
		String left = processNode(node.left);
		String right = processNode(node.right);
		String result = "(" + left + " AND " + right + ")";
		return result;
	}
	
	
	private String processNode(GCQLOrNode node) throws Exception{
		String left = processNode(node.left);
		String right = processNode(node.right);
		String result = "(" + left + " OR " + right + ")";
		return result;
	}
	
	private String processNode(GCQLNotNode node) throws Exception{
		String left = processNode(node.left);
		String right = processNode(node.right);
		String result = "(" + left + " NOT " + right + ")";
		return result;
	}
	
	private String processNode(GCQLTermNode node) throws Exception{
		String idx = node.getIndex();
		String table = null;
		String column = null;
		
		if (idx.contains(".")) {
			table = idx.substring(0, idx.indexOf("."));
			column = idx;
		} else {
			table = this.defaultTable;
			column = this.defaultTable + "." + idx;
		}
		
		this.tables.add(table);
		this.columns.add(column);
		
		String relation = node.getRelation().getBase();
		String sqlRel = null;
		
		if (relation.equalsIgnoreCase("==") || relation.equalsIgnoreCase("exact")) {
			sqlRel = "=";
		} else 
			sqlRel = relation;
		
		String value = node.getTerm();
		
		return column + " " + sqlRel + " " + value;
	}
}
