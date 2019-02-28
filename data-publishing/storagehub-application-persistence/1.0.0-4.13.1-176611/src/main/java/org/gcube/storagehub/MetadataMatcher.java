package org.gcube.storagehub;

import org.gcube.common.storagehub.model.Metadata;

public interface MetadataMatcher {
	
	public boolean check(Metadata metadata);
	
}
