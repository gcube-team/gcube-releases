package search.library.util.cql.query.tree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import search.library.util.cql.parser.ParseException;
import search.library.util.cql.parser.SimpleNode;
import search.library.util.cql.parser.gCQLParser;

public class GCQLQueryTreeManager {
	
	//static gCQLParser parser = new gCQLParser(System.in);
	
	
	public static GCQLNode parseGCQLString(String gCQLStr) {
		//Surround the string with *
		gCQLParser parser = new gCQLParser(System.in);
		String newStr = "#" + gCQLStr + "#";
		InputStream is = stringToStream(newStr);
		//gCQLParser.ReInit(is);
		parser.ReInit(is);
		try {
			//SimpleNode parseJJtree = gCQLParser.Start();
			SimpleNode parseJJtree = parser.Start();
			GCQLNode gTree = createCQLQueryTreeFromJJTree(parseJJtree);
			System.out.println(gTree.toCQL());
			return gTree;
		} catch (ParseException e) {
			/* An exception occured during parsing.
			 * Print the error message on standard output.
			 */
			System.out.println("-------------------------------");
			System.out.println("Sorry, couldn't parse that.");
			System.out.println(e.getMessage());
			System.out.println("-------------------------------");
			e.printStackTrace();
		}
		return null;
	}
	
	public static void printTreeSerialization(GCQLNode head) {
		head.printNode(0);
	}
	
