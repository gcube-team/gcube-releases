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

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * <code> ResultsetService </code> interface for server calls
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (0.1) 
 */
public interface ResultsetService extends RemoteService {

	/**
	 * Check whether the porlet has been loaded after a search or not
	 * 
	 * @return true if the user performed a search, false otherwise
	 */
	boolean isSearchActive();
	
	/**
	 * @return A <code>ResultsContainer</code> (Vector<ResultObj>, Optional paramters)  which contains the current yet shown results (if any)
	 *
	 */
	ResultsContainer getResultsFromSession();
	
	/**
	 * @param mode 0 means "get first results", 1 means "get previous results" and 2 means "get next results".
	 * @return a Vector containing the html representation of each record.
	 */
	ResultsContainer getResultFromSearchService(int mode);
	
	/**
	 * used to get the additional info for the digital objects,
	 * @param currPosition in resultset
	 * @return additional info (name, MimeType.....)
	 */
	Client_DigiObjectInfo getDigitalObjectInitialInfo(int currPosition);
	
	/**
	 * 
	 * @return
	 */
	String[] getCollectionNames();
	
	/**
	 * 
	 * @param qid
	 */
	void loadResults(String qid);
	
	/**
	 * 
	 * @return
	 */
	TreeNode getWorkspaceTree();
	
	/**
	 * return the id of the default basket
	 * @return the default basket
	 */
	String getDefaultBasket(); 
	/**
	 * 
	 * @param basketId
	 * @return
	 * @throws ItemNotFoundException
	 * @throws BasketNotFoundException
	 */
	List<BasketModelItem> getBasketContent(String basketId);
	
	/**
	 * read the basket from the session, if any
	 * @return
	 */
	BasketSerializable readBasketFromSession();
	
	/**
	 * add a single item in session
	 * @param item item to add
	 */
	Boolean storeBasketItemInSession(BasketModelItem toAdd);
	
	/**
	 * 
	 * @param toRemove item to remove
	 * @return remove result
	 */
	Boolean removeBasketItemFromSession(BasketModelItem toRemove);
	
	/**
	 * Calls the home library to save the current basket in session permanently	 * 
	 */
	boolean saveBasket();
	
	/**
	 * 
	 * @return
	 */
	QueryDescriptor getQueryDescFromSession();
	
	/**
	 * 
	 * @return a ResultNumber instance containing the num of results found so far plus a boolean that say if is still counting or not
	 */
	ResultNumber getResultsNo();
	
	/**
	 * 
	 * @param queryTerm
	 */
	public void submitSimpleQuery(String queryTerm);
	
	public String getObjectsPayload(String objectURI);
	
	public GenericTreeRecordBean getObjectInfo(String objectURI);
	
	public TreeMap<String, List<String>> getContentURLs(GenericTreeRecordBean recordBean);

	public String transformMetadata(String payload, ObjectType type);
	
	public Integer getCurrentQueryIndexNumber();
}
