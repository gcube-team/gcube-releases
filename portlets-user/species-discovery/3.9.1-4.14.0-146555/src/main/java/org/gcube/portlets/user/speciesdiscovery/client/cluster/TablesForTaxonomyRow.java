package org.gcube.portlets.user.speciesdiscovery.client.cluster;

import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEvent.TaxonomyJobType;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TablesForTaxonomyRow {
	
	private TabPanel tabPanel;
	private EventBus eventBus;
	
	private List<LightTaxonomyRow> parents = null;
	private TabItemForTaxonomyRow tabTR;
	
	private AbstractImagePrototype imageLoading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls());
	
//	private HashMap<String, TabItemForTaxonomyRow> hashTR = new HashMap<String, TabItemForTaxonomyRow>();

	
	public List<LightTaxonomyRow> getParents() {
		return parents;
	}

	public void setParents(List<LightTaxonomyRow> parents) {
		this.parents = parents;
	}


	public TablesForTaxonomyRow(TabPanel tabPanel2, EventBus eventBus2) {
		this.tabPanel = tabPanel2;
		this.eventBus = eventBus2;
		
		this.tabTR = new TabItemForTaxonomyRow(this, this.tabPanel, this.eventBus);
	}


	/**
	 * 
	 * @param taxonomy
	 * @param isNewTab -  set true if has been clicked a classification link, or false otherwise
	 * @return
	 */

	public VerticalPanel getPanelClassificationForTaxonomy(LightTaxonomyRow taxonomy, String dataSource, boolean isNewTab){
		
//		hashTR.put(taxonomy.getIdToString(),tabTR);
		
		tabTR.setCurrentTaxonomy(taxonomy);
		
		//DEBUG
//		System.out.println("in getPanelClassificationForTaxonomy...");
//		System.out.println("principal "+taxonomy.getName() + " id " + taxonomy.getServiceId()+" parents");
//		int i=0;
//		for (TaxonomyRow tax : taxonomy.getParents()) {
//			System.out.println(++i + " parent name " + tax.getName());
//		}
		
		return tabTR.getPanelClassificationForTaxonomy(taxonomy, dataSource, isNewTab);
	}


//	/**
//	 * 
//	 * @param row
//	 * @param isNewTab
//	 * @param dataSource
//	 * @param parentIndex
//	 * @return
//	 */
//	public FlexTable getHTMLTableForTaxonomyWithRef(final LightTaxonomyRow row,boolean isNewTab, final String dataSource) {
//		
//		return tabTR.getHTMLTableForTaxonomyWithRef(row, isNewTab, dataSource);
//	}
//	
	
	protected ContentPanel getPanelForTaxonomy(final LightTaxonomyRow taxon, final String dataSource, boolean isNewTab) {
		
		ContentPanel contentPanel = new ContentPanel();
//		contentPanel.setLayout(new FitLayout());
		contentPanel.setHeaderVisible(false);
		contentPanel.setBodyBorder(false);
		
		contentPanel.setStyleAttribute("padding", "5px");
		contentPanel.setStyleAttribute("margin", "5px");
		
		VerticalPanel verticaPanel = new VerticalPanel();
		verticaPanel.setSize("100%", "100%");
		
		contentPanel.add(new Html("<br/><br/><p style=\"font-size:18px;\"><b>" + taxon.getName() + "</b></p><hr>"));
		contentPanel.add(new Html("<br/><br/><p style=\"font-size:12px;\"><b>Scientific Classification</b></p><br/>"));
		
		contentPanel.add(getPanelClassificationForTaxonomy(taxon, dataSource, isNewTab));
		
		contentPanel.add(new Html("<p style=\"font-size:11px; margin-top:10px;\"><b>Status and Synonyms</b></p>"));
		contentPanel.add(getStatusAndSynonyms(taxon, dataSource));
		contentPanel.add(new Html("<p style=\"font-size:11px; margin-top:10px;\"><b>Metadata</b></p"));
		
//		HorizontalPanel hp = new HorizontalPanel();

//		contentPanel.add(getHTMLTableForTaxonomyWithRef(taxon, isNewTab, dataSource));

		
		final LayoutContainer lcRRTables = new LayoutContainer();
		lcRRTables.setStyleAttribute("width", "99%");
//				lcRRTables.setLayout(new FitLayout());
		lcRRTables.setStyleAttribute("margin", "5px");
		lcRRTables.setStyleAttribute("padding", "5px");
		lcRRTables.setStyleAttribute("font-size", "12px");
		contentPanel.add(lcRRTables);

		final Image loading = imageLoading.createImage();
		lcRRTables.add(loading);

		String urlRequest = GWT.getModuleBaseURL() + ConstantsSpeciesDiscovery.TAXONOMY_ROW_TABLE + "?" +"oid=" + taxon.getServiceId();
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlRequest);
		requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			requestBuilder.sendRequest("", new RequestCallback() {

			    @Override
			    public void onResponseReceived(Request request,  Response response) {
			    	lcRRTables.remove(loading);
			    	Html respHtml = new Html(response.getText());
			    	lcRRTables.add(respHtml);
			    	lcRRTables.layout();
			    }

			    @Override
			    public void onError(Request request, Throwable exception) {
			    	lcRRTables.remove(loading);
			    	lcRRTables.add(new Html("Sorry, an error occurred while contacting server, try again"));
			    }
			});
			
		} catch (RequestException e) {
			lcRRTables.remove(loading);
	    	lcRRTables.add(new Html("Sorry, an error occurred while contacting server, try again"));
		}	
		
		return contentPanel;
	
	}
	
	protected void addTabItem(final LightTaxonomyRow taxon, final String dataSource){
		
		this.tabTR = new TabItemForTaxonomyRow(this, this.tabPanel, this.eventBus);

		TabItem tabItem = new TabItem();
//		tabItem.setLayout(new FitLayout());
		tabItem.setScrollMode(Scroll.AUTO);
		tabItem.setClosable(true);
		
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		ToolBar toolbar = new ToolBar();
	
		
		Button btnSaveTaxonomyChildren = new Button(ConstantsSpeciesDiscovery.SAVE_TAXONOMY_CHILDREN);  
		Menu formatSubMenu = new Menu();
		btnSaveTaxonomyChildren.setMenu(formatSubMenu);  
		btnSaveTaxonomyChildren.setScale(ButtonScale.SMALL);  
		btnSaveTaxonomyChildren.setIconAlign(IconAlign.TOP);  
		btnSaveTaxonomyChildren.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		btnSaveTaxonomyChildren.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVE_TAXONOMY_CHILDREN, ConstantsSpeciesDiscovery.SAVES_TAXONOMY_CHILDREN_FROM_RESULT));
		
		MenuItem darwinCoreArchiveItem = new MenuItem(ConstantsSpeciesDiscovery.DARWIN_CORE_ARCHIVE);
		darwinCoreArchiveItem.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVES_IN_DARWIN_CORE_ARCHIVE_FORMAT));
		darwinCoreArchiveItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				eventBus.fireEvent(new CreateTaxonomyJobEvent(taxon, dataSource, TaxonomyJobType.BYCHILDREN));			
			}
		});
		
		formatSubMenu.add(darwinCoreArchiveItem);
		toolbar.add(btnSaveTaxonomyChildren);
		
		cp.setTopComponent(toolbar);
		cp.setBodyBorder(false);
