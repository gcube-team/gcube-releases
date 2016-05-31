package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.CollectionBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.FullTextIndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.MgmtPropertiesBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.RunningInstanceBean;

import com.google.gwt.user.client.rpc.RemoteService;

/** A RemoteService interface for the ManagementService following GWT RPC design */
public interface ManagementService extends RemoteService {

	/**
	 * A method to get all the available FullTextIndexManagement service running instances
	 * @return the list of running instances
	 */
	public List<RunningInstanceBean> getRunningInstances();
	
    /**
     * A method to get all collection beans of collections gathered from DIS
     * (real collections) along with all collection beans of collectionIDs
     * indices are stated to belong to (in their collectionID property) but
     * which do not exist in reality (fake collections)
     * 
     * @return - a List of the requested CollectionsByNameBeans containing the
     *         Collection information along with Index information (in the for
     *         of IndexBeans) for all indices under each collection.
     */
    public List<CollectionBean> getCollections();

    /**
     * A method to get IndexBeans for every Index connected to a specific
     * Collection
     * 
     * @param indexID -
     *            the ID of the Collection to get the Indices from
     * @return - an array of IndexBeans for all indices connected to the
     *         Collection
     */
    public IndexBean[] getIndices(String collectionID);

    /**
     * A method to get FullTextIndexTypeBeans for every fulltext index type
     * available in the VO
     * 
     * @return - the list of full text index type IDs
     */
    public List<FullTextIndexTypeBean> getAvailableIndexTypeIDs();
    
    /**
     * A method to get the IndexTypeID of an Index
     * 
     * @param indexID - The ID of the Index of which to get the IndexTypeID
     * @return the IndexTypeID of the index
     */
    public String getIndexTypeID(String indexID);

    /**
     * A method used to get information about the ResourceProperties of an Index
     * 
     * @param indexID -
     *            The ID of the Index of which to get the ResourceProperties
     * @return - The resource properties for the specified Index
     */
    public MgmtPropertiesBean getResourceProperties(String indexID);

    /**
     * Creates a new Index for a specified collection
     * 
     * @param indexID -
     *            The ID of cluster. Null if an ID should be
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
     * @return the IndexID of the newly created Index
     */
    public String createIndex(String clusterID, String collectionID, String RIEPR);

    /**
     * Removes/destroys an Index
     * 
     * @param indexID -
     *            The ID of the Index to destroy
     * @return The number of Index resources destroy
     */
    public Boolean removeIndex(String indexID, String collectionID);

    /**
     * Retrieves all the indexType descriptions from the IS.
     * 
     * @return the indexType descriptions
     */
    public Map<String, IndexTypeBean> getAllIndexTypes();
    
    /**
     * Saves an indexType to the IS.
     * 
     * @param idxType the indexType description
     * @return the saved indexType resource ID
     */
    public String saveIndexType(IndexTypeBean idxType);

    /**
     * Deletes an indexType from the IS.
     * 
     * @param idxType the indexType description
     */
    public void deleteIndexType(IndexTypeBean idxType);
    
    public List<String> query(String queryString, String indexID);
    
    public String updateIndex(String indexID, String collectionID, String rsLocator);
    
}
