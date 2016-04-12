package org.gcube.searchsystem.planning.commonvocabulary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;
import org.gcube.searchsystem.planning.maxsubtree.AndTree;
import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer;
import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer.GCQLCondition;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLBooleanNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLRelation;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.Modifier;

public class IndexRelationCommonSemantics {
	
	/**
	 * the logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(IndexRelationCommonSemantics.class.getName());
	
	private IndexRelationCommonSemantics() {
		
	}
	
	public static boolean examineCondition(GCQLCondition cond, AndTree result) throws CQLUnsupportedException{
		
		logger.trace("examine condition for relation: " + cond.getTerm().getRelation().getBase());
		
		//if it refers to the collection field
		if(cond.getTerm().getIndex().equals(Constants.COLLECTION_FIELD)) {
			//only this relation specifies collection information
			if(cond.getTerm().getRelation().getBase().equals(Constants.EQUALS)) {
				if(!checkCollection(result, cond.isNot(), cond.getTerm().getTerm()))
					return false;
			} else {
				throw new CQLUnsupportedException("For field: " + cond.getTerm().getIndex() 
						+ ", found a relation: " + cond.getTerm().getRelation());
			}
		
		//if it refers to the language field
		}else if(cond.getTerm().getIndex().equals(Constants.LANGUAGE_FIELD)) {
			//only this relation specifies language information
			if(cond.getTerm().getRelation().getBase().equals(Constants.EQUALS)) {
				if(!checkLanguage(result, cond.isNot(), cond.getTerm().getTerm()))
					return false;
			} else {
				throw new CQLUnsupportedException("For field: " + cond.getTerm().getIndex() 
						+ ", found a relation: " + cond.getTerm().getRelation());
			}
			
		//if it is the geosearch relation	
		}else if(cond.getTerm().getRelation().getBase().equals(Constants.GEOSEARCH)) {
				if(cond.isNot()) {
					cond.getTerm().getRelation().getModifiers().add(new Modifier(Constants.NOTMODIFIER));
					cond.setNot(false);
				}
				
				//Find the modifiers for collection and language
				Iterator<Modifier> modifierIter = cond.getTerm().getRelation().getModifiers().iterator();
				while( modifierIter.hasNext() ) {
					
					Modifier modifier = modifierIter.next();
					
					if(modifier.getType().equals(Constants.COLIDMODIFIER)) {
						String[] args = splitTerms(modifier.getValue());
						if(args.length > 1 || args.length < 1)
						{
							throw new CQLUnsupportedException("value of colid modifier for geosearch relation: " + modifier.getValue());
						}
						
						
						
						if(!checkCollection(result, false, args[0]))
							return false;
						//remove this modifier cause it will be added later in order to have a uniform behavior in geosearch cases
						modifierIter.remove();
					}
					if(modifier.getType().equals(Constants.LANGMODIFIER)) {
						String[] args = splitTerms(modifier.getValue());
						if(args.length > 1 || args.length < 1)
						{
							throw new CQLUnsupportedException("value of lang modifier for geosearch relation: " + modifier.getValue());
						}
						if(!checkLanguage(result, false, args[0]))
							return false;
						//remove this modifier cause it will be added later in order to have a uniform behavior in geosearch cases
						modifierIter.remove();
					}
				}
				
			//in this case we also add this condition in the tree
			result.getConditions().add(cond);
		}else{
			//add the condition to the tree
			result.getConditions().add(cond);
		}
		
		return true;
	}
	
	private static boolean checkLanguage(AndTree result, boolean not, String language) {
		language = removeQuotes(language);
		if(not) {
			//if there is already a language specified
			if(result.getLanguage() != null) {
				//if they are the same return false
				//this means that no result can derive from this AndTree
				if(result.getLanguage().equals(language)) {
					return false;
				}
				
			} else {
				result.getNotLanguages().add(language);
			}
		} else {
			//if there is already a language specified
			if(result.getLanguage() != null) {
				//if they are not the same return false
				//this means that no result can derive from this AndTree
				if(!result.getLanguage().equals(language)) {
					return false;
				}
				
			} else {
				result.setLanguage(language);
			}
		}
		return true;
	}
	
	private static boolean checkCollection(AndTree result, boolean not, String collection) {
		collection = removeQuotes(collection);
		if(not) {
			//if there is already a collection specified
			if(result.getCollection() != null) {
				//if they are the same return false
				//this means that no result can derive from this AndTree
				if(result.getCollection().equals(collection)) {
					return false;
				}
				
			} else {
				result.getNotCollections().add(collection);
			}
		} else {
			//if there is already a collection specified
			if(result.getCollection() != null) {
				//if they are not the same return false
				//this means that no result can derive from this AndTree
				if(!result.getCollection().equals(collection)) {
					return false;
				}
				
			} else {
				result.setCollection(collection);
			}
		}
		
		return true;
	}
	
	private static String[] splitTerms(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term.trim().split("\\s+");
	}
	
	private static String removeQuotes(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term;
	}

	public static GCQLNode createGCQLNodeFromMatchedFactors(ArrayList<GCQLCondition> mFactors,
			HashMap<String, HashSet<String>> colLangs) {
		//first examine if all the conditions refer to the geosearch relation
		boolean allConditionsGeo = areAllConditionsGeo(mFactors);
		
		//if all the conditions are geosearch
		if(allConditionsGeo) {
			
			return createTreeFromGeoConditionsAndColLangs(mFactors, colLangs);
			
		} else {
			//in this case we have other conditions
			//first we will create the tree that describes the collection-language conditions
			GCQLNode colLanguageNode = createColLangTree(colLangs);
			//then create a root node containing the actual conditions tree in the right side
			GCQLBooleanNode root = createRootFromAndConditions(mFactors);
			//connect the col-lang conditions in the left side
			root.left = colLanguageNode;
			
			return root;
		}
	}
	
	/**
	 * returns true only if all the conditions have the geosearch relation
	 * @param conditions - the conditions to be examined
	 * @return true only if all the conditions have the geosearch relation / false otherwise
	 */
	private static boolean areAllConditionsGeo(ArrayList<GCQLCondition> conditions) {
		boolean foundNonGeo = false;
		for(int i=0; i<conditions.size(); i++) {
			GCQLCondition current = conditions.get(i);
			//if this relation is not geosearch
			if(!current.getTerm().getRelation().getBase().equals(Constants.GEOSEARCH)) {
				foundNonGeo = true;
				//if this isn't the first relation to be examined
				if(i > 0) {
					//warn about the fact that a source must answer for mixed conditions(geo + something else)
					logger.error("It appears that a source must answer mixed conditions(geo + something else)");
				}
				break;
			}
		}
		
		return !foundNonGeo;
	}
	
