package org.gcube.portlets.user.results.client.control;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.results.client.ResultsDisplayer;
import org.gcube.portlets.user.results.client.components.BasketView;
import org.gcube.portlets.user.results.client.components.BasketViewItem;
import org.gcube.portlets.user.results.client.components.ResultItem;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.dialogBox.NoResultsPopup;
import org.gcube.portlets.user.results.client.draggables.DraggableRow;
import org.gcube.portlets.user.results.client.model.BasketModel;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;
import org.gcube.portlets.user.results.client.model.ResultObj;
import org.gcube.portlets.user.results.client.util.QuerySearchType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> Controller </code> class acts as the Controller in the MVC pattern
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 */

public class Controller {

	private ResultsDisplayer newresultset;
	private BasketModel basketModel;

	private PopupPanel popup;
	private boolean popupIsShown = false;
	private boolean collNameVisible = false;

	/**
	 * 
	 * @param newresultset
	 */
	public Controller(ResultsDisplayer newresultset) {
		this.newresultset = newresultset;
		basketModel = new BasketModel();
	}	
	/**
	 * Add a basket item to the model
	 * @param o
	 */
	public void addBasketItem(FlexTable table, DraggableRow row) {

		String title =  row.getTitle();
		//MODEL
		BasketModelItem addedItem = new BasketModelItem(row.getUri(), row.getOid(), title, row.getTitle(), row.getResObject().getCollectionID(), row.getType(), true);
		basketModel.addBasketItemToModel(addedItem);

		//VIEW
		BasketViewItem toAdd = new BasketViewItem(this, title, table, addedItem);
		if (! addedItem.isNew()) {
			toAdd.addStyleName("d4sFrame-highlight");
		}
		table.insertRow(0);
		table.setWidget(0, 0, toAdd);

		enableSaveButton(true);
	}

	/**
	 * Add a basket item to the model and to the view
	 * @param o
	 */
	public void addBasketItem(ResultObj obj) {

		//MODEL
		BasketModelItem addedItem = new BasketModelItem(obj.getObjectURI(), obj.getObjectURI(), obj.getTitle(), obj.getTitle(), obj.getCollectionID(), BasketModelItemType.INFO_OBJECT, true);
		basketModel.addBasketItemToModel(addedItem);

		//VIEW
		FlexTable table = BasketView.get().getTable();
		BasketViewItem toAdd = new BasketViewItem(this, obj.getTitle(), table, addedItem);
		if (! addedItem.isNew()) {
			toAdd.addStyleName("d4sFrame-highlight");
		}
		table.insertRow(0);
		table.setWidget(0, 0, toAdd);

		enableSaveButton(true);
	}


	/**
	 * checks if the item which is being added is present already in the basket
	 * @param oid: the basket item oid
	 * @return true if exists yet
	 */
	public boolean basketItemExistYet(String oid) {
		if (StringConstants.DEBUG)
			return false;
		List<BasketModelItem> items = basketModel.getChildren();

		for (BasketModelItem item : items) {
			if (item.isNew() && item.getOid().equals(oid))
				return true;
		}
		return false;
	}

	/**
	 * Add a basket item to the model and to the view
	 * @param o
	 */
	public void addQueryToBasket(String queryGivenName, String queryDesc, QuerySearchType querytype) {

		//MODEL
		//TODO was used in the belongTo parameter add it in the first
		//BasketModelItem addedItem = new BasketModelItem(queryGivenName, "", queryDesc, "---", BasketModelItemType.QUERY, true, querytype);
		BasketModelItem addedItem = new BasketModelItem(queryGivenName, queryGivenName, queryDesc, "---", BasketModelItemType.QUERY, true, querytype);
		basketModel.addBasketItemToModel(addedItem);

		//VIEW
		FlexTable table = BasketView.get().getTable();
		BasketViewItem toAdd = new BasketViewItem(this, queryGivenName, table, addedItem);
		table.setWidget(table.getRowCount(), 0, toAdd);

		enableSaveButton(true);
	}

	/**
	 * 
	 * @param enabled .
	 * 
	 * need to check if the page has been refreshed, so if enabled is = false but the page has been refreshed
	 * there could be some new elements that could be added
	 */
	public void enableSaveButton(boolean enabled) {
		newresultset.getHeader().enableSaveButton(enabled);
	}

	/**
	 * 
	 * @param enabled .
	 * 
	 * need to enable it once results have been displayed
	 */
	public void enableSavQueryButton(boolean enable) {
		newresultset.getHeader().enableSaveQuery(this, enable);
		newresultset.getFooter().enableSaveQuery(this, enable);
	}

	/**
	 * 
	 * @param enabled .
	 * 
	 * need to enable it once results have been displayed
	 */
	public void enableMovePageresults(boolean enable) {
		newresultset.getHeader().enableMovePageresults(enable);
		newresultset.getFooter().enableMovePageresults(enable);
	}


	/**
	 * copy all the page results to the basket
	 */
	public void addPageResultsToBasket() {
		List<ResultItem> currentDisplayedItems = getNewresultset().getRecordsPanel().currentDisplayedItems;
		for (ResultItem item : currentDisplayedItems) {
			addBasketItem(item.getResObject());
		}

		enableSaveButton(true);
	}

	/**
	 * 
	 * @param listbox
	 */
	public void addCollectionListbox(ListBox listbox) {
		newresultset.getRecordsPanel().showCollectionListbox(listbox);
	}

	/**
	 * adds a record to the recordpanel
	 * 
	 * @param toadd the RusultItem to add
	 */
	public void displayResults(Vector<ResultObj> results, int from, int to, String currTotal, boolean normalResults, HashMap<String, String> externalLinks) {
		newresultset.getRecordsPanel().clear();
		newresultset.getRecordsPanel().displayresults(results,  from, to, currTotal, normalResults);
	}

