package org.gcube.dataaccess.spql;

import org.antlr.v4.runtime.*;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.Term;
import org.gcube.dataaccess.spql.model.having.HavingExpression;
import org.gcube.dataaccess.spql.model.where.Condition;
import org.gcube.dataaccess.spql.model.where.WhereExpression;
import org.junit.Test;


public class TestParser {

	@Test
	public void parseQuery() throws Exception{
		String[] queries = new String[]{
				/*"'a', 'b' as ScientificName, 'mario' as CN in Obis, gbif where lowerBound is 1.2,1.0 AND fromDate is 12/31/23 return Taxon",
        		"'Mola mola', 'Abra alba' as ScientificName in Obis, GBIF return Occurrence",
        		"'Mola mola' as SN, 'white shark' as CN return Taxon",
        		"'white shark' as CommonName in Obis where lowerBound is 43.718823,10.422635",
        		"'Carcharodon carcharias' as ScientificName, 'shark' as CommonName in GBIF",*/
				"SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product",
				"SEARCH BY SN 'sarda sarda', 'Carcharodon carcharias' EXPAND WITH OBIS IN ITIS WHERE eventDate >= '2000/01/01' AND eventDate <= '2005/01/01' RETURN Occurrence",
				"SEARCH BY SN 'sarda sarda', CN 'sardina' RESOLVE WITH Obis",
				"SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product HAVING xpath(\"//product[type='TAXON' and counter>0]\") && exl(\"'true'=='true'\") && lucio(\"oh\")",
				"SEARCH BY SN 'sarda' EXPAND RETURN Product HAVING xpath(\"//parent[lower-case(rank)='genus' and lower-case(scientificName)='scombridae']\")",
				"SEARCH BY SN 'Carcharodon carcharias' in OBIS RETURN Product HAVING xpath(\"//product[type='OCCURRENCE' and counter>0]\")",
				"SEARCH BY SN 'sarda sarda' IN GBIF, SpeciesLink WHERE coordinate <= 40.0 , 40.0 AND coordinate >= -15.0 , 20.0 RETURN Product HAVING xpath(\"//product[type='Occurrence' and count>0]\")"
		};
		for (String query:queries) {
			Query result = SPQLQueryParser.parse(query);
			printQuery(result);
			//parseQuery(query);
			System.out.println();
		}

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