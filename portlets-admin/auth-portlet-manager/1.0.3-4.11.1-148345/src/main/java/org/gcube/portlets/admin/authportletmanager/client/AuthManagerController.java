package org.gcube.portlets.admin.authportletmanager.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.authportletmanager.client.event.AddPoliciesEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.AddQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.ListContextEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.ListPolicyEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.ListQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.RemovePoliciesEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.RemoveQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.UpdatePolicyEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.UpdateQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.DialogError;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.DialogLoader;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyAddDialog;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyDataGrid;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyDataProvider;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyDeleteDialog;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyFilter;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuotaFilter;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteAddDialog;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteDataGrid;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteDataProvider;
import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteDeleteDialog;
import org.gcube.portlets.admin.authportletmanager.client.resource.AuthResources;
import org.gcube.portlets.admin.authportletmanager.client.rpc.AuthManagerServiceAsync;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.ExpiredSessionServiceException;

import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class AuthManagerController {

	public static SimpleEventBus eventBus;

	private static final String JSP_TAG_ID = "AuthPortletManager";

	/** The MainPanell pg mng. */
	private  Container mainPanelLayout;

	/*Start Component for Tab Policy */

	/** The SearchPolicy pg mng. */
	private PolicyFilter policyfilter =new PolicyFilter();
	/** The policyDataGrid pg mng.  */
	private PolicyDataGrid policydatagrid =new PolicyDataGrid();


	/*Start Component for Tab Quota */
	/** The SearchQuota pg mng. */
	private QuotaFilter quotafilter =new QuotaFilter();
	/** The quoteDataGrid pg mng.  */
	private QuoteDataGrid quotedatagrid =new QuoteDataGrid();



	/** Dialog Box for Loading*/
	private DialogLoader dialogLoader = new DialogLoader();
	/** Dialog Box for Error*/
	private DialogError dialogError = new DialogError();

	private Integer countLoader=0;

	//	public String result_test;

	public AuthManagerController() {
		eventBus = new SimpleEventBus();
		
		
		
		init();
	}

	private void init() {
		loadContext();
		loadMainPanel();
		loadEnviromentPolicy();
		
		bindToEvents();
	}


	/**
	 * @return the eventBus
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	private void loadMainPanel(){
		GWT.log("AuthManager - LoadMainPanel");

		GWT.log("AuthManager -Init Start with -DEBUG_MODE:"+ConstantsSharing.DEBUG_MODE+" DEBUG_TOKEN:"+ConstantsSharing.DEBUG_TOKEN+" MOCKUP:"+ConstantsSharing.MOCK_UP);
		

		AuthResources.INSTANCE.authCSS().ensureInjected();

		mainPanelLayout = new Container();
		mainPanelLayout.setId("mainPanelLayout_AuthPortlet");

		mainPanelLayout.setWidth("98%");
		//Panel Policy 
		DockPanel dockPolicy = new DockPanel();
		dockPolicy.setStyleName("Management-Panel");
		dockPolicy.setWidth("100%");
		dockPolicy.add(policyfilter, DockPanel.NORTH);
		dockPolicy.add(policydatagrid, DockPanel.SOUTH);
		Tab tab_policy =new Tab();
		tab_policy.add(dockPolicy);
		tab_policy.setHeading("POLICY");

		//Panel Quota
		DockPanel dockQuota = new DockPanel();
		dockQuota.setStyleName("Management-Panel-Quota");
		dockQuota.setWidth("100%");
		dockQuota.add(quotafilter, DockPanel.NORTH);
		dockQuota.add(quotedatagrid, DockPanel.SOUTH);
		/*
		Tab tab_quota =new Tab();
		tab_quota.add(dockQuota);
		tab_quota.setHeading("QUOTA");
		 */

		TabPanel tabPanel = new TabPanel();
		tabPanel.add(tab_policy);
		//tabPanel.add(tab_quota);


		tab_policy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("AuthManager - Tab Policy");
				loadEnviromentPolicy();
			}
		});
		/*
		tab_quota.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("AuthManager - Tab Quota");
				loadEnviromentQuote();
			}
		});
		 */
		//select first tab
		tabPanel.selectTab(0);

		mainPanelLayout.add(tabPanel);
		bind(mainPanelLayout);
	}


	protected void bind(Widget mainWidget) {
		try {
			RootPanel root = RootPanel.get(JSP_TAG_ID);
			//Log.info("Root Panel: " + root);
			GWT.log("AuthManager - Root Panel: " + root, null);
			if (root == null) {
				GWT.log("AuthManager - Div with id " + JSP_TAG_ID	+ " not found, starting in dev mode");
				RootPanel.get().add(mainWidget);
			} else {
				GWT.log("AuthManager - Div with id " + JSP_TAG_ID	+ "  found, starting in portal mode");
				root.add(mainWidget);
			}
		} catch (Exception e) {
			e.printStackTrace();
			GWT.log("AuthManager - Error in attach viewport:" + e.getLocalizedMessage(), null);
		}
	}

	private void loadEnviromentPolicy() {
		countLoader=0;
		dialogLoader.startLoader();
		loadPolicy(4);
		loadCaller(4);
		loadServicePolicy(4);
		loadAccessPolicy(4);
	}	


	private void loadEnviromentQuote() {
		countLoader=0;
		dialogLoader.startLoader();
		loadCaller(2);
		loadQuote(2);
	}	






	private void loadPolicy(final int numberLoad){
		final String context=PolicyDataProvider.get().getContext();

		PolicyDataProvider.get().resetPolicyProvider();
		AuthManagerServiceAsync.INSTANCE
		.loadListPolicy(context,new AsyncCallback<ArrayList<PolicyAuth>>() {
			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failed to load  a list policy:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(ArrayList<PolicyAuth> result) {
				PolicyDataProvider.get().loadPolicyProvider(result);
				GWT.log("AuthManager - Load Policy complete on context:"+context);
				checkLoader(numberLoad,0);

			}
		});
	}

	/**
	 * Load a caller from AsyncCallBack from server
	 */
	private void loadCaller(final int numberLoad){
		//String context= "/gcube/devNext";
		String context=PolicyDataProvider.get().getContext();
		AuthManagerServiceAsync.INSTANCE
		.loadListCaller(context,new AsyncCallback<ArrayList<Caller>>() {

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {

					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failed to load a caller:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}

			}
			@Override
			public void onSuccess(ArrayList<Caller> callerList) {
				Entities.getInstance();
				Entities.setCallers(callerList);
				GWT.log("AuthManager - Load Caller complete");
				checkLoader(numberLoad,0);

			}
		});
	}



	public void loadServicePolicy(final int numberLoad){
		//String context= "/gcube/devNext";
		String context=PolicyDataProvider.get().getContext();
		AuthManagerServiceAsync.INSTANCE
		.loadListService(context,new AsyncCallback<Map<String, List<String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {

					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failed to load a service:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(Map<String, List<String>> result) {
				Entities.setServicesMap(result);
				GWT.log("AuthManager - Load Service complete");
				checkLoader(numberLoad,0);

			}
		});
	}

	/***
	 * Load a list access
	 */
	public void loadAccessPolicy(final int numberLoad){

		AuthManagerServiceAsync.INSTANCE
		.loadListAccess(new AsyncCallback <ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failes to load an Access:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(ArrayList<String> result) {
				Entities.setAccess(result);
				GWT.log("AuthManager - Load Access complete");
				checkLoader(numberLoad,0);


			}
		});
	}
	
	

	private void loadContext(){
		AuthManagerServiceAsync.INSTANCE
		.loadRetrieveListContexts(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failed to load  a list context:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(ArrayList<String> result) {
				GWT.log("AuthManager - Load Context complete loadListContext"+result.toString());
				// TODO Auto-generated method stub
				PolicyDataProvider.get().setContextList(result);
				policyfilter.setInitContext();
				
				
				
			}
		});
	}






	public void loadQuote(final int numberLoad){
		//reset provider
		QuoteDataProvider.get().resetQuoteProvider();
		AuthManagerServiceAsync.INSTANCE
		.loadListQuota(new AsyncCallback<ArrayList<Quote>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Failed to load  a list quote:"+caught.getLocalizedMessage());
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(ArrayList<Quote> result) {
				QuoteDataProvider.get().loadQuoteProvider(result);
				GWT.log("AuthManager - Load Quote complete");
				checkLoader(numberLoad,1);

			}
		});
	}



	/**
	 * Checkloader for stop dialogbox with loader when finished 
	 *
	 * @param  
	 * @return 
	 * @see    
	 */
	private void checkLoader(Integer operation,Integer typeGuide){
		countLoader++;
		if (countLoader.equals(operation)){
			GWT.log("AuthManager - Load Complete ");
			dialogLoader.stopLoader();


		}
	}


	

	protected void changeLanguage(String localeName) {
		Date now = new Date();
		long nowLong = now.getTime();
		nowLong = nowLong + (1000 * 60 * 60 * 24 * 21);
		now.setTime(nowLong);
		String cookieLang = Cookies.getCookie(ConstantsSharing.AM_LANG_COOKIE);
		if (cookieLang != null) {
			Cookies.removeCookie(ConstantsSharing.AM_LANG_COOKIE);
		}
		Cookies.setCookie(ConstantsSharing.AM_LANG_COOKIE, localeName, now);
		com.google.gwt.user.client.Window.Location.reload();
	}

	//
	public void restoreUISession() {
		//checkLocale();
		//showDefault();
	}

	// Bind Controller to events on bus
	private void bindToEvents() {


		//event bus for add multiple policy 
		eventBus.addHandler(AddPoliciesEvent.TYPE,
				new AddPoliciesEvent.AddPoliciesEventHandler() {

			@Override
			public void onAdd(AddPoliciesEvent event) {
				addPolicies(event);
			}
		});

		//event bus for update policy 
		eventBus.addHandler(UpdatePolicyEvent.TYPE,
				new UpdatePolicyEvent.UpdatePolicyEventHandler() {

			@Override
			public void onAdd(UpdatePolicyEvent event) {
				updatePolicy(event);
			}
		});

		//event bus for refresh list policy 
		eventBus.addHandler(ListPolicyEvent.TYPE,
				new ListPolicyEvent.ListPolicyEventHandler(){
			@Override
			public void onAdd(ListPolicyEvent event) {
				loadEnviromentPolicy();
			}
		});

		//event bus for delete multiple policy
		eventBus.addHandler(RemovePoliciesEvent.TYPE,
				new RemovePoliciesEvent.RemovePoliciesEventHandler(){

			@Override
			public void onAdd(RemovePoliciesEvent event) {
				removePolicies(event);
			}

		});		

		//event bus for load context
		eventBus.addHandler(ListContextEvent.TYPE,
				new ListContextEvent.ListContextEventHandler() {

			@Override
			public void onAdd(ListContextEvent event) {
				// TODO Auto-generated method stub
				loadContext();
			}


		});		


		/***
		 * SECTION QUOTE
		 */
		//event bus for add multiple quote 
		eventBus.addHandler(AddQuoteEvent.TYPE,
				new AddQuoteEvent.AddQuoteEventHandler() {

			@Override
			public void onAdd(AddQuoteEvent event) {
				addQuote(event);
			}
		});
		//event bus for update quote
		eventBus.addHandler(UpdateQuoteEvent.TYPE,
				new UpdateQuoteEvent.UpdateQuoteEventHandler() {

			@Override
			public void onAdd(UpdateQuoteEvent event) {
				updateQuote(event);
			}
		});
		//event bus for refresh quote
		eventBus.addHandler(ListQuoteEvent.TYPE,
				new ListQuoteEvent.ListQuoteEventHandler(){
			@Override
			public void onAdd(ListQuoteEvent event) {
				loadEnviromentQuote();
			}
		});
		//event bus for delete multiple quote
		eventBus.addHandler(RemoveQuoteEvent.TYPE,
				new RemoveQuoteEvent.RemoveQuoteEventHandler(){

			@Override
			public void onAdd(RemoveQuoteEvent event) {
				removeQuote(event);
			}

		});		





	}




	protected void addPolicies(AddPoliciesEvent event) {
		final List<PolicyAuth> policies = event.getPolicies();
		final PolicyAddDialog policyAddDialog =event.getDialog();
		dialogLoader.startLoader();
		String context=PolicyDataProvider.get().getContext();
		AuthManagerServiceAsync.INSTANCE.
		addPolicies(context,policies,new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Error add policy failed:"+caught.getLocalizedMessage());					
					caught.printStackTrace();
				}
			}
			public void onSuccess(Void result) {
				policyAddDialog.StopAppLoadingView();
				dialogLoader.stopLoader();
				loadPolicy(4);
			}

		});
	}

	protected void addQuote(AddQuoteEvent event) {
		final List<Quote> quote = event.getQuote();

		GWT.log("AuthManager - Demo");
		final QuoteAddDialog quotaAddDialog =event.getDialog();
		dialogLoader.startLoader();
		AuthManagerServiceAsync.INSTANCE.
		addQuote(quote,new AsyncCallback<List<Quote>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Error add policy failed:"+caught.getLocalizedMessage());					
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(List<Quote> quote) {
				for (Quote quota: quote){
					GWT.log("AuthManager - Inserted a new quota:"+quota.getCallerAsString()+
							" identifier:"+quota.getIdQuote()+
							" Time Interval:"+quota.getTimeInterval()+
							" Type:"+quota.getManager()+
							" Data Insert:"+quota.getDataInsert()
							);
					//update list for a new caller
					QuoteDataProvider.get().addQuoteProvider(quota);

				}
				quotaAddDialog.StopAppLoadingView();
				dialogLoader.stopLoader();
			};
		});
	}


	protected void updatePolicy(UpdatePolicyEvent event) {
		final PolicyAuth policies = event.getPolicies();
		final PolicyAddDialog policyUpdateDialog =event.getDialog();
		String context=PolicyDataProvider.get().getContext();
		dialogLoader.startLoader();
		AuthManagerServiceAsync.INSTANCE.updatePolicy(context,policies,new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {

					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("Error update policy failed:"+caught.getLocalizedMessage());					
					caught.printStackTrace();
				}

			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				GWT.log("AuthManager - Update complete");
				/*
				PolicyDataProvider.get().removePolicyProvider(policy.getIdpolicy());
				PolicyDataProvider.get().addPolicyProvider(policy);
				 */
				policyUpdateDialog.StopAppLoadingView();
				dialogLoader.stopLoader();
				loadPolicy(4);
			};
		});
	}


	protected void updateQuote(UpdateQuoteEvent event) {
		final Quote quote = event.getQuote();
		final QuoteAddDialog quoteUpdateDialog =event.getDialog();

		dialogLoader.startLoader();

		AuthManagerServiceAsync.INSTANCE.updateQuote(quote,new AsyncCallback<Quote>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("Error update quote failed:"+caught.getLocalizedMessage());					
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(Quote quote) {
				// TODO Auto-generated method stub
				GWT.log("AuthManager - Update complete");
				QuoteDataProvider.get().removeQuoteProvider(quote.getIdQuote());
				//GWT.log("quote update"+quote.getDataUpdate().toString());
				QuoteDataProvider.get().addQuoteProvider(quote);
				quoteUpdateDialog.StopAppLoadingView();
				dialogLoader.stopLoader();
			};
		});
	}


	protected void removePolicies(RemovePoliciesEvent event) {
		// TODO Auto-generated method stub
		final List<Long> identifier = event.getIdentifier();
		final PolicyDeleteDialog dialog =event.getDialog();
		dialogLoader.startLoader();
		AuthManagerServiceAsync.INSTANCE.deletePolicies(identifier,new AsyncCallback<List<Long>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Error delete policy failed:"+caught.getLocalizedMessage());
					//dialog.StopAppLoadingView();
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(List<Long> listRemovePolicies) {
				for (Long identifier:listRemovePolicies){
					PolicyDataProvider.get().removePolicyProvider(identifier);
					GWT.log("AuthManager - Delete complete:"+identifier);
				}
				dialog.StopAppLoadingView();
				dialogLoader.stopLoader();
			}
		});
	}

	protected void removeQuote(RemoveQuoteEvent event) {
		// TODO Auto-generated method stub
		final List<Long> identifier = event.getIdentifier();
		final QuoteDeleteDialog dialog =event.getDialog();
		dialogLoader.startLoader();
		AuthManagerServiceAsync.INSTANCE.deleteQuote(identifier,new AsyncCallback<List<Long>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ExpiredSessionServiceException) {
					GWT.log("AuthManager - Alert Expired Session");
					//sessionExpiredShowDelayed();
				} else {
					dialogLoader.stopLoader();
					dialogError.showError(caught.getLocalizedMessage());
					GWT.log("AuthManager - Error delete quote failed:"+caught.getLocalizedMessage());
					//dialog.StopAppLoadingView();
					caught.printStackTrace();
				}
			}
			@Override
			public void onSuccess(List<Long> listRemoveQuote) {
				for (Long identifier:listRemoveQuote){
					QuoteDataProvider.get().removeQuoteProvider(identifier);
					GWT.log("AuthManager - Delete quote complete:"+identifier);
				}
				dialog.StopAppLoadingView();
				dialogLoader.stopLoader();
			}
		});

	}

}