	/**
	 * This method gets the jjTree created by the GCQLParser and creates a GCQLTree. 
	 * The head of the tree is returned.
	 * The returned node will be an instance of GCQLBooleanNode/GCQLTermNode/GCQLSortNode/GCQLProjectNode.
	 * We don't consider the prefix assignment.
	 * @param simpleNode
	 * @return
	 */
	public static GCQLNode createCQLQueryTreeFromJJTree(SimpleNode simpleNode) {
		SimpleNode expressionNode = simpleNode;
		// Get the GCQLQuery node
		while (!expressionNode.toString().equals(OperationTypeConstants.gCQLQuery) && !expressionNode.toString().equals(OperationTypeConstants.cqlQuery)) {
			expressionNode = (SimpleNode) expressionNode.jjtGetChild(0);
		}
		
		GCQLNode mainTree;
		SimpleNode scopedClauseNode;
		GCQLSortNode sortNode = null;
		GCQLProjectNode projectNode = null;
		GCQLFuseNode fuseNode = null; 
		GCQLPrefixNode prefixNode = null;
		// we have the GCQLQuery node
		// check the number of the children of the query node
		int numChildren = expressionNode.jjtGetNumChildren();
		switch (numChildren) {
		case 0:
			// If we don't have any child, the empty string was given
			GCQLTermNode emptyTermNode = new GCQLTermNode();
			emptyTermNode.setTerm("");
			return emptyTermNode;
		case 1:
			// If we have only one child - this means that we just have a ScopedClause
			
			// get the ScopedClause node
			scopedClauseNode = (SimpleNode) expressionNode.jjtGetChild(0);
			mainTree = parseScopedClause(scopedClauseNode);
			return mainTree;
		case 2:
			// If we have two children - this means that we have also a SortBy or a Project clause or a Prefix Assignment
			// We need to create this node too and add it at the top of the tree as parent
			
			// get the ScopedClause node
			scopedClauseNode = (SimpleNode) expressionNode.jjtGetChild(0);
			mainTree = parseScopedClause(scopedClauseNode);
			
			// get the extension node
			SimpleNode extensionNode = (SimpleNode) expressionNode.jjtGetChild(1);
			// check if it is a project or a sortby extension
			if (extensionNode.toString().equals(OperationTypeConstants.sortSpec)) {
				sortNode = new GCQLSortNode();
				Vector<ModifierSet> sortIndexes = parseExtensionNode(extensionNode);
				sortNode.sortIndexes = sortIndexes;
				sortNode.subtree = mainTree;
				return sortNode;
			} else if (extensionNode.toString().equals(OperationTypeConstants.projectSpec)){
				projectNode = new GCQLProjectNode();
				Vector<ModifierSet> projectIndexes = parseExtensionNode(extensionNode);
				projectNode.projectIndexes = projectIndexes;
				projectNode.subtree = mainTree;
				return projectNode;
			} else if (extensionNode.toString().equals(OperationTypeConstants.fuseSpec)){
				fuseNode = new GCQLFuseNode();
				ModifierSet fuseMode = parseExtensionNode(extensionNode).firstElement();
				fuseNode.fuseMode = fuseMode;
				fuseNode.subtree = mainTree;
				return fuseNode;
			} else {
				// we have a prefix assignment
				// get the cqlQuery node
				SimpleNode cqlNode = (SimpleNode) expressionNode.jjtGetChild(1);
				// get the prefixAssignment node
				extensionNode = (SimpleNode)expressionNode.jjtGetChild(0);
				mainTree = createCQLQueryTreeFromJJTree(cqlNode);
				
				prefixNode = parsePrefixNode(extensionNode, mainTree);
				return prefixNode;
			}
		case 3:
			// If we have three children - this means that we have two of the SortBy, Project, Fuse clauses
			// We need to create these nodes too and add them at the top of the tree as parent
			
			// get the ScopedClause node
			scopedClauseNode = (SimpleNode) expressionNode.jjtGetChild(0);
			mainTree = parseScopedClause(scopedClauseNode);
			
			int firstChildType = 0;
			// get the 1st extension node
			SimpleNode extensionNode1 = (SimpleNode) expressionNode.jjtGetChild(1);
			// check if it is a project, a fuse or a sortby extension
			if (extensionNode1.toString().equals(OperationTypeConstants.sortSpec)) {
				sortNode = new GCQLSortNode();
				Vector<ModifierSet> sortIndexes = parseExtensionNode(extensionNode1);
				sortNode.sortIndexes = sortIndexes;
				sortNode.subtree = mainTree;
				firstChildType = 0;
			} else if (extensionNode1.toString().equals(OperationTypeConstants.projectSpec)){
				projectNode = new GCQLProjectNode();
				Vector<ModifierSet> projectIndexes = parseExtensionNode(extensionNode1);
				projectNode.projectIndexes = projectIndexes;
				projectNode.subtree = mainTree;
				firstChildType = 1;
			} else if (extensionNode1.toString().equals(OperationTypeConstants.fuseSpec)){
				fuseNode = new GCQLFuseNode();
				ModifierSet fuseMode = parseExtensionNode(extensionNode1).firstElement();
				fuseNode.fuseMode = fuseMode;
				fuseNode.subtree = mainTree;
				firstChildType = 2;
			}	
			// get the 1st extension node
			SimpleNode extensionNode2 = (SimpleNode) expressionNode.jjtGetChild(2);
			// check if it is a project, a fuse or a sortby extension
			if (extensionNode2.toString().equals(OperationTypeConstants.sortSpec)) {
				sortNode = new GCQLSortNode();
				Vector<ModifierSet> sortIndexes = parseExtensionNode(extensionNode2);
				sortNode.sortIndexes = sortIndexes;
				switch(firstChildType)
				{
				case 1:
					sortNode.subtree = projectNode;
					break;
				case 2:
					sortNode.subtree = fuseNode;
					break;
				}
				return sortNode;
			} else if (extensionNode2.toString().equals(OperationTypeConstants.projectSpec)){
				projectNode = new GCQLProjectNode();
				Vector<ModifierSet> projectIndexes = parseExtensionNode(extensionNode2);
				projectNode.projectIndexes = projectIndexes;
				switch(firstChildType)
				{
				case 0:
					projectNode.subtree = sortNode;
					break;
				case 2:
					projectNode.subtree = fuseNode;
					break;
				}
				return projectNode;
			} else if (extensionNode2.toString().equals(OperationTypeConstants.fuseSpec)){
				fuseNode = new GCQLFuseNode();
				ModifierSet fuseMode = parseExtensionNode(extensionNode2).firstElement();
				fuseNode.fuseMode = fuseMode;
				switch(firstChildType)
				{
				case 0:
					fuseNode.subtree = sortNode;
					break;
				case 1:
					fuseNode.subtree = projectNode;
					break;
				}
				return fuseNode;
			}			

			
		case 4:
			// IF we have four children - this means that we have also a SortBy, a Fuse and a Project clause
			// We need to create these nodes too and add them at the top of the tree as parents
			
			// get the ScopedClause node
			scopedClauseNode = (SimpleNode) expressionNode.jjtGetChild(0);
			mainTree = parseScopedClause(scopedClauseNode);
			
			// get the Sort node
			SimpleNode extensionSortNode = (SimpleNode) expressionNode.jjtGetChild(1);
			sortNode = new GCQLSortNode();
			Vector<ModifierSet> sortIndexes = parseExtensionNode(extensionSortNode);
			sortNode.sortIndexes = sortIndexes;
			sortNode.subtree = mainTree;
			
			// get the Project node
			SimpleNode extensionProjectNode = (SimpleNode) expressionNode.jjtGetChild(2);
			projectNode = new GCQLProjectNode();
			Vector<ModifierSet> projectIndexes = parseExtensionNode(extensionProjectNode);
			projectNode.projectIndexes = projectIndexes;
			projectNode.subtree = sortNode;
			
			// get the Fuse node
			SimpleNode extensionFuseNode = (SimpleNode) expressionNode.jjtGetChild(3);
			fuseNode = new GCQLFuseNode();
			ModifierSet fuseMode = parseExtensionNode(extensionFuseNode).firstElement();
			fuseNode.fuseMode = fuseMode;
			fuseNode.subtree = projectNode;
			
			return fuseNode;
			default:
				return null;
		}
	}
	
