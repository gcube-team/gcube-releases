package org.gcube.data.analysis.tabulardata.service.tabular;

import java.util.Calendar;
import java.util.List;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;

public interface TabularResource extends MetadataHolder<TabularResourceMetadata<?>> {
	
	TabularResourceId getId();
	
	Calendar getCreationDate();
	
	String getTableType();
	
	TabularResourceType getTabularResourceType();
	
	boolean isValid();
	
	List<HistoryStep> getHistory();
	
	List<String> getSharedWithUsers();
	
	List<String> getSharedWithGroups();
	
	String getOwner();
	
	void finalize();
	
	boolean isFinalized();
	
	boolean isLocked();

	org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource getRemoteTabularResource();
	
	
}
