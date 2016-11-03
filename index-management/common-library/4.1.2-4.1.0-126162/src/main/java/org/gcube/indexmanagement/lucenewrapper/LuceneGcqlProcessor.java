package org.gcube.indexmanagement.lucenewrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.gcube.indexmanagement.common.IndexType;
import org.gcube.indexmanagement.gcqlwrapper.GcqlProcessor;
import org.gcube.indexmanagement.gcqlwrapper.GcqlQueryContainer;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;
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

public class LuceneGcqlProcessor extends GcqlProcessor{
	
	private static final Logger logger = LoggerFactory.getLogger(LuceneGcqlProcessor.class);
	
	private static final String DISTINCT = "distinct";
	private boolean distinct = false;
	
	private static final String LUCENE_AND = " AND ";
	private static final String LUCENE_OR = " OR ";
	private static final String LUCENE_NOT = " NOT ";
	private LinkedHashMap<String,String> projectedFields = new LinkedHashMap<String, String>();
	/**
	 * It will hold the terms to be highlighted for each field 
	 */
	@SuppressWarnings("unused")
	private HashMap<String, ArrayList<String>> snippetTerm = new HashMap<String, ArrayList<String>>();
	
	public static void main(String[] args) {
		/*String test = "gaw   js	  saa  		";
		
		System.out.println(
		        java.util.Arrays.toString(
		        test.split("\\s+")
		    ));*/
		ArrayList<String> presentable = new ArrayList<String>();
		ArrayList<String> searchable = new ArrayList<String>();
		presentable.add("title");
		presentable.add("author");
		presentable.add("year");
		presentable.add("code");
		
		searchable.add("title");
		searchable.add("author");
		searchable.add("year");
		searchable.add("description");
		searchable.add("anotations");
		
		//String query = "(title any fish not (author any sanderson and year within \"1999 2001\")) or (allfields fuzzy Thyrwn or description proximity \"15 about to  burst\") project title author code";
		//String query = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (((79697524-e3bf-457b-891a-faa3a9b0385f contains image) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)))) project 79697524-e3bf-457b-891a-faa3a9b0385f";
		//String query = "((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (((79697524-e3bf-457b-891a-faa3a9b0385f contains image) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)))) project 79697524-e3bf-457b-891a-faa3a9b0385f";
		
		//query = "((((gDocCollectionID == \"14c1decd-347f-4783-81be-53119af7acf1\") and (gDocCollectionLang == \"en\"))) and (64130c65-c584-4fb7-b75f-337998462c87 = a*)) project 065725b6-e39b-40dd-bc86-acaa6aa3aedf 06785d3a-6812-4eb0-9730-dfcaea28e7e3 128c7cb9-ca15-4145-ab68-179086966646 ec208ad5-4e05-4761-a774-ec54f6c41a75 2ada6c76-1501-4a78-96b7-d0740e68d41e c428abff-db06-405e-a9b1-86ccaf5dd121 b582c1a2-cdc3-4d5e-9530-98ec948b73e0 1f539d35-3bbc-4f90-aaff-10d3032ef2b0 e2de8661-6803-4804-af22-dfb8e266bf28 9ce41eb7-ec2b-4324-aa02-25f1b56a227c ee3de342-75a9-4ba9-8657-a096717d0bf7 0dbbd193-38d2-4e19-9e17-7a64846e9940 6244b430-52c4-4ce9-94f4-e4db2aff31ef";
		
		
		String query = "((((gDocCollectionID == \"14c1decd-347f-4783-81be-53119af7acf1\") and (title == \"*\"))) and (allIndexes = a*)) project *";
		query = "((title == \"*\") and ((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (title > 2))) project title sortby title/distinct";
		try{
			QuerySnippetTermsPair luceneQuery = ((LuceneGcqlQueryContainer)(new LuceneGcqlProcessor().processQuery(presentable, searchable, query, null))).getLuceneQuery();
			System.out.println("*" + luceneQuery.query + "*");
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public GcqlQueryContainer processQuery(ArrayList<String> presentableFields, ArrayList<String> searchableFields, String gCQLQuery, RRadaptor adaptor) throws Exception {
		//store the presentable and searchable fields
		this.presentableFields = presentableFields;
		this.searchableFields =  searchableFields;
		//store the Resource Registry adaptor
		this.adaptor = adaptor;
		
		logger.trace("Number of Presentable Fields: " + this.presentableFields.size()
				+ ", Number of Searchable Fields: " + this.searchableFields.size());
		logger.trace("Presentable Fields: " + this.presentableFields
				+ ", Searchable Fields: " + this.searchableFields);
		
		logger.trace("CQL query: " + gCQLQuery);
		
		//use the gCQL parser to get a tree from the String
		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(gCQLQuery);
		//the processNode will run recursively, will retrieve 
		//the projected fields and will generate a lucene query
		QuerySnippetTermsPair querySnippetTerms = processNode(head);
		logger.trace("Processed Lucene query: " + querySnippetTerms.query);
		logger.trace("Processed snippet Terms: " + querySnippetTerms.snippetTerms.toString());

		return new LuceneGcqlQueryContainer(querySnippetTerms, this.projectedFields, this.distinct);
	}
	
//	public GcqlQueryContainer processQuery(ArrayList<String> presentableFields, ArrayList<String> searchableFields, String gCQLQuery) throws Exception {
//		return this.processQuery(presentableFields, searchableFields, null);
//	}

	private QuerySnippetTermsPair processNode(GCQLNode node) throws Exception{
		
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
	
	private QuerySnippetTermsPair processNode(GCQLProjectNode node) throws Exception{
		//add all the projections in the projected fields
		Vector<ModifierSet> projections = node.getProjectIndexes();
		for(ModifierSet projection : projections)
		{
			if(projection.getModifiers().size() > 0)
				if(projection.getModifiers().get(0).getType().equalsIgnoreCase(DISTINCT))
					distinct = true;
			
			//check if this projection is the wildcard
			if(projection.getBase().equals(IndexType.WILDCARD)) {
				distinct = false;
				projectedFields.clear();
				projectedFields.put(IndexType.WILDCARD, IndexType.WILDCARD);
				return processNode(node.subtree);
			}
			
			//get the field label for this field id
			String fieldLabel = null;
			
			if (adaptor != null)
				fieldLabel = adaptor.getFieldNameById(projection.getBase());
			else
				fieldLabel = projection.getBase();
			
			String projField = findPresentable(fieldLabel);
			if(projField == null)
			{
				logger.error("Projection: " + fieldLabel 
						+ ", " + projection.getBase() + "is not part of the presentable fields : " + this.presentableFields);
				continue;
			}
			projectedFields.put(projection.getBase(), projField);
		}
		//return the lucene query of the subtree
		return processNode(node.subtree);
	}
	
	private QuerySnippetTermsPair processNode(GCQLAndNode node) throws Exception{
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms, right.snippetTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms, right.snippetNotTerms);
		
		if (left.query == null && right.query == null)
			return null;
		
		result.query = "( ";
		if (left.query != null)
			result.query += left.query;
		if (left.query != null && right.query != null)
			result.query += LUCENE_AND;
		if (right.query != null)
			result.query += right.query;
		result.query += " )";
		
		return result;
	}
	
	private HashMap<String, ArrayList<String>> mergeSnippetTerms(HashMap<String, ArrayList<String>> left,
			HashMap<String, ArrayList<String>> right) {
		for(Entry<String, ArrayList<String>> current : right.entrySet()) {
			ArrayList<String> terms = left.get(current.getKey());
			if(terms == null) {
				left.put(current.getKey(), current.getValue());
			} else {
				terms.addAll(current.getValue());
			}
		}
		return left;
	}

	private QuerySnippetTermsPair processNode(GCQLOrNode node) throws Exception{
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms, right.snippetTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms, right.snippetNotTerms);
		
		//result.query = "( " + left.query + LUCENE_OR + right.query + " )";
		
		if (left.query == null && right.query == null)
			return null;
		
		result.query = "( ";
		if (left.query != null)
			result.query += left.query;
		if (left.query != null && right.query != null)
			result.query += LUCENE_OR;
		if (right.query != null)
			result.query += right.query;
		result.query += " )";
		
		return result;
	}
	
	private QuerySnippetTermsPair processNode(GCQLNotNode node) throws Exception{
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		//note that the right child's terms are inverted
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms, right.snippetNotTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms, right.snippetTerms);
		//result.query = "( " + left.query + LUCENE_NOT + right.query + " )";
		
		if (left.query == null && right.query == null)
			return null;
		
		result.query = "( ";
		if (left.query != null)
			result.query += left.query;
		if (left.query != null && right.query != null)
			result.query += LUCENE_NOT;
		if (right.query != null)
			result.query += right.query;
		result.query += " )";
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private QuerySnippetTermsPair processNode(GCQLTermNode node) throws Exception{
		//examine the index
		boolean found = false;
		boolean allFields = false;
		String index = null;
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		
		//if the field is the collection or the language then we won't be provided the fieldId
		//also change the relation
		if(node.getIndex().equals(IndexType.COLLECTION_FIELD) || node.getIndex().equals(IndexType.LANGUAGE_FIELD)) {
			
			node.getRelation().setBase(LuceneSearcher.SupportedRelations.adj.toString());
			index = node.getIndex();
			found = true;
			
		} else {
		
			String fieldLabel = null;
			
			if (adaptor != null)
				fieldLabel = adaptor.getFieldNameById(node.getIndex());
			else
				fieldLabel = node.getIndex();
			
			//case we want all fields
			if(fieldLabel.equalsIgnoreCase(LuceneSearcher.ALL_INDEXES))
			{
				allFields = true;
				found = true;
			} else {
				for(String field: searchableFields)
				{
					//we found a searchable field for the specified index
					if(fieldLabel.equalsIgnoreCase(field))
					{
						index = field;
						found = true;
						break;
					}
				}
			}
		}
		
		if(!found)
		{
			throw new Exception("Field: " + node.getIndex() + ", is not part of the searchable fields");
		}
		
		//the '=' relation must be transformed to the 'adj' relation
		if(node.getRelation().getBase().equals(LuceneSearcher.EQUALS)) {
			node.getRelation().setBase(LuceneSearcher.SupportedRelations.adj.toString());
		}
		
		StringBuffer luceneBuf = new StringBuffer();
		String processedTerm = null;
		ArrayList<String> snippetTerms = new ArrayList<String>();
		found = false;
		//examine the type of the relation
		
		
		String relation = node.getRelation().getBase();
		LuceneSearcher.SupportedRelations rel = null;
		
		if (relation.equalsIgnoreCase("==")){
			return new QuerySnippetTermsPair();
		}
		
		if (relation.equalsIgnoreCase(LuceneSearcher.EQUALS) || relation.equalsIgnoreCase(LuceneSearcher.SupportedRelations.adj.toString()))
			rel = LuceneSearcher.SupportedRelations.adj;
		else if (relation.equalsIgnoreCase("within"))
			rel = LuceneSearcher.SupportedRelations.within;
		else if (relation.equalsIgnoreCase("fuzzy"))
			rel = LuceneSearcher.SupportedRelations.fuzzy;
		else if (relation.equalsIgnoreCase("proximity"))
			rel = LuceneSearcher.SupportedRelations.proximity;
		else if (relation.equalsIgnoreCase("="))
			rel = LuceneSearcher.SupportedRelations.eq;
		else if (relation.equalsIgnoreCase(">"))
			rel = LuceneSearcher.SupportedRelations.gt;
		else if (relation.equalsIgnoreCase(">="))
			rel = LuceneSearcher.SupportedRelations.ge;
		else if (relation.equalsIgnoreCase("<"))
			rel = LuceneSearcher.SupportedRelations.lt;
		else if (relation.equalsIgnoreCase("<="))
			rel = LuceneSearcher.SupportedRelations.le;
		else
			throw new Exception("Relation: " + node.getRelation().getBase() + " is not supported");
		
		
		
		
		
		switch(rel) {
				//In this case we will search for the term anywhere inside a field
			case adj:
				processedTerm = node.getTerm();
				snippetTerms.add(processedTerm);
				break;
			//In this case we will search for terms similar to a SINGLE term
			case fuzzy:
				//We will try to split on whitespaces. Only one term must be specified
				String[] terms = splitTerms(node.getTerm());
				if(terms.length > 1)
				{
					throw new Exception("The fuzzy relation must have only one term. Received: " + node.getTerm());
				}
				processedTerm = terms[0].trim() + "~";
				snippetTerms.add(terms[0].trim());
				break;
			//In this case we will search for terms that are within a specific distance away	
			case proximity:
				//We will try to split on whitespaces. First term specifies the distance,
				//and is followed by the phrase
				terms =  splitTerms(node.getTerm());
				if(terms.length < 3)
				{
					throw new Exception("The proximity relation must have at least three terms. Received: " + node.getTerm());
				}
				int distance = 0;
				try{
					distance = Integer.parseInt(terms[0]);
				}catch (NumberFormatException e) {
					throw new Exception("Wrong syntax for proximity relation. Term 0 was: " + terms[0], e);
				}
				StringBuffer buf = new StringBuffer();
				for(int i=1; i<terms.length; i++){
					buf.append(terms[i] + " ");
					snippetTerms.add(terms[i]);
				}
				processedTerm = "\"" + buf.toString().trim() + "\"~" + distance;
				break;
			//In this case we will search for documents whose field values are between(lexicographically)
			//a lower and an upper bound
			case within:
				//We will try to split on whitespaces. First term specifies the lower bound,
				//and is followed by the upper bound
				terms = splitTerms(node.getTerm());
				if(terms.length != 2)
				{
					throw new Exception("The within relation must have exact two terms. Received: " + node.getTerm());
				}
				processedTerm = "[" + terms[0] + " TO " + terms[1] + "]";
				snippetTerms.add(terms[0]);
				snippetTerms.add(terms[1]);
				break;
				
			case gt:
				processedTerm = ">" + node.getTerm();
				snippetTerms.add(processedTerm);
				break;
			case ge:
				processedTerm = ">=" + node.getTerm();
				snippetTerms.add(processedTerm);
				break;
			case lt:
				processedTerm = "<" + node.getTerm();
				snippetTerms.add(processedTerm);
				break;
			case le:
				processedTerm = ">=" + node.getTerm();
				snippetTerms.add(processedTerm);
				break;
			default:
			throw new Exception("Possible bug. Relation: " + relation + " is reported to be supported but it is not handled here");
				
			}
		
		if(allFields)
		{
			@SuppressWarnings("unused")
			boolean first = true;
			//TODO: In the case of all fields we must examine if the luck of a field in a document causes problems
			
			luceneBuf.append(processedTerm);
			
			for(String field: searchableFields)
			{
//				if(!field.equals(LuceneSearcher.ALL_INDEXES)) {
//					if(first)
//						first = false;
//					else
//						luceneBuf.append(" OR ");
//					luceneBuf.append(field + ":" + processedTerm);
//				}
			
				//create the snippet terms
				if(!field.equals(IndexType.COLLECTION_FIELD) 
						&& !field.equals(IndexType.LANGUAGE_FIELD) 
						&& !field.equals(IndexType.DOCID_FIELD) 
						&& !field.equals(LuceneSearcher.ALL_INDEXES)) {
					result.snippetTerms.put(field, (ArrayList<String>)snippetTerms.clone());
				}
			}
			snippetTerms = null;
		} else {
			luceneBuf.append(index + ":" + processedTerm);
			//if (index == null) index = "null";
			//create the snippet terms
			if(!index.equals(IndexType.COLLECTION_FIELD) && !index.equals(IndexType.LANGUAGE_FIELD)) {
				result.snippetTerms.put(index, snippetTerms);
			}
		}
		
		logger.debug("Term Node result: " + luceneBuf.toString());
		
		result.query = "( " + luceneBuf.toString() + " )";
		return result;
	}
	

}