	private static Vector<ModifierSet> parseExtensionNode(SimpleNode extensionNode) {
		// get the number of children of the extension node
		// the number of children is the number of the indices (the size of the vector to be returned)
		int numChildren = extensionNode.jjtGetNumChildren();
		Vector<ModifierSet> vector = new Vector<ModifierSet>();
		for (int i = 0; i < numChildren; i++) {
			SimpleNode singleSpecNode = (SimpleNode)extensionNode.jjtGetChild(i);
			ModifierSet modSet = parseSingleSpecNode(singleSpecNode);
			vector.add(modSet);
		}
		return vector;
	}
	
	private static GCQLPrefixNode parsePrefixNode(SimpleNode prefixNode, GCQLNode mainTree) {
		int numChildren = prefixNode.jjtGetNumChildren();
		if (numChildren == 2) {
			SimpleNode prefixNd = (SimpleNode) prefixNode.jjtGetChild(0);
			String prefix = prefixNd.getText();
			SimpleNode uriNd = (SimpleNode) prefixNode.jjtGetChild(1);
			String uri = uriNd.getText();
			GCQLPrefixNode toReturn = new GCQLPrefixNode(prefix, uri, mainTree);
			return toReturn;
		} else {
			// only uri
			SimpleNode uriNd = (SimpleNode) prefixNode.jjtGetChild(0);
			String uri = uriNd.getText();
			GCQLPrefixNode toReturn = new GCQLPrefixNode("gcube", uri, mainTree);
			return toReturn;
		}
	}
	
	private static ModifierSet parseSingleSpecNode(SimpleNode singleSpecNode) {
		// get the number of children
		int numChildren = singleSpecNode.jjtGetNumChildren();
		SimpleNode indexNode;
		String indexName;
		ModifierSet modifierSet;
		switch (numChildren){
		case 1:
			// we just have an index
			// get the index node
			indexNode = (SimpleNode)singleSpecNode.jjtGetChild(0);
			indexName = indexNode.getText();
			modifierSet = new ModifierSet(indexName);
			return modifierSet;
		case 2:
			// we have an index and a modifier list
			// get the index node
			indexNode = (SimpleNode)singleSpecNode.jjtGetChild(0);
			indexName = indexNode.getText();
			modifierSet = new ModifierSet(indexName);
			// get the modifier list node
			SimpleNode modifierListNode = (SimpleNode)singleSpecNode.jjtGetChild(1);
			ArrayList<Modifier> modifiers = parseGCQLModifiersListNode(modifierListNode);
			modifierSet.setModifiers(modifiers);
			return modifierSet;
			default:
				return null;
		}
	}
	
	
	private static GCQLNode parseScopedClause(SimpleNode scopedClauseNode) {
		// check the number of children of the ScopedClause node
		int numChildren = scopedClauseNode.jjtGetNumChildren();
		
		switch (numChildren) {
		case 1:
			// we only have a SearchClause
			// get the SearchClause node
			SimpleNode searchClauseNode = (SimpleNode)scopedClauseNode.jjtGetChild(0);
			return parseSearchClauseNode(searchClauseNode);
		case 3:
			// we have a Boolean node that connects two Search Clauses
			// get the first search clause
			SimpleNode firstSearchClauseNode = (SimpleNode) scopedClauseNode.jjtGetChild(0);
			GCQLNode firstSearchClause = parseSearchClauseNode(firstSearchClauseNode);
			// get the second search clause
			SimpleNode secondSearchClauseNode = (SimpleNode) scopedClauseNode.jjtGetChild(2);
			GCQLNode secondSearchClause = parseSearchClauseNode(secondSearchClauseNode);
			// get the boolean node
			SimpleNode booleanNode = (SimpleNode) scopedClauseNode.jjtGetChild(1);
			GCQLBooleanNode booleanClause = parseBooleanGroupNode(booleanNode);
			
			// add the search clause nodes as children to the boolean node
			booleanClause.left = firstSearchClause;
			booleanClause.right = secondSearchClause;
			return booleanClause;
			default:
				return null;
		}
	}
	
