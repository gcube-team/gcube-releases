package org.gcube.portlets.user.workspace.client.view;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class StringNameFilterModel extends BaseModelData {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringNameFilterModel() {

	  }

	  public StringNameFilterModel(String name) {
	    setName(name);
	  }

	  public String getName() {
	    return get("name");
	  }
	  
	  public void setName(String name) {
	    set("name", name);
	  }

}
