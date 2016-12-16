package org.gcube.opensearch.opensearchdatasource.processor;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.Searchable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.OpenSearchDataSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLProxNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.ModifierSet;

public class OpenSearchGcqlProcessor extends GcqlProcessor {

	private static Logger logger = LoggerFactory.getLogger(OpenSearchGcqlProcessor.class.getName());
	private Map<String, String> projectedFields = new LinkedHashMap<String, String>();
	private Map<String, String> searchableExpressions = new HashMap<String, String>();
	
	private String collection = null;
	private String dataSourceLocator = null;
	private List<String> fields = null;
	private GCQLNode parsedQuery = null;
	private GCQLNodeAnnotation queryAnnotation = null;
	private OpenSearchGcqlQueryContainer queryContainer = new OpenSearchGcqlQueryContainer(new HashMap<String, String>());
	private boolean useRR;
	
	public boolean isUseRR() {
		return useRR;
	}

	public void setUseRR(boolean useRR) {
		this.useRR = useRR;
	}

	//TODO change
	public void setCollection(String collection) {
		this.collection = collection;
		this.queryContainer.queries.put(collection, new HashMap<String, ArrayList<OpenSearchGcqlCollectionQuery>>());
		this.queryContainer.queries.get(collection).put("*", new ArrayList<OpenSearchGcqlCollectionQuery>());
		this.queryContainer.queries.get(collection).get("*").add(new OpenSearchGcqlCollectionQuery());
	}
	
	public String getCollection() {
		return this.collection;
	}
	
	public void setDataSourceLocator(String dataSourceLocator) {
		this.dataSourceLocator = dataSourceLocator;
	}
	
	public void setAnnotationTree(GCQLNodeAnnotation annotationTree) {
		this.queryAnnotation = annotationTree;
	}
	
	public void setFields(List<String> fields) {
		this.fields = new ArrayList<String>();
		this.fields.addAll(fields);
	}
	
	public Map<String, String> getProjectedFields() {
		return this.projectedFields;
	}
	
	@Override
	public GCQLNode parseQuery(String gCQLQuery) {
		//use the gCQL parser to get a tree from the String
		this.parsedQuery = GCQLQueryTreeManager.parseGCQLString(gCQLQuery);
		return this.parsedQuery;
	}
	
	
	@Override
	public GcqlQueryContainer processQuery(List<String> presentableFields, List<String> searchableFields) throws Exception {
		//store the presentable and searchable fields
		this.presentableFields = presentableFields;
		this.searchableFields =  searchableFields;
	//	logger.trace("Number of Presentable Fields: " + this.presentableFields.size()
	//			+ ", Number of Searchable Fields: " + this.searchableFields.size());
	//	logger.trace("CQL query: " + gCQLQuery);
		
		//the processNode will run recursively, will retrieve 
		//the projected fields and will generate an opensearch query
		String openSearchQuery = processNode(parsedQuery, queryAnnotation).trim();
		logger.info("Processed OpenSearch query: " + openSearchQuery);
		
		return this.queryContainer;
	}

	private String processNode(GCQLNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		
		//cases for the possible node types
		if(node instanceof GCQLProjectNode)
			return processNode((GCQLProjectNode)node, nodeAnnotation);
		if(node instanceof GCQLAndNode)
			return processNode((GCQLAndNode)node, nodeAnnotation);
		if(node instanceof GCQLNotNode)
			return processNode((GCQLNotNode)node, nodeAnnotation);
		if(node instanceof GCQLOrNode)
			return processNode((GCQLOrNode)node, nodeAnnotation);
		if(node instanceof GCQLProxNode)
			return processNode((GCQLProxNode)node, nodeAnnotation);
		if(node instanceof GCQLTermNode)
			return processNode((GCQLTermNode)node, nodeAnnotation);
		
		throw new Exception("This node class is not supported: " + node.getClass().toString());
	}
	
	private String processNode(GCQLProjectNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		if(node.getProjectIndexes().size() == 1 && node.getProjectIndexes().get(0).getBase().equals("*")) {
			
			logger.info("got wildcard. will use all the fields : " + this.fields);
			logger.info("or presentable fields ?               : " + this.presentableFields);
			
			for(String f: this.fields) {
				
				if (useRR){
					projectedFields.put(Field.getFieldsWithName(false, f).get(0).getID(), f);
				} else {
					
					String projField = findPresentable(f);
					projectedFields.put(f, projField);
				}
			}
		}
		else {
		
			//add all the projections in the projected fields
			Vector<ModifierSet> projections = node.getProjectIndexes();
			for(ModifierSet projection : projections)
			{
				String fieldLabel = null;
				
				if (useRR)
					fieldLabel = QueryHelper.GetFieldNameById(projection.getBase());
				else
					fieldLabel = projection.getBase();
				
				
				String projField = findPresentable(fieldLabel);
				if(projField == null)
				{
					//throw new Exception("Projection: " + projection.getBase() + " is not part of the presentable fields");
					logger.warn("Projection: " + projection.getBase() + " is not part of the presentable fields");
					continue;
				}
				projectedFields.put(projection.getBase(), projField);
			}
		}
		
		logger.info("******************************************");
		logger.info("projectedFields : " + this.projectedFields);
		logger.info("******************************************");
		
		this.queryContainer.setProjectedFields(this.projectedFields);
		
		//return the OpenSearch query of the subtree
		return processNode(node.subtree, nodeAnnotation.left);
	}
	
