package org.gcube.data.analysis.tabulardata.metadata;

import java.util.List;

public interface Identifiable {

	String getOwner();
	
	List<String> getScopes();
	
	List<String> getSharedWith();
}