	private static GCQLBooleanNode parseBooleanGroupNode(SimpleNode booleanGroupNode) {
		// check the number of children of the boolean group node
		int numChildren = booleanGroupNode.jjtGetNumChildren();
		SimpleNode booleanOperator;
		String booleanStr;
		switch (numChildren) {
		case 1:
			// we just have a boolean operator
			// get the Boolean node
			booleanOperator = (SimpleNode) booleanGroupNode.jjtGetChild(0);
			booleanStr = booleanOperator.getText();
			if (booleanStr.equals(OperationTypeConstants.booleanAnd)) {
				GCQLAndNode andNode = new GCQLAndNode();
				return andNode;
			} else if (booleanStr.equals(OperationTypeConstants.booleanOr)) {
				GCQLOrNode orNode = new GCQLOrNode();
				return orNode;
			} else if (booleanStr.equals(OperationTypeConstants.booleanNot)) {
				GCQLNotNode notNode = new GCQLNotNode();
				return notNode;
			} else {
				GCQLProxNode proxNode = new GCQLProxNode();
				return proxNode;
			}
		case 2:
			// we have a boolean operator and modifiers
			// get the Modifiers node 
			SimpleNode modifiersNode = (SimpleNode) booleanGroupNode.jjtGetChild(1);
			ArrayList<Modifier> modifiers = parseGCQLModifiersListNode(modifiersNode);
			
			// get the Boolean node
			booleanOperator = (SimpleNode) booleanGroupNode.jjtGetChild(0);
			booleanStr = booleanOperator.getText();
			if (booleanStr.equals(OperationTypeConstants.booleanAnd)) {
				GCQLAndNode andNode = new GCQLAndNode();
				ModifierSet modifSet = new ModifierSet(OperationTypeConstants.booleanAnd);
				modifSet.setModifiers(modifiers);
				andNode.ms = modifSet;
				return andNode;
			} else if (booleanStr.equals(OperationTypeConstants.booleanOr)) {
				GCQLOrNode orNode = new GCQLOrNode();
				ModifierSet modifSet = new ModifierSet(OperationTypeConstants.booleanOr);
				modifSet.setModifiers(modifiers);
				orNode.ms = modifSet;
				return orNode;
			} else if (booleanStr.equals(OperationTypeConstants.booleanNot)) {
				GCQLNotNode notNode = new GCQLNotNode();
				ModifierSet modifSet = new ModifierSet(OperationTypeConstants.booleanNot);
				modifSet.setModifiers(modifiers);
				notNode.ms = modifSet;
				return notNode;
			} else {
				GCQLProxNode proxNode = new GCQLProxNode();
				ModifierSet modifSet = new ModifierSet(OperationTypeConstants.booleanProx);
				modifSet.setModifiers(modifiers);
				proxNode.ms = modifSet;
				return proxNode;
			}
			default:
				return null;
		}
	}
	
