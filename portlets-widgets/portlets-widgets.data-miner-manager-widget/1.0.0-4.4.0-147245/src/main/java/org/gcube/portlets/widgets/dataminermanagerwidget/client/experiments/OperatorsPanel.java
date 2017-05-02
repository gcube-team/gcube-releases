/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorCategory;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OperatorsClassificationEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OperatorsClassificationRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.OperatorsClassificationRequestType;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.Constants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OperatorsPanel extends FramedPanel {

	private static final String LOADING_MESSAGE = "Loading Operators...";
	// private static final String ERROR_GET_OPERATORS =
	// "Operators not loaded.";
	private static final String SHOW_ALL_OPERATORS_TOOLTIP = "Show all Operators without category classification.";
	private static final String SHOW_CATEGORIES_TOOLTIP = "Show Operators by Categories";
	private OperatorsPanelHandler handler;
	private VerticalLayoutContainer topV;
	private ToolBar toolBar;
	private VerticalLayoutContainer v;
	private OperatorsClassification operatorsClassification;
	private List<Operator> operators;

	// private ArrayList<OperatorCategoryPanel> categoryPanels;
	private Map<String, List<OperatorCategoryPanel>> mapCategoriesPanels = new HashMap<String, List<OperatorCategoryPanel>>();

	private enum View {
		CATEGORIES, FILTER, ALL
	};

	private View view = null;
	private String currentClassificationName = Constants.UserClassificationName;


	/**
	 * 
	 */
	public OperatorsPanel(OperatorsPanelHandler handler) {
		super();
		this.handler = handler;
		this.operators = new ArrayList<Operator>();
		init();
		bind();
		create();

	}

	private void init() {
		setHeadingText("Operators");
		setBodyStyle("backgroundColor:white;");
		setCollapsible(true);
	}

	private void bind() {
		EventBusProvider.INSTANCE
				.addHandler(
						OperatorsClassificationEvent.TYPE,
						new OperatorsClassificationEvent.OperatorsClassificationEventHandler() {

							@Override
							public void onOperatorsClassification(
									OperatorsClassificationEvent event) {
								Log.debug("OperatorsPanel catch OperatorsClassificationEvent: "
										+ event);
								manageOperatorsClassificationEvent(event);
							}
						});
	}

	private void manageOperatorsClassificationEvent(
			OperatorsClassificationEvent event) {
		if (event.getOperatorsClassificationRequestType().compareTo(
				OperatorsClassificationRequestType.ByName) == 0) {
			if (currentClassificationName.compareTo(event
					.getClassificationName()) == 0) {
				waitMessage(false);
				if (event.getOperatorsClassification() != null) {
					operatorsClassification = event
							.getOperatorsClassification();
					operators.clear();
					operators.addAll(event.getOperatorsClassification()
							.getOperators());
					String operatorId = event.getOperatorId();

					Log.debug("ShowCategoriesList");
					List<OperatorCategoryPanel> categoryPanels = mapCategoriesPanels
							.get(currentClassificationName);
					if (categoryPanels == null) {
						categoryPanels = new ArrayList<OperatorCategoryPanel>();
						if (operatorsClassification != null) {
							for (OperatorCategory cat : operatorsClassification
									.getOperatorCategories()) {
								categoryPanels.add(new OperatorCategoryPanel(
										handler, cat));
							}
						}
						mapCategoriesPanels.put(currentClassificationName,
								categoryPanels);
					}

					v.clear();
					for (OperatorCategoryPanel panel : categoryPanels){
						v.add(panel);
						
					}
					view = View.CATEGORIES;
					if(operatorId!=null&&!operatorId.isEmpty()){
						OperatorCategory operatorCategoryDefault=null;
						Operator operatorDefault=null;
						for(Operator op:operators){
							if(op.getId().compareTo(operatorId)==0){
								operatorDefault=op;
								operatorCategoryDefault=op.getCategory();
								break;
							}
						}
						if(operatorCategoryDefault!=null){
							for (OperatorCategoryPanel opCategoryPanel : categoryPanels){
								if(opCategoryPanel.getCategory().compareTo(operatorCategoryDefault)==0){
									opCategoryPanel.setOperatorDefault(operatorDefault);
									break;
								}
								
							}
						}
						
						
					}
					
					forceLayout();
				}

			}
		}

	}

	private void create() {
		topV = new VerticalLayoutContainer();
		initToolbar();
		topV.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		SimpleContainer operators = new SimpleContainer();
		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		operators.add(v);
		topV.add(operators, new VerticalLayoutData(1, 1, new Margins(0)));
		add(topV);
		waitMessage(true);
		OperatorsClassificationRequestEvent operatorsClassificationRequestEvent = new OperatorsClassificationRequestEvent(
				currentClassificationName, true);
		Log.debug("OperatorsPanel fire: " + operatorsClassificationRequestEvent);
		EventBusProvider.INSTANCE
				.fireEvent(operatorsClassificationRequestEvent);
	}

	/**
	 * 
	 */
	private void initToolbar() {
		toolBar = new ToolBar();

		final StoreFilterField<String> filterField = new StoreFilterField<String>() {

			@Override
			protected boolean doSelect(Store<String> store, String parent,
					String item, String filter) {
				Log.debug("StoreFilterField: " + item + " " + filter);
				return false;
			}

		};

		filterField.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				String searchText = filterField.getCurrentValue();
				if (searchText == null || searchText.isEmpty()) {
					showCategoriesList(false);
				} else {
					if (searchText.length() >= 2) {
						showFilteredList(searchText);
					}
				}

			}
		});
		filterField.setWidth(100);

		TextButton showAllOperatorsButton = new TextButton();
		showAllOperatorsButton.setIcon(DataMinerManagerPanel.resources
				.sortAscending());
		showAllOperatorsButton
				.addSelectHandler(new SelectEvent.SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						filterField.clear();
						showAllOperatorsList();

					}
				});
		showAllOperatorsButton.setToolTip(SHOW_ALL_OPERATORS_TOOLTIP);

		TextButton showCategoriesButton = new TextButton();
		showCategoriesButton.setIcon(DataMinerManagerPanel.resources.tree());
		showCategoriesButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				filterField.clear();
				showCategoriesList(false);

			}
		});

		showCategoriesButton.setToolTip(SHOW_CATEGORIES_TOOLTIP);

		final TextButton btnMenuPerspective = new TextButton(
				currentClassificationName);
		btnMenuPerspective
				.setIcon(DataMinerManagerPanel.resources.userPerspective());

		Menu menuPerspective = new Menu();

		for (final String perspectiveName : Constants.ClassificationNames) {
			final ImageResource img = perspectiveName
					.equals(Constants.UserClassificationName) ? DataMinerManagerPanel.resources
					.userPerspective() : DataMinerManagerPanel.resources
					.computationPerspective();
			MenuItem perspectiveItem = new MenuItem(perspectiveName);
			perspectiveItem.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					filterField.clear();
					currentClassificationName = perspectiveName;
					btnMenuPerspective.setText(perspectiveName);
					btnMenuPerspective.setIcon(img);
					showCategoriesList(true);

				}
			});
			perspectiveItem.setIcon(img);
			menuPerspective.add(perspectiveItem);
		}
		btnMenuPerspective.setMenu(menuPerspective);

		toolBar.add(showCategoriesButton);
		toolBar.add(showAllOperatorsButton);
		toolBar.add(filterField);
		toolBar.add(btnMenuPerspective);

		return;
	}

	private void waitMessage(boolean show) {
		if (show)
			this.mask(LOADING_MESSAGE);
		else
			this.unmask();
	}

	private void showFilteredList(String searchText) {

		List<Operator> filteredOperators = new ArrayList<Operator>();
		List<String> ids = new ArrayList<String>();
		for (Operator op : operators)
			// check for filtering and prevent duplicates
			if (op.getName().toLowerCase().contains(searchText.toLowerCase())
					&& !ids.contains(op.getId())) {
				filteredOperators.add(op);
				ids.add(op.getId());
			}

		v.clear();

		HTML html = new HTML("Filtered results <span class='counter'>("
				+ filteredOperators.size() + " item"
				+ (filteredOperators.size() == 1 ? "" : "s") + " found)</span>");
		html.addStyleName("filterResultText");
		v.add(html);

		for (Operator op : filteredOperators)
			v.add(new OperatorPanel(op, handler));
		view = View.FILTER;
		forceLayout();

	}

	private void showCategoriesList(boolean force) {

		try {
			if (force || view != View.CATEGORIES) {
				Log.debug("ShowCategoriesList");
				List<OperatorCategoryPanel> categoryPanels = mapCategoriesPanels
						.get(currentClassificationName);
				if (categoryPanels == null) {
					categoryPanels = new ArrayList<OperatorCategoryPanel>();
					if (operatorsClassification != null) {
						for (OperatorCategory cat : operatorsClassification
								.getOperatorCategories()) {
							categoryPanels.add(new OperatorCategoryPanel(
									handler, cat));
						}
					}
					mapCategoriesPanels.put(currentClassificationName,
							categoryPanels);
				}

				v.clear();
				for (OperatorCategoryPanel panel : categoryPanels)
					v.add(panel);
				view = View.CATEGORIES;
				forceLayout();
			}
		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void showAllOperatorsList() {
		if (view != View.ALL) {
			v.clear();

			HTML html = new HTML("All Operators <span class='counter'>("
					+ operators.size() + " item"
					+ (operators.size() == 1 ? "" : "s") + " found)</span>");
			html.addStyleName("filterResultText");
			v.add(html);

			for (Operator op : operators)
				v.add(new OperatorPanel(op, handler));
			view = View.ALL;
			forceLayout();

		}
	}

}
