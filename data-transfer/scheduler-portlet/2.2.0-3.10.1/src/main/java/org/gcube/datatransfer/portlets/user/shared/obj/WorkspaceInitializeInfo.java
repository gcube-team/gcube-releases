package org.gcube.datatransfer.portlets.user.shared.obj;

import java.io.Serializable;

import org.gcube.common.homelibrary.home.workspace.Workspace;

import com.thoughtworks.xstream.XStream;

public class WorkspaceInitializeInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	//------------------------
	
	protected static XStream xstream = new XStream();
	
	protected Workspace workspace;
	
	public WorkspaceInitializeInfo(){
	}
	
	public String toXML(){
		return xstream.toXML(this);
	}

	public Workspace getWorkspace() {
		return workspace;
	}
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	
}
