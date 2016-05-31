package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

public interface FieldsImageResources extends Resources  {
	
	 /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("field.png")
    ImageResource treeLeaf();
    
    /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("fieldslistclosed.png")
    ImageResource treeClosed();
    
    /**
     * Image for a tree leaf. In this context it displays a Field
     */
    @Source("fieldslistopened.png")
    ImageResource treeOpen();

	
}
