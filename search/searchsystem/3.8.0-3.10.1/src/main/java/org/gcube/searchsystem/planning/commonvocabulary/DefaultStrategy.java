package org.gcube.searchsystem.planning.commonvocabulary;

import search.library.util.cql.query.tree.GCQLNode;

public class DefaultStrategy {
	
	private DefaultStrategy() {
		
	}
	
	public static GCQLNode addDefaultProjections(GCQLNode subtree) {
		//the default behavior for now is to leave the cql query as it is
		//this can more efficient for a source, since it won't have to do
		//filtering on the fields to add in the output. The alternative 
		//would be to project only to default fields(that should be 
		//presentable in all sources) like ObjectId and language 
		return subtree;
	}

}
