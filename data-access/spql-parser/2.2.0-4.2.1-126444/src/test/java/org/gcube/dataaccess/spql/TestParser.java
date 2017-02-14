package org.gcube.dataaccess.spql;

import java.util.List;
import java.util.Map.Entry;

import org.antlr.runtime.*;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.Term;
import org.gcube.dataaccess.spql.model.TermType;
import org.gcube.dataaccess.spql.model.error.QueryError;
import org.gcube.dataaccess.spql.model.having.HavingExpression;
import org.gcube.dataaccess.spql.model.where.Condition;
import org.gcube.dataaccess.spql.model.where.WhereExpression;


public class TestParser {

	public static void main(String args[]) {
		String[] queries = new String[]{
				/*"'a', 'b' as ScientificName, 'mario' as CN in Obis, gbif where lowerBound is 1.2,1.0 AND fromDate is 12/31/23 return Taxon",
        		"'Mola mola', 'Abra alba' as ScientificName in Obis, GBIF return Occurrence",
        		"'Mola mola' as SN, 'white shark' as CN return Taxon",
        		"'white shark' as CommonName in Obis where lowerBound is 43.718823,10.422635",
        		"'Carcharodon carcharias' as ScientificName, 'shark' as CommonName in GBIF",*/
				//"SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product HAVING taxon > 0",
				//"SEARCH BY SN 'sarda sarda', 'Carcharodon carcharias' EXPAND WITH OBIS IN ITIS WHERE eventDate >= '2000/01/01' AND eventDate <= '2005/01/01' RETURN Occurrence",
				//"SEARCH BY SN 'sarda sarda', CN 'sardina' RESOLVE WITH Obis"
				//"SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product HAVING xpath(\"//product[type='TAXON' and counter>0]\") && exl(\"'true'=='true'\") && lucio(\"oh\")",
				//"SEARCH BY SN 'sarda' EXPAND RETURN Product HAVING xpath(\"//parent[lower-case(rank)='genus' and lower-case(scientificName)='scombridae']\")"
				//"SEARCH BY SN 'Carcharodon carcharias' in OBIS RETURN Product HAVING xpath(\"//product[type='OCCURRENCE' and counter>0]\")",
				"SEARCH BY SN 'Animalia' UNFOLD WITH OBIS in GBIF RETURN OCCURRENCE"
		};
		for (String query:queries) {
			try {
				Query result = SPQLQueryParser.parse(query);
				printQuery(result);
				//parseQuery(query);
				System.out.println();
			} catch (ParserException e)
			{
				e.printStackTrace();
				for (QueryError error:e.getErrors()) System.out.println(error.getErrorMessage());
			}
		}

	}

	protected static void parseQuery(String query)
	{
		System.out.println("Parsing "+query);
		SPQLLexer lex = new SPQLLexer(new ANTLRStringStream(query));

		CommonTokenStream tokens = new CommonTokenStream(lex);

		SPQLParser parser = new SPQLParser(tokens);


		try {
			Query result = parser.query();

			printQuery(result);


		} catch (RecognitionException e) {
			System.out.println("Error parsing "+query);
			e.printStackTrace();
		}
		System.out.println();
		System.out.println();
	}

	protected static void printQuery(Query query)
	{

		System.out.println("Search By:");
		for (Term term:query.getTerms()) {
			System.out.println("  Type: "+term.getType());
			System.out.println("   Words: "+term.getWords());
			if (term.getResolveClause()!=null) System.out.println("   ResolveClause: "+term.getResolveClause().getDatasources());
			if (term.getExpandClause()!=null) System.out.println("   ExpandClause: "+term.getExpandClause().getDatasources());
			if (term.getUnfoldClause()!=null) System.out.println("   UnfoldClause: "+term.getUnfoldClause().getDatasource());
		}
		System.out.println();

		System.out.println("In: "+(query.getDatasources()!=null?query.getDatasources():""));
		System.out.println();

		System.out.println("Where:");
		WhereExpression whereExpression = query.getWhereExpression();
		if (whereExpression!=null) {
			for (Condition condition:whereExpression.getConditions()) System.out.println("  "+condition.getParameter()+" "+condition.getOperator()+" "+condition.getValue().getTextValue());
		}
		System.out.println();

		System.out.println("Return: "+query.getReturnType());
		System.out.println();

		System.out.println("Having: ");
		HavingExpression havingExpression = query.getHavingExpression();
		if (havingExpression!=null) System.out.println("  "+havingExpression.getExpression());            
		System.out.println();

	}
}