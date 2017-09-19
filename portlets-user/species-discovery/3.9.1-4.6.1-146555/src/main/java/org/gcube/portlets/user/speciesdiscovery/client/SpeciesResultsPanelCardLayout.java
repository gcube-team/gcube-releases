package org.gcube.portlets.user.speciesdiscovery.client;

import org.gcube.portlets.user.speciesdiscovery.client.gridview.ResultRowResultsPanel;
import org.gcube.portlets.user.speciesdiscovery.client.gridview.TaxonomyRowResultsPanel;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Element;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SpeciesResultsPanelCardLayout extends LayoutContainer {

	private CardLayout cardLayout = new CardLayout();
	private static SpeciesResultsPanelCardLayout instance;
	private ContentPanel activePanel = null;
	private ResultRowResultsPanel resultRowPanel;
	private TaxonomyRowResultsPanel taxonomyRowPanel;
	private SpeciesCapability currentSelectedCapability;
	

	public static SpeciesResultsPanelCardLayout getInstance() {
		return instance;
	}

	
	public SpeciesResultsPanelCardLayout(final EventBus eventBus, StreamPagingLoader loader, SearchController searchController) {
		this.resultRowPanel = new ResultRowResultsPanel(eventBus, loader,searchController);
		this.taxonomyRowPanel = new TaxonomyRowResultsPanel(eventBus, loader);
		
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		setLayout(new FitLayout());

		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(cardLayout);
		
		cp.add(resultRowPanel);
		cp.add(taxonomyRowPanel);
		
		cardLayout.setActiveItem(resultRowPanel);
		activePanel = resultRowPanel;
		
		add(cp);

	};

	
	public void updateCurrentGridView(SpeciesCapability capability) {
		this.currentSelectedCapability = capability;
		switchGridPanel();
	}
	
	
	private void switchGridPanel(){
		
		switch (currentSelectedCapability) {
		
		case RESULTITEM:
			
			activePanel = resultRowPanel;
			resultRowPanel.resetStore();
			cardLayout.setActiveItem(resultRowPanel);

			break;
			
		case TAXONOMYITEM:

			activePanel = taxonomyRowPanel;
			taxonomyRowPanel.resetStore();
			cardLayout.setActiveItem(taxonomyRowPanel);
			
			break;
			
		}
		
	}


	public void activeToolBarButtons(boolean b) {
		
		if(activePanel.equals(resultRowPanel))
			resultRowPanel.activeToolBarButtons(b);
		else if(activePanel.equals(taxonomyRowPanel))
			taxonomyRowPanel.activeToolBarButtons(b);
	}


	public void setFilterActive(boolean b, String filterValue) {
		if(activePanel.equals(resultRowPanel))
			resultRowPanel.setFilterActive(b, filterValue);
		else if(activePanel.equals(taxonomyRowPanel))
			taxonomyRowPanel.setFilterActive(b, filterValue);
		
	}


	public void activeBtnShowOnlySelected(boolean b) {
		if(activePanel.equals(resultRowPanel))
			resultRowPanel.activeBtnShowOnlySelected(b);
		else if(activePanel.equals(taxonomyRowPanel))
			taxonomyRowPanel.activeBtnShowOnlySelected(b);
		
	}


	public TaxonomyRowResultsPanel getTaxonomyRowPanel() {
		return taxonomyRowPanel;
	}
	
	public void setMaskGridPanel(boolean mask){
		
		if(activePanel.equals(resultRowPanel)){
			
			if(mask)
				resultRowPanel.getClassicGridView().mask(ConstantsSpeciesDiscovery.REQUEST_DATA, ConstantsSpeciesDiscovery.LOADINGSTYLE);
			else
				resultRowPanel.getClassicGridView().unmask();
		}
			
		else if(activePanel.equals(taxonomyRowPanel)){
			if(mask)
				taxonomyRowPanel.getClassicGridView().mask(ConstantsSpeciesDiscovery.REQUEST_DATA, ConstantsSpeciesDiscovery.LOADINGSTYLE);
			else
				taxonomyRowPanel.getClassicGridView().unmask();
		}

	}


	public ResultRowResultsPanel getResultRowPanel() {
		return resultRowPanel;
	}
	
}
