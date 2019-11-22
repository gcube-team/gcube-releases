package org.gcube.application.aquamaps.aquamapsspeciesview.client.search;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.event.dom.client.KeyCodes;

public class SpeciesSearchForm extends ContentPanel {

	
	public static SpeciesSearchForm instance;
	
	public static SpeciesSearchForm getInstance() {
		return instance;
	}
	
	//**** Form Fields

	final TextField<String> titleField = new TextField<String>(); 
	final Button search=new Button("Search >>");
	FormPanel basicForm = new FormPanel();
	public ActiveFiltersGrid advanced=new ActiveFiltersGrid();
	
	public SpeciesSearchForm() {
		instance=this;
		this.setFrame(true);
		this.setHeight(80);  
		this.setLayout(new VBoxLayout(VBoxLayoutAlign.STRETCH));
		this.setBorders(false);  
		this.setBodyBorder(false);
		this.setHeaderVisible(false);
		
		
		ComponentPlugin plugin = new ComponentPlugin() {  
			public void init(Component component) {  
				component.addListener(Events.Render, new Listener<ComponentEvent>() {  
					public void handleEvent(ComponentEvent be) {  
						El elem = be.getComponent().el().findParent(".x-form-element", 3);  
						// should style in external CSS  rather than directly  
//						elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));  
					}  
				});  
			}  
		}; 
		
		  
		basicForm.setBorders(false);  
		basicForm.setBodyBorder(false);  
		basicForm.setLabelWidth(75);  
		basicForm.setPadding(5);  
		basicForm.setHeaderVisible(false); 
		basicForm.setFrame(true);
		basicForm.setAutoHeight(true);
		
		titleField.setFieldLabel("Search for name(s)"); 
		titleField.setData("text", "Type what species you are looking for");
		titleField.setEmptyText("Type here for begin search within species names");
		
		
		titleField.addKeyListener(new KeyListener(){
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if(event.getKeyCode()==KeyCodes.KEY_ENTER)
					sendSearchRequest();
			}
		});
		
		
		search.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				sendSearchRequest();
			}
		});
		
//		theForm.add(titleField, new FormData("80%"));
//		
//		theForm.add(search);
		
		LayoutContainer firstRow = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		firstRow.setHeight(25);
		LabelField searchLabel=new LabelField("Search for name(s):");
		searchLabel.setAutoWidth(true);		
		firstRow.add(searchLabel, new RowData(-1, -1, new Margins(0, 1, 0, 1)));
		firstRow.add(titleField, new RowData(.95, -1));
		firstRow.add(search,new RowData(-1, -1));
		firstRow.setBorders(false);
		basicForm.add(firstRow, new FormData("100%"));
		
		VBoxLayoutData flex1=new VBoxLayoutData(new Margins(0));
//		flex1.setFlex(0);
		this.add(basicForm,flex1);
		
		
//		ContentPanel advanced=new ContentPanel(new RowLayout(Orientation.HORIZONTAL));
//		AdvancedFilterForm advancedForm=new AdvancedFilterForm();
//		advancedForm.setBorders(false);
//		advanced.add(advancedForm, new RowData(.5, 1, new Margins(0)));
//		advanced.add(new ActiveFiltersGrid(),new RowData(.5,1,new Margins(0)));
		
		
		advanced.setCollapsible(true);
		advanced.setExpanded(false);
		advanced.setFrame(true);
		advanced.setTitleCollapse(true);
		advanced.setHeight(120);
		advanced.setBodyBorder(false);
		advanced.setBorders(false);
		
		advanced.addListener(Events.BeforeCollapse, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				Log.debug("Before Collapse");
				AquaMapsSpeciesView.get().northData.setSize(80);
				AquaMapsSpeciesView.get().updateSize();
			};
		});
		advanced.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				Log.debug("Before Expand");
				AquaMapsSpeciesView.get().northData.setSize(250);
				AquaMapsSpeciesView.get().updateSize();
			};
		});
		
		VBoxLayoutData flex2=new VBoxLayoutData(new Margins(0));
		flex2.setFlex(1);
		
		this.add(advanced,flex2);
//		ToggleButton toggleAdvanced=new ToggleButton("Show Advanced Search Options..");
//		toggleAdvanced.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				if(((ToggleButton)ce.getButton()).isPressed()){
//					Log.debug("PRESSED, activating advanced..");
//					AquaMapsSpeciesView.get().northData.setSize(200);
//				}else{
//					Log.debug("DE-PRESSED, DE-activating advanced..");
//					AquaMapsSpeciesView.get().northData.setSize(80);
//				}
//				AquaMapsSpeciesView.get().updateSize();
//				
//			}
//		});
//		this.add(toggleAdvanced);
	}


	public void setSearchSettings(SettingsDescriptor result) {
		titleField.setValue(result.getSpeciesSearchDescriptor().getGenericSearchFieldValue());
		advanced.setFilters(result.getSpeciesSearchDescriptor().getAdvancedFilterList());
		advanced.setSelectedResource(result.getSelectedHspen());
	}
	
	public void sendSearchRequest(){
		AquaMapsSpeciesView.get().mainPanel.mask("Updating filters..");
		AquaMapsSpeciesView.localService.setGenericSearchFilter(titleField.getValue(), PortletCommon.refreshSpeciesCallback);
	}
	
}
