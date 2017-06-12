/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateSelectedEvent;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class TemplateFormSwitcherPanel extends LayoutContainer implements TemplateSwitcherInteface{

	protected SimpleComboBox<String> comboTemplateType = new SimpleComboBox<String>();
	protected SimpleComboBox<String> comboOnErrors = new SimpleComboBox<String>();
	protected TextField<String> numberOfColumns = new TextField<String>();
	protected TextField<String> name = new TextField<String>();
	protected TextArea description = new TextArea();
	protected TextField<String> agency = new TextField<String>();
	
	private Integer numberOfColumnsIntValue;
	private FormData formData = new FormData("-20");  
	protected Button buttonCreateTemplate = new Button("Create Template");
	private Button buttonCancel = new Button("Cancel");
	
	private HorizontalPanel hp;
	private FormPanel formPanel = new FormPanel();
	private TemplateFormSwitcherPanel instance = this;
	private Html htmlHelper = new Html();
	private ContentPanel helperCP = new ContentPanel();
	
	private Long serverId = null;

//	private List<TdTTemplateType> templates;
	private Map<String, TdTTemplateType> hashTemplate;
//	private List<String> onErrors;
	private TdTemplateController templateController;
	
	  @Override  
	  protected void onRender(Element parent, int index) {  
	    super.onRender(parent, index);  
//	    formData = new FormData("-10");  
	    hp = new HorizontalPanel();
//	    vp.setLayout(new FitLayout());
		int width = TdTemplateConstants.WINDOW_WIDTH-20;
		int height = TdTemplateConstants.WINDOW_HEIGHT-40;
		hp.setSize(width, height);
		formPanel.setWidth(390);
		helperCP.setLayout(new FitLayout());
		helperCP.setHeaderVisible(false);
		helperCP.setBorders(false);
		helperCP.setBodyBorder(false);
//		helperCP.setScrollMode(Scroll.AUTOY);
		helperCP.setScrollMode(Scroll.AUTO);
		helperCP.setWidth(385);
		helperCP.setHeight(TdTemplateConstants.WINDOW_HEIGHT-50);
		

//		htmlHelper.setHtml(TdTemplateAbstractResources.INSTANCE.helptemplate().getText());
	    
	    
	    TdTemplateController.tdTemplateServiceAsync.getTemplateHelper(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				 htmlHelper.setHtml(result);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				 htmlHelper.setHtml("Sorry :-(, <br/> an error occurred on recovering <br/><b>Template Helper</b>");
				
			}
		});
	    
	    htmlHelper.setStyleAttribute("margin-left", "4px");
		helperCP.add(htmlHelper);
	
		formPanel.addStyleName("custommarginleft");
		hp.add(helperCP);
		hp.add(formPanel);
		
	    hp.setStyleAttribute("padding", "10px");
	    add(hp);  
	  }  
	  
	  
	
	public TemplateFormSwitcherPanel(TdTemplateController controller) {
		this.templateController = controller;
		
		formPanel.setHeaderVisible(false);
	    formPanel.setFrame(false);  
//	    formPanel.setWidth(350); 

	    initComboTemplates(false);
	    initComboOnErrors(false);
		initListeners();
		
		formPanel.add(comboTemplateType, formData);
		
		name.setFieldLabel("Name");
		name.setAllowBlank(false);
		formPanel.add(name, formData);
		
		agency.setFieldLabel("Agency");
		agency.setAllowBlank(true);
		formPanel.add(agency,formData);
		
		description.setFieldLabel("Description");
		description.setAllowBlank(true);
		formPanel.add(description,formData);
		
		formPanel.add(comboOnErrors, formData);

	    numberOfColumns.setFieldLabel("Number of Columns");  
	    numberOfColumns.setAllowBlank(false); 
	    numberOfColumns.setEmptyText("maximum is "+TdTemplateConstants.MAXCOLUMNS);
//	    numberOfColumns.getFocusSupport().setPreviousId(simple.getButtonBar().getId());  
	    formPanel.add(numberOfColumns, formData);  

	    formPanel.add(buttonCreateTemplate);
	    formPanel.setButtonAlign(HorizontalAlignment.CENTER);
	    
	    FormButtonBinding binding = new FormButtonBinding(formPanel);  
	    binding.addButton(buttonCreateTemplate);  
	}

	/**
	 * 
	 */
	private void initComboTemplates(boolean enabled) {
		
		comboTemplateType.setFieldLabel("Template type");
		comboTemplateType.setEditable(false);
		comboTemplateType.setTriggerAction(TriggerAction.ALL);
		comboTemplateType.setEmptyText("Choose template type");
		comboTemplateType.setAllowBlank(false);
		comboTemplateType.setEnabled(enabled);
	}
	
	
	private void initComboOnErrors(boolean enabled) {
		
		comboOnErrors.setFieldLabel("On Error");
		comboOnErrors.setEditable(false);
		comboOnErrors.setTriggerAction(TriggerAction.ALL);
		comboOnErrors.setEmptyText("Choose action");
		comboOnErrors.setAllowBlank(false);
		comboOnErrors.setEnabled(enabled);
	}



	protected void initListeners() {

		buttonCreateTemplate.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				try{
					if(isValidForm()){
						GWT.log("Fire event TemplateSelectedEvent in TemplateFormSwitcherPanel");
						templateController.getInternalBus().fireEvent(new TemplateSelectedEvent(instance));
					}
				}catch (Exception e) {
					e.printStackTrace();
				}	
			}
		});
	}
	
	@Override
	public void setTemplates(List<TdTTemplateType> result){

		if(result!=null){
			hashTemplate = new HashMap<String, TdTTemplateType>();
			for (TdTTemplateType tdTTemplateType : result) {
				hashTemplate.put(tdTTemplateType.getName(), tdTTemplateType);
			}
		
			List<String> ls = new ArrayList<String>();
	
			for (TdTTemplateType template : result) ls.add(template.getId());
	
			comboTemplateType.add(ls);
			comboTemplateType.setEnabled(true);
			layout();
		}
	}
	
	@Override
	public void setOnErrors(List<String> onErrors){
		
		if(onErrors!=null && onErrors.size()>0){
			
			comboOnErrors.add(onErrors);
			comboOnErrors.setEnabled(true);
		}
		
		layout();
	}

	public boolean isValidForm(){
		
		if(name.isValid() && comboTemplateType.isValid() && numberOfColumns.isValid() && comboOnErrors.isValid()){
			
			try{
				numberOfColumnsIntValue = Integer.valueOf(numberOfColumns.getValue());
				if(numberOfColumnsIntValue<=0)
					throw new Exception("The field 'Number of Columns' is less than one");
				else if(numberOfColumnsIntValue>TdTemplateConstants.MAXCOLUMNS)
					throw new Exception("The field 'Number of Columns' is greater than maximum of "+TdTemplateConstants.MAXCOLUMNS);
				
				if(comboTemplateType.getSelection().size()==0)
					throw new Exception("The field 'Template Type' must be selected");
				
				if(comboOnErrors.getSelection().size()==0)
					throw new Exception("The field 'On Error' must be selected");
				
			}catch (NumberFormatException e) {
				MessageBox.alert("Error", "The field 'Number of Columns' is not an integer", null).show();
				return false;
			}catch (Exception e) {
				MessageBox.alert("Error", e.getMessage(), null).show();
				return false;
			}
			
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public String getType(){
		
		if(comboTemplateType.getSelection().size()>0)
			return comboTemplateType.getSelection().get(0).getValue();
		
		return null;
	}
	
	@Override
	public TdTTemplateType getTdTTemplateType(){
		
		if(hashTemplate!=null){
			return hashTemplate.get(getType());
		}
		
		return null;
	}
	
	@Override
	public String getName(){
		return name.getValue();
	}
	
	@Override
	public String getDescription(){
		return description.getValue();
	}
	
	@Override
	public int getNumberOfColumns(){
		return numberOfColumnsIntValue;
	}

	@Override
	public String getAgency() {
		return agency.getValue();
	}
	
	@Override
	public String getOnError(){
		
		if(comboOnErrors.getSelection().size()>0)
			return comboOnErrors.getSelection().get(0).getValue();
		
		return null;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.TemplateSwitcherInteface#getServerId()
	 */
	@Override
	public Long getServerId() {
		return serverId;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.TemplateSwitcherInteface#setServerId(java.lang.Long)
	 */
	@Override
	public void setServerId(Long id) {
		this.serverId = id;
	}
}
