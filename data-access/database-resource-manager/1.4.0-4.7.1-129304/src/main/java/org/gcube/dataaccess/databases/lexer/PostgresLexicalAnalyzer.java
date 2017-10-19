package org.gcube.dataaccess.databases.lexer;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

/**
 * Class that allows to filter queries no read-only compliant considering the
 * Postgres database and by means of a lexical analysis
 */
public class PostgresLexicalAnalyzer extends LexicalAnalyzer {

//	private ArrayList<String> BlackList = new ArrayList<String>(); // Keywords'

	// blacklist

	public PostgresLexicalAnalyzer() {
		ArrayList<String> BlackList = new ArrayList<String>();  // Keywords' blacklist
		BlackList = super.getBlackList();

		updateBlackList(BlackList);

	}

	// update the keywords' blacklist
	private void updateBlackList(ArrayList<String> BlackList) {
		
		 // Keywords'
		// blacklist
				
		BlackList.add("COPY");
		BlackList.add("COMMENT");
		BlackList.add("SELECT INTO");
		BlackList.add("UNLISTEN");
		BlackList.add("VACUUM");
		BlackList.add("VALUES");
		BlackList.add("SECURITY LABEL");
		BlackList.add("REASSIGN OWNED");
		BlackList.add("ABORT");
		BlackList.add("CHECKPOINT");
		BlackList.add("CLOSE");
		BlackList.add("CLUSTER");
		BlackList.add("DEALLOCATE");
		BlackList.add("DISCARD");
		BlackList.add("END");
		BlackList.add("LISTEN");
		BlackList.add("LOAD");
		BlackList.add("MOVE");
		BlackList.add("NOTIFY");
		BlackList.add("REFRESH MATERIALIZED VIEW");
		BlackList.add("REINDEX");
//		BlackList.add("RESET");

		// BlackList.add("SET ROLE");

		// BlackList.add("SET SESSION AUTHORIZATION");

		// BlackList.add("SET TRANSACTION");

		// BlackList.add("SET CONSTRAINTS");

		AnalysisLogger.getLogger().debug(
				"PostgresLexicalAnalyzer->: blacklist updated");

	}

}
