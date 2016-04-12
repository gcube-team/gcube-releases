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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 May 14th 2012
 */
public class GxtSeachAndFilterPanel extends LayoutContainer {

	private GxtComboBox comboBoxGxt = null;
	private ComboBox<ScopeModel> cbViewScope = null;
	private final TextField<String> textSearch = new TextField<String>();
	private Button bSearch = new Button(ConstantsPortlet.SEARCH);
//	private Image bSearchIn = Resources.getIconSearchWs().createImage();
//	private HorizontalPanel hpSearchIn = new HorizontalPanel();
//	private String searchInFolderId = ""; //is the root
	private Button bCancel = new Button(ConstantsPortlet.CANCEL);
	private Button bSave = new Button(ConstantsPortlet.SAVE);
//	private Label labelSearchIn = new Label();
	private TextField<String> textFull = new TextField<String>();
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private SimpleComboBox<String> cbNameFilter = null;
//	private HorizontalPanel hpToolbarPathPanel = null;
	private boolean isSearchActive = false;
	private VerticalPanel verticalPanel = new VerticalPanel();

	private HorizontalPanel hp = new HorizontalPanel();
//	private HorizontalPanel hp2 = new HorizontalPanel();
	
	private HorizontalPanel hpMain = new HorizontalPanel();
	private GxtBreadcrumbPathPanel toolbarPahtPanel;

	public GxtSeachAndFilterPanel(GxtBreadcrumbPathPanel toolBarPathPanel) {

		setLayout(new FitLayout());
		setBorders(true);
		this.setId("SearchAndFilter");
		this.setBorders(false);
//		this.setStyleAttribute("background-color", "#d0def0");
		this.comboBoxGxt = new GxtComboBox();
		this.cbViewScope = this.comboBoxGxt.getComboViewScope();
		this.cbNameFilter = this.comboBoxGxt.getComboStringFilter();
		this.textSearch.setId("text-search");
//		this.textSearch.setStyleAttribute("margin-bottom", "0px");
		this.cbViewScope.setWidth(360);
//		this.cbViewScope.setAutoWidth(true);
		this.toolbarPahtPanel = toolBarPathPanel;
		
		hp.setStyleAttribute("margin-left", "2px");
		seVisibleButtonsCancelSave(false);

		textSearch.setAllowBlank(true);
		textSearch.setEmptyText(ConstantsPortlet.SEARCHBYNAME);
//		textSearch.setWidth(325);
//		textSearch.setHeight(12);

		/* COMMENTED TO FIX https://support.social.isti.cnr.it/ticket/87
		textSearch.setRegex("^[a-zA-Z0-9]+[ a-zA-Z0-9_().-]*"); //alphanumeric
		textSearch.getMessages().setRegexText(ConstantsExplorer.MESSAGE_SEARCH_FORCE_APHANUMERIC);
		textSearch.setAutoValidate(true);
		*/
		hp.add(textSearch);
		
		bSearch.setStyleName("wizardButton");
//		bSearch.getElement().getStyle().setMarginLeft(3, Unit.PX);
		bCancel.setStyleName("wizardButton");
		bCancel.getElement().getStyle().setMarginLeft(3, Unit.PX);
		bSave.setStyleName("wizardButton");
		bSave.getElement().getStyle().setMarginLeft(3, Unit.PX);
		
		hp.add(bSearch);
		hp.add(bSave);
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		
//		hp.setHeight(20);
//		hpMain.setHeight(20);
		bSearch.setWidth("70px");
		bSave.setWidth("70px");
		
		cbViewScope.setStyleAttribute("margin-left", "132px");
		cbViewScope.setStyleAttribute("margin-top", "2px");
		
		hpMain.setId("hpMain");
		hpMain.setStyleAttribute("background", "#D0DEF0");
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
	/*
	private void initSearchIn(){
		
		hpSearchIn.removeAll();
		searchInFolderId = "";
		labelSearchIn.setText("");
//		labelSearchIn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hpSearchIn.add(labelSearchIn);
		hpSearchIn.setStyleAttribute("margin-left", "3px");
		hpSearchIn.add(bSearchIn);
		hpSearchIn.layout(true);
	}
	
	private void selectedFolderToSearch(Item item){
		searchInFolderId = item.getId();
		labelSearchIn.setText("in "+item.getName());
		labelSearchIn.setTitle("search in "+item.getName());
	}*/

	/**
	 * Update window size
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
	
	public void searchText(String value){
		textSearch.setValue(value);
		seVisibleButtonsCancelSave(true);
		String parentId = toolbarPahtPanel.getLastParent()!=null?toolbarPahtPanel.getLastParent().getIdentifier():null;
		AppController.getEventBus().fireEvent(new SearchTextEvent(value, parentId));
	}

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
//		bCancel.addClickHandler(new ClickHandler() {			
//			@Override
//			public void onClick(ClickEvent event) {
//				if(textSearch.getValue()!=null && !textSearch.getValue().isEmpty() && textSearch.isValid()){
//					searchCancel();
//					AppController.getEventBus().fireEvent(new SearchTextEvent(null));
//				}
//			}
//		});
		
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

	public void resetFields(){
		this.textSearch.reset();
		this.textFull.reset();
		this.fromDate.reset();
		this.toDate.reset();
		this.cbNameFilter.reset();

	}
	
	private boolean isValidSearch(){
		return textSearch.getValue()!=null && !textSearch.getValue().isEmpty() && textSearch.isValid();
	}

	public void seVisibleButtonsCancelSave(boolean flag){

		this.bCancel.setVisible(flag);
		this.bSave.setVisible(flag);
	}

	public void searchCancel(){
		resetFields();
//		initSearchIn();
		seVisibleButtonsCancelSave(false);
	}

	public boolean isSearchActive(){
		return this.isSearchActive;
	}

	public void setListScope(List<ScopeModel> listScope){

		this.comboBoxGxt.setListScope(listScope);
	}

	public void selectScopeByIndex(int index){

		this.cbViewScope.setValue(cbViewScope.getStore().getAt(index)); 
	}

	public void setSearchActive(boolean isSearchActive) {
		this.isSearchActive = isSearchActive;
	}

	public void setVisibleButtonSave(boolean bool){
		this.bSave.setVisible(bool);
	}

	public void setEmptyText(String emptyText){
		textSearch.setEmptyText(emptyText);
	}

	public GxtBreadcrumbPathPanel getToolbarPathPanel() {
		return toolbarPahtPanel;
	}

}