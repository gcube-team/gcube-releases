package search.library.util.cql.parser;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLFuseNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLSortNode;
import search.library.util.cql.query.tree.GCQLTermNode;

public class PrintJJTree {

	
	//static gCQLParser parser = new gCQLParser(System.in);
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		try {
//			/* Start parsing from the nonterminal "Start". */
//			SimpleNode parseTree = parser.Start();
//			
//			
//			
//			/* If parsing completed without exceptions, print the results in parse tree on standard output */
//			parseTree.dump("");
//			
//			System.out.println("**************");
//			printMyTree(parseTree,0);
//			
//			System.out.println("********************************************************");
//			System.out.println("********************************************************");
//			
//			GCQLNode headOFTree = GCQLQueryTreeManager.createCQLQueryTreeFromJJTree(parseTree);
//			String cry = headOFTree.toCQL();
//			System.out.println(cry);
//		} catch (Exception e) {
//			/* An exception occured during parsing.
//			 * Print the error message on standard output.
//			 */
//			System.out.println("-------------------------------");
//			System.out.println("Sorry, couldn't parse that.");
//			System.out.println(e.getMessage());
//			System.out.println("-------------------------------");
//			e.printStackTrace();
//		}
//		return;
//
//	}
	
	public static void main(String[] args) {
//		GCQLNode head = GCQLQueryTreeManager.parseGCQLString("\"dinosaur\" any fish sortBy dc.date/sort.descending dc.title/sort.ascending");
//		String query = "title any fish not ((author any sanderson and year within \"1999 2001\") or (allfields fuzzy Thyrwn or (description proximity \"15 about to burst\"))) project title author code";
//		GCQLNode head = GCQLQueryTreeManager.parseGCQLString("((geo geosearch/inclusion=0/ranker=\"GenericRanker false\"/refiner=\"TemporalRefinerMon Jul 11 17:51:28 GMT+02:00 2011 Mon Jul 11 17:51:28 GMT+02:00 2011\" \"-90.0 180.0 -90.0 -180.0 90.0 -180.0 90.0 180.0\") and (((((gDocCollectionID == cdabe220-a6ff-11e0-9d70-fda94ff03826) or (gDocCollectionID == cdabe220-a6ff-11e0-9d70-fda94ff03826))) and (gDocCollectionLang == en)))) project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687 b7f6b092-eed9-4a1f-b869-3f248034683c");
//		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(query);
//		GCQLNode head = GCQLQueryTreeManager.parseGCQLString("> dc = \"info:srw/context-sets/1/dc-v1.1\" dc.title any fish or dc.creator any sanderson ");
//		GCQLNode head = GCQLQueryTreeManager.parseGCQLString("((((882bcfc4-c756-42e1-b0d4-32bfb6be03a3 =/config:numOfResults=500 \"repName%da='a)\") and (b6b737d4-eaef-4666-abda-975842b02e6b = 180))and (gDocCollectionID == 227b6790-a30d-11e0-91ab-ca34f60d2e2d)) and (b3c8aaaf-8d83-4ed7-bbbf-247139e46c86 = 500)) sortby gDocCollectionID project 438fe479-2479-436a-ae56-be2a30c25792 rank panagiotis");

		String TESTQUERY1 = "((((author any \"Joe\") and (title proximity \" invorves \")) not (gDocCollectionLang == \"fr\")) or ((gDocCollectionID == \"A\") and ((title any \"Will\") or (author any \"Norm\"))))";
		String TESTQUERY2 = "((geo geosearch \"11 7 20 100\") and ((type exact \"new\") and ((geo geosearch \"-2 -6 8 4\") and ((gDocCollectionLang == \"en\")" 
				+ " and (((desc any \"new\") and (gDocCollectionID == \"C\")) or ((abstract exact \"new\") and ((spec any \"new\") or (tech any \"new\"))))))))";
		
		
		String q = "((ac6e40cd-f6b7-4e5e-a199-30e3944064e3 = tuna) and (((gDocCollectionID == 8b554f50-9add-11e2-91cf-988c12b92fe5) or (gDocCollectionID == d686c177-8d4c-4947-b85a-f6e8ef65620e)))) project * fuse tuna";
//		q= "((530669f8-f7fe-4b7e-a35d-eeebbba16925 == \"*\") and (gDocCollectionID == 7374617475733D756E707562)) project 530669f8-f7fe-4b7e-a35d-eeebbba16925/distinct";
//		q = "((82a37f5a-9b57-4ce7-b34c-17ff9fca9fa6 == \"*\") and (gDocCollectionID == 7374617475733D756E707562)) project 82a37f5a-9b57-4ce7-b34c-17ff9fca9fa6 1 2 3 4 5 6 sortby 7";
//		q = "((((gDocCollectionID == 8afa8050-7b7c-11e2-b8a4-e3f7b403b9a5) and (gDocCollectionLang == en))) and (977ec5d3-7a99-4262-8251-8332c4c16766 = salmon)) project f5c47c98-ffb1-4998-9126-3daed26155a0 4b9b2594-9ffe-4f40-8de1-698818dfecc0 2f3cc371-e505-48a0-8f8f-128edcdae38f 46f35fd0-bdb4-4bea-9900-6d4d92eb3f68 2cf87076-01f4-4423-bfc3-ff838b5451f9";
//		q = "((((((gDocCollectionID == \"8b554f50-9add-11e2-91cf-988c12b92fe5\") and (gDocCollectionLang == \"en\"))) and (977ec5d3-7a99-4262-8251-8332c4c16766 = fish))) or (((((gDocCollectionID == \"8b554f50-9add-11e2-91cf-988c12b92fe5\") and (gDocCollectionLang == \"en\"))) and (977ec5d3-7a99-4262-8251-8332c4c16766 = tuna)))) project f5c47c98-ffb1-4998-9126-3daed26155a0 4b9b2594-9ffe-4f40-8de1-698818dfecc0";
		q = "((gDocCollectionID == \"faoAutocompleteCollection\") and ((label = τόνος*) and (label = سمك))) project gDocCollectionID gDocCollectionLang label type";
		q = "((gDocCollectionID == \"faoCollection\") and ((technology_used = None) or ((technology_used = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((technology_used = non) or ((technology_used = hame&Atilde;&sect;ons) or ((technology_used = et) or ((technology_used = Lignes) or ((technology_used = specified) or ((technology_used = not) or ((technology_used = Line) or ((technology_used = Hook) or ((technology_used = Comoros) or ((technology_used = kwassa) or ((technology_used = Kwassa) or ((technology_used = Comoros) or ((technology_used = tuna) or ((technology_used = Yellowfin) or ((gear_used = None) or ((gear_used = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((gear_used = non) or ((gear_used = hame&Atilde;&sect;ons) or ((gear_used = et) or ((gear_used = Lignes) or ((gear_used = specified) or ((gear_used = not) or ((gear_used = Line) or ((gear_used = Hook) or ((gear_used = Comoros) or ((gear_used = kwassa) or ((gear_used = Kwassa) or ((gear_used = Comoros) or ((gear_used = tuna) or ((gear_used = Yellowfin) or ((type_of_vessel = None) or ((type_of_vessel = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((type_of_vessel = non) or ((type_of_vessel = hame&Atilde;&sect;ons) or ((type_of_vessel = et) or ((type_of_vessel = Lignes) or ((type_of_vessel = specified) or ((type_of_vessel = not) or ((type_of_vessel = Line) or ((type_of_vessel = Hook) or ((type_of_vessel = Comoros) or ((type_of_vessel = kwassa) or ((type_of_vessel = Kwassa) or ((type_of_vessel = Comoros) or ((type_of_vessel = tuna) or ((type_of_vessel = Yellowfin) or ((country = None) or ((country = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((country = non) or ((country = hame&Atilde;&sect;ons) or ((country = et) or ((country = Lignes) or ((country = specified) or ((country = not) or ((country = Line) or ((country = Hook) or ((country = Comoros) or ((country = kwassa) or ((country = Kwassa) or ((country = Comoros) or ((country = tuna) or ((country = Yellowfin) or ((species_english_name = None) or ((species_english_name = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((species_english_name = non) or ((species_english_name = hame&Atilde;&sect;ons) or ((species_english_name = et) or ((species_english_name = Lignes) or ((species_english_name = specified) or ((species_english_name = not) or ((species_english_name = Line) or ((species_english_name = Hook) or ((species_english_name = Comoros) or ((species_english_name = kwassa) or ((species_english_name = Kwassa) or ((species_english_name = Comoros) or ((species_english_name = tuna) or ((species_english_name = Yellowfin) or ((title = None) or ((title = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((title = non) or ((title = hame&Atilde;&sect;ons) or ((title = et) or ((title = Lignes) or ((title = specified) or ((title = not) or ((title = Line) or ((title = Hook) or ((title = Comoros) or ((title = kwassa) or ((title = Kwassa) or ((title = Comoros) or ((title = tuna) or ((title = Yellowfin) or ((text = None) or ((text = sp&Atilde;&copy;cifi&Atilde;&copy;s) or ((text = non) or ((text = hame&Atilde;&sect;ons) or ((text = et) or ((text = Lignes) or ((text = specified) or ((text = not) or ((text = Line) or ((text = Hook) or ((text = Comoros) or ((text = kwassa) or ((text = Kwassa) or ((text = Comoros) or ((text = tuna) or ((text = Yellowfin)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))) project gDocCollectionID gDocCollectionLang title text provenance country type_of_vessel gear_used technology_used species_english_name  S";
		GCQLNode head = GCQLQueryTreeManager.parseGCQLString(q);
		
		GCQLQueryTreeManager.printTreeSerialization(head);
		
		System.out.println(head.toCQL());
		
		System.out.println(excludeCollectionClause(head).toCQL());
		
		return;

	}
	
	
	private static String getRankMode(GCQLNode head) {
		if(head instanceof GCQLFuseNode)
		{
			String rankMode = ((GCQLFuseNode) head).getFuseMode().toCQL(); 
			head = ((GCQLFuseNode) head).subtree;
			return rankMode;
		}
		else if(head instanceof GCQLProjectNode)
			return getRankMode(((GCQLProjectNode) head).subtree);
		else if(head instanceof GCQLSortNode)
			return getRankMode(((GCQLSortNode) head).subtree);
		else
			return "no";
	}
	
	public static void printMyTree(SimpleNode node, int n) {
		int numChildren = node.jjtGetNumChildren();
		
		//System.out.println("Num children: " + numChildren);
		System.out.println();
		for (int i = 0; i < n; i++) {
			System.out.print(" ");
		}
		System.out.println(node.toString() + " ***** " + node.getText());


		int k = n+1;
		for (int i = 0; i < numChildren; i++) {
			printMyTree((SimpleNode)node.jjtGetChild(i), k);
		}
	}

	public static GCQLNode excludeCollectionClause(GCQLNode head)
	{
		if(head instanceof GCQLProjectNode)
		{
			((GCQLProjectNode) head).subtree = excludeCollectionClause(((GCQLProjectNode) head).subtree);
			return head;
		}
		else if(head instanceof GCQLFuseNode)
		{
			((GCQLFuseNode) head).subtree = excludeCollectionClause(((GCQLFuseNode) head).subtree);
			return head;
		}
		else if(head instanceof GCQLSortNode)
		{
			((GCQLSortNode) head).subtree = excludeCollectionClause(((GCQLSortNode) head).subtree);
			return head;
		}
		else if(head instanceof GCQLAndNode)
		{
			((GCQLAndNode) head).left = excludeCollectionClause(((GCQLAndNode) head).left);
			((GCQLAndNode) head).right = excludeCollectionClause(((GCQLAndNode) head).right);
			GCQLNode left = ((GCQLAndNode) head).left;
			GCQLNode right = ((GCQLAndNode) head).right;
			if(left == null && right == null)
				return null;
			else if(left == null)
				return right;
			else if(right == null)
				return left;
			else
				return head;
		}
		else if(head instanceof GCQLOrNode)
		{
			((GCQLOrNode) head).left = excludeCollectionClause(((GCQLOrNode) head).left);
			((GCQLOrNode) head).right = excludeCollectionClause(((GCQLOrNode) head).right);
			GCQLNode left = ((GCQLOrNode) head).left;
			GCQLNode right = ((GCQLOrNode) head).right;
			if(left == null && right == null)
				return null;
			else if(left == null)
				return right;
			else if(right == null)
				return left;
			else
				return head;
		}
		else if(head instanceof GCQLTermNode)
		{
			String index = ((GCQLTermNode)head).getIndex();
			if(index.equals("gDocCollectionID") || index.equals("gDocCollectionLang"))
				return null;
			else
				return head;
		}
		return head;	
	}
	
}
