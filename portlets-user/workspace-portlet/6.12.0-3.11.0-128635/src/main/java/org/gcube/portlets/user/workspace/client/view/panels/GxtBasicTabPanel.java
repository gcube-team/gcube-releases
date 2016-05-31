package org.gcube.portlets.user.workspace.client.view.panels;

import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.view.GxtComboBox;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.user.client.Window;

/**
 * This class is not used
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtBasicTabPanel extends LayoutContainer {

	//Tab View
	private TabItem tabView = null;
	private TabItem tabSearch = null;
	private TabPanel tabPanel = null;
	private Text txtPath = new Text("PATH:");  
	private Label labelPath = new Label("");
	private GxtComboBox comboBoxGxt = null;
	private ComboBox<ScopeModel> cbViewScope = null;
//	private Text txtSwitch = new Text("Explorer View:");
	private Text txtViewScope = new Text("View Scope:");
	public enum viewSwitchType {TREE,SHORTCUT};
	private final String EMPTY = "EMPTY";
	
	//Tab Search
	private final TextField<String> textSearch = new TextField<String>();
	private Button bSearch = new Button("Search");
	private Button bCancel = new Button("Cancel");
	private TextField<String> textFull = new TextField<String>();
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private final String FIELDVALIDATEERROR = "The field must be alphanumeric";
	private SimpleComboBox<String> cbNameFilter = null;
//	TableData tdContentView = null;
	
	public GxtBasicTabPanel() {

//		Log.trace("Initializing GxtBasicTabPanel");
		
		setLayout(new FitLayout());
		setBorders(true);
		setId("ContentPanelTab");
		
		this.tabPanel = new TabPanel();
		this.tabView = new TabItem("View");
		this.tabSearch = new TabItem("Search");
		this.comboBoxGxt = new GxtComboBox();
		this.cbViewScope = this.comboBoxGxt.getComboViewScope();
		this.cbNameFilter = this.comboBoxGxt.getComboStringFilter();
		this.initTabView();
		this.initTabSearch();
	}

	private void initTabView() {

		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new RowLayout());
		cp.setBorders(false);
		cp.setBodyBorder(false);
//		cp.setStyleAttribute("margin-top", "20px");

		tabPanel.setBorders(false);
		tabPanel.setBodyBorder(false);
		tabPanel.setStyleAttribute("margin-top", "10px");
		tabPanel.setTabMargin(10);
		tabPanel.setTabWidth(250);
		tabPanel.setBorderStyle(false);
		tabPanel.setPlain(true);

        VBoxLayout vBoxLayout = new VBoxLayout();  
        vBoxLayout.setPadding(new Padding(5));  
        vBoxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.LEFT);   
        VBoxLayoutData flex = new VBoxLayoutData(new Margins(30, 10, 10, 10));  
	
        txtPath.addStyleName("pad-text");  
        txtPath.setStyleAttribute("backgroundColor", "white");  
        txtPath.setAutoWidth(true);
       
//        Radio radioTree = new Radio();  
//        radioTree.setBoxLabel("Tree");  
//        radioTree.setValue(true);
//        radioTree.setValueAttribute(viewSwitchType.TREE.toString());
//        Radio radioShortcut = new Radio();  
//        radioShortcut.setBoxLabel("Shortcut");
//        radioShortcut.setValueAttribute(viewSwitchType.SHORTCUT.toString());
//        RadioGroup radioGroup = new RadioGroup();  
//        radioGroup.setFieldLabel("Afecto");  
//        radioGroup.add(radioTree);  
//        radioGroup.add(radioShortcut);
//        radioGroup.setStyleAttribute("margin-left", "20px");
        
        HorizontalPanel hpPath = new HorizontalPanel();
        hpPath.setLayout(new FitLayout());
        hpPath.setBorders(true);
        hpPath.add(txtPath);
        this.labelPath.setEnabled(false);
        this.setLabelPath("");
        this.labelPath.setStyleAttribute("margin-left", "20px");
        hpPath.add(this.labelPath);
        
        HorizontalPanel hpScope = new HorizontalPanel();
        hpScope.setLayout(new FitLayout());
        hpScope.add(txtViewScope);
        
        //Select in combo the first element of store  
        this.cbViewScope.addListener(Events.Attach, new Listener<BaseEvent>(){
            public void handleEvent(BaseEvent be) {
            	cbViewScope.setValue(cbViewScope.getStore().getAt(0)); 
            }
        });
        
        this.cbViewScope.addSelectionChangedListener(new SelectionChangedListener<ScopeModel>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ScopeModel> se) {
						
//				Window.alert("selected scope" + se.getSelectedItem().get("name")); 
				
				//TODO
				
			}
		});
        
        this.cbViewScope.setStyleAttribute("margin-left", "20px");
        hpScope.add(this.cbViewScope);
        
//        HorizontalPanel hpRadio = new HorizontalPanel();
//        hpRadio.setLayout(new FitLayout());
//        hpRadio.add(txtSwitch);
//        hpRadio.add(radioGroup);
        
	    cp.add(hpPath,flex);
        cp.add(hpScope,flex);
//        cp.add(hpRadio, flex);

		this.tabView.add(cp);
		
		tabPanel.add(tabView);
		add(tabPanel);
	}
	
	
	private void initTabSearch() {

		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new RowLayout());
		cp.setBorders(false);
		cp.setBodyBorder(false);
		
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		
//		formPanel.setFrame(true);
		formPanel.setWidth(350);

		FormData formData = new FormData("-20"); 


		cbNameFilter.setFieldLabel("Name Filter");  
//		combo.setDisplayField("name");  
		formPanel.add(cbNameFilter, formData);
		
		textSearch.setFieldLabel("Item Name");
		textSearch.setAllowBlank(false);
		textSearch.setRegex("^[ a-zA-Z0-9_-]*$");
//		textSearch.getMessages().setRegexText(FIELDVALIDATEERROR);
		formPanel.add(textSearch, formData);
		
		fromDate.setName("date");
		fromDate.setFieldLabel("From");
	    formPanel.add(fromDate);

		toDate.setName("date");
		toDate.setFieldLabel("To");
		formPanel.add(toDate);
	    
		textFull.setFieldLabel("Full Text");
		textFull.setAllowBlank(true);
		formPanel.add(textFull, formData);
		
		this.tabSearch.add(formPanel);
		
		bSearch.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (textSearch.getValue() == null || textSearch.getValue() == "" || !textSearch.isValid()) {
					// textSearch.fireEvent(Events.ValidateEdit);
					textSearch.markInvalid(FIELDVALIDATEERROR);
					// textSearch.forceInvalid("The field must be alphanumeric");

				} else {

					Window.alert("submit"); //TODO aysnc ws
//					HistoryExample.addItem("search");
				}
			}
		});
		
		
		this.bCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				resetFieldsInTabSearch();
				
			}
		});
	  
	    formPanel.setButtonAlign(HorizontalAlignment.LEFT);  
	  
	    formPanel.addButton(this.bSearch); 
	    formPanel.addButton(this.bCancel);
 
	    
	    FormButtonBinding binding = new FormButtonBinding(formPanel);  
	    binding.addButton(this.bSearch);
		
		tabPanel.add(tabSearch);
		add(tabPanel);

	}

	
	public void resetFieldsInTabSearch(){
		this.textSearch.reset();
		this.textFull.reset();
		this.fromDate.reset();
		this.toDate.reset();
		this.cbNameFilter.reset();

	}
	
	public LayoutContainer getTabs() {

		return this;
	}
	
	public String getValueComboNameFilter(){
		
		return this.cbViewScope.getRawValue();
		
	}
	
	public void setLabelPath(String path){
		
		if(path != null && (!path.isEmpty()))
			this.labelPath.setText(path);
		else
			this.labelPath.setText(EMPTY);
		
		
	}
	
}