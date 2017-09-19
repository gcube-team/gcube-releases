package org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers;

import java.util.HashMap;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.YasmeenMatcher;


public class MixedLexicalMatcher extends YasmeenMatcher{

	public MixedLexicalMatcher(String sandboxfolder, double threshold, int maxResults, HashMap<String, String> parameters) {
		super(sandboxfolder, threshold, maxResults,parameters);
	}

	@Override
	protected String getMatchlets() {
		return "-mSn -man -may -mSnt 0.0001 -mant 0.0001 -mayt 0.0001";
	}

	@Override
	protected String getLexicalDistancesWeights() {
		return "-law 100:100:100";
	}

	@Override
	protected String getStemming() {
		return "-mNgn -mNsn -man -may -mNgnt 0.0001 -mNsnt 0.0001 -mant 0.0001 -mayt 0.0001";
	}
}
