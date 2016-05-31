package org.gcube.portlets.user.searchportlet.client;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

public interface CollectionsTreeImageResources extends Resources  {
	
	 /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("none.gif")
    ImageResource treeLeaf();
    
    /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("tree_closed.gif")
    ImageResource treeClosed();
    
    /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("tree_open.gif")
    ImageResource treeOpen();

	
}
