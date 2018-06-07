/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.manager.polling;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 28, 2013
 *
 */
public class CachedTaskId {
	
	private String id;
	
	
	/**
	 * @param id
	 */
	public CachedTaskId(String id) {
		super();
		this.id = id;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj==null)
			return false;
		
		if(!(obj  instanceof CachedTaskId))
			return false;
		
		CachedTaskId ext = (CachedTaskId) obj;
		
		if(ext.getId()==null)
			return false;
			
		if(this.getId()==null)
			return false;
			
		if(ext.getId().compareToIgnoreCase(this.getId())==0)
			return true;
			
	
		return false;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	

}
