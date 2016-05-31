package org.gcube.portlets.user.results.server.servlet;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.portlets.user.results.client.components.TreeNode;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.model.BasketSerializable;
import org.gcube.portlets.user.results.client.util.QueryDescriptor;
import org.gcube.portlets.user.results.client.util.QuerySearchType;
import org.gcube.portlets.user.results.shared.ContentInfo;
import org.gcube.portlets.user.results.shared.SearchableFieldBean;


public class NewresultsetServiceUtil {

	/** Logger */
	private static Logger _log = Logger.getLogger(NewresultsetServiceUtil.class);

	protected static WorkspaceFolder getDefaultBasketWorkspaceFolder(NewresultsetServiceImpl caller) {
		Workspace workspaceArea = null;
		WorkspaceFolder basket = null;
		basket = (WorkspaceFolder)caller.getASLSession().getAttribute(NewresultsetServiceImpl.SESSION_DEFAULT_BASKET_DIR);
		try {
			// Not in session
			if (basket == null) {
				workspaceArea = caller.getWorkspaceArea();
				WorkspaceFolder root = workspaceArea.getRoot();
				basket = (WorkspaceFolder)root.find(NewresultsetServiceImpl.DEFAULT_BASKET_DIR);
				// Does not exist in workspace
				if (basket == null) {
					_log.debug("There is no default basket for the user. Going to create it");
					basket = workspaceArea.createFolder(NewresultsetServiceImpl.DEFAULT_BASKET_DIR, "This folder contains the objects saved from search results", root.getId());
				}
				caller.getASLSession().setAttribute(NewresultsetServiceImpl.SESSION_DEFAULT_BASKET_DIR, basket);
			}
			_log.debug("Default Basket folder has name -> " + basket.getName() +  " ID -> " + basket.getId());
		} catch (Exception e1) {

		} 
		return basket;
	}

