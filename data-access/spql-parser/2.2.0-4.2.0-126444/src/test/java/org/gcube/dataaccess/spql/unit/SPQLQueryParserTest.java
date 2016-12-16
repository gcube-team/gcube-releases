/**
 * 
 */
package org.gcube.dataaccess.spql.unit;

import static org.junit.Assert.*;

import org.antlr.runtime.debug.DebugParser;
import org.gcube.dataaccess.spql.ParserException;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.ExpandClause;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.Term;
import org.gcube.dataaccess.spql.model.TermType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SPQLQueryParserTest {

	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test
	public void testParse_SN() throws ParserException {
		String queryText = "SEARCH BY SN 'sarda sarda'";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());
		assertTrue(query.getTerms().size() == 1);

		//one term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sarda sarda", term.getWords().get(0));

		assertNull(term.getExpandClause());
		assertNull(term.getResolveClause());

		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());

		//no having expression specified
		assertNull(query.getHavingExpression());

		//no return type specified
		assertNull(query.getReturnType());
	}
	
	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test
	public void testParse_SNs() throws ParserException {
		String queryText = "SEARCH BY SN 'sarda sarda', 'Calendula officinalis'";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());
		assertTrue(query.getTerms().size() == 1);

		//one term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 2);
		assertEquals("sarda sarda", term.getWords().get(0));
		assertEquals("Calendula officinalis", term.getWords().get(1));

		assertNull(term.getExpandClause());
		assertNull(term.getResolveClause());

		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());

		//no having expression specified
		assertNull(query.getHavingExpression());

		//no return type specified
		assertNull(query.getReturnType());
	}

	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test
	public void testParse_SN_EXPAND() throws ParserException {
		String queryText = "SEARCH BY SN 'sarda sarda' EXPAND";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());
		assertTrue(query.getTerms().size() == 1);

		//one term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sarda sarda", term.getWords().get(0));

		assertNotNull(term.getExpandClause());
		assertNull(term.getResolveClause());

		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());

		//no having expression specified
		assertNull(query.getHavingExpression());

		//no return type specified
		assertNull(query.getReturnType());
	}
	
	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test
	@Ignore
	public void testParse_SN_EXPAND_datasource() throws ParserException {
		String queryText = "SEARCH BY SN 'sarda sarda' EXPAND OBIS";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());
		assertTrue(query.getTerms().size() == 1);

		//one term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sarda sarda", term.getWords().get(0));

		assertNotNull(term.getExpandClause());
		
		ExpandClause expandClause = term.getExpandClause();
		assertTrue(expandClause.getDatasources().size() == 1);
		assertEquals("OBIS", expandClause.getDatasources().get(0));
		
		assertNull(term.getResolveClause());

		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());

		//no having expression specified
		assertNull(query.getHavingExpression());

		//no return type specified
		assertNull(query.getReturnType());
	}

	@Test
	public void testParse_SN_CN() throws ParserException {
		String queryText = "SEARCH BY SN 'sarda sarda', CN 'sardina' RESOLVE";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());

		//two terms
		assertTrue(query.getTerms().size() == 2);

		//first term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sarda sarda", term.getWords().get(0));

		assertNull(term.getExpandClause());
		assertNull(term.getResolveClause());
		
		//second term
		term = query.getTerms().get(1);
		assertNotNull(term);
		assertEquals(TermType.COMMON_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sardina", term.getWords().get(0));

		assertNull(term.getExpandClause());
		assertNotNull(term.getResolveClause());


		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());

		//no having expression specified
		assertNull(query.getHavingExpression());

		//no return type specified
		assertNull(query.getReturnType());
	}
	
	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test(expected=ParserException.class)
	public void testParse_RESOLVE_EXCEPTION() throws ParserException {
		
		String queryText = " SEARCH BY SN 'sarda' RESOLVE EXPAND RETURN Product HAVING xpath(\"//parent[lower-case(rank)='genus' and lower-case(scientificName)='scombridae']\")";
		Query query = SPQLQueryParser.parse(queryText);
	}
	
	/**
	 * Test method for {@link org.gcube.dataaccess.spql.SPQLQueryParser#parse(java.lang.String)}.
	 * @throws ParserException 
	 */
	@Test
	public void testParse_HAVING() throws ParserException {
		
		String queryText = " SEARCH BY SN 'sarda'";
		Query query = SPQLQueryParser.parse(queryText);

		//Query
		assertNotNull(query);

		//almost one term
		assertNotNull(query.getTerms());
		assertTrue(query.getTerms().size() == 1);

		//one term
		Term term = query.getTerms().get(0);
		assertNotNull(term);
		assertEquals(TermType.SCIENTIFIC_NAME, term.getType());

		assertTrue(term.getWords().size() == 1);
		assertEquals("sarda", term.getWords().get(0));

		assertNull(term.getExpandClause());
		assertNull(term.getResolveClause());

		//no datasource specified
		assertTrue(query.getDatasources().size() == 0);

		//no where expression specified
		assertNull(query.getWhereExpression());
		
		//no return type specified
		assertNull(query.getReturnType());

		//no having expression specified
		assertNull(query.getHavingExpression());

	}


}
