package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;



import java.util.ArrayList;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class BoundsFilter extends ColumnContainer implements AdvancedSearchPanelInterface {

	private NumberField upperBoundLatitudeField;
	private NumberField upperBoundLongitudeField;
	private NumberField lowerBoundLatitudeField;
	private NumberField lowerBoundLongitudeField;
	private ArrayList<DataSourceModel> availablePlugin;
	
	private ContentPanel boundsPanel = new ContentPanel();
	
	private Button btnResetAllFilters = new Button("Reset Filters");
	private TextArea txtAreaInfo = new TextArea();

	
	public BoundsFilter() {
		initBoundsPanel();
		btnResetAllFilters.setStyleName("button-hyperlink");
		availablePlugin = new ArrayList<DataSourceModel>();
	}
	
	@Override
	public ContentPanel getPanel() {
		return boundsPanel;
	}

	private void initBoundsPanel() {
		
		boundsPanel.setHeaderVisible(false);
		boundsPanel.setBodyBorder(false);

		upperBoundLatitudeField = new NumberField();
		upperBoundLatitudeField.setPropertyEditorType(Float.class);
		upperBoundLatitudeField.setEmptyText("Latitude");

		upperBoundLongitudeField = new NumberField();
		upperBoundLongitudeField.setPropertyEditorType(Float.class);
		upperBoundLongitudeField.setEmptyText("Longitude");

		MultiField<Float> uppertBound = new MultiField<Float>("Upper Bound",upperBoundLatitudeField, upperBoundLongitudeField);uppertBound.setSpacing(5);
		left.add(uppertBound);

		lowerBoundLatitudeField = new NumberField();
		lowerBoundLatitudeField.setPropertyEditorType(Float.class);
		lowerBoundLatitudeField.setEmptyText("Latitude");

		lowerBoundLongitudeField = new NumberField();
		lowerBoundLongitudeField.setPropertyEditorType(Float.class);
		lowerBoundLongitudeField.setEmptyText("Longitude");

		MultiField<Float> lowerBound = new MultiField<Float>("Lower Bound",lowerBoundLatitudeField, lowerBoundLongitudeField);
		lowerBound.setSpacing(5);

		left.add(lowerBound);
		left.add(uppertBound);

//		// Horizontal alignment right
//		LayoutContainer containerRightAlign = new LayoutContainer();
//		HBoxLayout layout2 = new HBoxLayout();
//		layout2.setPadding(new Padding(5));
//		layout2.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
//		layout2.setPack(BoxLayoutPack.END);
//		containerRightAlign.setLayout(layout2);
//
//		HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
//		containerRightAlign.add(btnResetAllFilters, layoutData);
		
		
		btnResetAllFilters.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				resetAdvancedFields();
				
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
	    txtAreaInfo.setValue(ConstantsSpeciesDiscovery.AVAILABLEFILTERBOUND + ": \n"); 

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
//		right.add(cp);

		boundsPanel.add(columnContainer);
		

	}

	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.BOUNDS.getLabel();
	}
	
	
	public void resetAdvancedFields() {
		upperBoundLatitudeField.reset();
		upperBoundLongitudeField.reset();
		lowerBoundLatitudeField.reset();
		lowerBoundLongitudeField.reset();
	}

	public NumberField getUpperBoundLatitudeField() {
		return upperBoundLatitudeField;
	}

	public NumberField getUpperBoundLongitudeField() {
		return upperBoundLongitudeField;
	}

	public NumberField getLowerBoundLatitudeField() {
		return lowerBoundLatitudeField;
	}

	public NumberField getLowerBoundLongitudeField() {
		return lowerBoundLongitudeField;
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