	private static GCQLNode createTreeFromGeoConditionsAndColLangs(ArrayList<GCQLCondition> geoConditions, 
			HashMap<String, HashSet<String>> colLangs) {
		
		//Note that geo conditions are not inverted in this level.
		//When a NOT flag is added in a geo condition, a modifier for the 
		//geosearch relation is used for this not flag(handled at the source level)
		//This action is performed during the PushOrUpNotDown stage
		
		GCQLNode firstNode = null;
		GCQLOrNode orNode = null;
		//for all the collections
		for(Entry<String, HashSet<String>> colLang : colLangs.entrySet()) {
			String collectionID = colLang.getKey();
			//for all the languages of this collection
			for(String language : colLang.getValue()) {
				
				GCQLTermNode leaf = null;
				GCQLAndNode andNode = null;
				//for all the geosearch conditions
				for(GCQLCondition condition : geoConditions) {
					GCQLTermNode newNode = TreeTransformer.GCQLCondition.cloneTerm(condition.getTerm());
					//add modifiers for colID and language
					Modifier colModifier = new Modifier(Constants.COLIDMODIFIER, "=", "\"" + collectionID + "\"");
					Modifier langModifier = new Modifier(Constants.LANGMODIFIER, "=", "\"" + language + "\"");
					newNode.getRelation().getModifiers().add(colModifier);
					newNode.getRelation().getModifiers().add(langModifier);
					
					//if there is no leaf node yet
					if(leaf == null) {
						leaf = newNode;
					} else {
						//if there is no andNode yet
						if(andNode == null) {
							andNode = new GCQLAndNode();
							andNode.left = leaf;
							andNode.right = newNode;
						} else {
							GCQLAndNode newAndNode = new GCQLAndNode();
							newAndNode.left = andNode;
							newAndNode.right = newNode;
							andNode = newAndNode;
						}
					}
				}
				//if there is no other node yet
				if(firstNode == null) {
					if(andNode == null) {
						firstNode = leaf;
					} else {
						firstNode = andNode;
					}
				} else {
					//if there is no orNode yet
					if(orNode == null) {
						orNode = new GCQLOrNode();
						orNode.left = firstNode;
						if(andNode == null) {
							orNode.right = leaf;
						} else {
							orNode.right = andNode;
						}
					} else {
						GCQLOrNode newOrNode = new GCQLOrNode();
						newOrNode.left = orNode;
						if(andNode == null) {
							newOrNode.right = leaf;
						} else {
							newOrNode.right = andNode;
						}
						orNode = newOrNode;
					}
				}
			}				
		}
		//if there is no OR
		if(orNode == null) {
			return firstNode;
		} else {
			return orNode;
		}
	}
	
