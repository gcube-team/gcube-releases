package org.gcube.searchsystem.planning.maxsubtree;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.grs.store.buffer.CacheBufferStore;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.gcube.searchsystem.environmentadaptor.EnvironmentAdaptor;
import org.gcube.searchsystem.environmentadaptor.ResourceRegistryAdapter;
import org.gcube.searchsystem.planning.Orchestrator;
import org.gcube.searchsystem.planning.Planner;
import org.gcube.searchsystem.planning.commonvocabulary.Constants;
import org.gcube.searchsystem.planning.commonvocabulary.DefaultStrategy;
import org.gcube.searchsystem.planning.commonvocabulary.OperatorSemantics;
import org.gcube.searchsystem.planning.exception.CQLTreeSyntaxException;
import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;
import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer.GCQLCondition;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLRelation;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.ModifierSet;

public class MaxSubtreePlanner implements Planner{

	/**
	 * the logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(MaxSubtreePlanner.class.getName());
	
	private ArrayList<String> priorities = new ArrayList<String>();
	
	//this list will store the warnings produced during planning
	private ArrayList<String> warnings = new ArrayList<String>();
	
	static private EnvironmentAdaptor environmentAdaptor = null;
	
	//private long timeSpendOnRegistry = 0;
	
	String query = null;
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	/*
	 * test queries used for debugging special cases
	 */
	private static final String TESTQUERY1 = "((((author any \"Joe\") and (title proximity \" invorves \")) not (gDocCollectionLang == \"fr\")) or ((gDocCollectionID == \"A\") and ((title any \"Will\") or (author any \"Norm\"))))";
	private static final String TESTQUERY2B = "((geo geosearch \"11 7 20 100\") and ((type exact \"new\") and ((geo geosearch \"-2 -6 8 4\") and ((gDocCollectionID == \"C\") and ((gDocCollectionLang == \"en\")" 
		+ " and (((desc any \"new\") and (gDocCollectionID == \"C\")) or ((abstract exact \"new\") and ((spec any \"new\") or (tech any \"new\")))))))))";
	private static final String TESTQUERY2 = "((geo geosearch \"11 7 20 100\") and ((type exact \"new\") and ((geo geosearch \"-2 -6 8 4\") and ((gDocCollectionLang == \"en\")" 
		+ " and (((desc any \"new\") and (gDocCollectionID == \"C\")) or ((abstract exact \"new\") and ((spec any \"new\") or (tech any \"new\"))))))))";
	private static final String TESTQUERY3 = TESTQUERY1 + " or " + TESTQUERY2;
	public static final String DEFAULTPRIORITY = "default";

	static final String WILDCARD = "*";

	private static final String DISTINCT = "distinct";
	
