/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ResourceDetailDecorator.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.types.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo 
 */
public class ResourceDetailModel {
	private static boolean initialized = false;

	public static final String SERVICE_INSTALL_KEY = "toDeploy";

	private static HashMap<String, ColumnModel> RECORD_DEFINITION = null;
	private static HashMap<String, ModelType> XML_MAPPING = null;
	private static HashMap<String, String[]> REQUIRED_FIELDS = null;

	private static void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		RECORD_DEFINITION = new HashMap<String, ColumnModel>();
		XML_MAPPING = new HashMap<String, ModelType>();

		/*********************************************
		 * GHN
		 ********************************************/
		// The column model for grid representation
		List<ColumnConfig> modelColumns = new ArrayList<ColumnConfig>();

		modelColumns.add(new ColumnConfig("Name", "Name", 250));
		
		
		
		ColumnConfig status = new ColumnConfig("Status", "Status", 65);

		GridCellRenderer<BaseModelData> statusRender = new GridCellRenderer<BaseModelData>() {
			@Override
			public String render(BaseModelData model, String property,	ColumnData config, 
					int rowIndex, int colIndex,	ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				String statusToCheck = (String)model.get(property);  
				String style = "gray";
				if (statusToCheck.compareTo("certified") == 0)
					style = "green";
				else if (statusToCheck.compareTo("ready") == 0)
					style = "orange";
				return "<span style='color:" + style + "'>" + statusToCheck + "</span>"; 
			}    	 
		};    
		status.setRenderer(statusRender);  		
		modelColumns.add(status);
		
		modelColumns.add(new ColumnConfig("LastUpdate", "Last Updated", 130));
		modelColumns.add(new ColumnConfig("gCoreVersion", "Version", 50));
		modelColumns.add(new ColumnConfig("ghnVersion", "Distro v.", 50));

		
		ColumnConfig ramLeft = new ColumnConfig("VirtualAvailable", "Mem avail.", 70);
		ramLeft.setAlignment(HorizontalAlignment.RIGHT);
		ramLeft.setEditor(new CellEditor(new NumberField()));   
		final NumberFormat number = NumberFormat.getFormat("#,##0;(#,##0)"); 

		GridCellRenderer<BaseModelData> ramRender = new GridCellRenderer<BaseModelData>() {
			@Override
			public String render(BaseModelData model, String property,	ColumnData config, 
					int rowIndex, int colIndex,	ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				int val = Integer.parseInt((String)model.get(property)); 
				
				int tot = Integer.parseInt((String) model.get("VirtualSize"));
				
				int percentage = (val * 100) / tot; 
				
				String style = val < 100 ? "red" : "green";    
				String toDisplay =  number.format(val).replaceAll(",", ".");
				toDisplay += " MB";
							
				return "<span style='color:" + style + "'>" + percentage + "% ("+toDisplay+")</span>"; 
			}    	 
		};    
		ramLeft.setRenderer(ramRender);   	

		modelColumns.add(ramLeft);
	
				
		ColumnConfig localSpace = new ColumnConfig("LocalAvailableSpace", "HD Space left", 70);
		localSpace.setAlignment(HorizontalAlignment.RIGHT);
		localSpace.setEditor(new CellEditor(new NumberField()));   

		GridCellRenderer<BaseModelData> mbRender = new GridCellRenderer<BaseModelData>() {
			@Override
			public String render(BaseModelData model, String property,	ColumnData config, 
					int rowIndex, int colIndex,	ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				int val = 0;
				try {
					val = Integer.parseInt((String)model.get(property));
				} catch (NumberFormatException e) {
					val = 0;
				}
				String style = val < 1000000 ? "red" : "green";    
				String toDisplay =  number.format(val);
				if (toDisplay.length() > 4)
					toDisplay = toDisplay.substring(0, toDisplay.length()-4).replaceAll(",", ".");
				toDisplay += " MB";
				return "<span style='color:" + style + "'>" + toDisplay + "</span>"; 
			}    	 
		};   

