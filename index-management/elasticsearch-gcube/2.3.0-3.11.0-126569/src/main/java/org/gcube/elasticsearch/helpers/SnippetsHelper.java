package org.gcube.elasticsearch.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.gcube.indexmanagement.common.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class SnippetsHelper {
	private static final Logger logger = LoggerFactory.getLogger(SnippetsHelper.class);

	private static final SnippetsComparator snippetsComparator = new SnippetsComparator();
	public static final String SNIPPET_TOKEN = "...";
	public static final List<String> NOT_HIGHLIGHTED_FIELDS = Arrays.asList(IndexType.COLLECTION_FIELD,
			IndexType.DOCID_FIELD, IndexType.LANGUAGE_FIELD);

	private static class SnippetsComparator implements Comparator<String> {
		
		@Override
		public int compare(String o1, String o2) {
			return o2.length() - o1.length();
		}
	}

	public static String createSnippetString(SearchHit hit, int maxSnippetCount) {
		List<String> snippets = new ArrayList<String>();

		logger.trace("Creating snippets for hit : " + hit);

		for (Entry<String, HighlightField> snippet : hit.getHighlightFields().entrySet()){
			try {
				for (Text text : snippet.getValue().getFragments())
					snippets.add(text.string().trim());
			} catch (Exception e) {
				logger.warn("error processing snippet for : " + snippet.getKey() + " with value " + snippet.getValue());
			}
		}

		logger.trace("snippets found : " + snippets);

		String snippet = joinTop(snippets, SNIPPET_TOKEN, maxSnippetCount);
		
		logger.trace("snippets string : " + snippet);

		return snippet;
	}
	
	public static String joinTop(Iterable<String> list, String token, Integer count){
//		list = Iterables.filter(
//				 list,
//			     Predicates.notNull()
//			  );
		List<String> newList = byLengthOrdering
				.greatestOf(list, count);
		
		String snippet = Joiner
				.on(token)
				.join(newList);
		
		return snippet;
	}
	
	private static Ordering<String> byLengthOrdering = new Ordering<String>() {
	  public int compare(String left, String right) {
	    return Ints.compare(left.length(), right.length());
	  }
	};
	
}
