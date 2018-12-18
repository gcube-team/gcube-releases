package org.gcube.spatial.data.sdi.model.metadata;

import java.util.HashSet;

public class TemplateInvocationBuilder {

	// Constants
	public static final class THREDDS_ONLINE{
		public static final String ID="THREDDS_ONLINE_RESOURCES";
		public static final String HOSTNAME="hostname";
		public static final String FILENAME="filename";
		public static final String CATALOG="catalog";
	}
	
	
	
	
	private HashSet<TemplateInvocation> invocations=new HashSet<TemplateInvocation>();
	
	
	public TemplateInvocationBuilder threddsOnlineResources(String hostname, String filename, String catalog){
		TemplateInvocation toAdd=new TemplateInvocation();
		toAdd.setToInvokeTemplateID(THREDDS_ONLINE.ID);
		
		toAdd.addParameter(THREDDS_ONLINE.HOSTNAME, hostname);
		toAdd.addParameter(THREDDS_ONLINE.FILENAME, filename);
		toAdd.addParameter(THREDDS_ONLINE.CATALOG, catalog);		
		invocations.add(toAdd);
		return this;
	}
	
	public HashSet<TemplateInvocation> get(){
		return invocations;
	}
}

