package org.gcube.application.framework.search.library.util;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class QuerySanitizer {

	private static Logger logger = LoggerFactory.getLogger(QuerySanitizer.class);

	
	public static String sanitizeQuery(final String query) {
		if(query==null)
			return null;
		
		List<String> q = Splitter.on("\"").trimResults().splitToList(query);

		if (q.size() % 2 != 1) {
			logger.warn("number of quotes should be even");
			throw new IllegalArgumentException("number of quotes should be even");
		}

		List<String> sanitizedQuery = Lists.newArrayList();

		String subTerm = null;
		for (int i = 0; i != q.size(); ++i) {
			subTerm = q.get(i);

			if (i % 2 == 0) // out of quotes
				sanitizedQuery.addAll(sanitizeSubQuery(subTerm));
			else
				// in quotes
				sanitizedQuery.add("\"" + subTerm + "\"");
		}

		return Joiner.on(" ").join(sanitizedQuery);
	}

	static List<String> sanitizeSubQuery(final String query) {

		String sanitized = query;//StringEscapeUtils.escapeHtml(query);

		String queryWithoutSymbols = CharMatcher.anyOf(reservedSymbolsCharset)
				.replaceFrom(sanitized, "");

		List<String> terms = Splitter.on(CharMatcher.WHITESPACE)
				.omitEmptyStrings().trimResults()
				.splitToList(queryWithoutSymbols);

		List<String> sanitizedTerms = Lists.newArrayList();

		for (String term : terms) {
			if (reservedKeywords.contains(term.toLowerCase()))
				sanitizedTerms.add("\"" + term + "\"");
			else
				sanitizedTerms.add(term);
		}

		return sanitizedTerms;
	}

	final static Set<String> reservedKeywords = Sets.newHashSet("and", "or",
			"not", "prox", "fuse", "sortby", "project");

	final static List<String> reservedSymbols = Lists.newArrayList(",", ".",
			"-", "&", ")", "(", "]", "[", "=", "==", ">", "<", "<=", ">=",
			"<>", "/");

	final static CharSequence reservedSymbolsCharset = Joiner.on("").join(
			reservedSymbols);

}
