package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.Engine;

public abstract class Chunk {

	
	protected Engine engine;
	
	public Chunk(Engine engine){
		this.engine = engine;
	}
	
}
