package org.gcube.portlets.user.results.client.model;

import java.util.HashMap;
import java.util.Vector;

import org.gcube.portlets.user.results.client.ResultsetService;
import org.gcube.portlets.user.results.client.ResultsetServiceAsync;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;


/**
 * The <code> Model </code> class represents the current application state in the MVC pattern 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (0.1) 
 */
public class Model {
	/**
	 * Create the client proxy. The cast is always safe because the 
	 * generated proxy implements the asynchronous interface automatically.
	 */	
	private ResultsetServiceAsync resultService = (ResultsetServiceAsync) GWT.create(ResultsetService.class);
	private ServiceDefTarget endpoint = (ServiceDefTarget) resultService;


	/**
	 * to handle multiple pages
	 */
	private int startToShowAt = 0;
	private int stopToShowAt = 0;

	/**
	 * this class holds the current elements in basket
	 */
	private BasketModel basketModel;

	/**
	 * 
	 */
	private final Controller controller;
	/**
	 * 
	 */	
	public static Model singleton = null;
	/**
	 * 
	 * @return .
	 */
	public static Model get() {
		return singleton;
	}

	/**
	 * 
	 * @param controller
	 */
	public Model(final Controller controller) {
		singleton = this;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()+"NewresultsetServiceImpl");
	
		this.controller = controller;	

