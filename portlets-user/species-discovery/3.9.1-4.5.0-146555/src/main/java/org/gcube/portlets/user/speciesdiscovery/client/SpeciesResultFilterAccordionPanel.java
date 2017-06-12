package org.gcube.portlets.user.speciesdiscovery.client;

import org.gcube.portlets.user.speciesdiscovery.client.filterresult.ResultFilterPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.EventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SpeciesResultFilterAccordionPanel extends ContentPanel {

	public final String headerFilterResultPanel = "Filter your results";
	private ResultFilterPanelManager resultFilterManager = ResultFilterPanelManager.getInstance();

	public SpeciesResultFilterAccordionPanel(EventBus eventBus, StreamPagingLoader streamPagingLoader) {
		init();
		resultFilterManager.bind(streamPagingLoader);
		resultFilterManager.setEventBus(eventBus);
	}

	private void init() {
		setHeaderVisible(true);
		setHeading(headerFilterResultPanel);

		setBodyBorder(false);
		setLayout(new AccordionLayout());
		// setIcon(Resources.ICONS.accordion());

		for (ContentPanel panel : resultFilterManager.getListResultFilterPanel()) {
			panel.setAnimCollapse(false);
			panel.setLayout(new FitLayout());
			add(panel);
		}
	}
	
	public void resetFilters(){
		resultFilterManager.resetFilters();
	}

}
