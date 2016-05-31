package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.rr.ResourceRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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

public class LuceneGcqlProcessor extends GcqlProcessor {

	// static GCUBELog logger = new GCUBELog(LuceneGcqlProcessor.class);
	private static Logger logger = LoggerFactory
			.getLogger(LuceneGcqlProcessor.class);

	private static final String LUCENE_AND = " AND ";
	private static final String LUCENE_OR = " OR ";
	private static final String LUCENE_NOT = " NOT ";
	private LinkedHashMap<String, String> projectedFields = new LinkedHashMap<String, String>();

	/**
	 * It will hold the terms to be highlighted for each field
	 */

	public GcqlQueryContainer processQuery(String gCQLQuery, RRadaptor adaptor)
			throws Exception {
		// store the Resource Registry adaptor
		this.adaptor = adaptor;

		logger.info("CQL query: " + gCQLQuery);

		// use the gCQL parser to get a tree from the String
		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(gCQLQuery);
		// the processNode will run recursively, will retrieve
		QuerySnippetTermsPair querySnippetTerms = processNode(head);
		logger.info("Processed Lucene query: " + querySnippetTerms.query);
		logger.info("Processed snippet Terms: "
				+ querySnippetTerms.snippetTerms.toString());

		return new LuceneGcqlQueryContainer(querySnippetTerms,
				this.projectedFields);
	}

	private QuerySnippetTermsPair processNode(GCQLNode node) throws Exception {

		// cases for the possible node types
		if (node instanceof GCQLProjectNode)
			return processNode((GCQLProjectNode) node);
		if (node instanceof GCQLAndNode)
			return processNode((GCQLAndNode) node);
		if (node instanceof GCQLNotNode)
			return processNode((GCQLNotNode) node);
		if (node instanceof GCQLOrNode)
			return processNode((GCQLOrNode) node);
		if (node instanceof GCQLTermNode)
			return processNode((GCQLTermNode) node);

		throw new Exception("This node class is not supported: "
				+ node.getClass().toString());
	}

	

	private QuerySnippetTermsPair processNode(GCQLProjectNode node) throws Exception{
		//add all the projections in the projected fields
		Vector<ModifierSet> projections = node.getProjectIndexes();
		for(ModifierSet projection : projections)
		{
			//check if this projection is the wildcard
			if(projection.getBase().equals(Constants.WILDCARD)) {
				projectedFields.clear();
				projectedFields.put(Constants.WILDCARD, Constants.WILDCARD);
				return processNode(node.subtree);
			}
			
			
			//get the field label for this field id
			// "ff"
			String fieldLabel = null;
			try
			{
				fieldLabel = adaptor.getFieldNameById(projection.getBase());	
			}
			catch(Exception e)
			{
				logger.error("Could not find FieldNameById",e);
			}
			

			projectedFields.put(projection.getBase(), fieldLabel);
		}
		//return the lucene query of the subtree
		return processNode(node.subtree);
	}
	
	private QuerySnippetTermsPair processNode(GCQLAndNode node)
			throws Exception {
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms,
				right.snippetTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms,
				right.snippetNotTerms);
		result.query = "( " + left.query + LUCENE_AND + right.query + " )";

