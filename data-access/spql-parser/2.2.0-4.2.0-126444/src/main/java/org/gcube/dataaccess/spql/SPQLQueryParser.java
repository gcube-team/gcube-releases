/**
 * 
 */
package org.gcube.dataaccess.spql;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.error.QueryError;
import org.gcube.dataaccess.spql.model.error.SyntaxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SPQLQueryParser {
	
	protected static Logger logger = LoggerFactory.getLogger(SPQLQueryParser.class);

	public static Query parse(String query) throws ParserException
	{
		logger.debug("parse query: {}",query);
		try {
			SPQLLexer lex = new SPQLLexer(new ANTLRStringStream(query));

			CommonTokenStream tokens = new CommonTokenStream(lex);

			SPQLParser parser = new SPQLParser(tokens);

			Query result = parser.query();
			List<QueryError> errors = result.check();
			if (!errors.isEmpty()) throw new ParserException("There are semantic error in the query: "+query, errors);
			
			return result;
			
		} catch(Throwable e)
		{
			logger.error("An error occurred parsing the query "+query, e);
			SyntaxError syntaxError = new SyntaxError(e.getMessage());
			throw new ParserException("There are syntax error in the query: "+query, syntaxError);
		}
	}

}
