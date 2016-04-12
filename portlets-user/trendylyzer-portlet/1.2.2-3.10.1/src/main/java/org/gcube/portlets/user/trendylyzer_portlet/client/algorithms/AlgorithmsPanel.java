package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.event.MenuEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class AlgorithmsPanel extends ContentPanel {
	public final static String[] classificationNames = {"User Perspective", "Computation Perspective"};
	private static final String LOADING_MESSAGE = "Loading Algorithms...";
	protected static final String ERROR_GET_OPERATORS = "Algorithms not loaded.";
	private static final String SHOW_ALL_OPERATORS_TOOLTIP = "Show all Algorithms without category classification.";
	private static final String SHOW_CATEGORIES_TOOLTIP = "Show Algorithms by Categories";
	private AlgorithmsPanelHandler handler;
	private ToolBar toolbar;
	private Map<String, List<AlgorithmCategoryPanel>> mapCategoriesPanels = new HashMap<String, List<AlgorithmCategoryPanel>>();
	private enum View {CATEGORIES, FILTER, ALL};
	private View view = null;
	private String currentClassificationName = "User Perspective";

	/**
	 * 
	 */
	 public AlgorithmsPanel(AlgorithmsPanelHandler handler) {
		super();
		this.handler = handler;

		//this.setHeading(".: Algorithm");
		this.setScrollMode(Scroll.AUTO);
		this.setToolbar();
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.ContentPanel#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		// if the operators classification is not loaded, let's load by an rpc
		if (TrendyLyzer_portlet.getAlgorithmsClassifications()==null) {
			waitMessage(true);
			TrendyLyzer_portlet.getService().getAlgorithmsClassifications(new AsyncCallback<List<AlgorithmClassification>>() {
				@Override
				public void onSuccess(List<AlgorithmClassification> result) {
					waitMessage(false);
					TrendyLyzer_portlet.setAlgorithmsClassifications(result);
					showCategoriesList(false);
				}
				@Override
				public void onFailure(Throwable caught) {
					waitMessage(false);
					MessageBox.alert("Error", ERROR_GET_OPERATORS, null);
				}
			});
		} else
			showCategoriesList(false);
	}

	/**
	 * 
	 */
	private void setToolbar() {
		toolbar = new ToolBar();
		
////		Button showAllOperatorsButton = new Button("",Images.showAllOperators(), new SelectionListener<ButtonEvent>() {
////			@Override
////			public void componentSelected(ButtonEvent ce) {
////				showAllOperatorsList();
////			}
////		});
////		showAllOperatorsButton.setToolTip(SHOW_ALL_OPERATORS_TOOLTIP);
////		
////		Button showCategoriesButton = new Button("",Images.showCategories(), new SelectionListener<ButtonEvent>() {
////			@Override
////			public void componentSelected(ButtonEvent ce) {
////				showCategoriesList(false);
////			}
////		});
////		showCategoriesButton.setToolTip(SHOW_CATEGORIES_TOOLTIP);
				
	    final TriggerField<String> filterField = new TriggerField<String>();
	    filterField.setWidth(100);
	    filterField.setTriggerStyle("x-form-clear-trigger");
	    filterField.addListener(Events.TriggerClick, new Listener(){
	    	@Override
			public void handleEvent(BaseEvent be) {
				filterField.setValue(null);
				showCategoriesList(false);
			}	    	
	    });
	    
	    final DelayedTask task = new DelayedTask(new Listener(){
	    	@Override
			public void handleEvent(BaseEvent be) {
				String searchText = filterField.getRawValue();
				if (searchText==null || searchText.contentEquals(""))
					showCategoriesList(false);
				if (searchText.length()>=2)
					showFilteredList(searchText);				
			}
	    });
	    
	    filterField.addKeyListener(new KeyListener(){
	    	@Override
	    	public void componentKeyUp(ComponentEvent event) {
	    		super.componentKeyUp(event);
	    		if (!event.isSpecialKey() || event.getKeyCode() == KeyCodes.KEY_BACKSPACE || event.getKeyCode() == 46)
	    			task.delay(500);
	    	}
	    });
	    
	    
//	    final Button btnMenuPerspective = new Button(currentClassificationName, Images.userPerspective());
//	    Menu menuPerspective = new Menu();
//	    for (final String perspectiveName : classificationNames) {
//	    	final AbstractImagePrototype img = perspectiveName.equals("User Perspective") ? Images.userPerspective() : Images.computationPerspective();
//	    	menuPerspective.add(new MenuItem(perspectiveName, img, new SelectionListener<MenuEvent>(){
//	    		@Override
//	    		public void componentSelected(MenuEvent ce) {
//	    			currentClassificationName = perspectiveName;
//	    			btnMenuPerspective.setText(perspectiveName);
//	    			btnMenuPerspective.setIcon(img);
//	    			showCategoriesList(true);
//	    		}
//	    	}));
//	    }
//	    btnMenuPerspective.setMenu(menuPerspective);
		
	    
	    //toolbar.add(showCategoriesButton);
	   // toolbar.add(showAllOperatorsButton);
	    toolbar.add(filterField);
	    //toolbar.add(btnMenuPerspective);
	    
		this.setTopComponent(toolbar);
	}

	/**
	 * @return
	 */
	private ToolTipConfig createToolTip(AlgorithmCategory cat) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setText(cat.getBriefDescription());  
		tooltipConfig.setTitle(cat.getName());  
		tooltipConfig.setMouseOffset(new int[] {0, 0});  
		tooltipConfig.setAnchor("left");
		tooltipConfig.setDismissDelay(0);
		tooltipConfig.setTemplate(new Template(
				getTooltipTemplate(GWT.getModuleBaseURL(), cat.getId(), cat.hasImage(), cat.getDescription())
				));  
		tooltipConfig.setMaxWidth(300);
		return tooltipConfig;
	}

	private native String getTooltipTemplate(String base, String id, boolean hasImage, String description) /*-{ 
    	var html = [ 
			"<div class='categoryItemTooltip'>",
				"<img src='" + base + "../images/categories/"+(hasImage ? id : "DEFAULT_IMAGE")+".png' >",
				description,
			"</div>" 
			
		]; 
		return html.join(""); 
	}-*/;  


	
	private void waitMessage(boolean show) {
		if (show)
			this.mask(LOADING_MESSAGE, "x-mask-loading");
		else
			this.unmask();
	}
	private void showFilteredList(String searchText) {
		List<Algorithm> algorithms = TrendyLyzer_portlet.getAlgorithmClassificationByName(currentClassificationName).getAlgorithms();
		List<Algorithm> filteredOperators = new ArrayList<Algorithm>();
		List<String> ids = new ArrayList<String>();
		for (Algorithm alg: algorithms)
			// check for filtering and prevent duplicates
			if (alg.getName().toLowerCase().contains(searchText.toLowerCase())
					&& !ids.contains(alg.getId())) {				
				filteredOperators.add(alg);
				ids.add(alg.getId());
			}

		this.removeAll();

		Html html = new Html("Filtered results <span class='counter'>("+filteredOperators.size()+" item" + (filteredOperators.size()==1 ?"" : "s") +" found)</span>");
		html.addStyleName("filterResultText");		
		this.add(html);
		
		for (Algorithm alg: filteredOperators)
			this.add(new AlgorithmPanel(alg, handler));
		
		this.layout();
		view = View.FILTER; 
	}
	

	
	
	private void showCategoriesList(boolean force) {
		if (force || view != View.CATEGORIES) {
			List< AlgorithmCategoryPanel> categoryPanels = mapCategoriesPanels.get(currentClassificationName);
			if (categoryPanels==null) {
				// get category panels from classification
				categoryPanels = new ArrayList<AlgorithmCategoryPanel>();
				for (AlgorithmCategory cat : TrendyLyzer_portlet.getAlgorithmClassificationByName(currentClassificationName).getOperatorCategories())
					categoryPanels.add(new AlgorithmCategoryPanel(handler, cat));
				mapCategoriesPanels.put(currentClassificationName, categoryPanels);
			}
			
			this.removeAll();
			for (AlgorithmCategoryPanel panel: categoryPanels)
				this.add(panel);
			this.layout();
			view = View.CATEGORIES;
		}
	}
	
//	private void showAllOperatorsList() {
//		if (view != View.ALL) {
//			List<Algorithm> algorithms = TrendyLyzer_portlet.getAlgorithmClassificationByName(currentClassificationName).getAlgorithms();
//
//			this.removeAll();
//
//			Html html = new Html("All Operators <span class='counter'>("+algorithms.size()+" item" + (algorithms.size()==1 ?"" : "s") +" found)</span>");
//			html.addStyleName("filterResultText");
//			this.add(html);
//			
//			for (Algorithm alg: algorithms)
//				this.add(new AlgorithmPanel(alg, handler));
//			
//			this.layout();
//			view = View.ALL;
//		}
//	}
}
