package org.gcube.portlets.admin.sepeditor.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class InitInfo implements Serializable {
	private ArrayList<String> scopes;
	private FilledRuntimeResource rr2edit;
	public InitInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InitInfo(ArrayList<String> scopes, FilledRuntimeResource rr2edit) {
		super();
		this.scopes = scopes;
		this.rr2edit = rr2edit;
	}
	public ArrayList<String> getScopes() {
		return scopes;
	}
	public void setScopes(ArrayList<String> scopes) {
		this.scopes = scopes;
	}
	public FilledRuntimeResource getRr2edit() {
		return rr2edit;
	}
	public void setRr2edit(FilledRuntimeResource rr2edit) {
		this.rr2edit = rr2edit;
	}

	
}