		localSpace.setRenderer(mbRender);   		
		modelColumns.add(localSpace);
		
		
		//Optional
		ColumnConfig ramTotal = new ColumnConfig("VirtualSize", "V. Memory total", 70);
		ramTotal.setAlignment(HorizontalAlignment.RIGHT);
		ramTotal.setEditor(new CellEditor(new NumberField()));   

		GridCellRenderer<BaseModelData> ramTotRender = new GridCellRenderer<BaseModelData>() {
			@Override
			public String render(BaseModelData model, String property,	ColumnData config, 
					int rowIndex, int colIndex,	ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				int val = Integer.parseInt((String)model.get(property));    
				String toDisplay =  number.format(val).replaceAll(",", ".");
				toDisplay += " MB";
				return toDisplay; 
			}    	 
		};    
		ramTotal.setRenderer(ramTotRender);   	
		modelColumns.add(ramTotal);		
		
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("Uptime", "Up Time", 100));		
		modelColumns.add(new ColumnConfig("LoadLast15Min", "Load Last 15 Min", 100));
		modelColumns.add(new ColumnConfig("LoadLast1Min", "Load Last 1 Min", 100));
		modelColumns.add(new ColumnConfig("LoadLast5Min", "Load Last 5 Min", 100));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		ColumnModel cm = new ColumnModel(modelColumns);
		// The hidden fields after the 5 column
		for (int i = 7; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.GHN.name(), cm);

		// defines the xml structure
		ModelType type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("Status");
		type.addField("Name");
		type.addField("Uptime");
		type.addField("LastUpdate");
		type.addField("VirtualAvailable");	
		type.addField("VirtualSize");	
		type.addField("gCoreVersion", "gcf-version");
		type.addField("ghnVersion", "ghn-version");
		type.addField("LocalAvailableSpace");
		type.addField("LoadLast15Min");
		type.addField("LoadLast1Min");
		type.addField("LoadLast5Min");
		type.addField("Scopes");

		// These fields are internally used and not showable
		type.addField("SubType");
		type.addField("Type");

		XML_MAPPING.put(ResourceTypeDecorator.GHN.name(), type);


		/*********************************************
		 * Collection
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("Name", "Name", 470));
		modelColumns.add(new ColumnConfig("NumberOfMembers", "Cardinality", 70));
		modelColumns.add(new ColumnConfig("LastUpdateTime", "Last Updated", 170));
		modelColumns.add(new ColumnConfig("ID", "ID", 220));

		//Optional
		modelColumns.add(new ColumnConfig("CreationTime", "Creation Time", 170));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));
		//modelColumns.add(new ColumnConfig("NumberOfMembers", "Number Of Members", 115)); //not available anymore


		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 4; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.Collection.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("Name");
		type.addField("CreationTime");
		type.addField("LastUpdateTime");
		type.addField("NumberOfMembers");
		type.addField("Scopes");
		type.addField("SubType");
		type.addField("Type");


		XML_MAPPING.put(ResourceTypeDecorator.Collection.name(), type);


		/*********************************************
		 * Service
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();

		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 200));
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 260));
		modelColumns.add(new ColumnConfig("Version", "Main Package Version", 100));
		//Optional
		modelColumns.add(new ColumnConfig("Shareable", "Shareable", 100));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 3; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.Service.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("Version");
		type.addField("Shareable");
		type.addField("Scopes");

		// These fields are internally used and not showable
		type.addField("SubType");
		type.addField("Type");


		XML_MAPPING.put(ResourceTypeDecorator.Service.name(), type);



		/*********************************************
		 * InstallableSoftware
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();

		// adds the checkbox to the model
		CheckColumnConfig checkColumn =
			new CheckColumnConfig(ResourceDetailModel.SERVICE_INSTALL_KEY, "Deploy", 60);
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		modelColumns.add(checkColumn);

		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 250));
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 250));
		modelColumns.add(new ColumnConfig("Version", "Main Package Version", 100));
		//Optional
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 3; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.InstallableSoftware.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("Version");
		type.addField("Scopes");

		// These fields are internally used and not showable
		type.addField("SubType");
		type.addField("Type");


		XML_MAPPING.put(ResourceTypeDecorator.InstallableSoftware.name(), type);



		/*********************************************
		 * RunningInstance
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 200));
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 260));
		modelColumns.add(new ColumnConfig("Version", "Version", 100));
		modelColumns.add(new ColumnConfig("Status", "Status", 65));
		modelColumns.add(new ColumnConfig("GHN", "GHN", 300));
		//Optional
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 5; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.RunningInstance.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("Version");
		type.addField("Status");
		// a) This is the GHNID type.addField("GHN", "/Profile/GHN/@UniqueID");
		// b) While this is its name
		type.addField("GHN", "ghn-name");
		type.addField("Scopes");

		// These fields are internally used and not showable
		type.addField("SubType");
		type.addField("Type");


		XML_MAPPING.put(ResourceTypeDecorator.RunningInstance.name(), type);



		/*********************************************
		 * VIEW
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("ViewName", "View Name", 200));
		modelColumns.add(new ColumnConfig("SourceKey", "Source Key", 200));
		modelColumns.add(new ColumnConfig("LastUpdate", "Last Update", 270));
		modelColumns.add(new ColumnConfig("Cardinality", "Cardinality", 100));		
		//Optional
		modelColumns.add(new ColumnConfig("ViewType", "View Type", 170));	
		modelColumns.add(new ColumnConfig("RelatedCollectionId", "Related Collection Id", 220));	
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 170));	
		modelColumns.add(new ColumnConfig("Termination", "Termination Time", 270));

		modelColumns.add(new ColumnConfig("Source", "Source", 230));
		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 170));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("RI", "RI", 220));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 4; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.VIEW.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ViewName");
		type.addField("Cardinality");
		type.addField("ViewType");
		type.addField("RelatedCollectionId");		
		type.addField("Source");
		type.addField("SourceKey");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("Termination", "TerminationTimeHuman");
		type.addField("LastUpdate", "LastUpdateHuman");
		type.addField("Scopes", "/scopes");
		type.addField("SubType", "SubType");
		type.addField("RI");
		type.addField("Type");
		
		
		GWT.log("VIew Name: " + ResourceTypeDecorator.VIEW.name());
		XML_MAPPING.put(ResourceTypeDecorator.VIEW.name(), type);


		/*********************************************
		 * GenericResource
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("Name", "Name", 200));
		modelColumns.add(new ColumnConfig("Description", "Description", 400));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));

		//Optional
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("SubType", "Secondary Type", 170));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 3; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.GenericResource.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("Name");
		type.addField("Description");
		type.addField("Scopes");
		type.addField("SubType");
		type.addField("Type");

		XML_MAPPING.put(ResourceTypeDecorator.GenericResource.name(), type);
		
		/*********************************************
		 * RuntimeResource
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("Name", "Name", 200));
		modelColumns.add(new ColumnConfig("Host", "Host", 250));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));


		//Optional
		modelColumns.add(new ColumnConfig("ID", "ID", 100));
		modelColumns.add(new ColumnConfig("SubType", "Category", 370));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 4; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.RuntimeResource.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("Name");
		type.addField("Scopes");
		type.addField("Host");
		type.addField("SubType");
		type.addField("Type");

		XML_MAPPING.put(ResourceTypeDecorator.RuntimeResource.name(), type);



		/*********************************************
		 * WSResources
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("SourceKey", "Source Key", 230));
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 170));
		modelColumns.add(new ColumnConfig("Termination", "Termination Time", 270));
		modelColumns.add(new ColumnConfig("LastUpdate", "Last Update", 270));
		//Optional
		modelColumns.add(new ColumnConfig("ID", "ID", 220));
		modelColumns.add(new ColumnConfig("Source", "Source", 230));
		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 170));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 300));
		modelColumns.add(new ColumnConfig("RI", "RI", 220));
		modelColumns.add(new ColumnConfig("SubType", "SubType", 200));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 4; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.WSResource.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName(ResourceTypeDecorator.WSResource.name());

		type.addField("ID");
		type.addField("Source");
		type.addField("SourceKey");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("Termination", "TerminationTimeHuman");
		type.addField("LastUpdate", "LastUpdateHuman");
		type.addField("Scopes", "/scopes");
		type.addField("SubType", "SubType");
		type.addField("RI");
		type.addField("Type");
		XML_MAPPING.put(ResourceTypeDecorator.WSResource.name(), type);


		/*********************************************
		 * PROFILES OF RELATED RESOURCES: GHN
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("ServiceClass", "Service Class", 200));
		modelColumns.add(new ColumnConfig("ServiceName", "Service Name", 260));
		modelColumns.add(new ColumnConfig("ServiceVersion", "Service Version", 100));
		modelColumns.add(new ColumnConfig("MainVersion", "Main Version", 100));
		modelColumns.add(new ColumnConfig("Status", "Status", 65));
		//Optional
		modelColumns.add(new ColumnConfig("ID", "ID", 220));

		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 5; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.GHNRelated.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("ServiceVersion");
		type.addField("MainVersion");
		type.addField("Status");
		XML_MAPPING.put(ResourceTypeDecorator.GHNRelated.name(), type);


		/*********************************************
		 * PROFILES OF RELATED RESOURCES: RunningInstance
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("Key", "Name", 200));
		modelColumns.add(new ColumnConfig("Value", "Value", 260));
		// create the column model
		cm = new ColumnModel(modelColumns);
		RECORD_DEFINITION.put(ResourceTypeDecorator.RunningInstanceRelated.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("Key");
		type.addField("Value");
		XML_MAPPING.put(ResourceTypeDecorator.RunningInstanceRelated.name(), type);


		/*********************************************
		 * PROFILES OF RELATED RESOURCES: Service
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("RIID", "RI ID", 90));
		modelColumns.add(new ColumnConfig("ServiceStatus", "RI Status", 70));
		modelColumns.add(new ColumnConfig("RIVersion", "Serv.Version", 70));
		modelColumns.add(new ColumnConfig("ActivationTime", "RI ActivationTime", 170));
		modelColumns.add(new ColumnConfig("GHNName", "GHN Name", 100));
		modelColumns.add(new ColumnConfig("GHNStatus", "GHN Status", 70));

		modelColumns.add(new ColumnConfig("GHNID", "GHN ID", 100));
		modelColumns.add(new ColumnConfig("GHNSite", "GHN Site", 100));
		modelColumns.add(new ColumnConfig("GHNLoad15Min", "GHNLoad15Min", 50));
		modelColumns.add(new ColumnConfig("GHNLoad5Min", "GHNLoad5Min", 50));
		modelColumns.add(new ColumnConfig("GHNLoad1Min", "GHNLoad1Min", 50));
		modelColumns.add(new ColumnConfig("GHNActivationTime", "GHNActivationTime", 100));
		modelColumns.add(new ColumnConfig("GHNLastUpdate", "GHNLastUpdate", 100));
		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 6; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}
		RECORD_DEFINITION.put(ResourceTypeDecorator.ServiceRelated.name(), cm);

		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("RIID");
		type.addField("ServiceStatus");
		type.addField("ActivationTime");
		type.addField("GHNID");
		type.addField("RIVersion");
		type.addField("GHNName");
		type.addField("GHNSite");
		type.addField("GHNStatus");
		type.addField("GHNLoad15Min");
		type.addField("GHNLoad5Min");
		type.addField("GHNLoad1Min");
		type.addField("GHNActivationTime");
		type.addField("GHNLastUpdate");
		XML_MAPPING.put(ResourceTypeDecorator.ServiceRelated.name(), type);

		/*********************************************
		 * MODEL FOR SWEEPER GHN
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();

		modelColumns.add(new ColumnConfig("Name", "Name", 130));
		modelColumns.add(new ColumnConfig("Status", "Status", 50));
		ColumnConfig minElapsed = (new ColumnConfig("UpdateMinutesElapsed", "Minutes from Update", 130));
		minElapsed.setAlignment(HorizontalAlignment.CENTER);
		modelColumns.add(minElapsed);
		modelColumns.add(new ColumnConfig("AllocatedRI", "#RI", 40));
		modelColumns.add(new ColumnConfig("LastUpdate", "LastUpdate", 130));

		// hidden fields
		modelColumns.add(new ColumnConfig("Actions", "Actions", 260));
		modelColumns.add(new ColumnConfig("ID", "ID", 200));
		modelColumns.add(new ColumnConfig("Type", "Type", 100));
		modelColumns.add(new ColumnConfig("Location", "Location", 100));
		modelColumns.add(new ColumnConfig("Domain", "Domain", 100));
		modelColumns.add(new ColumnConfig("IPAddress", "IPAddress", 100));
		modelColumns.add(new ColumnConfig("Scopes", "Scopes", 100));


		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 5; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}


		RECORD_DEFINITION.put(ResourceTypeDecorator.Sweeper_GHN.name(), cm);
		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("Name");
		type.addField("Actions");

		type.addField("Status");
		type.addField("AllocatedRI");
		type.addField("Type");
		type.addField("Location");
		type.addField("Domain");
		type.addField("IPAddress");
		type.addField("Scopes");
		type.addField("LastUpdate");
		type.addField("UpdateMinutesElapsed");
		XML_MAPPING.put(ResourceTypeDecorator.Sweeper_GHN.name(), type);


		/*********************************************
		 * MODEL FOR SWEEPER RI
		 ********************************************/
		// The column model for grid representation
		modelColumns = new ArrayList<ColumnConfig>();

		modelColumns.add(new ColumnConfig("ID", "ID", 120));
		modelColumns.add(new ColumnConfig("ServiceClass", "ServiceClass", 100));
		modelColumns.add(new ColumnConfig("ServiceName", "ServiceName", 100));
		modelColumns.add(new ColumnConfig("ghnid", "GHN ID", 120));

		// hidden fields
		modelColumns.add(new ColumnConfig("Actions", "Actions", 260));
		modelColumns.add(new ColumnConfig("ServiceStatus", "Status", 90));
		modelColumns.add(new ColumnConfig("ActivationTime", "Activation Time", 140));


		// create the column model
		cm = new ColumnModel(modelColumns);
		for (int i = 4; i < modelColumns.size(); i++) {
			cm.setHidden(i, true);
		}

		RECORD_DEFINITION.put(ResourceTypeDecorator.Sweeper_RI.name(), cm);
		// defines the xml structure
		type = new ModelType();
		type.setRoot("Resources");
		type.setRecordName("Resource");
		type.addField("ID");
		type.addField("ServiceStatus");
		type.addField("ServiceClass");
		type.addField("ServiceName");
		type.addField("ActivationTime");
		type.addField("ghnid");
		type.addField("Actions");
		XML_MAPPING.put(ResourceTypeDecorator.Sweeper_RI.name(), type);



		/*********************************************
		 * VALIDATORS
		 ********************************************/
		/*
		 * REQUIRED FIELDS
		 */

		REQUIRED_FIELDS = new HashMap<String, String[]>();

		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.GHN.name(),
				new String[] {
					"Name",
					"SubType",
					"ID",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.Collection.name(),
				new String[] {
					"Name",
					"SubType",
					"ID",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.GenericResource.name(),
				new String[] {
					"Name",
					"SubType",
					"ID",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.RuntimeResource.name(),
				new String[] {
					"Name",
					"SubType",
					"ID",
					"Host",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.VIEW.name(),
				new String[] {
					"Name",
					"SubType",
					"ID",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.RunningInstance.name(),
				new String[] {
					"ServiceClass",
					"ServiceName",
					"ID",
					"SubType",
					"Scopes",
					"GHN"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.Service.name(),
				new String[] {
					"ServiceClass",
					"ServiceName",
					"ID",
					"SubType",
					"Scopes"
				});
		REQUIRED_FIELDS.put(
				ResourceTypeDecorator.WSResource.name(),
				new String[] {
					"SourceKey",
					"ServiceName",
					"ID",
					"SubType",
					"Scopes"
				});
	}


	public static final ColumnModel getRecordDefinition(final String nodeID) {
		init();
		return RECORD_DEFINITION.get(nodeID);
	}

	public static final ModelType getXMLMapping(final String nodeID) {
		init();
		return XML_MAPPING.get(nodeID);
	}

	public static final String[] getRequiredFields(final String type) {
		init();
		return REQUIRED_FIELDS.get(type);
	}
}
