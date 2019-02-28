package org.gcube.common.homelibary.model.versioning;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JCRVersion implements WorkspaceVersion{

	String name;
	Calendar created;
	String user;
	String remotePath;
	long size;
	boolean isCurrentVersion;

	
	@Override
	public boolean isCurrentVersion() {
		return isCurrentVersion;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Calendar getCreated() {
		return created;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getRemotePath() {
		return remotePath;
	}	

	@Override
	public long getSize() {
		return size;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public void setCurrentVersion(boolean isCurrentVersion) {
		this.isCurrentVersion = isCurrentVersion;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return String.format("[ versionID:%s, created:%s , createdBy:%s, remotePath:%s, size:%s]", name ,sdf.format(created.getTime()), user, remotePath, size);
	}


}