		return result;
	}

	private HashMap<String, ArrayList<String>> mergeSnippetTerms(
			HashMap<String, ArrayList<String>> left,
			HashMap<String, ArrayList<String>> right) {
		for (Entry<String, ArrayList<String>> current : right.entrySet()) {
			ArrayList<String> terms = left.get(current.getKey());
			if (terms == null) {
				left.put(current.getKey(), current.getValue());
			} else {
				terms.addAll(current.getValue());
			}
		}
		return left;
	}

	private QuerySnippetTermsPair processNode(GCQLOrNode node) throws Exception {
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms,
				right.snippetTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms,
				right.snippetNotTerms);
		result.query = "( " + left.query + LUCENE_OR + right.query + " )";

		return result;
	}

	private QuerySnippetTermsPair processNode(GCQLNotNode node)
			throws Exception {
		QuerySnippetTermsPair left = processNode(node.left);
		QuerySnippetTermsPair right = processNode(node.right);
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();
		// note that the right child's terms are inverted
		result.snippetTerms = mergeSnippetTerms(left.snippetTerms,
				right.snippetNotTerms);
		result.snippetNotTerms = mergeSnippetTerms(left.snippetNotTerms,
				right.snippetTerms);
		result.query = "( " + left.query + LUCENE_NOT + right.query + " )";

		return result;
	}

	private QuerySnippetTermsPair processNode(GCQLTermNode node)
			throws Exception {
		// examine the index
		boolean found = false;
		boolean allFields = false;
		String index = null;
		QuerySnippetTermsPair result = new QuerySnippetTermsPair();

		// if the field is the collection or the language then we won't be
		// provided the fieldId
		// also change the relation
		if (node.getIndex().equals(Constants.COLLECTION_FIELD)
				|| node.getIndex().equals(Constants.LANGUAGE_FIELD)) {

			node.getRelation().setBase(
					Constants.SupportedRelations.adj.toString());
			index = node.getIndex();
			found = true;

		} else {
			String fieldLabel = null;
			try
			{
				fieldLabel = adaptor.getFieldNameById(node.getIndex());
			}
			catch(Exception e)
			{
				fieldLabel = "ff";
			}
			// case we want all fields
			if (fieldLabel.equalsIgnoreCase(Constants.ALL_INDEXES)) {
				allFields = true;
				found = true;
			} else {
				index = fieldLabel;
				found = true;
			}
		}

		if (!found) {
			throw new Exception("Field: " + node.getIndex()
					+ ", is not part of the searchable fields");
		}

		// the '=' relation must be transformed to the 'adj' relation
		if (node.getRelation().getBase().equals(Constants.EQUALS)) {
			node.getRelation().setBase(
					Constants.SupportedRelations.adj.toString());
		}

		StringBuffer luceneBuf = new StringBuffer();
		String processedTerm = null;
		//ArrayList<String> snippetTerms = new ArrayList<String>();
		found = false;
		// examine the type of the relation
		for (Constants.SupportedRelations relation : Constants.SupportedRelations
				.values()) {
			if (node.getRelation().getBase()
					.equalsIgnoreCase(relation.toString())) {
				switch (relation) {
				// In this case we will search for the term anywhere inside a
				// field
				case adj:
					processedTerm = node.getTerm();
					//snippetTerms.add(processedTerm);
					break;
				// In this case we will search for terms similar to a SINGLE
				// term
				case fuzzy:
					// We will try to split on whitespaces. Only one term must
					// be specified
					String[] terms = splitTerms(node.getTerm());
					if (terms.length > 1) {
						throw new Exception(
								"The fuzzy relation must have only one term. Received: "
										+ node.getTerm());
					}
					processedTerm = terms[0].trim() + "~";
					//snippetTerms.add(terms[0].trim());
					break;
				// In this case we will search for terms that are within a
				// specific distance away
				case proximity:
					// We will try to split on whitespaces. First term specifies
					// the distance,
					// and is followed by the phrase
					terms = splitTerms(node.getTerm());
					if (terms.length < 3) {
						throw new Exception(
								"The proximity relation must have at least three terms. Received: "
										+ node.getTerm());
					}
					int distance = 0;
					try {
						distance = Integer.parseInt(terms[0]);
					} catch (NumberFormatException e) {
						throw new Exception(
								"Wrong syntax for proximity relation. Term 0 was: "
										+ terms[0], e);
					}
					StringBuffer buf = new StringBuffer();
					for (int i = 1; i < terms.length; i++) {
						buf.append(terms[i] + " ");
						//snippetTerms.add(terms[i]);
					}
					processedTerm = "\"" + buf.toString().trim() + "\"~"
							+ distance;
					break;
				// In this case we will search for documents whose field values
				// are between(lexicographically)
				// a lower and an upper bound
				case within:
					// We will try to split on whitespaces. First term specifies
					// the lower bound,
					// and is followed by the upper bound
					terms = splitTerms(node.getTerm());
					if (terms.length != 2) {
						throw new Exception(
								"The within relation must have exact two terms. Received: "
										+ node.getTerm());
					}
					processedTerm = "[" + terms[0] + " TO " + terms[1] + "]";
					//snippetTerms.add(terms[0]);
					//snippetTerms.add(terms[1]);
					break;
				default:
					throw new Exception(
							"Possible bug. Relation: "
									+ relation
									+ " is reported to be supported but it is not handled here");

				}
				// we found the relation break
				found = true;
				break;
			}
		}

		if (!found) {
			throw new Exception("Relation: " + node.getRelation().getBase()
					+ " is not supported");
		}

		if (allFields) {
			luceneBuf.append("ALL_FIELDS" + ":" + processedTerm);
			// create the snippet terms
//			if (!field.equals(Constants.COLLECTION_FIELD)
//					&& !field.equals(Constants.LANGUAGE_FIELD)
//					&& !field.equals(Constants.DOCID_FIELD)
//					&& !field.equals(Constants.ALL_INDEXES)) {
//				result.snippetTerms.put(field,
//						(ArrayList<String>) snippetTerms.clone());
//			}
//			snippetTerms = null;
		} else {
			luceneBuf.append(index + ":" + processedTerm);
			// create the snippet terms
//			if (!index.equals(Constants.COLLECTION_FIELD)
//					&& !index.equals(Constants.LANGUAGE_FIELD)) {
//				result.snippetTerms.put(index, snippetTerms);
//			}
		}

		logger.debug("Term Node result: " + luceneBuf.toString());

		result.query = "( " + luceneBuf.toString() + " )";
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("hello");
		ResourceRegistry.startBridging();
		while(!ResourceRegistry.isInitialBridgingComplete()) TimeUnit.SECONDS.sleep(10);
		RRadaptor rra = new RRadaptor("gcube/devNext");
		String query = "((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (7fc65620-ceec-47fa-9caa-29b0ed6786a2 = map)) project 7bead703-8d02-4228-a429-9aa57dfa7058 cdc40d31-db01-45f8-a374-4078d6678341 23487454-3f50-4944-ad6f-89e8b137d343 f9c0e76a-d5f9-4361-8ad0-5404fd7fbfcb";
		LuceneGcqlQueryContainer q = (LuceneGcqlQueryContainer) new LuceneGcqlProcessor().processQuery(query,rra);
//		System.out.println(q.getLuceneQuery().query);
//		System.out.println(q.getProjectedFields());
		String finalQuery = q.getLuceneQuery().query;
		Set<String> keySet = q.getProjectedFields().keySet();
		if(keySet.size()>0)
		{
			finalQuery += " project";
			for(String key : keySet)
			{
				finalQuery += " " + q.getProjectedFields().get(key);
			}
		}
		System.out.println(finalQuery);
	}

}
