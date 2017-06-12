/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 4, 2015
 */
public class ListContact<T extends ContactLogin> extends ArrayList<T> implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 544202687567940083L;

	/**
	 * 
	 */
	public ListContact() {
	}
	
	/* (non-Javadoc)
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		
		if(o==null)
			return false;
		
		ContactLogin contact = (ContactLogin) o;
		
		if(contact.getLogin()==null)
			return false;
		

		for (int i = 0; i < this.size(); i++){
			
			ContactLogin log = get(i);
            if (log.getLogin()!=null && log.getLogin().compareTo(contact.getLogin())==0)
                return true;
    	}
		
		return false;
	}
}
