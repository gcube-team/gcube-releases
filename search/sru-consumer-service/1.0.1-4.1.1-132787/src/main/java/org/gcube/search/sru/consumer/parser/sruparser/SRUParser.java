package org.gcube.search.sru.consumer.parser.sruparser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.gcube.search.sru.consumer.parser.sruparser.tree.GCQLNode;

public class SRUParser {

	private static Logger logger = Logger.getLogger(SRUParser.class);
	
	public GCQLNode parse(String query, String lang, List<String> allIndexes)
	{
		SRUParserLexer lexer = new SRUParserLexer(new ANTLRStringStream(query));
		SRUParserParser parser = new SRUParserParser(new CommonTokenStream(lexer));
		
		try {
			GCQLNode q = parser.sruQuery(lang, allIndexes);
			return q;
		} catch (RecognitionException e) {
			logger.error("Error while parsing: ",e);
		}
		return null;
	}
	
	public static void main(String[] args){
		String input = "\"\"";
		
		input = "(gDocCollectionID == \"faoCollection\")";
		
		input = "((((allIndexes = \"Joe\") and (title proximity \" invorves \")) not (gDocCollectionLang == \"fr\")) or ((gDocCollectionID == \"A\") and ((title any \"Will\") or (author any \"Norm\"))))";

//		input = "((geo geosearch \"11 7 20 100\") and ((type exact \"new\") and ((geo geosearch \"-2 -6 8 4\") and ((gDocCollectionLang == \"en\")" 
//				+ " and (((desci any \"new\") and (gDocCollectionID == \"C\")) or ((abstract exact \"new\") and ((spec any \"new\") or (tech any \"new\"))))))))";
		
		//input = "((c0b3f995-e51b-49fd-8413-47077791aa53 == \"*\") and (overlaps(cdcdcdcdc,[[3,4],[4,5],[44.5,44.3]]))) project dfdfdfdf  dfdfdfd asssaa sdsds  c0b3f995-e51b-49fd-8413-47077791aa53/distinct sortby c0b3f995-e51b-49fd-8413-47077791aa53 DESC";
		
		List<String> paok = new ArrayList<String>();
		List<String> paokara = new ArrayList<String>();
		paok.add("paokara");
		paok.add("paokia");
		paok.add("paoktzis");
		
		GCQLNode q = new SRUParser().parse(input, "paok", paokara);
		System.out.println(q);
		System.out.println(q.toCQL());

	}

}