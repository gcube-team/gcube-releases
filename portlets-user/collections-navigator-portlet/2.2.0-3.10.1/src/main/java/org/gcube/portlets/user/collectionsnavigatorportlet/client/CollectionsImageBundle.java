package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

public interface CollectionsImageBundle extends Resources {
	
	@Source("tree_open.gif")
	ImageResource treeOpen();
    
    @Source("tree_closed.gif")
    ImageResource treeClosed();

}