	private String processNode(GCQLAndNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		return processNode(node.left, nodeAnnotation.left)
		+ " " +  //TODO get rid of this
		processNode(node.right, nodeAnnotation.right);
	}
	
	private String processNode(GCQLOrNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		return processNode(node.left, nodeAnnotation.left)
				+ " " +  //TODO get rid of this
		processNode(node.right, nodeAnnotation.right);
		//throw new Exception("Or operator is not supported by OpenSearch gCQL");
	}
	
	private String processNode(GCQLNotNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		throw new Exception("Not operator is not supported by OpenSearch gCQL");
	}
	
	private String processNode(GCQLProxNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		throw new Exception("Proximity operator is not supported by OpenSearch gCQL");
	}
	
	private String processNode(GCQLTermNode node, GCQLNodeAnnotation nodeAnnotation) throws Exception{
		//examine the index
		boolean found = false;
		String index = null;
		String fieldLabel = null;
		StringBuffer processedQuery = new StringBuffer();
		if(node.getRelation().getModifiers().size() > 0)
		{
			if(node.getRelation().getModifiers().get(0).getType().equals(OpenSearchConstants.configNumOfResultsQName)
				|| node.getRelation().getModifiers().get(0).getType().equals(OpenSearchConstants.configSequentialResultsQName))
			{
				if(!this.queryContainer.queries.get(this.collection).get("*").get(0).hasParameter(node.getRelation().getModifiers().get(0).getType()))
					this.queryContainer.queries.get(this.collection).get("*").get(0).addParameter(node.getRelation().getModifiers().get(0).getType(), node.getRelation().getModifiers().get(0).getValue());
			}
			processedQuery.append(node.getRelation().getModifiers().get(0).getType() + "=" + "\"" + node.getRelation().getModifiers().get(0).getValue() + "\" ");
		}
		if(node.getIndex().equals(OpenSearchDataSourceConstants.COLLECTION_FIELD) || node.getIndex().equals(OpenSearchDataSourceConstants.LANGUAGE_FIELD))
			return processedQuery.toString();
		else {
		
			if (useRR)
				fieldLabel = QueryHelper.GetFieldNameById(node.getIndex());
			else
				fieldLabel = node.getIndex();
			for(String field: searchableFields)
			{
				//we found a searchable field for the specified index
				if(fieldLabel != null && fieldLabel.equalsIgnoreCase(field))
				{
					index = field;
					found = true;
					break;
				}
			}
		}
		
		if(!found)
		{
			throw new Exception("Field: " + node.getIndex() + ", is not part of the searchable fields");
		}
		
		String openSearchParameter = null;
		found = false;
		boolean foundParameter = false;
		if((openSearchParameter = this.searchableExpressions.get(node.getIndex())) == null) {
			
			if (useRR) {
				Set<Searchable> ss = Field.getById(true, node.getIndex()).getSearchables();
				
				for(Searchable s : ss) {
					if(s.getCollection().equals(collection) && s.getLocator().equals(dataSourceLocator)) {
						openSearchParameter = s.getExpression();
						foundParameter = true;
						break;
					}
				}
				if(foundParameter == false) throw new Exception("Could not find OpenSearch parameter in a Searchable expression");
			} else {
				if (node.getIndex().equals("allIndexes")){
					openSearchParameter = "http%3A%2F%2Fa9.com%2F-%2Fspec%2Fopensearch%2F1.1%2F:searchTerms";
				} else if (node.getIndex().equals("startPage")) {
					openSearchParameter = "http%3A%2F%2Fa9.com%2F-%2Fspec%2Fopensearch%2F1.1%2F:startPage";
				} 
			}
			
			
			
			
			this.searchableExpressions.put(node.getIndex(), openSearchParameter);
		}
		//examine the type of the relation
		
		for(OpenSearchConstants.SupportedRelations relation : OpenSearchConstants.SupportedRelations.values())
		{
			switch(relation) {
			case eq:
				boolean foundField = false;
				for(String field : this.searchableFields) {
					if(field.equals(fieldLabel)) {
						foundField = true;
						break;
					}
				}
				if(foundField == false) throw new Exception("Could not find field " + fieldLabel + " among searchable fields");
			}	
			if(node.getRelation().getBase().equalsIgnoreCase(relation.toString()))
			{
				found = true;
				break;
			}
		}
		
		if(!found)
		{
			throw new Exception("Relation: " + node.getRelation().getBase() + " is not suppprted");
		}
		
		String processedTerm = removeQuotes(node.getTerm());
		this.queryContainer.queries.get(this.collection).get("*").get(0).addParameter(openSearchParameter, processedTerm);
		processedQuery.append(openSearchParameter + "=" + "\"" + processedTerm + "\"");
		
		
		logger.trace("Term Node result: " + processedQuery.toString());
		
		return processedQuery.toString();
	}
	
	
	static Set<Searchable> getSearchablesByName(String fieldName) throws ResourceRegistryException{
		
		List<Field> fields = Field.getAll(true);
		for (Field field : fields){
			if (field.getName().equalsIgnoreCase(fieldName)){
				return field.getSearchables();
			}
		}
		
		
		return Collections.emptySet();
	} 

}
