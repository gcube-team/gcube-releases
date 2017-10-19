package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DateFilter extends ColumnContainer implements AdvancedSearchPanelInterface {
	
	private DateField fromDate;
	private DateField toDate;
	private Button btnResetAllFilters = new Button("Reset Filters");
	private ContentPanel dateFilterPanel = new ContentPanel();
	private ArrayList<DataSourceModel> availablePlugin;
	private TextArea txtAreaInfo = new TextArea();

	@Override
	public ContentPanel getPanel() {
		return dateFilterPanel;
	}

	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.DATE.getLabel();
	}
	
	public DateFilter(){
		initDateFilter();
		btnResetAllFilters.setStyleName("button-hyperlink");
		availablePlugin = new ArrayList<DataSourceModel>();
	}

	private void initDateFilter() {
		
		dateFilterPanel.setHeaderVisible(false);
		dateFilterPanel.setBodyBorder(false);
		
		fromDate = new DateField();
		fromDate.setStyleAttribute("marginRight", "15px");
		fromDate.setEmptyText("From");

		toDate = new DateField();
		toDate.setEmptyText("To");

		MultiField<Float> dateField = new MultiField<Float>("Date bounds", fromDate, toDate);
		dateField.setSpacing(5);
		
		
		left.add(dateField);
//		right.add(btnResetAllFilters);
		
		
		btnResetAllFilters.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				resetAdvancedFields();
				
			}
		});
		
		
		dateFilterPanel.add(columnContainer);
		
		fromDate.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {

				if(fromDate.isValid())
					toDate.setMinValue(fromDate.getValue());
				else{
//					toDate.reset();
					toDate.getDatePicker().clearState();
				}
			}
        });
		
		toDate.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {

				if(toDate.isValid()){
					fromDate.setMaxValue(toDate.getValue());
				}
				else{
//					fromDate.reset();
					fromDate.getDatePicker().clearState();
				}
			}
        });
		
	    final ContentPanel cp = new ContentPanel();  
	    ButtonBar buttonBar = new ButtonBar();
	    
	    ToggleButton toggleInfo = new ToggleButton("");
	    toggleInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {  
	        public void componentSelected(ButtonEvent ce) {  
	        	
	          if (cp.isVisible()) {  
//	            cp.el().slideOut(Direction.LEFT, FxConfig.NONE);  
	            cp.setVisible(false);
	          } else { 
	        	 cp.setVisible(true);
//	        	  cp.el().slideIn(Direction.RIGHT, FxConfig.NONE);
	           
	          }  
//	        	 cp.setVisible(true);
//	        	 cp.el().fadeToggle(FxConfig.NONE);  
	        }  
	      });  
	    
	    toggleInfo.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getInfoIcon()));
	    
	    toggleInfo.toggle(false);
		
//	    buttonBar.add(btnResetAllFilters);
	    
	    buttonBar.add(toggleInfo);
	    
	    cp.setVisible(false);
	    cp.setHeading("Bounds");  
	    cp.setBodyBorder(false);
	    
	    txtAreaInfo.setReadOnly(true);
	    txtAreaInfo.setValue(ConstantsSpeciesDiscovery.AVAILABLEFILTERDATE + ": \n"); 

	    cp.setLayout(new FitLayout());
	    	
	    cp.add(txtAreaInfo);  
	    cp.setWidth(300);
	    cp.setHeight(50);
	    cp.setScrollMode(Scroll.AUTOY);
	    cp.setHeaderVisible(false);
	    
	    
	    HorizontalPanel hp = new HorizontalPanel();
	    cp.setWidth(310);
//	    hp.setSpacing(5);
	    
	    cp.setStyleAttribute("margin-left", "5px");
	    cp.setStyleAttribute("margin-top", "2px");
	    
	    toggleInfo.setStyleAttribute("margin-left", "5px");
	    toggleInfo.setStyleAttribute("margin-top", "2px");
	    
	    hp.add(btnResetAllFilters);
	    hp.add(cp);
	    hp.add(toggleInfo);

		right.add(hp);

	}
	
	public void resetAdvancedFields() {
		fromDate.getDatePicker().clearState();
		toDate.getDatePicker().clearState();
		fromDate.reset();
		toDate.reset();
		
	}

	public DateField getFromDate() {
		return fromDate;
	}

	public DateField getToDate() {
		return toDate;
	}

	public void addAvailablePlugInfo(DataSourceModel plugin, SpeciesCapability capability){
		
		availablePlugin.add(plugin);
		String currentValue = txtAreaInfo.getValue();
		txtAreaInfo.setValue(currentValue + plugin.getName() + " (" + capability.getName() + "); \n");
	}

	@Override
	public ArrayList<DataSourceModel> getAvailablePlugIn() {
		return availablePlugin;
	}

}
