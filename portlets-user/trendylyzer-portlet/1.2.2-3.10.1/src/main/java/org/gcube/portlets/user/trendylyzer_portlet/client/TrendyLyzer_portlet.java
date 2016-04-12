package org.gcube.portlets.user.trendylyzer_portlet.client;




import java.util.List;

import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmClassification;
import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.OccurencePanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Resources;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.JobsPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.species_info.SpeciesInfoPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TrendyLyzer_portlet implements EntryPoint {
	public static Resources resources = GWT.create(Resources.class);
	private static List<AlgorithmClassification> algorithmsClassifications = null;
	private static final String TL_DIV = "contentDiv";
	private LayoutContainer menu;
	public enum MenuItem {OCCURENCES, INFOSP, COMPUTATIONS};	
	private static final TrendyLyzerPortletServiceAsync trendyLService = GWT.create(TrendyLyzerPortletService.class);
	private Header header;
	private LayoutContainer trendyLyzerLayout;
	private LayoutContainer centerPanel = new LayoutContainer(new FitLayout());
	private BorderLayoutData centerPanelData;
	private LayoutContainer previousPanel;
	private static TabularData tabularData;
	private OccurencePanel occPanel= new OccurencePanel();
	private SpeciesInfoPanel spPanel= new SpeciesInfoPanel();
	private JobsPanel resultsPanel= new JobsPanel();
	public void onModuleLoad() {
		trendyLyzerLayout = new LayoutContainer();
		trendyLyzerLayout.setLayout(new BorderLayout());
		trendyLyzerLayout.setStyleAttribute("background-color", "#FFFFFF");
		
		BorderLayoutData northPanelData = new BorderLayoutData(LayoutRegion.NORTH, 80);
		northPanelData.setCollapsible(false);
		northPanelData.setFloatable(false);
		northPanelData.setHideCollapseTool(true);
		northPanelData.setSplit(false);
		northPanelData.setMargins(new Margins(0, 0, 5, 0));

		
		header = new Header() {
			@Override
			public void select(MenuItem menuItem) {
				if (menuItem==null)
					switchTo(menu);
				else if (menuItem==MenuItem.OCCURENCES)
					switchTo(occPanel);
				else if (menuItem==MenuItem.INFOSP)
					switchTo(spPanel);
				else if (menuItem==MenuItem.COMPUTATIONS)
					switchTo(resultsPanel);
			}			
		};
		trendyLyzerLayout.add(header, northPanelData);

		// CENTER
		
		centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		centerPanel.setStyleAttribute("padding-bottom", "5px");
		
		trendyLyzerLayout.add(centerPanel, centerPanelData);
		menu = createMenuPanel();
		centerPanel.add(menu);
		previousPanel = menu;
		RootPanel.get(TL_DIV).add(trendyLyzerLayout);
			
		
	}
	private LayoutContainer createMenuPanel() {
		LayoutContainer topLc = new LayoutContainer();
		LayoutContainer lc = new LayoutContainer();
		//lc.setLayout(new RowLayout(Orientation.HORIZONTAL));
		
//		lc.setSize(640, 400);
		lc.addStyleName("smLayoutContainer");
		lc.addStyleName("smMenu");


		LayoutContainer itemInputSpace = createMenuItem(
				"Observed   Trends", 
				"This section allows to produce Species and Taxa trends.",
				resources.experiment(),
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						switchTo(occPanel);
						header.setMenuSelected(MenuItem.OCCURENCES);
					}
				});
	
		lc.add(itemInputSpace);
		
		LayoutContainer itemJobs = createMenuItem(
				"Check the Computations", 
				"This section allows to check the status of the computation. By clicking on the completed jobs it is possible to visualize the data set contents.",
				resources.computationIcon(),
				new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						switchTo(resultsPanel);
						header.setMenuSelected(MenuItem.COMPUTATIONS);
					}
				});
		lc.add(itemJobs);

	


		topLc.add(lc);
		return topLc;
	}


	private HorizontalPanel createMenuItem(String title, String description, ImageResource imgResource, Listener<BaseEvent> listener) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("smMenuItem");
		hp.addListener(Events.OnClick, listener);

		Image img = new Image(imgResource);

		Html text = new Html("<b>"+title+"</b><br>"+description);
		text.addStyleName("smMenuItemText");

		hp.add(text, new TableData("350px", "100px"));		
		hp.add(img);
		return hp;
	}

	protected void switchTo(LayoutContainer lc) {
		
		boolean updt = (previousPanel!=lc && (previousPanel==menu || lc==menu));
			
				
		centerPanel.remove(previousPanel);
		centerPanel.add(lc);
		centerPanel.layout();
		previousPanel = lc;
		
		if (updt)
			updateSize();
	}
	public static List<AlgorithmClassification> getAlgorithmsClassifications() {
		return algorithmsClassifications;
	}

	/**
	 * @param AlgorithmClassification the AlgorithmClassification to set
	 */
	public static void setAlgorithmsClassifications(
			List<AlgorithmClassification> algorithmsClassifications) {
		TrendyLyzer_portlet.algorithmsClassifications = algorithmsClassifications;
	}

	public static AlgorithmClassification getDefaultAlgorithmClassification() {
		if (algorithmsClassifications==null)
			return null;
		AlgorithmClassification find = null;
		for (AlgorithmClassification alg : algorithmsClassifications)
			if (alg.getName().equals(Constants.computationClassificationName))
				find = alg;
		return find;
	}

	public static AlgorithmClassification getAlgorithmClassificationByName(String classificationName) {
		if (algorithmsClassifications==null)
			return null;
		AlgorithmClassification find = null;
		for (AlgorithmClassification oc : algorithmsClassifications)
			if (oc.getName().equals(classificationName))
				find = oc;
		return (find==null ? getDefaultAlgorithmClassification() : find);
	}

	public static TrendyLyzerPortletServiceAsync getService() {
		return trendyLService;
	}

	/**
	 * @return
	 */
	public static TabularData getTabularData() {
		if (tabularData == null)
			tabularData = new TabularData(Constants.TD_DATASOURCE_FACTORY_ID); 
		return tabularData;
	}
	private void updateSize() {
		RootPanel smDiv = RootPanel.get(TL_DIV);

		int topBorder = smDiv.getAbsoluteTop();
		int leftBorder = smDiv.getAbsoluteLeft();

		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer == null)?0:(footer.getOffsetHeight()-15));
		int rootWidth = Window.getClientWidth() - 2* leftBorder-5; //- rightScrollBar;

//		System.out.println("New Statistical Manager dimension Width: "+rootWidth+"; Height: "+rootHeight);

		if (previousPanel == menu)
			trendyLyzerLayout.setSize(rootWidth, 700);
		else
			trendyLyzerLayout.setSize(rootWidth, rootHeight);	
	}

}