	private static GCQLNode createColLangTree(HashMap<String, HashSet<String>> colLangs) {
		//for all the collections
		GCQLNode firstNode = null;
		GCQLNode colLanguageNode = null;
		for(Entry<String, HashSet<String>> colLang : colLangs.entrySet()) {
			String collectionID = colLang.getKey();
			//for all the languages of this collection
			GCQLTermNode leaf = null;
			GCQLOrNode orNode = null;
			for(String language : colLang.getValue()) {
				GCQLTermNode current = new GCQLTermNode();
				current.setIndex(Constants.LANGUAGE_FIELD);
				GCQLRelation newRelation = new GCQLRelation();
				newRelation.setBase(Constants.EQUALS);
				current.setRelation(newRelation);
				current.setTerm("\"" + language + "\"");
				if(leaf == null) {
					leaf = current;
				} else {
					if(orNode == null) {
						orNode = new GCQLOrNode();
						orNode.left = leaf;
						orNode.right = current;
					} else {
						GCQLOrNode newOrNode = new GCQLOrNode();
						newOrNode.left = orNode;
						newOrNode.right = current;
						orNode = newOrNode;
					}
				}
			}
			//create a term node for the collection ID
			GCQLTermNode collectionTermNode = new GCQLTermNode();
			collectionTermNode.setIndex(Constants.COLLECTION_FIELD);
			GCQLRelation newRelation = new GCQLRelation();
			newRelation.setBase(Constants.EQUALS);
			collectionTermNode.setRelation(newRelation);
			collectionTermNode.setTerm("\"" + collectionID + "\"");
			
			//combine the collection with the languages
			GCQLAndNode andNode = new GCQLAndNode();
			andNode.left = collectionTermNode;
			if(orNode == null) {
				andNode.right = leaf;
			} else {
				andNode.right = orNode;
			}
			
			//if there is no other node yet
			if(firstNode == null) {
				firstNode = andNode;
			} else {
				if(colLanguageNode == null) {
					GCQLOrNode colLangOrNode = new GCQLOrNode();
					colLangOrNode.left = firstNode;
					colLangOrNode.right = andNode;
					colLanguageNode = colLangOrNode;
				} else {
					GCQLOrNode colLangOrNode = new GCQLOrNode();
					colLangOrNode.left = colLanguageNode;
					colLangOrNode.right = andNode;
					colLanguageNode = colLangOrNode;
				}
			}
		}
		
		if(colLanguageNode == null) {
			return firstNode;
		} else {
			return colLanguageNode;
		}
	}
	
