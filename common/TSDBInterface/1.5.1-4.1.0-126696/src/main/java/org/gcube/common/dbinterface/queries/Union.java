package org.gcube.common.dbinterface.queries;

public interface Union extends Selection {

	public void setRightQuery(Selection query) ;
	
	public void setLeftQuery(Selection query);
		
}
