package org.gcube.dataaccess.databases.lexer;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

/**
 * Class that allows to filter queries no read-only compliant considering the
 * MySQL database and by means of a lexical analysis
 */

public class MySQLLexicalAnalyzer extends LexicalAnalyzer {

	

	public MySQLLexicalAnalyzer() {

		ArrayList<String> BlackList = new ArrayList<String>();  // Keywords' blacklist
		BlackList = super.getBlackList();

		updateBlackList(BlackList);

	}

	// update the keywords' blacklist
	private void updateBlackList(ArrayList<String> BlackList) {

		// Mysql's keywords
		BlackList.add("RENAME");
		BlackList.add("REPLACE");
		BlackList.add("LOAD DATA INFILE");
		BlackList.add("CALL");
		BlackList.add("HANDLER");
		BlackList.add("UNLOCK");
		BlackList.add("DEALLOCATE PREPARE");
		BlackList.add("OPEN");
		BlackList.add("CLOSE");
		BlackList.add("BACKUP");
		BlackList.add("CHECK");
		BlackList.add("CHECKSUM");
		BlackList.add("OPTIMIZE");
		BlackList.add("REPAIR");
		BlackList.add("RESTORE");
		BlackList.add("CACHE");
		BlackList.add("FLUSH");
		BlackList.add("KILL");
		BlackList.add("LOAD INDEX INTO CACHE");
		BlackList.add("PURGE BINARY LOGS");
//		BlackList.add("RESET");

		AnalysisLogger.getLogger().debug(
				"MySQLLexicalAnalyzer->: blacklist updated");

	}

}
