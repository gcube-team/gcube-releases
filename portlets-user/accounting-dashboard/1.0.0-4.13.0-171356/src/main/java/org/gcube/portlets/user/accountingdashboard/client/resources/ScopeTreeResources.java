package org.gcube.portlets.user.accountingdashboard.client.resources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.CellTree.Style;

public interface ScopeTreeResources extends Resources {

	interface ScopeTreeStyle extends Style {
	    /**
	     * The path to the default CSS styles used by this resource.
	     */
	    String DEFAULT_CSS = "uiScopeTree.css";
	  }
	
	
    /**
     * The styles used in this widget.
     */
    @Source(ScopeTreeStyle.DEFAULT_CSS)
    ScopeTreeStyle cellTreeStyle();
    
    @Source("cellTreeSelectedBackgroundCustom.png")
	ImageResource cellTreeSelectedBackgroundCustom();
	
  }
