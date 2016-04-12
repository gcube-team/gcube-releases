package org.gcube.portlets.user.results.client.model;

import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.results.client.ResultsDisplayer;
import org.gcube.portlets.user.results.client.ResultsetService;
import org.gcube.portlets.user.results.client.ResultsetServiceAsync;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.panels.LeftPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * 
 * @author massi
 *
 */
public class BasketModel {
	/**
	 * (1) Create the client proxy. The cast is always safe because the 
	 * generated proxy implements the asynchronous interface automatically.
	 */	
	private ResultsetServiceAsync resultService = (ResultsetServiceAsync) GWT.create(ResultsetService.class);
	private ServiceDefTarget endpoint = (ServiceDefTarget) resultService;


	/**
	/**
	 * 
	 */
	private List<BasketModelItem> children;
	/**
	 * 
	 * @param children
	 */
	public BasketModel(List<BasketModelItem> children) {
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewresultsetServiceImpl";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		this.children = children;
	}
	/**
	 * initializes itslef by reading the basket from session
	 */
	public BasketModel() {
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewresultsetServiceImpl";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		children = new LinkedList<BasketModelItem>();
		AsyncCallback<BasketSerializable> callback = new AsyncCallback<BasketSerializable>() {

			public void onFailure(Throwable caught) {
				Window.alert("Server failure while retrieving basket: " + caught.getMessage());			
			}
			public void onSuccess(BasketSerializable result) {
				if (result != null) {
					children = result.getItems();
					BasketModel basketModel = new BasketModel(children);
					ResultsDisplayer.get().getLeftPanel().showBasket(basketModel);
					ResultsDisplayer.get().getController().setCurrBasketName(result.getName());
					ResultsDisplayer.get().getController().setCurrBasketPath(result.getPath());
					if (isThereAnyNewItem())
						ResultsDisplayer.get().getController().enableSaveButton(true);
				}
				//TODO on 23
				else {
					ResultsDisplayer.get().getController().setCurrBasketName("My Default Basket");
					ResultsDisplayer.get().getController().setCurrBasketPath("");
				}
					
			}			
		};
		resultService.readBasketFromSession(callback);
	}

	public List<BasketModelItem> getChildren() {
		return children;
	}

	/**
	 * read the basket from the homeLibrary, call the VIEW fro display elements.
	 * 
	 * @param controller contorller
	 * @param basketViewPanel the panel where to display basketItems
	 * @param basketId the basket to open
	 */
	public void openBasket(Controller controller, final LeftPanel basketViewPanel, String basketId) {

		AsyncCallback<List<BasketModelItem>> callback = new AsyncCallback<List<BasketModelItem>>() {

			public void onFailure(Throwable caught) {
				Window.alert("Server failure while retrieving basket: " + caught.getMessage());			
			}
			public void onSuccess(List<BasketModelItem> result) {
				children = result;
				BasketModel toPass = new BasketModel(result);
				basketViewPanel.showBasket(toPass);
			}			
		};
		resultService.getBasketContent(basketId, callback);
	}


	public void addBasketItemToModel(BasketModelItem o) {
		children.add(o);
		
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				Window.alert("Server failure while trying to store item in basket: " + caught.getMessage());			
			}
			public void onSuccess(Boolean result) {
				if (! result.booleanValue())
					Window.alert("Server error while trying to store item in basket");
			}			
		};
		resultService.storeBasketItemInSession(o, callback);
	}

	public void removeBasketItemFromModel(BasketModelItem o) {
		children.remove(o);
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				Window.alert("Server failure while trying to remove item from basket");			
			}
			public void onSuccess(Boolean result) {
				if (! result.booleanValue())
					Window.alert("Server error while trying to remove item from basket");
			}			
		};
		resultService.removeBasketItemFromSession(o, callback);
	}
	
	
	/**
	 * 
	 * @return true if there is at least one new element in the basket (an element which has not been saved yet)
	 */
	public boolean isThereAnyNewItem() {

		for (BasketModelItem item : children) 
			if (item.isNew()) return true;
		
		return false;
	}
}