	private static GCQLBooleanNode createRootFromAndConditions(ArrayList<GCQLCondition> conditions) {
		//first examine if all the conditions are inverted
		boolean foundAtLeastOne = false;
		//indicates the first position of a non-inverted condition(if any)
		int pos = 0;
		for(GCQLCondition cond : conditions) {
			if(!cond.isNot()) {
				foundAtLeastOne = true;
				break;
			}
			pos++;
		}
		
		//if all the conditions are inverted the root node will be a NOT
		GCQLBooleanNode root = null;
		if(!foundAtLeastOne) {
			root = new GCQLNotNode();
		} else {
			//else it will be an AND
			root = new GCQLAndNode();
			//set the pos condition first
			GCQLCondition tmp = conditions.remove(pos);
			conditions.add(0, tmp);
		}
		
		GCQLTermNode firstNode = null;
		GCQLNode rightNode = null;
		for(GCQLCondition cond : conditions) {
			
			GCQLTermNode currentNode = TreeTransformer.GCQLCondition.cloneTerm(cond.getTerm());
			
			if(firstNode == null) {
				firstNode = currentNode;
			} else {
				GCQLBooleanNode newNode = null;
				if(foundAtLeastOne) {
					if(cond.isNot()) {
						newNode = new GCQLNotNode();
					} else {
						newNode = new GCQLAndNode();
					}
				} else {
					newNode = new GCQLOrNode();
				}
				if(rightNode == null) {
					newNode.left = firstNode;
					newNode.right = currentNode;
					rightNode = newNode;
				} else {
					newNode.left = rightNode;
					newNode.right = currentNode;
					rightNode = newNode;
				}
			}
		}
		
		//connect the outcome to the right child of the root
		if(rightNode == null) {
			root.right = firstNode;
		} else {
			root.right = rightNode;
		}
		
		return root;
	}
	
	public static GCQLNode createGCQLNodeFromAndTrees(
			ArrayList<AndTree> andTrees, HashMap<String, HashSet<String>> leafColLangs) {
		
		GCQLNode firstNode = null;
		GCQLBooleanNode orNode = null;
		
		//for all the andTrees
		for(AndTree tree : andTrees) {
			
			GCQLNode currentNode = null;
			
			//create a map for this collection-language
			String collectionID = tree.getCollection(); 
			String language = tree.getLanguage();
			HashMap<String, HashSet<String>> colLang = new HashMap<String, HashSet<String>>();
			HashSet<String> lang = new HashSet<String>();
			lang.add(language);
			colLang.put(collectionID, lang);
			
			//put this collection-language in the map
			HashSet<String> languageSet = leafColLangs.get(collectionID);
			if(languageSet == null) {
				languageSet = new HashSet<String>();
				leafColLangs.put(collectionID, languageSet);
			}
			languageSet.add(language);
			
			//examine if all the conditions of this tree are geo conditions
			if(areAllConditionsGeo(tree.getConditions())) {
				currentNode = createTreeFromGeoConditionsAndColLangs(tree.getConditions(), colLang);
			} else {
				//first we will create the tree that describes the collection-language condition
				GCQLNode colLanguageNode = createColLangTree(colLang);
				//then create a root node containing the actual conditions tree in the right side
				GCQLBooleanNode newNode = createRootFromAndConditions(tree.getConditions());
				//connect the col-lang condition in the left side
				newNode.left = colLanguageNode;
				currentNode = newNode;
			}
			
			if(firstNode == null) {
				firstNode = currentNode;
			} else {
				if(orNode == null) {
					orNode = new GCQLOrNode();
					orNode.left = firstNode;
					orNode.right = currentNode;
				} else {
					GCQLOrNode newNode = new GCQLOrNode();
					newNode.left = orNode;
					newNode.right = currentNode;
					orNode = newNode;
				}
			}
		}
		
		if(orNode == null) {
			return firstNode;
		} else {
			return orNode;
		}
	}
}
