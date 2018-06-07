package org.gcube.common.storagehub.model.expressions.logical;

import org.gcube.common.storagehub.model.Path;
import org.gcube.common.storagehub.model.expressions.Expression;

public class ISDescendant implements Expression<Boolean>{
	
	private Path path;
	
	protected ISDescendant() {}
	
	public ISDescendant(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}
	
}
