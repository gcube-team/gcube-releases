package org.gcube.portlets.user.joinvre.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.joinvre.client.responsive.ResponsivePanel;
import org.gcube.portlets.user.joinvre.client.ui.LoadingPanel;
import org.gcube.portlets.user.joinvre.client.ui.TabPageDescription;
import org.gcube.portlets.user.joinvre.shared.TabbedPage;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class JoinVRE implements EntryPoint {
	Logger logger = Logger.getLogger(JoinVRE.class.getName());
	public static final String GET_OID_PARAMETER = "siteId";
	public static final String ORGANISATIONS_LABEL = "Organisations";
	public static final String CATEGORIES_LABEL = "Thematic areas";

	private final JoinServiceAsync joinService = GWT.create(JoinService.class);

	private VerticalPanel mainPanel = new VerticalPanel();

	private TabPanel mainTabPanel = new TabPanel(Tabs.ABOVE);

	public void onModuleLoad() {
		checkIsReferral();		
	}
	/**
	 * first async callback
	 */
	private void checkIsReferral() {
		logger.log(Level.INFO,"checkIsReferral()");
		if (getSiteLiferayId() == null) {
			displayVREs();
		}
		else {
			Long vreId = -1L;
			try {
				vreId = Long.parseLong(getSiteLiferayId());
			}
			catch (Exception ex) {
				logger.log(Level.WARNING, "site id is not a number " + ex.getMessage());
				return;
			}
			joinService.getSelectedVRE(vreId, new AsyncCallback<VRE>() {
				@Override
				public void onFailure(Throwable caught) {
					logger.log(Level.SEVERE,"getSelectedVRE error " + caught.getMessage());
					Window.alert("Server error");
				}
				@Override
				public void onSuccess(final VRE vre) {
					logger.log(Level.INFO, "A VRE was Returned");	
					if (vre == null) {
						GWT.log("A VRE Returned is null");	
						displayVREs();
						return;
					}
					else {
						ResponsivePanel rp = null;
						logger.log(Level.INFO, "A Valid VRE was Returned");	
						switch (vre.getUserBelonging()) {
						case BELONGING:
							logger.log(Level.INFO, "User is Belonging");	
							Location.assign(vre.getFriendlyURL());
							break;
						case PENDING:
							logger.log(Level.INFO, "User is Pending");	
							rp = displayVREs();
							break;
						default: //Not belonging
							logger.log(Level.INFO, "User is NOT Belonging");	
							rp = displayVREs();
							checkInvitation(vre, vre.getId(), rp);
							break;
						}
					}
				}
			});			
		}
	}
	private ResponsivePanel displayVREs() {
		final ResponsivePanel toReturn = new ResponsivePanel();
		mainPanel.setWidth("100%");
		mainPanel.add(toReturn);		
		joinService.isTabbedPanel(new AsyncCallback<List<TabbedPage>>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(List<TabbedPage> tabNames) {
				if (tabNames != null) {
					GWT.log("TabbedPanel");
					showTabs(toReturn, tabNames);
				}

			}
		});
		RootPanel.get("JoinVRE-Container").add(mainPanel);
		return toReturn;

	}
	/**
	 * 
	 * @param rp
	 * @param tabNames
	 */
	private void showTabs(ResponsivePanel rp, List<TabbedPage> tabNames) {
		int i = 0;
		for (final TabbedPage theTabPage : tabNames) {
			final Tab tab2Add = new Tab();
			tab2Add.setHeading(theTabPage.getName());
			if (i == 0) {
				tab2Add.add(new TabPageDescription(theTabPage));
				tab2Add.add(rp); //we add the responsivepanel to the first tab only and we set it active
				tab2Add.setActive(true);
			}
			else {
				tab2Add.add(new LoadingPanel());
				tab2Add.addClickHandler(new ClickHandler() {			
					@Override
					public void onClick(ClickEvent event) {
						final ResponsivePanel rpPort = new ResponsivePanel(tab2Add);	
						tab2Add.clear();
						tab2Add.add(new TabPageDescription(theTabPage));
						tab2Add.add(rpPort);
					}
				});
			}
			mainTabPanel.add(tab2Add);
			i++;
		}

		mainTabPanel.selectTab(0);
		mainPanel.clear();
		mainPanel.add(mainTabPanel);

		addCategoriesTab(mainTabPanel);		
		addOrganisationsTab(mainTabPanel);		
	}
	/**
	 * add the Organisation Tab to the page, lazy load
	 * @param mainTabPanel
	 */
	private void addOrganisationsTab(final TabPanel mainTabPanel) {		
		joinService.getAllOrganisations(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {				
			}
			@Override
			public void onSuccess(List<String> result) {
				if (result.size() > 0) {
					final DropdownTab dropdownCategoriesTab = new DropdownTab(ORGANISATIONS_LABEL);
					for (String orgName : result) {
						final String organizationName = orgName;

						final Tab tab2Add = new Tab();
						tab2Add.setHeading(organizationName);				
						dropdownCategoriesTab.add(tab2Add);

						tab2Add.addClickHandler(new ClickHandler() {							
							@Override
							public void onClick(ClickEvent event) {
								tab2Add.clear();
								tab2Add.add(new ResponsivePanel(organizationName));
							}
						});
					}
					mainTabPanel.add(dropdownCategoriesTab);
				}
			}
		});	
	}
	/**
	 * add the Categories Tab to the page, lazy load
	 * @param mainTabPanel
	 */
	private void addCategoriesTab(final TabPanel mainTabPanel) {		
		joinService.getAllCategories(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {				
			}
			@Override
			public void onSuccess(ArrayList<String> result) {
				if (result.size() > 0) {
					final DropdownTab dropdownCategoriesTab = new DropdownTab(CATEGORIES_LABEL);
					for (String catName : result) {
						final String categoryName = catName;

						final Tab tab2Add = new Tab();
						tab2Add.setHeading(categoryName);				
						dropdownCategoriesTab.add(tab2Add);

						tab2Add.addClickHandler(new ClickHandler() {							
							@Override
							public void onClick(ClickEvent event) {
								tab2Add.clear();
								tab2Add.add(new ResponsivePanel(new VRECategory(1L, categoryName, "")));
							}
						});
					}
					mainTabPanel.add(dropdownCategoriesTab);
				}
			}
		});	
	}

	/**
	 * check if it has to show just one feed
	 * @return
	 */
	private String getSiteLiferayId() {
		return Window.Location.getParameter(GET_OID_PARAMETER);
	}
	/**
	 * 
	 * @param vre
	 * @param groupId
	 * @param rp
	 */
	private void checkInvitation(final VRE vre, final long groupId, final ResponsivePanel rp) {
		joinService.isExistingInvite(groupId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String inviteId) {
				//inviteId != null = there exist an invite
				if (inviteId != null) {
					rp.showInviteDialog(vre, inviteId);
				} else
					rp.requestMembership(vre);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server error");				
			}
		});
	}

}