	/*
	 * main for debugging special cases
	 */
	public static void main(String[] args) {
		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(TESTQUERY3);
		
		MaxSubtreePlanner planner = new MaxSubtreePlanner(new ArrayList<String>(), new ResourceRegistryAdapter(new EnvHintCollection()));
		planner.priorities.add(DEFAULTPRIORITY);
		try {
			PlanNode plan = planner.plan(head);
			System.out.print(plan.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public MaxSubtreePlanner(ArrayList<String> priorities, EnvironmentAdaptor environmentAdaptor) {
		super();
		this.priorities = priorities;
		//the adaptor used currently(this could change and be specified dynamically)
		MaxSubtreePlanner.environmentAdaptor = environmentAdaptor;
	}
	
	public ArrayList<String> getPriorities() {
		return priorities;
	}

	public void setPriorities(ArrayList<String> priorities) {
		this.priorities = priorities;
	}
	
	public ArrayList<String> getWarnings() {
		return warnings;
	}

	public void clearWarnings() {
		this.warnings = new ArrayList<String>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PlanNode plan(GCQLNode root) throws CQLTreeSyntaxException, CQLUnsupportedException {
		
		//clear the warning list
		clearWarnings();
		
		logger.info("rewritting query for max subtree planner");
		
		//rewrite the CQL tree in order to detect the collection and language tags
		long before = System.currentTimeMillis();
		MaxSubtreeRewritter rewriter = new MaxSubtreeRewritter(root);
		long after = System.currentTimeMillis();
		logger.info("profiling: MaxSubtreeRewritter creation " + (after - before) + " millis");
		
		before = System.currentTimeMillis();
		ArrayList<AndTree> subtreeList = rewriter.rewrite();
		after = System.currentTimeMillis();
		logger.info("profiling: rewriting in : " + (after - before) + " millis");
		
		
		//start the planning following the specified priorities
		for(String indication : this.priorities) {
			
			logger.info("started composition for indication: " + indication);
			
			//create a clone of the AndTree list
			ArrayList<AndTree> subtrees = new ArrayList<AndTree>();
			for(AndTree tree : subtreeList) {
				subtrees.add((AndTree)tree.clone());
			}
			
			logger.info("enhance trees with sources for indication: " + indication);
			
			before = System.currentTimeMillis();
			try{
				subtrees = MaxSubtreePlanner.getEnhanceAndTreesWithSources(subtrees, rewriter.getProjections(), indication, environmentAdaptor);
			} catch (CQLUnsupportedException cqle) {
				logger.error("enhance with sources failed: ", cqle);
				continue;
			}
			after = System.currentTimeMillis();
			logger.info("profiling: enhanceAndTreesWithSources returned after: " + (after - before) + " millis");
			//logger.info("Time spend on Environment Adaptor until now: " + timeSpendOnRegistry + " millis");
			
			
			logger.trace("enhance with sources returned: ");
			before = System.currentTimeMillis();
			for(AndTree tree : subtrees) {
				logger.trace("Tree for collection: " + tree.collection + ", language: " + tree.language);
				for(int i=0; i<tree.conditions.size(); i++) {
					GCQLCondition condition = tree.conditions.get(i);
					logger.trace("Not: " + condition.not + ", term: " + condition.term.toCQL());
					logger.trace("Sources: " + Arrays.toString(tree.sources.get(i).toArray(new String[tree.sources.get(i).size()])));
				}
			}
			after = System.currentTimeMillis();
			logger.info("profiling: enhancing stage time : " + (after - before) + " millis");
			
			if(subtrees.size() == 0) {
				logger.info("no trees after enhancement for indication: " + indication);
				continue;
			}
			
			logger.info("starting 2 phase composer for indication: " + indication);
			
			before = System.currentTimeMillis();
			TwoPhaseComposer composer = new TwoPhaseComposer(subtrees, rewriter.getProjections());
			GeneralTreeNode rootNode = composer.compose();
			after = System.currentTimeMillis();
			logger.info("profiling: TwoPhaseComposer returned after: " + (after - before) + " millis");
			
			//create the HashSet for projections
			
			before = System.currentTimeMillis();
			boolean distinct = false;
			List<String> sortByParts = new ArrayList<String>();
			
			boolean inSortBy = false;
			
			HashSet<String> projections = new HashSet<String>();
			for(ModifierSet mod : rewriter.getProjections()) {
				String proj = mod.getBase();
				
				logger.info("checking projection : " + proj);
				
				if (proj.equalsIgnoreCase("sortby")){
					inSortBy = true;
				}
				
				
				if (inSortBy){
					sortByParts.add(proj);
					if (proj.equalsIgnoreCase("asc") || proj.equalsIgnoreCase("desc"))
						inSortBy = false;
					
				} else {
				
					projections.add(proj);
					
					//check if there is a distinct indication
					//Note that it doesn't matter which is the field 
					//where the indication is attached(and it is the 
					//only modifier allowed)
					if(mod.getModifiers().size() > 0)
						if(mod.getModifiers().get(0).getType().equalsIgnoreCase(DISTINCT))
							distinct = true;
				}
			}
			logger.info("projections : " + projections);
			logger.info("sortByParts : " + sortByParts);
			
			after = System.currentTimeMillis();
			logger.info("profiling: distinct and projections get in : " + (after - before) + " millis");
			
			logger.info("starting node specialization for indication: " + indication);
			
			before = System.currentTimeMillis();
			PlanNode node =  specializeNode(rootNode, projections, indication, distinct, sortByParts);
			after = System.currentTimeMillis();
			logger.info("profiling: node specialization time : " + (after - before) + " millis");
			
			return node;
		}
		
		return null;
	}
	
	private PlanNode specializeNode(GeneralTreeNode node, Set<String> projectionsNeeded, String indication, boolean distinct, List<String> sortByParts) throws CQLUnsupportedException{
		switch(node.type) {
			case AND:
				return specializeAndNode(node, projectionsNeeded, indication, distinct, sortByParts);
				
			case OR:
				return specializeOrNode(node, projectionsNeeded, indication, distinct, sortByParts);
				
			case LEAF:
				return specializeLeafNode(node, projectionsNeeded, indication, distinct, sortByParts);
				
			case NOT:
				logger.error("The NOT case should not happen in the current implementation");
				return specializeNotNode(node, projectionsNeeded, indication, distinct, sortByParts);
				
			default:
				throw new CQLUnsupportedException("Only AND, OR, LEAF (and NOT) cases are permitted");
					
		}
	}
	
	private PlanNode specializeOrNode(GeneralTreeNode node, Set<String> projectionsNeeded, String indication, boolean distinct, List<String> sortByParts) throws CQLUnsupportedException {
		//get the semantics for this case
		String semantics = OperatorSemantics.getOrOperationSemantics(indication);
		
		//process the children
		ArrayList<PlanNode> children = new ArrayList<PlanNode>();
		HashSet<String> commonProjections = null;
		for(GeneralTreeNode child : node.children) {
			PlanNode current = specializeNode(child, projectionsNeeded, indication, distinct, sortByParts);
			
			if(commonProjections == null) {
				commonProjections = new HashSet<String>(current.getProjections());
			} else {
				commonProjections.retainAll(current.getProjections());
			}
			
			children.add(current);
		}
		
		//create the arguments for this case
		HashMap<String, String> args = OperatorSemantics.createOrOperationArgs(semantics, Constants.DEFAULT, indication);
		
		if (this.query != null){
			args.put("query", this.query);
		}
		
		return new OperatorNode(semantics, args, children, commonProjections);
	}
	
	private PlanNode specializeAndNode(GeneralTreeNode node, Set<String> projectionsNeeded, String indication, boolean distinct, List<String> sortByParts) throws CQLUnsupportedException {
		
		//the distinct currently can not be supported for the join case
		distinct = false;
		logger.warn("the distinct currently can not be supported for the join case setting it to false");
		
		//get the semantics for this case
		String semantics = OperatorSemantics.getAndOperationSemantics(indication);
		
		ArrayList<PlanNode> result = new ArrayList<PlanNode>();
		Set<String> currentProjections = new HashSet<String>(projectionsNeeded);
		
		for(GeneralTreeNode child : node.children) {
			PlanNode output = specializeNode(child, currentProjections, indication, distinct, sortByParts);
			result.add(output);
			
			//keep only the projections not satisfied yet
			currentProjections.removeAll(output.getProjections());			
		}
		
		PlanNode current = result.remove(0);
		Iterator<PlanNode> iter = result.iterator();
		while(iter.hasNext()) {
			
			PlanNode left = iter.next();
			//set the two children
			ArrayList<PlanNode> children = new ArrayList<PlanNode>();
			children.add(left);
			children.add(current);
			
			//create the arguments
			HashMap<String, String> args = null;
			Set<String> leftProjections = left.getProjections();
			if(leftProjections.size() == 0) {
				//get payload from right
				args = OperatorSemantics.createAndOperationArgs(semantics, indication, Constants.PAYLOADRIGHT);
			} else {
				//get payload from both
				args = OperatorSemantics.createAndOperationArgs(semantics, indication, Constants.PAYLOADBOTH);
			}
			
			//the projections of this node will be the union of the two
			Set<String> proj = new HashSet<String>(current.getProjections());
			proj.addAll(leftProjections);			
			
			current = new OperatorNode(semantics, args, children, proj);
			
			iter.remove();
		}
		
		return current;
	}
	
	private PlanNode specializeLeafNode(GeneralTreeNode node, Set<String> projectionsNeeded, String indication, boolean distinct, List<String> sortByParts) throws CQLUnsupportedException {
		//find out which of the sources of this leaf support the requested set of projections
		Set<String> sources = new HashSet<String>();
		HashSet<String> maxSet = null;
		GCQLNode gcql = null;
		
		if(projectionsNeeded.contains(WILDCARD)) {
			
			gcql = new GCQLProjectNode();
			((GCQLProjectNode)gcql).subtree = node.gcql;
			//create the list of ModifierSet for the projections
			((GCQLProjectNode)gcql).getProjectIndexes().add(new ModifierSet(WILDCARD));
			
			maxSet = new HashSet<String>();
			maxSet.add(WILDCARD);
			sources = node.sources;			
						
		} else {
			
			if(projectionsNeeded.size() > 0) {
				
				HashMap<String, HashSet<String>> projectionsPerSource = null;
				
				logger.trace("getting projections for sources");
				
				try {
					
					logger.trace("log getProjections per source args - sources: " + Arrays.toString(node.sources.toArray(new String[node.sources.size()])) 
							+ " - projectionsNeeded: " + Arrays.toString(projectionsNeeded.toArray(new String[projectionsNeeded.size()])));
					for(Entry<String, HashSet<String>> current : node.colLangs.entrySet()) {
						logger.trace("log getProjections per source args - col: " + current.getKey() 
								+ " - langs" + Arrays.toString(current.getValue().toArray(new String[current.getValue().size()])));
					}
					
					long before = System.currentTimeMillis();
					projectionsPerSource = environmentAdaptor.getProjectionsPerSource(new HashSet<String>(node.sources), projectionsNeeded, node.colLangs);
					//log getProjections per source args
					long after = System.currentTimeMillis();
					long total = (after -before);
					logger.info("getProjectionsPerSource returned after total: " + total + " millis");
					//timeSpendOnRegistry += total;
					
				}catch (Exception e) {
					logger.error("getProjectionsPerSource failed!", e);
					throw new CQLUnsupportedException("specializeLeafNode could not complete. getProjectionsPerSource failed: " + e.getMessage());
				}
				
				for(Entry<String, HashSet<String>> current : projectionsPerSource.entrySet()) {
					logger.trace("source: " + current.getKey());
					for(String projection : current.getValue()) {
						logger.trace("projected field: " + projection);
					}
				}
				
				//find the max set of projections supported 
				for(Entry<String, HashSet<String>> current : projectionsPerSource.entrySet()) {
					HashSet<String> currentSet = current.getValue();
					if(maxSet == null || currentSet.size() > maxSet.size()) {
						logger.trace("source: " + current.getKey() + " has projection : " + current.getValue());
						maxSet = currentSet;
					}
				}
				
				//find which are the sources that have the same set of projections with the maxSet
				for(Entry<String, HashSet<String>> current : projectionsPerSource.entrySet()) {
					HashSet<String> currentSet = current.getValue();
					HashSet<String> tmpSet = new HashSet<String>(maxSet);
					tmpSet.retainAll(currentSet);
					//if all the elements remain in the set, the current set is the max Set
					if(tmpSet.size() == maxSet.size()) {
						logger.trace("source: " + current.getKey() + " has projection : " + current.getValue());
						sources.add(current.getKey());
					}
				}
				
				//add the projections to the GCQL query
				
				logger.info("max set : " + maxSet);
				if(maxSet != null && maxSet.size() > 0) {
					gcql = new GCQLProjectNode();
					((GCQLProjectNode)gcql).subtree = node.gcql;
					//create the list of ModifierSet for the projections
					logger.info("~> distinct : " + distinct);
					for(String proj : maxSet) {
						ModifierSet modSet = new ModifierSet(proj);
						
						logger.info("~> checking proj : " + proj);
						
						if(distinct) {
							if (!proj.equalsIgnoreCase("sortby")&& !proj.equalsIgnoreCase("asc") && !proj.equalsIgnoreCase("desc") && !proj.equalsIgnoreCase("fuse")){
								//set distinct modifier only once
								distinct = false;
								modSet.addModifier(DISTINCT);
							}
						}
						((GCQLProjectNode)gcql).getProjectIndexes().add(modSet);
					}
				} else {
					gcql = DefaultStrategy.addDefaultProjections(node.gcql);
				}
			} else {
				gcql = DefaultStrategy.addDefaultProjections(node.gcql);
			}
			
			if(maxSet == null) {
				maxSet = new HashSet<String>();
				sources = node.sources;
			}
		
		}
		
		//append the sortby part at the end
		if (sortByParts.size() >= 2){
			for(String proj : sortByParts){
				ModifierSet modSet = new ModifierSet(proj);
				((GCQLProjectNode)gcql).getProjectIndexes().add(modSet);
			}
			
			maxSet.add(sortByParts.get(1));
		}
		
		return new DataSourceNode(sources, new HashMap<String, String>(), gcql.toCQL(), maxSet);
	}
	
	private PlanNode specializeNotNode(GeneralTreeNode node, Set<String> projectionsNeeded, String indication, boolean distinct, List<String> sortByParts) throws CQLUnsupportedException {
		//get the semantics for this case
		String semantics = OperatorSemantics.getNotOperationSemantics(indication);
		
		//we expect two children
		if(node.children.size() != 2) {
			throw new CQLUnsupportedException("NOT General Tree node doesn't have exactly two children");
		}
		ArrayList<PlanNode> children = new ArrayList<PlanNode>();
		PlanNode left = specializeNode(node.children.get(0), projectionsNeeded, indication, distinct, sortByParts);
		children.add(left);
		children.add(specializeNode(node.children.get(1), new HashSet<String>(), indication, distinct, sortByParts));
		
		HashMap<String, String> args = OperatorSemantics.createNotOperationArgs(semantics, Constants.DEFAULT, indication);
		
		return new OperatorNode(semantics, args, children, left.getProjections());
	}
	
	
	//static Map<String, ArrayList<AndTree>> cache = new HashMap<String, ArrayList<AndTree>>();
	public static Cache<CacheElement, ArrayList<AndTree>> cache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(30, TimeUnit.MINUTES)
			.build();
			
	
	static long lastRRUpdate = 0;
	
	
	public static ArrayList<AndTree> getEnhanceAndTreesWithSources(ArrayList<AndTree> subtrees, Vector<ModifierSet> proj, String indication, EnvironmentAdaptor environmentAdaptor) throws CQLUnsupportedException{
		
		
		logger.trace("subtrees       : " + subtrees);
		logger.trace("proj           : ");
		List<String> projections = new ArrayList<String>();
		for(ModifierSet projection : proj) {
			projections.add(projection.getBase());
			logger.trace("\t" + projection.toCQL());
		}
		
		logger.trace("indication     : " + indication);
		
		
		ArrayList<AndTree> newSubtrees = new ArrayList<AndTree>();
		HashMap<String, String> reserved = new HashMap<String, String>();
		
		int cnt = 0;
		for (AndTree tree : subtrees){
			AndTree newAndTree = (AndTree)tree.clone();
			for (GCQLCondition condition : newAndTree.conditions){
				String newTerm = "RESERVED" + cnt;
				reserved.put(newTerm, condition.term.getTerm().trim());
				logger.info("will replace " + condition.term.getTerm() + " with " + newTerm);
				cnt++;
				condition.term.setTerm(newTerm);
			}
			newSubtrees.add(newAndTree);
		}
		
		logger.trace("reserved       : " + reserved);
		logger.trace("newSubtrees       : " + newSubtrees);
		
		if (environmentAdaptor.getLastUpdate() < 0){
			logger.error("Resource Registry has not been initialized yet");
			return null;
		}
		logger.trace("last RR snapshot      : " + lastRRUpdate + " last RR update : " + environmentAdaptor.getLastUpdate());
		if (environmentAdaptor.getLastUpdate() > lastRRUpdate){
			//cache.clear();
			cache.invalidateAll();
			lastRRUpdate = environmentAdaptor.getLastUpdate();
		}
		
		CacheElement cacheKey = new CacheElement(newSubtrees, projections, indication);
		//String cacheKey = newSubtrees.toString() + projections.toString() + indication;
		
		logger.trace("cacheKey : " + cacheKey.hashCode());
		for (CacheElement el : cache.asMap().keySet())
			logger.trace("\t cache keys  : " + el.hashCode());
		
		ArrayList<AndTree> templateTree = null;
		templateTree = cache.getIfPresent(cacheKey);
		if (templateTree == null) {
			ArrayList<AndTree> value = enhanceAndTreesWithSources(newSubtrees, projections, indication);
			if (value != null && value.size() > 0)
				cache.put(cacheKey, value);
			templateTree = value;
		}
		
		logger.trace("templateTree       : " + templateTree);
		
		ArrayList<AndTree> returnedTree = new ArrayList<AndTree>(templateTree.size());
		for (AndTree tree : templateTree)
			returnedTree.add((AndTree) tree.clone());
		
		logger.trace("returnedTree       : " + returnedTree);
		
		logger.trace("reserved       : " + reserved);
		for (AndTree tree : returnedTree){
			for (GCQLCondition condition : tree.conditions){
				if (condition == null){
					logger.info("got null condition");
					continue;
				}
				logger.info("examining " + condition.toString());
				if (condition.term == null){
					logger.info("got null condition term");
					continue;
				}
				
				if (condition.term.getTerm() == null){
					logger.info("got null condition term getTerm");
					continue;
				}
					logger.info("examining " + condition.toString());
				String oldTerm = reserved.get(condition.term.getTerm().trim());
				logger.info("will replace " + condition.term.getTerm() + " with " + oldTerm);
				condition.term.setTerm(oldTerm);
			}
		}
		
		logger.trace("returnedTree       : " + returnedTree);
		
		return returnedTree;

	}
	
	private static ArrayList<AndTree> enhanceAndTreesWithSources(ArrayList<AndTree> subtrees, List<String> projections, String indication) throws CQLUnsupportedException{
		
		ArrayList<AndTree> result = new ArrayList<AndTree>();
		
		//get the projections
		
		
		//find the trees which do not specify a collection and language
		for(int i=0; i<subtrees.size(); i++) {
			
			AndTree currentTree = subtrees.get(i);
			//if a specific collection is not specified
			if(currentTree.collection == null) {
				//if a specific language is not specified
				if(currentTree.language == null) {
					
					logger.trace("get collection-languages for conditions: ");
					Map<String, List<String>> fieldRelationMap = getFieldRelationMap(currentTree);
					for(Entry<String, List<String>> fieldRelations : fieldRelationMap.entrySet()) {
						logger.trace("field: " + fieldRelations.getKey());
						for(String relation : fieldRelations.getValue()) {
							logger.trace("relation: " + relation);
						}
					}
					
					Map<String, Set<String>> collectionLangs = null;
					
					try {
						long before = System.currentTimeMillis();
						collectionLangs = environmentAdaptor.getCollectionLangsByFieldRelation(fieldRelationMap, projections);
						long after = System.currentTimeMillis();
						long total = (after -before);
						logger.info("getCollectionLangsByFieldRelation returned after total: " + total + " millis");
						//timeSpendOnRegistry += total;
						
					}catch (Exception e) {
						logger.error("getCollectionLangsByFieldRelation failed!", e);
						throw new CQLUnsupportedException("enhanceAndTreesWithSources could not complete. getCollectionLangsByFieldRelation failed: " + e.getMessage());
					}
						
					
					logger.trace("registry returned collection-languages: ");
					for(Entry<String, Set<String>> colLangs : collectionLangs.entrySet()) {
						logger.trace("collection: " + colLangs.getKey());
						for(String language : colLangs.getValue()) {
							logger.trace("language: " + language);
						}
					}
					
					//replace the current abstract AndTree with trees that refer 
					//to specific collection and languages
					HashSet<String> notCols = new HashSet<String>(currentTree.notCollections);
					HashSet<String> notLangs = new HashSet<String>(currentTree.notLanguages);
					if(collectionLangs != null) {
						for(Entry<String, Set<String>> currentColLangs : collectionLangs.entrySet()) {
							String collection = currentColLangs.getKey();
							
							//if the current collection is declared in the not collections
							if(notCols.contains(collection))
								continue;
							
							//iterate over the languages for this collection
							for(String language : currentColLangs.getValue()) {
								//if the current language is declared in the not languages
								if(notLangs.contains(language))
									continue;
								
								AndTree tree = createNewTree(currentTree, collection, language, indication);
								if(tree != null) {
									result.add(tree);
								}
							}
						}
					}
				//if a language is specified 
				} else {
					
					logger.trace("get collections for language: " + currentTree.language + ", for conditions: ");
					Map<String, List<String>> fieldRelationMap = getFieldRelationMap(currentTree);
					for(Entry<String, List<String>> fieldRelations : fieldRelationMap.entrySet()) {
						logger.trace("field: " + fieldRelations.getKey());
						for(String relation : fieldRelations.getValue()) {
							logger.trace("relation: " + relation);
						}
					}
					
					Set<String> collections = null;
					
					try{
						
						long before = System.currentTimeMillis();
						collections = environmentAdaptor.getCollectionByFieldRelationLang(fieldRelationMap, currentTree.language, projections);
						long after = System.currentTimeMillis();
						long total = (after -before);
						logger.info("getCollectionByFieldRelationLang returned after total: " + total + " millis");
						//timeSpendOnRegistry += total;
						
					}catch (Exception e) {
						logger.error("getCollectionByFieldRelationLang failed!", e);
						throw new CQLUnsupportedException("enhanceAndTreesWithSources could not complete. getCollectionByFieldRelationLang failed: " + e.getMessage());
					}
						
					for(String collection : collections) {
						logger.trace("collection: " + collection);
					}
					
					//replace the current abstract AndTree with trees that refer 
					//to specific collection and languages
					HashSet<String> notCols = new HashSet<String>(currentTree.notCollections);
					if(collections != null) {
						//iterate over the collections
						for(String collection : collections) {
							//if the current collection is declared in the not collections
							if(notCols.contains(collection))
								continue;
							
							AndTree tree = createNewTree(currentTree, collection, currentTree.language, indication);
							if(tree != null) {
								result.add(tree);
							}
						}
					}
				}
			//if a collection is specified	
			} else {
				//if a specific language is not specified
				if(currentTree.language == null) {
					
					logger.trace("get languages for collection: " + currentTree.collection + ", for conditions: ");
					Map<String, List<String>> fieldRelationMap = getFieldRelationMap(currentTree);
					for(Entry<String, List<String>> fieldRelations : fieldRelationMap.entrySet()) {
						logger.trace("field: " + fieldRelations.getKey());
						for(String relation : fieldRelations.getValue()) {
							logger.trace("relation: " + relation);
						}
					}
					
					Set<String> languages = null;
					
					try{
						
						long before = System.currentTimeMillis();
						languages = environmentAdaptor.getLanguageByFieldRelationCol(fieldRelationMap, currentTree.collection, projections);
						long after = System.currentTimeMillis();
						long total = (after -before);
						logger.info("getLanguageByFieldRelationCol returned after total: " + total + " millis");
						//timeSpendOnRegistry += total;
						
					}catch (Exception e) {
						logger.error("getLanguageByFieldRelationCol failed!", e);
						throw new CQLUnsupportedException("enhanceAndTreesWithSources could not complete. getLanguageByFieldRelationCol failed: " + e.getMessage());
					}
					
					for(String language : languages) {
						logger.trace("language: " + language);
					}
					
					//replace the current abstract AndTree with trees that refer 
					//to specific collection and languages
					HashSet<String> notLangs =  new HashSet<String>(currentTree.notLanguages);
					if(languages != null) {
						//iterate over the languages
						for(String language : languages) {
							//if the current language is declared in the not languages
							if(notLangs.contains(language))
								continue;
							
							AndTree tree = createNewTree(currentTree, currentTree.collection, language, indication);
							if(tree != null) {
								result.add(tree);
							}
						}
					}
				//if a specific language is specified
				} else {
					//replace the current AndTree with a tree that specifies the sources
					//satisfying the each of the conditions
					logger.trace("specific collection: " + currentTree.collection + ", language: " + currentTree.language);
					AndTree tree = createNewTree(currentTree, currentTree.collection, currentTree.language, indication);
					if(tree != null) {
						result.add(tree);
					}
				}
			}
		}
		
		return result;
	}
	
	static private AndTree createNewTree(AndTree currentTree, String collection, String language, String indication) throws CQLUnsupportedException{
		//create a new tree with the same conditions and a specific collection, language
		AndTree newTree = new AndTree();
		newTree.setConditions(new ArrayList<GCQLCondition>(currentTree.conditions));
		newTree.setCollection(collection);
		newTree.setLanguage(language);
		
		//find the sources that can satisfy each of the conditions
		for(GCQLCondition condition : currentTree.conditions) {
			
			logger.info("Get sources for condition: Index - " 
					+ condition.getTerm().getIndex() + ", Relation - " 
					+ condition.getTerm().getRelation().getBase() + ", Collection - "
					+ collection + ", Language - " + language 
					+ ", Indication - " + indication);
			
			Set<String> sources = null;
			
			try{
				
				long before = System.currentTimeMillis();
				
				sources = environmentAdaptor.getSourceIdsForFieldRelationCollectionLanguage(
						condition.getTerm().getIndex(), condition.getTerm().getRelation().getBase(),
						collection, language, indication);
				long after = System.currentTimeMillis();
				long total = (after -before);
				logger.info("getSourceIdsForFieldRelationCollectionLanguage returned after total: " + total + " millis");
				
				
			}catch (Exception e) {
				logger.error("getSourceIdsForFieldRelationCollectionLanguage failed!", e);
				throw new CQLUnsupportedException("createNewTree could not complete. getSourceIdsForFieldRelationCollectionLanguage failed: " + e.getMessage());
			}
			
			logger.info("Sources returned (from getSourceIdsForFieldRelationCollectionLanguage): " + sources);
			
			//if there are no sources for this condition
			if(sources == null || sources.size() == 0) {
				
				String msg = condition.getTerm().toCQL();
				if(condition.isNot()) {
					msg = "not(" + msg + ")";
				}
				msg = "There is no source for the criterion: " + msg 
				+ ", for collectionID: " + collection + " and language: " + language;
				
				logger.error(msg);
				
				//if this is the default priority then add a warning and continue without adding a AndTree
				if(indication.equals(DEFAULTPRIORITY)) {
					//warnings.add(msg);
					return null;
				} else {
					throw new CQLUnsupportedException("For indication " + indication + ": " + msg);
				}
			}
			
			newTree.sources.add(new LinkedHashSet<String>(sources));
		}
		
		return newTree;
	}
	
	private static Map<String, List<String>> getFieldRelationMap(AndTree andTree) {
		Map<String, List<String>> map =  new HashMap<String, List<String>>();
		
		//scan the conditions
		for(GCQLCondition condition : andTree.getConditions()) {
			String field = condition.getTerm().getIndex();
			String relation = condition.getTerm().getRelation().getBase();
			
			List<String> relations = map.get(field);
			//if there is no relation specified for this field until now
			if(relations == null) {
				relations = new ArrayList<String>();
				map.put(field, relations);
			}
			relations.add(relation);
		}
		
		return map;
	}
	
	
	//the following 6 methods are provided by the registry
	//these versions are just for a debugging purpose
	
	private HashMap<String, HashSet<String>> getProjectionsPerSourceDummy(
			Set<String> sources, Set<String> projectionsNeeded,
			HashMap<String, HashSet<String>> colLangs) {
		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		for(String source : sources) {
			HashSet<String> proj = new HashSet<String>(projectionsNeeded);
			result.put(source, proj);
		}
		return result;
	}
	
	private Map<String, List<String>> getCollectionLangsByFieldRelationDummy(Map<String, List<String>> fieldRelationMap, List<String> projections) {
		Map<String, List<String>> colLangs = new HashMap<String, List<String>>();
		ArrayList<String> langs = new ArrayList<String>();
		langs.add("en");
		langs.add("fr");
		colLangs.put("A", langs);
		colLangs.put("B", new ArrayList<String>(langs));
		colLangs.put("C", new ArrayList<String>(langs));
		
		return colLangs;
	}
	
	private List<String> getCollectionByFieldRelationLangDummy(Map<String, List<String>> fieldRelationMap, String language, List<String> projections) {
		ArrayList<String> cols = new ArrayList<String>();
		cols.add("A");
		cols.add("B");
		cols.add("C");
		return cols;
	}
	
	private List<String> getLanguageByFieldRelationColDummy(Map<String, List<String>> fieldRelationMap, String collection, List<String> projections) {
		ArrayList<String> langs = new ArrayList<String>();
		langs.add("en");
		langs.add("fr");
		return langs;
	}
	
	private Set<String> getSourceIdsForFieldRelationCollectionLanguageDummy(
			String field, String relation, String collection, String language, String indication) {
		if(relation.equals(Constants.GEOSEARCH)) {
			Set<String> result = new HashSet<String>();
			//result.add("GEO" + collection);
			result.add("GEO" + collection + language);
			return result;
		}
		if(relation.equals("exact")) {
			Set<String> result = new HashSet<String>();
			//result.add("FWD" + collection);
			result.add("FWD" + collection + language);
			return result;
		}
		
		//else
		Set<String> result = new HashSet<String>();
		//result.add("FT" + collection);
		result.add("FT" + collection + language);
		return result;
		
	}	
	
}
