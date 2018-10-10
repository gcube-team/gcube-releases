package org.gcube.common.homelibary.model.versioning;

import java.util.Calendar;

public interface WorkspaceVersion {
	
	public String getName();
	
	public Calendar getCreated();
	
	public String getUser();
	
	public String getRemotePath();

	public long getSize();
	
	public boolean isCurrentVersion();
	
	
}
