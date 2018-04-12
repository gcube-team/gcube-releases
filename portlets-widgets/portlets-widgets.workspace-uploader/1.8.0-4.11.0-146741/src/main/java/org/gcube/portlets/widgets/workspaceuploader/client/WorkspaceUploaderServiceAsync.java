package org.gcube.portlets.widgets.workspaceuploader.client;

import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface WorkspaceUploaderServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 4, 2015
 */
public interface WorkspaceUploaderServiceAsync
{

    
    /**
     * Gets the upload status.
     *
     * @param uploaderId the uploader id
     * @param callback the callback
     * @return the upload status
     */
    void getUploadStatus(String uploaderId, AsyncCallback<WorkspaceUploaderItem> callback );
    
    
    /**
     * Item exists in workpace folder.
     *
     * @param parentId the parent id
     * @param itemName the item name
     * @param callback the callback
     */
    void itemExistsInWorkpaceFolder(String parentId, String itemName, AsyncCallback<String> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * Aug 4, 2015
     */
    public static final class Util 
    { 
        private static WorkspaceUploaderServiceAsync instance;

        /**
         * Gets the single instance of Util.
         *
         * @return single instance of Util
         */
        public static final WorkspaceUploaderServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (WorkspaceUploaderServiceAsync) GWT.create( WorkspaceUploaderService.class );
            }
            return instance;
        }

        /**
         * Instantiates a new util.
         */
        private Util()
        {
            // Utility class should not be instantiated
        }
    }


	void getWorkspaceId(AsyncCallback<String> callback);
}
