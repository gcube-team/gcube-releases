/**
 * 
 */
package org.gcube.portlets.user.results.client;

import java.util.List;
import java.util.TreeMap;

import org.gcube.portlets.user.results.client.components.TreeNode;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.model.BasketSerializable;
import org.gcube.portlets.user.results.client.model.Client_DigiObjectInfo;
import org.gcube.portlets.user.results.client.model.ResultNumber;
import org.gcube.portlets.user.results.client.model.ResultsContainer;
import org.gcube.portlets.user.results.client.util.QueryDescriptor;
import org.gcube.portlets.user.results.shared.GenericTreeRecordBean;
import org.gcube.portlets.user.results.shared.ObjectType;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <code> ResultsetServiceAsync </code> async version of  <code> ResultsetService </code>
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (0.1) 
 */
public interface ResultsetServiceAsync {
	
	
	/**
	 * Check whether the porlet has been loaded after a search or not
	 * 
	 * return true if the user performed a search, false otherwise
	 */
	public void isSearchActive(AsyncCallback<Boolean> callback);
	
	/**
	 * return A <code>ResultsContainer</code> (Vector<ResultObj>, Optional paramters) which contains the current yet shown results (if any)
	 *
	 */
	public void getResultsFromSession(AsyncCallback<ResultsContainer> callback);
	
	/**
	 * 
	 * @param mode m
	 * @param callback c
	 */	
	public void getResultFromSearchService(int mode, AsyncCallback<ResultsContainer> result);
	/**
	 * used to get the additional info for the digital objects,
	 * @param currPosition in resultset
	 */
	public void getDigitalObjectInitialInfo(int currPosition, AsyncCallback<Client_DigiObjectInfo> result);
	
	
	
	
	/**
	 * used to get the additional info for the digital objects given a oid
	 * @param objectid the oid
	 */
	//public void getDigitalObjectOnDemand(String objectid, String belongsTo, BasketModelItemType type, AsyncCallback<Client_DigiObjectInfo> result);
	
	
	/**
	 * return the url of the thumbnail given the oid
	 * @param oid
	 * @return
	 */
	//public void getThumbnail(String oid, AsyncCallback<String> result);
	
	/**
	 * 
	 * @param callback
	 * @return
	 */
	public void getCollectionNames(AsyncCallback<String[]> callback);
	
	/**
	 * 
	 * @param qid
	 */
	@SuppressWarnings("unchecked")
	public void loadResults(String qid, AsyncCallback callback);
	
	/**
	 * 
	 * @return
	 */
	public void getWorkspaceTree(AsyncCallback<TreeNode> callback);
	
	
	/**
	 * return the id of the default basket
	 * @param callback
	 */
	public void getDefaultBasket(AsyncCallback<String> callback); 
	
	/**
	 * 
	 * @param basketId
	 */
	public void getBasketContent(String basketId, AsyncCallback<List<BasketModelItem>> callback);
	
	/**
	 * 
	 * @return
	 */
	public void readBasketFromSession(AsyncCallback<BasketSerializable> callback);
	
	/**
	 * add a single item in session
	 * @param item item to add
	 */
	public void storeBasketItemInSession(BasketModelItem item, AsyncCallback<Boolean> callback);
	
	/**
	 * 
	 * @param toRemove item to remove
	 * @return remove result
	 */
	public void removeBasketItemFromSession(BasketModelItem toRemove, AsyncCallback<Boolean> callback);
	
	/**
	 * 
	 * @param CollectionId c 
	 * @param OIDForAnn c
	 * @param TitleOfTheDocument c
	 * @param callback c
	 */
	//public void getAnnotations(String CollectionId, String OIDForAnn, String TitleOfTheDocument, AsyncCallback<Void> callback);
	/**
	 * This method stores on the session some information that is needed from the Content Viewer portlet in order to operate
	 * @param objectID the OID of the Object
	 */
	//public void setContentViewerOID(String objectID, AsyncCallback<Void> callback);
	/**
	 * 
	 * @param CollectionId c
	 * @param objectID c
	 * @param callback c
	 */
	//public void getMetadata(String collectionId, String MetadataCollectionId, String objectID, AsyncCallback<Void> callback);
	/**
	 * Calls the home library to save the current basket in session permanently	 * 
	 */
	public void saveBasket(AsyncCallback<Boolean> callback);
	/**
	 * 
	 * @param callback
	 */
	public void getQueryDescFromSession(AsyncCallback<QueryDescriptor> callback);
	
	/**
	 * 
	 * @param md_oid
	 * @param MetadataCollectionId
	 * @return
	 */
	//public void getMetadataFormatAndLang(String oid, String belongsTo, AsyncCallback<MetadataDescriptor> callback);
	
	/**
	 * 
	 * @param resNo
	 */
	public void getResultsNo(AsyncCallback<ResultNumber> resNo);
	
	/**
	 * 
	 * @param queryTerm
	 */
	public void submitSimpleQuery(String queryTerm, AsyncCallback<Void> resNo);
	
	public void getObjectsPayload(String objectURI, AsyncCallback<String> callback);
	
	public void getObjectInfo(String objectURI, AsyncCallback<GenericTreeRecordBean> callback);
	
	public void getContentURLs(GenericTreeRecordBean recordBean, AsyncCallback<TreeMap<String, List<String>>> callback);
	
	public void transformMetadata(String payload, ObjectType type, AsyncCallback<String> callback);
	
	public void getCurrentQueryIndexNumber(AsyncCallback<Integer> callback);
}