		/**
		 * Check if the user performed a combined search by clicking on search per collection first
		 * 	if so get the collections list and create a listbox with them
		 * else perform a regular search
		 * 
		 */
		AsyncCallback<String[]> collections_callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				loadResults();
			}
			public void onSuccess(String[] collectionsList) {	
				if(collectionsList != null) {
					if(collectionsList.length  < 1)
						return;
					ListBox listBox = new ListBox(false);
					for ( int i = 0; i < collectionsList.length; i++) {
						if(collectionsList[i].startsWith("selected:"))
						{
							listBox.addItem(collectionsList[i].substring(9), i + "");
							listBox.setSelectedIndex(i);
						}
						else
							listBox.addItem(collectionsList[i], i + "");
					}

					listBox.addChangeHandler(new ChangeHandler() {
						
						@Override
						public void onChange(ChangeEvent arg0) {
							AsyncCallback callback = new AsyncCallback() {
								public void onFailure(Throwable caught) {
									controller.getNewresultset().getRecordsPanel().clear();
									controller.hideNoResultsPopup();
									loadResults();

								}
								public void onSuccess(Object result) {	
									controller.getNewresultset().getRecordsPanel().clear();
									controller.hideNoResultsPopup();
									loadResults();	
								}
							};
							//Changes the internal query (in the query group) to be presented - based on collection name
							resultService.loadResults(((ListBox) arg0.getSource()).getValue(((ListBox) arg0.getSource()).getSelectedIndex()), callback);
							
						}
					});
					controller.addCollectionListbox(listBox);
				}
				loadResults();
			}			
		};		
		resultService.getCollectionNames(collections_callback);
	}


	/**
	 * This method is called if the user has not searched per collection
	 *
	 */
	public void loadResults() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				//Window.alert("The Call Failed on Server");
			}
			public void onSuccess(Boolean result) {		
				boolean keepSearching =  result.booleanValue();
				if (! keepSearching)
					getResultsFromSession();
				else {
					getResultsFromSearchService(StringConstants.GETFIRST);
				}
			}			
		};		
		resultService.isSearchActive(callback);
	}

	/**
	 * Look in the session if there are results, if so display them.
	 * if not display no results were found
	 *
	 */
	public void getResultsFromSession() {
		controller.showLoading();
		AsyncCallback<ResultsContainer> callback = new AsyncCallback<ResultsContainer>() {
			public void onFailure(Throwable caught) {
				//Window.alert("The Call Failed on Server");
			}
			public void onSuccess(ResultsContainer resultContainer) {		
				if (resultContainer != null) {
					controller.hideLoading();

				} else {
					controller.hideLoading();
					controller.showNoResultsPopup("", false);
				}			
			}			
		};		
		resultService.getResultsFromSession(callback);
	}

	/**
	 * Look in the session if there are results, if so display them.
	 * if not display no results were found
	 * 
	 * @param mode if mode == 0 get the first record of the resultset, 
	 * 					mode == 1 get the previous, mode = 2 get the next
	 *
	 */
	public void getResultsFromSearchService(int mode) {
		controller.showLoading();
		AsyncCallback<ResultsContainer> callback = new AsyncCallback<ResultsContainer>() {
			public void onFailure(Throwable caught) {
				//Window.alert("The Call Failed on Server" + caught.getMessage());
			}
			public void onSuccess(ResultsContainer resultContainer) {	
				//inside eclipse
				if (StringConstants.DEBUG) {
					controller.displayResults(null, 1, 5, "5", true, null);
				}
				//in the portal	
				else {
					if (resultContainer != null && resultContainer.getType() == ResultType.RESULTS) {
						controller.hideLoading();

						/*
						 * options[0] contains the starting point
						 * 	[1] contains true if back button has to be enabled
						 *	[2] contains true if next button has to be enabled
						 *	[3] contains the number of results per page
						 *  [4] contains the estimated number of results so far
						 *  [5] contains 1 if the user performed a browse field value to treat these results differently
						 *  [6] contains  the displaybale query 
						 *  
						 */
						String[] options = resultContainer.getOptionalParams();
						boolean back = Boolean.valueOf(options[1]).booleanValue();
						boolean forward = Boolean.valueOf(options[2]).booleanValue();

						Vector<ResultObj> results = resultContainer.getResultRecords();

//						//get from where to start show the records
						startToShowAt = Integer.valueOf(options[0]).intValue();
						int from = startToShowAt;
						int resultsNoPerPage = Integer.valueOf(options[3]).intValue();
						int to = startToShowAt + resultsNoPerPage -1;	
						
						stopToShowAt = to;
						
						if (stopToShowAt >= results.size()) {
							stopToShowAt = results.size();
						}
						final String currTotal  = options[4];

						boolean normalresults = Boolean.valueOf(options[5]).booleanValue();
				
						controller.enableNextButton(forward);
						controller.enablePrevButton(back);
					
						final Timer timer = new Timer() {
							@Override
							public void run() {
								AsyncCallback<ResultNumber> rescallback = new AsyncCallback<ResultNumber>() {
									public void onFailure(Throwable caught) {
										controller.setPageDisplayer("displaying " + startToShowAt + " - " + stopToShowAt);			
									}
									public void onSuccess(ResultNumber result) {
										if (result.isCountEnded() && result.getResultsNoSofar() >= 500) {
											controller.setPageDisplayer("displaying " + startToShowAt + " - " + stopToShowAt + " of more than 500!");
										}
										else
											controller.setPageDisplayer("displaying " + startToShowAt + " - " + stopToShowAt + " of " + result.getResultsNoSofar() + result.getExtraText());
										if (! result.isCountEnded())
											schedule(5000);	
										
									}							
								};								
								resultService.getResultsNo(rescallback);							
							}
						};						
						timer.schedule(1500);
						//display the query
						controller.displayQuery(options[6]);
						//display the results
						
						//controller.setExternalLinks(resultContainer.getActivelinksIntoVRE());
						//TODO Comment out if needed
						controller.displayResults(results, from, to, currTotal, normalresults, new HashMap<String,String>());
					}
					//this is the case of a returning error
					else  if (resultContainer != null && resultContainer.getType() == ResultType.ERROR) {
						controller.hideLoading();
						controller.showNoResultsPopup(resultContainer.getOptionalParams()[0], false);
					}
					//this is the case of renurning 0 results, query in this case is in the [0] position
					else {
						controller.hideLoading();
						controller.showNoResultsPopup("", false);
						controller.displayQuery(resultContainer.getOptionalParams()[0]);
					}			
				}

			}			
		};		
		resultService.getResultFromSearchService(mode, callback);
	}

	public ResultsetServiceAsync getResultService() {
		return resultService;
	}

	public BasketModel getBasketModel() {
		return basketModel;
	}

	public void setBasketModel(BasketModel basketModel) {
		this.basketModel = basketModel;
	}



}
