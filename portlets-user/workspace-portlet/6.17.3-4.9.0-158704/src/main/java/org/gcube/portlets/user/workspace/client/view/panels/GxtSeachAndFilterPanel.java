package org.gcube.portlets.user.workspace.client.view.panels;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.event.SaveSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.event.ScopeChangeEvent;
import org.gcube.portlets.user.workspace.client.event.SearchTextEvent;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.view.GxtComboBox;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBreadcrumbPathPanel;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class GxtSeachAndFilterPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 1, 2016
 */
public class GxtSeachAndFilterPanel extends LayoutContainer {

	private GxtComboBox comboBoxGxt = null;
	private ComboBox<ScopeModel> cbViewScope = null;
	private final TextField<String> textSearch = new TextField<String>();
	private Button bSearch = new Button(ConstantsPortlet.SEARCH);
	private Button bCancel = new Button(ConstantsPortlet.CANCEL);
	private Button bSave = new Button(ConstantsPortlet.SAVE);
	private TextField<String> textFull = new TextField<String>();
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private SimpleComboBox<String> cbNameFilter = null;
	private boolean isSearchActive = false;
	private VerticalPanel verticalPanel = new VerticalPanel();
	private HorizontalPanel hp = new HorizontalPanel();

	private HorizontalPanel hpMain = new HorizontalPanel();
	private GxtBreadcrumbPathPanel toolbarPahtPanel;

	/**
	 * Instantiates a new gxt seach and filter panel.
	 *
	 * @param toolBarPathPanel the tool bar path panel
	 */
	public GxtSeachAndFilterPanel(GxtBreadcrumbPathPanel toolBarPathPanel) {

		setLayout(new FitLayout());
		setBorders(true);
		this.setId("SearchAndFilter");
		this.setBorders(false);
		this.comboBoxGxt = new GxtComboBox();
		this.cbViewScope = this.comboBoxGxt.getComboViewScope();
		this.cbNameFilter = this.comboBoxGxt.getComboStringFilter();
		this.textSearch.setId("text-search");
		this.cbViewScope.setWidth(360);
		this.toolbarPahtPanel = toolBarPathPanel;

		hp.setStyleAttribute("margin-left", "2px");
		seVisibleButtonsCancelSave(false);

		textSearch.setAllowBlank(true);
		textSearch.setEmptyText(ConstantsPortlet.SEARCHBYNAME);

		/* COMMENTED TO FIX https://support.social.isti.cnr.it/ticket/87
		textSearch.setRegex("^[a-zA-Z0-9]+[ a-zA-Z0-9_().-]*"); //alphanumeric
		textSearch.getMessages().setRegexText(ConstantsExplorer.MESSAGE_SEARCH_FORCE_APHANUMERIC);
		textSearch.setAutoValidate(true);
		*/
		hp.add(textSearch);

//		bSearch.setStyleName("wizardButton");
//		bSearch.getElement().getStyle().setMarginLeft(3, Unit.PX);
//		bCancel.setStyleName("wizardButton");
		bCancel.getElement().getStyle().setMarginLeft(3, Unit.PX);
//		bSave.setStyleName("wizardButton");
		bSave.getElement().getStyle().setMarginLeft(3, Unit.PX);

		hp.add(bSearch);
		hp.add(bSave);
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		bSearch.addStyleName("button_toolbar");
		bSave.addStyleName("button_toolbar");
		cbViewScope.setStyleAttribute("margin-left", "132px");
		cbViewScope.setStyleAttribute("margin-top", "2px");

		hpMain.setId("hpSearchAndFilter");
//		hpMain.setStyleAttribute("background", "#D0DEF0");
		verticalPanel.setId("VerticalPanelSearchAndFilter");
		verticalPanel.setStyleAttribute("background", "#D0DEF0");
		hpMain.add(hp);
		hpMain.setVerticalAlign(VerticalAlignment.MIDDLE);

		//SOLUTION FOR SCOPE INVISIBLE
		cbViewScope.setVisible(false);
		verticalPanel.add(this.toolbarPahtPanel.getToolBarPathPanel());
		verticalPanel.add(hpMain);
		verticalPanel.setVerticalAlign(VerticalAlignment.MIDDLE);

		this.addListeners();

		add(verticalPanel);

		updateSize();
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});

		layout();
	}

	/**
	 * Update window size.
	 */
	public void updateSize() {
		RootPanel workspace = RootPanel.get(ConstantsPortlet.WORKSPACEDIV);
		int leftBorder = workspace.getAbsoluteLeft();
		int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;

		//SOLUTION FOR SCOPE INVISIBLE
		hp.setWidth(rootWidth);

//		ORIGINAL CODE
//		hp.setWidth(rootWidth - 500);
	}

	/**
	 * Search text.
	 *
	 * @param value the value
	 */
	public void searchText(String value){
		textSearch.setValue(value);
		seVisibleButtonsCancelSave(true);
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
//					seVisibleButtonsCancelSave(true);
//					AppController.getEventBus().fireEvent(new SearchTextEvent(textSearch.getValue()));
					searchText(textSearch.getValue());
				}else if(textSearch.getValue()==null || textSearch.getValue().isEmpty()){
					searchCancel();
				}
			}
		});

		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() == 13) { // KEY_ENTER
					if(isValidSearch()){
						searchText(textSearch.getValue());
					}else if(textSearch.getValue()==null || textSearch.getValue().isEmpty()){
						searchCancel();
					}
				}
			}
		};

		textSearch.addKeyListener(keyListener);
//

		bSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(textSearch.getValue()!=null && !textSearch.getValue().isEmpty() && textSearch.isValid()){
					if (textSearch.getValue() != null && textSearch.getValue().length()>0) {
						String parentId = toolbarPahtPanel.getLastParent()!=null?toolbarPahtPanel.getLastParent().getIdentifier():null;
						AppController.getEventBus().fireEvent(new SaveSmartFolderEvent(null, textSearch.getValue(), parentId));
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
		this.textSearch.reset();
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
		return textSearch.getValue()!=null && !textSearch.getValue().isEmpty() && textSearch.isValid();
	}

	/**
	 * Se visible buttons cancel save.
	 *
	 * @param flag the flag
	 */
	public void seVisibleButtonsCancelSave(boolean flag){

		this.bCancel.setVisible(flag);
		this.bSave.setVisible(flag);
	}

	/**
	 * Search cancel.
	 */
	public void searchCancel(){
		resetFields();
		seVisibleButtonsCancelSave(false);
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
	 * Sets the visible button save.
	 *
	 * @param bool the new visible button save
	 */
	public void setVisibleButtonSave(boolean bool){
		this.bSave.setVisible(bool);
	}

	/**
	 * Sets the empty text.
	 *
	 * @param emptyText the new empty text
	 */
	public void setEmptyText(String emptyText){
		textSearch.setEmptyText(emptyText);
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