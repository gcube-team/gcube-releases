/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Nov 4, 2015
 */
public class ListContact<T extends InfoContactModel> extends ArrayList<T> implements Serializable{
	
	
	private static final long serialVersionUID = 544202687567940083L;

	public ListContact() {
	}

	public boolean contains(InfoContactModel infoContactModel) {
		
		if(infoContactModel==null)
			return false;
		
		
		if(infoContactModel.getLogin()==null)
			return false;
		

		for (int i = 0; i < this.size(); i++){
			
			InfoContactModel log = get(i);
            if (log.getLogin()!=null && log.getLogin().compareTo(infoContactModel.getLogin())==0)
                return true;
    	}
		
		return false;
	}
}
