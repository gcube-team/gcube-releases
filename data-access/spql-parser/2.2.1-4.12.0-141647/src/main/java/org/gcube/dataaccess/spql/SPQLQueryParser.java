package org.gcube.dataaccess.spql;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.gcube.dataaccess.spql.model.Query;
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

		SPQLLexer lex = new SPQLLexer(new ANTLRInputStream(query));

		CommonTokenStream tokens = new CommonTokenStream(lex);

		SPQLParser parser = new SPQLParser(tokens);
		final List<String> errors = new ArrayList<String>();
		parser.addErrorListener(new ANTLRErrorListener() {

			@Override
			public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2,
					int arg3, final String arg4, RecognitionException arg5) {
				errors.add(arg4);

			}

			@Override
			public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2,
					int arg3, int arg4, ATNConfigSet arg5) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2,
					int arg3, BitSet arg4, ATNConfigSet arg5) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3,
					boolean arg4, BitSet arg5, ATNConfigSet arg6) {
				// TODO Auto-generated method stub

			}
		});

		Query result = parser.query().result;
		if (!errors.isEmpty()) throw new ParserException("There are semantic error in the query: "+query, errors);

		return result;

	}

}
