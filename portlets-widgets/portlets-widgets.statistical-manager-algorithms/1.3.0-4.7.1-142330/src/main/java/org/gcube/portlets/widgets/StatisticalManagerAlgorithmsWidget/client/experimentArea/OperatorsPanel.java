/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Constants;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Services;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorCategory;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorsClassification;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;



/**
 * @author ceras
 *
 */
public class OperatorsPanel extends ContentPanel {

	private static final String LOADING_MESSAGE = "Loading Operators...";
	protected static final String ERROR_GET_OPERATORS = "Operators not loaded.";
	private static final String SHOW_ALL_OPERATORS_TOOLTIP = "Show all Operators without category classification.";
	private static final String SHOW_CATEGORIES_TOOLTIP = "Show Operators by Categories";
	private OperatorsPanelHandler handler;
	private ToolBar toolbar;
//	private ArrayList<OperatorCategoryPanel> categoryPanels;
	private Map<String, List<OperatorCategoryPanel>> mapCategoriesPanels = new HashMap<String, List<OperatorCategoryPanel>>();
	private enum View {CATEGORIES, FILTER, ALL};
	private View view = null;
	private String currentClassificationName = Constants.userClassificationName;
	protected  Logger logger = Logger.getLogger("logger");

	/**
	 * 
	 */
	public OperatorsPanel(OperatorsPanelHandler handler) {
		super();
		logger.log(Level.SEVERE,"OperatorsPanel Constructor");

		this.handler = handler;

		this.setHeading(".: Operators");
		this.setScrollMode(Scroll.AUTO);
		this.setToolbar();
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.ContentPanel#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		logger.log(Level.SEVERE,"insize onRender of OperatorsPane");

		super.onRender(parent, pos);
		
		// if the operators classification is not loaded, let's load by an rpc
		if (Services.getOperatorsClassifications()==null) {
			waitMessage(true);
			Services.getStatisticalService().getOperatorsClassifications(StatisticalManagerExperimentsWidget.instance().getListSelectedAlg(), new AsyncCallback<List<OperatorsClassification>>() {
	
				@Override
				public void onSuccess(List<OperatorsClassification> result) {
					waitMessage(false);
					Services.setOperatorsClassifications(result);
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
		
		Button showAllOperatorsButton = new Button("",Images.showAllOperators(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showAllOperatorsList();
			}
		});
		showAllOperatorsButton.setToolTip(SHOW_ALL_OPERATORS_TOOLTIP);
		
		Button showCategoriesButton = new Button("",Images.showCategories(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showCategoriesList(false);
			}
		});
		showCategoriesButton.setToolTip(SHOW_CATEGORIES_TOOLTIP);
				
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
	    
	    
	    final Button btnMenuPerspective = new Button(currentClassificationName, Images.userPerspective());
	    Menu menuPerspective = new Menu();
	    for (final String perspectiveName : Constants.classificationNames) {
	    	final AbstractImagePrototype img = perspectiveName.equals(Constants.userClassificationName) ? Images.userPerspective() : Images.computationPerspective();
	    	menuPerspective.add(new MenuItem(perspectiveName, img, new SelectionListener<MenuEvent>(){
	    		@Override
	    		public void componentSelected(MenuEvent ce) {
	    			currentClassificationName = perspectiveName;
	    			btnMenuPerspective.setText(perspectiveName);
	    			btnMenuPerspective.setIcon(img);
	    			showCategoriesList(true);
	    		}
	    	}));
	    }
	    btnMenuPerspective.setMenu(menuPerspective);
		
	    
	    toolbar.add(showCategoriesButton);
	    toolbar.add(showAllOperatorsButton);
	    toolbar.add(filterField);
	    toolbar.add(btnMenuPerspective);
	    
		this.setTopComponent(toolbar);
	}

	/**
	 * @return
	 */
	private ToolTipConfig createToolTip(OperatorCategory cat) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setText(cat.getBriefDescription());  
		tooltipConfig.setTitle("<br>&nbsp;&nbsp;"+cat.getName());  
		tooltipConfig.setMouseOffset(new int[] {0, 0});  
		tooltipConfig.setAnchor("left");
		tooltipConfig.setDismissDelay(0);
		String imgUri=Images.categoryImagesMap.get(cat.hasImage()?cat.getId():"DEFAULT_IMAGE").getSafeUri().asString();
		tooltipConfig.setTemplate(new Template(
				getTooltipTemplate(imgUri, cat.getDescription())
				));  
		//		config.setCloseable(true);  
		tooltipConfig.setMaxWidth(300);
		return tooltipConfig;
	}

	private native String getTooltipTemplate(String imgUri, String description) /*-{ 
    	var html = [ 
			"<div class='categoryItemTooltip'>",
				"<img src='"+imgUri+"' >",
				description,
			"</div>" 
			//    '<div><ul style="list-style: disc; margin: 0px 0px 5px 15px">', 
			//    '<li>5 bedrooms</li>', 
			//    '<li>2 baths</li>', 
			//    '<li>Large backyard</li>', 
			//    '<li>Close to metro</li>', 
			//    '</ul>', 
			//    '</div>' 
		]; 
		return html.join(""); 
	}-*/;  


	
	private void waitMessage(boolean show) {
		if (show)
			this.mask(LOADING_MESSAGE, Constants.maskLoadingStyle);
		else
			this.unmask();
	}

	private void showFilteredList(String searchText) {
		List<Operator> operators = Services.getOperatorsClassificationByName(currentClassificationName).getOperators();
		List<Operator> filteredOperators = new ArrayList<Operator>();
		List<String> ids = new ArrayList<String>();
		for (Operator op: operators)
			// check for filtering and prevent duplicates
			if (op.getName().toLowerCase().contains(searchText.toLowerCase())
					&& !ids.contains(op.getId())) {				
				filteredOperators.add(op);
				ids.add(op.getId());
			}

		this.removeAll();

		Html html = new Html("Filtered results <span class='counter'>("+filteredOperators.size()+" item" + (filteredOperators.size()==1 ?"" : "s") +" found)</span>");
		html.addStyleName("filterResultText");		
		this.add(html);
		
		for (Operator op: filteredOperators)
			this.add(new OperatorPanel(op, handler));
		
		this.layout();
		view = View.FILTER; 
	}
	
	private void showCategoriesList(boolean force) {
		if (force || view != View.CATEGORIES) {
			List<OperatorCategoryPanel> categoryPanels = mapCategoriesPanels.get(currentClassificationName);
			if (categoryPanels==null) {
				// get category panels from classification
				categoryPanels = new ArrayList<OperatorCategoryPanel>();
				for (OperatorCategory cat : Services.getOperatorsClassificationByName(currentClassificationName).getOperatorCategories())
					categoryPanels.add(new OperatorCategoryPanel(handler, cat));
				mapCategoriesPanels.put(currentClassificationName, categoryPanels);
			}
			
			this.removeAll();
			for (OperatorCategoryPanel panel: categoryPanels)
				this.add(panel);
			this.layout();
			view = View.CATEGORIES;
		}
	}
	
	private void showAllOperatorsList() {
		if (view != View.ALL) {
			List<Operator> operators = Services.getOperatorsClassificationByName(currentClassificationName).getOperators();

			this.removeAll();

			Html html = new Html("All Operators <span class='counter'>("+operators.size()+" item" + (operators.size()==1 ?"" : "s") +" found)</span>");
			html.addStyleName("filterResultText");
			this.add(html);
			
			for (Operator op: operators)
				this.add(new OperatorPanel(op, handler));
			
			this.layout();
			view = View.ALL;
		}
	}
	
}