//		cp.setScrollMode(Scroll.AUTOY);
		
		String tabName = taxon.getName() + " ("+ dataSource + ")";
		tabItem.setText(tabName);
		
		cp.add(getPanelForTaxonomy(taxon, dataSource, true));
		
		tabItem.add(cp);
		
		tabPanel.add(tabItem);
			
	}
	
	
	public FlexTable getStatusAndSynonyms(final LightTaxonomyRow taxon, final String dataSource){
		
		String refId = null;
		String statusRemark="";
		String statusName="";
		
		
		if (taxon.getStatusRefId() != null && !taxon.getStatusRefId().isEmpty())
			refId = taxon.getStatusRefId();
		
		if (taxon.getStatusRemarks() != null)
			statusRemark = taxon.getStatusRemarks();
		
		if (taxon.getStatusName() != null)
			statusName = taxon.getStatusName();
		
		//BUILD TABLE WITH STATUS REF ID; STATUS REMARK AND SYNONYMS
		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("imagetable-status");

		flexTable.setWidget(0, 0, new Label(TaxonomyGridField.STATUSREFNAME.getName()));
		flexTable.setWidget(0, 1, new Label(statusName));

		if (refId != null) {

			final String status;
			
			if(!statusRemark.isEmpty())
				status = statusRemark;
			else
				status = statusName;
			
			final String referenceId = refId;

			flexTable.setWidget(1, 0, new Label(TaxonomyGridField.STATUS_REMARKS.getName()));

			AbstractImagePrototype synonyms = AbstractImagePrototype.create(Resources.INSTANCE.getSearch());

			final Image imageStatusRemark = synonyms.createImage();
			imageStatusRemark.setStyleName("image-load-synonyms");
			imageStatusRemark.setAltText("show accepted name");
			imageStatusRemark.setTitle("show accepted name");
			final LayoutContainer layoutContainer = new LayoutContainer();

			final Label labelStatusRemark = new Label(statusRemark);
			labelStatusRemark.addStyleName("status-of");
			layoutContainer.add(labelStatusRemark);

			imageStatusRemark.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					flexTable.remove(layoutContainer);
					if(taxon.getParents().size()>0)
						tabTR.getTaxonomyByReferenceId(flexTable, 1, 1, status, referenceId, dataSource, taxon.getParents());
//						getTaxonomyByReferenceId(flexTable, 2, 1, status, referenceId, dataSource, taxon.getParents());

				}
			});

			layoutContainer.add(labelStatusRemark);
			layoutContainer.add(imageStatusRemark);

			flexTable.setWidget(1, 1, layoutContainer);

		} else {
			flexTable.setWidget(1, 0, new Label(TaxonomyGridField.STATUS_REMARKS.getName()));
			flexTable.setWidget(1, 1, new Label(statusRemark));
		}

		
		//Create row get synonyms
		flexTable.setWidget(2, 0,new Label(TaxonomyGridField.SYNONYMS.getName()));

		AbstractImagePrototype synonyms = AbstractImagePrototype.create(Resources.INSTANCE.getSearch());

		final Image imageSynonyms = synonyms.createImage();
		imageSynonyms.setStyleName("image-load-synonyms");
		imageSynonyms.setAltText("show synonyms");
		imageSynonyms.setTitle("show synonyms");

		imageSynonyms.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				flexTable.remove(imageSynonyms);

				if(taxon.getParents().size()>0){
					tabTR.getSynonymsByReferenceId(flexTable, 2, 1, taxon.getServiceId(),dataSource, taxon.getParents());
				}
			}
		});

		flexTable.setWidget(2, 1, imageSynonyms);
		
		return flexTable;
	}

}
