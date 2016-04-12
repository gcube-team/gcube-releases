package org.gcube.portlets.user.results.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

public interface TreeImageResources extends Resources  {
	
//	 /**
//     * Image for a tree leaf.
//     */
//	@Source("leaf.png")
//    ImageResource treeLeaf();
    
    /**
     * Image for a closed node.
     */
    @Source("treeclosed.png")
    ImageResource treeClosed();
    
    /**
     * Image for an open node.
     */
    @Source("treeopened.png")
    ImageResource treeOpen();

	
}
