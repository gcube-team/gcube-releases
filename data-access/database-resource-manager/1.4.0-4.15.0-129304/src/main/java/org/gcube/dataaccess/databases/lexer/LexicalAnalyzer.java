package org.gcube.dataaccess.databases.lexer;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

/**
 * Class that allows to filter queries no read-only compliant by means of a
 * lexical analysis
 */
public class LexicalAnalyzer {

	private ArrayList<String> BlackList = new ArrayList<String>(); // Keywords'

	// blacklist

	// Method that performs the lexical analysis
	public boolean analyze(String query) throws Exception {

		boolean NotAllowed = false;

		// building the keywords' blacklist
		// the following keywords are common for MySQL and PostgreSQL databases.

		BlackList.add("INSERT");
		BlackList.add("DELETE");
		BlackList.add("UPDATE");
		BlackList.add("CREATE");
		BlackList.add("ALTER");
		BlackList.add("DROP");
		BlackList.add("GRANT");
		BlackList.add("REVOKE");
		BlackList.add("TRUNCATE");

		BlackList.add("DO");
		BlackList.add("START TRANSACTION");
		BlackList.add("COMMIT");
		BlackList.add("BEGIN");
		BlackList.add("ROLLBACK");
		BlackList.add("SET");
		BlackList.add("SAVEPOINT");
		BlackList.add("RELEASE SAVEPOINT");
		BlackList.add("LOCK");

		BlackList.add("DECLARE");
		BlackList.add("PREPARE");
		BlackList.add("FETCH");
		// BlackList.add("EXPLAIN");
		BlackList.add("ANALYZE");
		BlackList.add("EXECUTE");
		BlackList.add("SHOW");
		BlackList.add("RESET");

		String queryParsed = null;

		int NumOfBlackList = BlackList.size();

		AnalysisLogger.getLogger().debug(
				"LexicalAnalyzer->blacklist size: " + NumOfBlackList);

		// System.out.println("size blacklist: " + NumOfBlackList);

		// parse the query using the regular expressions
		queryParsed = LexicalAnalyzer.parseRegularExpressions(query);

		// check if the query contains a word defined in the blacklist
		NotAllowed = check(queryParsed);
		
		AnalysisLogger.getLogger().debug(
				"LexicalAnalyzer->query not allowed: " + NotAllowed);

		if (NotAllowed == true) {

			throw new Exception("Only read-only queries are allowed");

		}

		return NotAllowed;

	}

	// Method that checks if the query contains a word defined in the blacklist
	public boolean check(String query) {

		boolean NotAllowed = false;

		check_tokens: for (int j = 0; j < BlackList.size(); j++) {

			// System.out.println("BlackList word: " + BlackList.get(j));

			// keyword check with regex regular expression

			String Keyword = BlackList.get(j);

			String regex = ".*\\b" + Keyword.replaceAll(" +", "[ ]\\+")
					+ "\\b.*";

			if (query.toUpperCase().matches(regex)) {

				NotAllowed = true;

				break check_tokens;

			}

			// if (Keyword.contains(" ")) {
			//
			// String[] arrayKeyword = Keyword.split(" ");
			//
			// int i;
			// // boolean notContained = false;
			//
			// String Regex = "";
			// String regexKeyword = regexKeyword = ".*\\b" + arrayKeyword[0]
			// + "\\s*";
			// Regex = regexKeyword;
			//
			// for (i = 1; i < arrayKeyword.length; i++) {
			//
			// if (i == arrayKeyword.length - 1) {
			//
			// Regex = Regex + arrayKeyword[i] + "\\b.*";
			//
			// } else {
			//
			// Regex = Regex + arrayKeyword[i] + "\\s*";
			//
			// }
			//
			// }
			//
			// if (query.toUpperCase().matches(Regex)) {
			//
			// // notContained = true;
			//
			// AnalysisLogger.getLogger().debug(
			// "LexicalAnalyzer-> : the query contains the word in the blacklist "
			// + BlackList.get(j));
			//
			// NotAllowed = true;
			//
			// break check_tokens;
			//
			// }
			//
			// } else {
			//
			// String regexKeyword = ".*\\b" + BlackList.get(j) + "\\b.*";
			//
			// if (query.toUpperCase().matches(regexKeyword)) {
			//
			// AnalysisLogger.getLogger().debug(
			// "LexicalAnalyzer-> : the query contains the word in the blacklist "
			// + BlackList.get(j));
			//
			// NotAllowed = true;
			//
			// break check_tokens;
			//
			// }
			//
			// }

		}

		return NotAllowed;

	}

	public ArrayList<String> getBlackList() {

		return BlackList;

	}

	private static String parseRegularExpressions(String phrase) {

		//replace the \n with the " "
		phrase = phrase.replaceAll("\n", " ");
		
		// replacement of the punctuation characters
		// String todelete = "[\\]\\[!#$%&()*+,./:;<=>?@\\^_{|}~-]";
		String todelete = "[\\]\\[!#$%&()*+,./:;<=>?@\\^{|}~-]";
		phrase = phrase.replaceAll(todelete, " ");
		phrase = phrase.replaceAll("[ ]+", " ");

		AnalysisLogger.getLogger().debug(
				"LexicalAnalyzer-> : replacing query " + phrase);

		// elimination by means of a replacement of the word enclosed in '',
		// "",``
		String apex = "'.*'";
		phrase = phrase.replaceAll(apex, "");
		String apex2 = "\".*\"";
		phrase = phrase.replaceAll(apex2, "");
		String apex3 = "`.*`";
		phrase = phrase.replaceAll(apex3, "");

		AnalysisLogger.getLogger().debug(
				"LexicalAnalyzer-> : parsed string " + phrase);

		return phrase;

	}

}
