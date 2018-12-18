package org.gcube.portlets.user.workspace.client.view.panels;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.gridevent.ScopeChangeEvent;
import org.gcube.portlets.user.workspace.client.gridevent.SearchTextEvent;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.view.GxtComboBox;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBreadcrumbPathPanel;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class GxtSeachAndFilterPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 1, 2016
 */
public class GxtSeachAndFilterPanel extends LayoutContainer {

	private GxtComboBox comboBoxGxt = null;
	private ComboBox<ScopeModel> cbViewScope = null;
	//private final TextField<String> textSearch = new TextField<String>();
	
	InputAddOn searchAddOn = new InputAddOn();
	private TextBox textBox = new TextBox();
	private Button bSearch = new Button(ConstantsPortlet.SEARCH);
	private Button bHide = new Button("X");
	private TextField<String> textFull = new TextField<String>();
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private SimpleComboBox<String> cbNameFilter = null;
	private boolean isSearchActive = false;
	private VerticalPanel container = new VerticalPanel();
	private HorizontalPanel mainPane = new HorizontalPanel();
	private Button iconSearchButton = new Button("", IconType.SEARCH);
	private SimplePanel hpMain = new SimplePanel();
	private GxtBreadcrumbPathPanel toolbarPahtPanel;

	/**
	 * Instantiates a new gxt seach and filter panel.
	 *
	 * @param toolBarPathPanel the tool bar path panel
	 */
	public GxtSeachAndFilterPanel(GxtBreadcrumbPathPanel toolBarPathPanel) {
		
		setLayout(new FitLayout());
		setBorders(false);
		this.setId("SearchAndFilter");
		this.setBorders(false);
		this.comboBoxGxt = new GxtComboBox();
		this.cbViewScope = this.comboBoxGxt.getComboViewScope();
		this.cbNameFilter = this.comboBoxGxt.getComboStringFilter();
		this.textBox.setId("text-search");
		this.toolbarPahtPanel = toolBarPathPanel;
	
		searchAddOn.add(textBox);
		searchAddOn.add(bSearch);
		searchAddOn.add(bHide);
		textBox.setPlaceholder(ConstantsPortlet.SEARCHBYNAME);
		hpMain.add(searchAddOn);
		mainPane.setId("VerticalPanelSearchAndFilter");
	
		iconSearchButton.setSize(ButtonSize.LARGE);
		iconSearchButton.setType(ButtonType.LINK);
		iconSearchButton.getElement().getStyle().setColor("#999");
		
		//SOLUTION FOR SCOPE INVISIBLE
		cbViewScope.setVisible(false);

	
		mainPane.add(hpMain);
		mainPane.add(iconSearchButton);
		mainPane.add(this.toolbarPahtPanel.getToolBarPathPanel());
		
		mainPane.setVerticalAlign(VerticalAlignment.MIDDLE);

		this.addListeners();

		container.add(mainPane);
		add(container);

		updateSize();
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});
		hpMain.setVisible(false);
		iconSearchButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hpMain.setVisible(true);
				iconSearchButton.setVisible(false);
			}
		});
		layout();
		bHide.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hpMain.setVisible(false);
				iconSearchButton.setVisible(true);
			}
		});
	
	}

	/**
	 * Update window size.
	 */
	public void updateSize() {
		RootPanel workspace = RootPanel.get(ConstantsPortlet.WORKSPACEDIV);
		int leftBorder = workspace.getAbsoluteLeft();
		int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;
		mainPane.setWidth(rootWidth);
		mainPane.setHeight(35);
		setHeight(35);
	}

	/**
	 * Search text.
	 *
	 * @param value the value
	 */
	public void searchText(String value){
		textBox.setValue(value);
		String parentId = toolbarPahtPanel.getLastParent()!=null?toolbarPahtPanel.getLastParent().getIdentifier():null;
		AppController.getEventBus().fireEvent(new SearchTextEvent(value, parentId));
	}

	/**
	 * Adds the listeners.
	 */
	private void addListeners(){

		bSearch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isValidSearch()){
					searchText(textBox.getValue());
				}else if(textBox.getValue()==null || textBox.getValue().isEmpty()){
					searchCancel();
				}
			}
		});

		textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == 13) { // KEY_ENTER
					if(isValidSearch()){
						searchText(textBox.getValue());
					}else if(textBox.getValue()==null || textBox.getValue().isEmpty()){
						searchCancel();
					}
				}
				
			}
		});
		

		this.cbViewScope.addSelectionChangedListener(new SelectionChangedListener<ScopeModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ScopeModel> se) {

				ScopeModel scope = se.getSelectedItem();

				AppController.getEventBus().fireEvent(new ScopeChangeEvent(scope.getId()));

			}
		});

	}

	/**
	 * Reset fields.
	 */
	public void resetFields(){
		this.textBox.setValue("");
		this.textFull.reset();
		this.fromDate.reset();
		this.toDate.reset();
		this.cbNameFilter.reset();

	}

	/**
	 * Checks if is valid search.
	 *
	 * @return true, if is valid search
	 */
	private boolean isValidSearch(){
		return textBox.getValue()!=null && !textBox.getValue().isEmpty();
	}


	/**
	 * Search cancel.
	 */
	public void searchCancel(){
		resetFields();
	}

	/**
	 * Checks if is search active.
	 *
	 * @return true, if is search active
	 */
	public boolean isSearchActive(){
		return this.isSearchActive;
	}

	/**
	 * Sets the list scope.
	 *
	 * @param listScope the new list scope
	 */
	public void setListScope(List<ScopeModel> listScope){

		this.comboBoxGxt.setListScope(listScope);
	}

	/**
	 * Select scope by index.
	 *
	 * @param index the index
	 */
	public void selectScopeByIndex(int index){

		this.cbViewScope.setValue(cbViewScope.getStore().getAt(index));
	}

	/**
	 * Sets the search active.
	 *
	 * @param isSearchActive the new search active
	 */
	public void setSearchActive(boolean isSearchActive) {
		this.isSearchActive = isSearchActive;
	}


	/**
	 * Sets the empty text.
	 *
	 * @param emptyText the new empty text
	 */
	public void setEmptyText(String emptyText){
		textBox.setText(emptyText);
	}

	/**
	 * Gets the toolbar path panel.
	 *
	 * @return the toolbar path panel
	 */
	public GxtBreadcrumbPathPanel getToolbarPathPanel() {
		return toolbarPahtPanel;
	}

}