	/**
	 * 
	 * @param enable
	 */
	public void enableNextButton(boolean enable) {	
		if (enable) {
			newresultset.getHeader().showNextButton();
			newresultset.getFooter().showNextButton();
		}
		else {
			newresultset.getHeader().hideNextButton();
			newresultset.getFooter().showNextButton();
		}
	}

	/**
	 * 
	 * @param enable
	 */
	public void enablePrevButton(boolean enable) {
		if (enable) {
			newresultset.getHeader().showPrevButton();
			newresultset.getHeader().showFirstPageButton();
			newresultset.getFooter().showPrevButton();
			newresultset.getFooter().showFirstPageButton();
		}
		else {
			newresultset.getHeader().hidePrevButton();
			newresultset.getHeader().hideFirstPageButton();
			newresultset.getFooter().hidePrevButton();
			newresultset.getFooter().hideFirstPageButton();
		}
	}

	public void addButtonsBottom(CellPanel panel) {
		//panel.add(newresultset.getHeader().cloneSwitchPagePanel());
	}

	/**
	 * 
	 * @return
	 */
	public ResultsDisplayer getNewresultset() {
		return newresultset;
	}		
	/**
	 * This method closes the loading gif   
	 */
	public void hideLoading()
	{
		DialogBox loading = newresultset.getRecordsPanel().loading;
		loading.hide();
		enableSavQueryButton(true);
	}

	public void highlightBasket(boolean highlight) {
		if (highlight)
			newresultset.getLeftPanel().getMainLayout().addStyleName("basket-highlight");
		else 
			newresultset.getLeftPanel().getMainLayout().removeStyleName("basket-highlight");
	}

	/***
	 * 
	 *
	 */
	public void nextPageButtonClicked() {
		newresultset.getModel().getResultsFromSearchService(StringConstants.GETNEXT);
	}


	/**
	 * 
	 * @param toDisplay the query to display
	 */
	public void displayQuery(String toDisplay) {
		newresultset.getRecordsPanel().displayQuery(toDisplay);
	}


	/***
	 * 
	 * @param html name to show
	 */
	public void openFolder(String folderID) {
		basketModel.openBasket(this, newresultset.getLeftPanel(), folderID);
		enableSaveButton(false);
	}

	/**
	 * clean the basket
	 * 
	 */
	public void cleanBasket() {
		basketModel = new BasketModel();
		VerticalPanel tmp = newresultset.getLeftPanel().getMainLayout();
		tmp.remove(tmp.getWidgetCount()-1);
		enableSaveButton(false);
	}

	/***
	 * 
	 *
	 */
	public void prevPageButtonClicked() {
		newresultset.getModel().getResultsFromSearchService(StringConstants.GETPREVIOUS);
	}
	
	/***
	 * 
	 *
	 */
	public void firstPageButtonClicked() {
		newresultset.getModel().getResultsFromSearchService(StringConstants.GETFIRST);
	}

	/**
	 * 
	 * @param o
	 */
	public boolean removeBasketItemFromModel(BasketModelItem o) {
		if (o.isNew().booleanValue()) {
			basketModel.removeBasketItemFromModel(o);
			return true;
		}
		Window.alert("This item was already present in the basket and cannot be removed," +
		" please use Workspace portlet to remove it.");
		return false;
	}


	public void saveBasket() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not save Basket, session maybe expired or server reported an error " + caught.getMessage());
			}

			public void onSuccess(Boolean result) {
				if (result.booleanValue())
					Window.alert("New elements have been succesfully sent to your workspace area");
			}					
		};
		ResultsDisplayer.get().getModel().getResultService().saveBasket(callback);
	}

	/***
	 * 
	 * @param html name to show
	 */
	public void setCurrBasketName(String html) {
		newresultset.getHeader().setCurrentBasket(html);
	}

	/***
	 * 
	 * @param path the path to show
	 */
	public void setCurrBasketPath(String path) {
		newresultset.getLeftPanel().setCurrPath(path);
	}

	public void setNewresultset(ResultsDisplayer newresultset) {
		this.newresultset = newresultset;
	}

	public void setPageDisplayer(String html) {
		newresultset.getHeader().setPageDisplayer(html);
		newresultset.getFooter().setPageDisplayer(html);
	}

	/**
	 * This method show the loading gif during ajax server calls
	 */
	public void showLoading()
	{
		DialogBox loading = newresultset.getRecordsPanel().loading;

		loading.setStyleName("unknown");
		int left = Window.getClientWidth() / 2 - 70;

		loading.setPopupPosition(left, 400);
		loading.show();
	}


	/**
	 * This method show the popup for error or no results after a search
	 * 
	 * @param showMsg true if you got an error during search, false otherwise
	 */
	public void showNoResultsPopup(String msg, boolean showMsg)	{
		popup = new NoResultsPopup(msg, false, showMsg);

		popup.setStyleName("unknown");
		int left = Window.getClientWidth() / 2 - 70;

		popup.setPopupPosition(left, 400);
		popup.show();
		popupIsShown = true;
	}

	public void hideNoResultsPopup() {
		if (popupIsShown)
			popup.hide();
	}
	public BasketModel getBasketModel() {
		return basketModel;
	}
	
	/**
	 * for each current displayed items, trigger the collection name visibility
	 * @param visible
	 */
	public void setCollectionNameVisibility(boolean visible) {
		List<ResultItem> res = getNewresultset().getRecordsPanel().currentDisplayedItems;
		for (ResultItem resultItem : res) {
			resultItem.setCollectionVisibility(visible);
		}
		collNameVisible = visible;
	}
	public boolean isCollNameVisible() {
		return collNameVisible;
	}
}
