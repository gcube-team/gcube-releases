package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.CollectionBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.FullTextIndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.MgmtPropertiesBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.RunningInstanceBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/** An asynchronous interface for the ManagementService following GWT RPC design */
public interface ManagementServiceAsync {

	/**
	 * A method to get all the available FullTextIndexManagement service running instances
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getRunningInstances(AsyncCallback<List<RunningInstanceBean>> callback);
	
    void getCollections(AsyncCallback<List<CollectionBean>> callback);

    /**
     * A method to get IndexBeans for every Index connected to a specific
     * Collection
     * 
     * @param indexID -
     *            the ID of the Collection to get the Indices from
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void getIndices(String collectionID, AsyncCallback<IndexBean[]> callback);

    /**
     * A method to get FullTextIndexTypeBeans for every fulltext index type
     * available in the VO
     * 
     * @return - the list of full text index type IDs
     * @throws Exception - Server side exception as dictated by GWTs RPC framework
     */
    public void getAvailableIndexTypeIDs(AsyncCallback<List<FullTextIndexTypeBean>> callback);
    
    /**
     * A method to get the IndexTypeID of an Index
     * 
     * @param indexID -
     *            The ID of the Index of which to get the IndexTypeID
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void getIndexTypeID(String indexID, AsyncCallback<String> callback);

    /**
     * A method used to get information about the ResourceProperties of an Index
     * 
     * @param indexID -
     *            The ID of the Index of which to get the ResourceProperties
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void getResourceProperties(String indexID, AsyncCallback<MgmtPropertiesBean> callback);
   

    /**
     * Creates a new Index for a specified collection
     * 
     * @param clusterID -
     *            The ID of the cluster to be created. Null if an ID should be
     *            asigned automatically.
     * @param collectionID -
     *            the CollectionID of the Collection the Index should belong to
     * @param indexTypeID -
     *            the IndexType of the Index to create (NULL allowed if ROWSETs
     *            used to update this index contain indexTypeID)
     * @param contentType -
     *            the Content Type of the Index to create (usually MetaData or
     *            RealData)
     * @param RIEPR - the EPR of the Running Instance to use for the index creation
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void createIndex(String clusterID, String collectionID, String RIEPR, AsyncCallback<String> callback);

    /**
     * Removes/destroys an Index
     * 
     * @param indexID -
     *            The ID of the Index to destroy
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void removeIndex(String indexID, String collectionID, AsyncCallback<Boolean> callback);

	void query(String queryString, String indexID,
			AsyncCallback<List<String>> callback);

	void updateIndex(String indexID, String collectionID, String rsLocator,
			AsyncCallback<String> callback);


    /**
     * Retrieves all the indexType descriptions from the IS.
     * 
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
   public void getAllIndexTypes(AsyncCallback<Map<String, IndexTypeBean>> callback);
    
    /**
     * Saves an indexType to the IS.
     * 
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void saveIndexType(IndexTypeBean idxType, AsyncCallback<String> callback);

    /**
     * Deletes an indexType from the IS.
     * 
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
     */
    public void deleteIndexType(IndexTypeBean idxType, AsyncCallback<Void> callback);
}
