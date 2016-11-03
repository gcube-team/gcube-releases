package org.gcube.data.tr;

import org.gcube.data.streams.generators.Processor;
import org.gcube.data.trees.data.Tree;

public class SourceDecorator extends Processor<Tree> {
	
	private final String sourceId;
	
	public SourceDecorator(String sourceId) {
		this.sourceId=sourceId;
	}
	
	@Override
	protected void process(Tree element) {
		element.setSourceId(sourceId);
	}
}