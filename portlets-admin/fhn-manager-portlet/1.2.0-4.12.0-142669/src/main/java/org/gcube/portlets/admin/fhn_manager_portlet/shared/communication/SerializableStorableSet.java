package org.gcube.portlets.admin.fhn_manager_portlet.shared.communication;

import java.util.HashSet;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class SerializableStorableSet implements IsSerializable {
	
	private HashSet<Storable> theSet=null;	

	
	public HashSet<Storable> getTheSet() {
		return theSet;
	}
	
	public void setTheSet(HashSet<Storable> theSet) {
		this.theSet = theSet;
	}
	public SerializableStorableSet() {
		// TODO Auto-generated constructor stub
	}

	public SerializableStorableSet(HashSet<Storable> theSet) {
		super();
		this.theSet = theSet;
	}
	
	
}
