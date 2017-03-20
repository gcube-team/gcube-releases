package org.gcube.portlets.admin.vredeployer.client.view.panels.builders;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.control.Controller;
import org.gcube.portlets.admin.vredeployer.shared.GHNBean;
import org.gcube.portlets.admin.vredeployer.shared.GHNProfile;
import org.gcube.portlets.admin.vredeployer.shared.RunningInstance;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
/**
 * 
 * @author massi
 *
 */
public class GhnGrid {

	private List<GHNProfile> myGHNList;
	private Controller controller;

	public GhnGrid(Controller controller, List<GHNProfile> list) {
		myGHNList = list;	
		this.controller = controller;
	}

	public List<GHNProfile> getGHNList() {
		return myGHNList;
	}

	public ContentPanel getGrid() {
		List<GHNBean> toShow = new ArrayList<GHNBean>();

		for (GHNProfile p : myGHNList) {
			toShow.add(new GHNBean(p.getId(), p.getHost(), p.getMemory().getVirtualAvailable(),
					p.getSite().getDomain(), p.isSelected(), p.getMemory().getLocalAvailableSpace(),
					p.getSite().getLocation(), p.getSite().getCountry(), p.isSecure()));
		}


		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  		

		column.setId("id");  
		column.setHeader("ID");  
		column.setWidth(50);  
		column.setHidden(true);
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("host");  
		column.setHeader("Host name");  
		column.setWidth(150);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("domain");  
		column.setHeader("Domain"); 
		column.setHidden(true);
		column.setWidth(150);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("memory");  
		column.setHeader("mem avail."); 
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(40);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("diskspace");  
		column.setHeader("disk avail."); 
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setWidth(50);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("country");  
		column.setHeader("Country"); 
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(true);
		column.setWidth(50);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("location");  
		column.setHeader("Location"); 
		column.setHidden(true);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(50);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("isSecure");  
		column.setHeader("isSecure"); 
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(true);
		column.setWidth(40);  
		configs.add(column); 

		CheckColumnConfig checkColumn = new CheckColumnConfig("isSelected", "isSelected", 35) {
			@Override
			protected void onMouseDown(final GridEvent<ModelData> ge) {
				super.onMouseDown(ge);
				GHNBean bean = (GHNBean) ge.getModel();
				showGHNAdditionalInfo(bean.getId(), myGHNList);
			}	
		};
		checkColumn.setHeader("Select");  
		checkColumn.setAlignment(HorizontalAlignment.CENTER);

		CheckBox checkbox = new CheckBox();

		CellEditor checkBoxEditor = new CellEditor(checkbox);

		checkColumn.setEditor(checkBoxEditor);
		configs.add(checkColumn);  

		final ColumnModel cm = new ColumnModel(configs); 

		/**
		 * load the grid data
		 */
		final ListStore<GHNBean> store = new ListStore<GHNBean>();
		//store.groupBy("domain");
		store.sort("isSelectable", SortDir.DESC);
		store.add(toShow);  

		Grid<GHNBean> grid = new Grid<GHNBean>(store, cm);

		grid.addPlugin(checkColumn);
		grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("host"); 
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 	


		gridPanel.addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				store.rejectChanges();  
			}  
		}));  



		gridPanel.addButton(new Button("Commit changes", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				List<GHNBean> ghns = store.getModels();

				int selectedCounter = 0;
				for (GHNBean ghn : ghns) 
					if (ghn.isSelected())	
						selectedCounter++;
				
				if (selectedCounter < 1)
					MessageBox.alert("Alert", "At least one node must be added for deploying ", null);  
				else {					
					GHNBean[] selectedGHNIds = new GHNBean[selectedCounter];
					//get the selected ghnID
					int j = 0;
					for (int i = 0; i < ghns.size(); i++) 
						if (ghns.get(i).isSelected()) {
							selectedGHNIds[j] = ghns.get(i);;
							j++;
						}

					GWT.log("Storing GHN List");
					store.commitChanges();
					controller.setGHNsSelected(selectedGHNIds);
				}

			}  
		}));  
		return gridPanel;
	}

	/**
	 * 
	 * @param ghnID
	 * @param ghnodes
	 */
	private void showGHNAdditionalInfo(String ghnID, List<GHNProfile> ghnodes) {
		for (GHNProfile ghn : ghnodes) {
			if (ghn.getId().compareTo(ghnID) == 0) {
				ContentPanel cp = new ContentPanel();
				cp.setHeaderVisible(false);
				cp.setBodyBorder(false);
				String toAdd = "<h3>" + ghn.getHost() + "</h3>";

				toAdd += "<br /><b>Site:</b>";
				toAdd += "<ul>";
				toAdd += "<li>&nbsp; - &nbsp; <b>Location:</b> " +  ghn.getSite().getLocation() + "</li>" ;
				toAdd += "<li>&nbsp; - &nbsp; <b>Country:</b> " +  ghn.getSite().getCountry() + "</li>" ;
				toAdd += "<li>&nbsp; - &nbsp; <b>Domain:</b> " +  ghn.getSite().getDomain() + "</li>" ;

				toAdd += "</ul>";

				toAdd += "<br /><b>Running Instances:</b>";

				List<RunningInstance> ris = ghn.getRunningInstances();

				if (ris != null) {	
					toAdd += "<ul>";
					for (RunningInstance ri : ris) {
						toAdd += "<li>&nbsp; - &nbsp; <b>"+ ri.getName()+ "</b> (" +  ri.getServiceClass() + ")</li>" ;
					}		
					if (ris.size() == 0)
						toAdd += "<li>&nbsp; - &nbsp; none</li>" ;
					toAdd += "</ul>";
				}


				cp.add(new HTML(toAdd, true ));	
				cp.setStyleAttribute("margin", "10px");
				cp.setLayout(new FitLayout());
				controller.setEastPanelContent(cp);
				break;
			}

		}


	}
}