	private static GCQLNode parseSearchClauseNode(SimpleNode searchClauseNode) {
		
		GCQLTermNode termNode = new GCQLTermNode();
		
		// check the number of the children of the SearchClause node 
		int numChildren = searchClauseNode.jjtGetNumChildren();
		
		SimpleNode searchTerm;
		String searchTermStr;
		switch (numChildren) {
		case 1:
			// we only have one SearchTerm OR a CQLQuery 
			// Get the node
			searchTerm = (SimpleNode)searchClauseNode.jjtGetChild(0);
			if (searchTerm.toString().equals(OperationTypeConstants.cqlQuery)) {
				// get the scoped clause node
				SimpleNode scopedClause = (SimpleNode)searchTerm.jjtGetChild(0);
				return parseScopedClause(scopedClause);
			}
			else {
				searchTermStr = searchTerm.getText();
				// create the GCQLTermNode
				termNode.setTerm(searchTermStr);
				return termNode;
			}
		case 3:
			// we have an index, a relation and a search term
			termNode = new GCQLTermNode();
			// get the index node
			SimpleNode tempNode = (SimpleNode) searchClauseNode.jjtGetChild(0);
			String index = tempNode.getText();
			termNode.setIndex(index);
			// get the relation node
			tempNode = (SimpleNode) searchClauseNode.jjtGetChild(1);
			GCQLRelation relation = parseGCQLRelationNode(tempNode);
			termNode.setRelation(relation);
			// get the search term node
			searchTerm = (SimpleNode)searchClauseNode.jjtGetChild(2);
			searchTermStr = searchTerm.getText();
			termNode.setTerm(searchTermStr);
			return termNode;
			default:
				return null;
		}
	}
	
	
	static GCQLRelation parseGCQLRelationNode(SimpleNode relNode) {
		GCQLRelation rel = new GCQLRelation();
		// check the number of children of the relation
		int numChildren = relNode.jjtGetNumChildren();
		SimpleNode comparitor;
		String relBase;
		
		switch (numChildren){
		case 1:
			// we have only a comparitor
			// get the comparitor node
			comparitor = (SimpleNode)relNode.jjtGetChild(0);
			relBase = comparitor.getText();
			rel.setBase(relBase);
			return rel;
		case 2:
			// we have a comparitor and modifiers also
			// get the comparitor node
			comparitor = (SimpleNode)relNode.jjtGetChild(0);
			relBase = comparitor.getText();
			rel.setBase(relBase);
			// get the ModifiersList node
			SimpleNode modifiersList = (SimpleNode)relNode.jjtGetChild(1);
			ArrayList<Modifier> modifiers = parseGCQLModifiersListNode(modifiersList);
			rel.setModifiers(modifiers);
			return rel;
			default:
				return null;
		}
	}
	
	static ArrayList<Modifier> parseGCQLModifiersListNode(SimpleNode modifiersListNode) {
		// get the number of children (modifiers) of the modifiers list
		int numModifiers = modifiersListNode.jjtGetNumChildren();
		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		for (int i = 0; i < numModifiers; i++) {
			// get the modifier node
			SimpleNode modif = (SimpleNode) modifiersListNode.jjtGetChild(i);
			Modifier modifier = parseGCQLModifierNode(modif);
			modifiers.add(modifier);
		}
		return modifiers;
	}
	
	
	static Modifier parseGCQLModifierNode(SimpleNode modifNode) {
		// get the number of children of the modifier node
		int numChildren = modifNode.jjtGetNumChildren();
		Modifier modifier;
		SimpleNode modifierNameNode;
		String modifName;
		switch (numChildren) {
		case 1:
			// we have just a modifier name
			modifierNameNode = (SimpleNode)modifNode.jjtGetChild(0);
			modifName = modifierNameNode.getText();
			modifier = new Modifier(modifName);
			return modifier;
		case 3:
			// we have a modifier name, a comparitor symbol and a modifier value
			modifierNameNode = (SimpleNode)modifNode.jjtGetChild(0);
			modifName = modifierNameNode.getText();
			// comparitor symbol
			SimpleNode tempNode = (SimpleNode)modifNode.jjtGetChild(1);
			String comparSymbol = tempNode.getText();
			tempNode = (SimpleNode) modifNode.jjtGetChild(2);
			String modifValue = tempNode.getText();
			
			modifier = new Modifier(modifName, comparSymbol, modifValue);
			return modifier;
			default:
				return null;
		}
	}
	
	
	private static InputStream stringToStream(String str) {
		/**
		 * Convert String to InputStream using ByteArrayInputStream
		 * class. This class constructor takes the string byte array
		 * which can be done by calling the getBytes() method.
		 */
		try {
			InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
			return is;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