	/**
	 * Return a  List<BasketModelItem> given a basketid
	 * it uses the hobe library and store the read basket in the session
	 * 
	 */
	protected static List<BasketModelItem> getBasketContent(NewresultsetServiceImpl caller, String basketId) {

		Workspace root = null;
		try {
			root = caller.getWorkspaceArea();
		} catch (WorkspaceFolderNotFoundException e) {e.printStackTrace();
		} catch (InternalErrorException e) { e.printStackTrace();
		} catch (HomeNotFoundException e) {	e.printStackTrace();
		}

		WorkspaceItem item = null;
		try {
			item = root.getItem(basketId);
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
		}
		_log.debug("Item Type: "+item.getType());
		if (item.getType() != WorkspaceItemType.FOLDER) { 
			_log.debug("The item id does not belong to a basket id:" + basketId);
			return new LinkedList<BasketModelItem>();
		}
		else {
			List<BasketModelItem> readBasket = new LinkedList<BasketModelItem>();// NewresultsetServiceUtil.fillBasket((WorkspaceFolder) item);
			try {
				caller.storeBasketInSession(new BasketSerializable(basketId, item.getName(), item.getPath(), readBasket));
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
			_log.debug("Basket " + basketId + " stored in session");

			return readBasket;
		}				
	}
	/**
	 * fill the basket 
	 * @param basket
	 * @return
	 */

	//TODO: Change first parameter of constructor for BasketModelItem to URI instead if item.getId
	private static List<BasketModelItem> fillBasket(WorkspaceFolder basket) {		
		List<BasketModelItem> toReturn = new LinkedList<BasketModelItem>();
		return toReturn;
	}

	/**
	 * Calls the home library to save the current basket in session permanently	 * 
	 *
	 */
	protected static boolean saveBasket(NewresultsetServiceImpl caller) {
		BasketSerializable toSave = caller.readBasketFromSession();
		if (toSave != null) {
			List<BasketModelItem> items = toSave.getItems();
			_log.debug("Number of items in basket to save -> " + items.size());

			WorkspaceFolder basket = getDefaultBasketWorkspaceFolder(caller);

			try {
				FolderBulkCreator bbc = basket.getNewFolderBulkCreator();
				_log.debug("********************************  BASKET CREATOR CALLED    items size: -> " + items.size());

				for (BasketModelItem item : items) {
					if (item.isNew()) {
						item.setIsNew(false);

						_log.debug("Saving item to basket with URI -> " + item.getUri() + " and name -> " + item.getName()); 
						switch (item.getItemType()) {
						case OPENSEARCH:
							bbc.createExternalUrl(item.getUri());
							break;
						case QUERY:
							QueryType type = convertGWTquerytype(item.getSearchType());
							_log.debug("****************************************************  SAVING QUERY: TYPE-> " + type);
							_log.debug("****************************************************  SAVING QUERY: with name-> " + item.getName() + "and description-> " + item.getDescription());
							bbc.createQuery(item.getName(), item.getDescription(), type);
							break;
						//Standard Result Object coming from Tree Collections
						default:
							bbc.createDocumentItem(URI.create(item.getUri()));

							ContentInfo info = caller.getContentToSave(item.getUri());
							if (info != null) {
								// TODO Create the external file here
								try {
									basket.createExternalFileItem(item.getName(), item.getDescription(), info.getMimeType(), info.getIs());
								} catch (Exception e) {
									_log.debug("Could not create the external file to basket. Continuing without the external file", e);
								}
							}
							else
								_log.debug("InputStream was null for the main content of this object. Did not manage to save it. Either its type is unsupported or an error occurred");
							break;
						}			
					}
				} //end for
				//we persist changes
				bbc.commit();
			}
			catch (InsufficientPrivilegesException e) {	
				_log.debug(e.getMessage());
				return false;
			}
			catch (InternalErrorException e) { 
				_log.debug(e.getMessage());
				return false;
			}		
			return true;
		}
		else
			_log.debug("Failed to retrieve basket from session. returning with false flag");
		return false;
	}


	private static QueryType convertGWTquerytype(QuerySearchType type) {
		switch (type) {
		case ADVANCED:
			return QueryType.ADVANCED_SEARCH;
		case GENERIC:
			return QueryType.GENERIC_SEARCH;
		case BROWSE:
			return QueryType.BROWSE;
		default:
			return QueryType.SIMPLE_SEARCH;
		}
	}

	/**
	 * Return an instance of the Basket given a basketid
	 * @param basketId
	 * @return
	 */
	public static WorkspaceFolder getBasketInstance(NewresultsetServiceImpl caller, String basketId) {
		Workspace root = null;
		try {
			root = caller.getWorkspaceArea();
		} catch (WorkspaceFolderNotFoundException e) {e.printStackTrace();
		} catch (InternalErrorException e) { e.printStackTrace();
		} catch (HomeNotFoundException e) {	e.printStackTrace();
		}

		WorkspaceItem item = null;
		try {
			item = root.getItem(basketId);
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
		}
		_log.debug("Item Type: " + item.getType());
		if (item.getType() != WorkspaceItemType.FOLDER) { 
			_log.debug("The item id does not belong to a basket id -> " + basketId);
			return null;
		}
		return (WorkspaceFolder) item;
	}

	/**
	 * 
	 * @param folder
	 * @return
	 * @throws InternalErrorException
	 */
	protected static TreeNode fillWorkspaceTree(WorkspaceFolder folder) throws InternalErrorException {
		List<TreeNode> children = new LinkedList<TreeNode>();

		TreeNode tmp = null;
		if (folder.getType() == WorkspaceItemType.FOLDER)
			tmp = new TreeNode(folder.getName(), folder.getId(), folder.getPath(), children,	StringConstants.TYPE_BASKET, folder.getParent() == null);
		else {
			tmp = new TreeNode(folder.getName(), folder.getId(), folder.getPath(), children,	StringConstants.TYPE_FOLDER, folder.getParent()== null);
		}
		//TODO I have commented these lines out to avoid getting all children
		//		for (WorkspaceItem child: folder.getChildren()) {
		//			if (child.getType() == WorkspaceItemType.FOLDER)
		//				children.add(fillWorkspaceTree((WorkspaceFolder) child));
		//		}
		return tmp;
	}

	/**
	 * Constructs a String representation of the query given a QueryDescriptor
	 * @param qd the QueryDescriptor
	 * @return a String representation of the query
	 */
	protected static String getDisplayableQuery(QueryDescriptor qd) {
		String toReturn = "";
		switch (qd.getType()) {
		case BROWSE:		
			toReturn = " BROWSE BY " + qd.getBrowseBy() + " IN ";
			for (int i = 0; i < qd.getSelectedCollections().size(); i++) {
				toReturn += "'" + qd.getSelectedCollections().get(i) + "'";
				if (i != qd.getSelectedCollections().size()-1)
					toReturn += ", ";
			}			
			return toReturn;
		case SIMPLE:
			toReturn = " '" + qd.getSimpleTerm() + "' IN ";
			for (int i = 0; i < qd.getSelectedCollections().size(); i++) {
				toReturn += "'" + qd.getSelectedCollections().get(i) + "'";
				if (i != qd.getSelectedCollections().size()-1)
					toReturn += ", ";
			}			
			return toReturn;
		case ADVANCED:
			for (SearchableFieldBean f : qd.getAdvancedFields())
				toReturn += f.getName() + " = " + f.getValue() + ", ";
			toReturn = toReturn.substring(0, toReturn.length()-1);
			toReturn += " IN ";
			for (int i = 0; i < qd.getSelectedCollections().size(); i++) {
				toReturn += "'" + qd.getSelectedCollections().get(i) + "'";
				if (i != qd.getSelectedCollections().size()-1)
					toReturn += ", ";
			}			
			return toReturn;
		case BROWSE_FIELDS:
			toReturn = "Distinct Values of: '" + qd.getBrowseBy() + "' FOR ";
			for (int i = 0; i < qd.getSelectedCollections().size(); i++) {
				toReturn += "'" + qd.getSelectedCollections().get(i) + "'";
				if (i != qd.getSelectedCollections().size()-1)
					toReturn += ", ";
			}
			return toReturn;
		case GENERIC:
			return qd.getSimpleTerm() + " IN all available collections";
//		case CQL_QUERY:
//			return qd.getSimpleTerm();
		default:
			return "";
		}
	}

}

