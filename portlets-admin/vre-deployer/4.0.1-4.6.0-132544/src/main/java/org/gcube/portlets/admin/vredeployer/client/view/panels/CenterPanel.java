package org.gcube.portlets.admin.vredeployer.client.view.panels;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.VredeployerService;
import org.gcube.portlets.admin.vredeployer.client.VredeployerServiceAsync;
import org.gcube.portlets.admin.vredeployer.client.constants.ImagesConstants;
import org.gcube.portlets.admin.vredeployer.client.control.Controller;
import org.gcube.portlets.admin.vredeployer.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredeployer.client.util.CheckTimer;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.CloudDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.GhnGrid;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.OverallDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.ResourcesDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.ServicesDeployStatus;
import org.gcube.portlets.admin.vredeployer.shared.GHNBean;
import org.gcube.portlets.admin.vredeployer.shared.GHNProfile;
import org.gcube.portlets.admin.vredeployer.shared.VREDescrBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class CenterPanel extends ContentPanel {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final VredeployerServiceAsync deployerService = GWT.create(VredeployerService.class);

	private Controller controller;
	/**
	 * 
	 */
	private CheckTimer checkTimer = null;

	private boolean showingSummary = false;

	public CenterPanel(Controller c) {		
		controller = c;
		this.setHeading("");
		this.setFrame(true);
		this.setBodyStyle("backgroundColor: white;");
		setButtonAlign(HorizontalAlignment.CENTER);
		this.mask("Loading VRE definition for Approval", "loading-indicator");
	}


	/**
	 * 
	 */
	public void emptyPanel() {
		this.removeAll();		
	}

	/**
	 * view for description vre step
	 * @param vDesc
	 */
	@SuppressWarnings("deprecation")
	public void showVreDescription(VREDescrBean vDesc) {  

		FormData formData = new FormData("-20");  

		FormPanel form = new FormPanel();  
		form.setHeaderVisible(false);
		form.setFrame(false);  
		form.setWidth("100%");  
		form.setLayout(new FlowLayout());  

		FieldSet fieldSet = new FieldSet();  
		fieldSet.setHeading("VRE Information");  
		//fieldSet.setCheckboxToggle(true);  

		FormLayout layout = new FormLayout();  
		layout.setLabelWidth(100);  
		fieldSet.setLayout(layout);  

		TextField<String> vrename = new TextField<String>();  
		vrename.setFieldLabel("Name");  
		vrename.setValue(vDesc.getName());
		fieldSet.add(vrename, formData);  

		TextField<String> vredesigner = new TextField<String>();  
		vredesigner.setFieldLabel("Designer"); 
		vredesigner.setValue(vDesc.getDesigner());
		fieldSet.add(vredesigner, formData);  

		TextField<String> vremanager = new TextField<String>();  
		vremanager.setFieldLabel("Manager");  
		vremanager.setValue(vDesc.getManager());
		fieldSet.add(vremanager, formData);  

		TextArea description = new TextArea();  
		description.setFieldLabel("Description");  
		description.setValue(vDesc.getDescription());
		fieldSet.add(description, formData);  

		form.add(fieldSet);  
		form.setReadOnly(true);

		fieldSet = new FieldSet();  
		fieldSet.setHeading("Life time");  
		fieldSet.setCollapsible(true);  

		layout = new FormLayout();  
		layout.setLabelWidth(100);  
		fieldSet.setLayout(layout);  

		TextField<String> from = new TextField<String>();  
		from.setFieldLabel("From");
		from.setValue(vDesc.getStartTime().toGMTString());
		fieldSet.add(from, formData);  

		TextField<String> to = new TextField<String>();    
		to.setFieldLabel("To");  
		to.setValue(vDesc.getEndTime().toGMTString());
		fieldSet.add(to, formData);  

		form.add(fieldSet);  
		//		form.setButtonAlign(HorizontalAlignment.CENTER);  
		//		form.addButton(new Button("Save"));  
		//		form.addButton(new Button("Cancel"));  

		this.add(form);  
	}  


	/**
	 * view for the functionality step
	 * @param root
	 */
	public void showFunctionality(VREFunctionalityModel root) {
		GWT.log("root func: " + root.getChildCount());
		TreePanel<ModelData> tree;
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(root.getChildren(), true);
		tree =  new TreePanel<ModelData>(store);
		tree.setWidth(300);
		tree.setDisplayProperty("name");
		add(tree);
		// Handles the selection
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// The icon decorator
		tree.setIconProvider(
				new ModelIconProvider<ModelData>() {
					public AbstractImagePrototype getIcon(final ModelData model) {
						if (model.get("node") != null && model.get("icon") != null) {
							return IconHelper.createStyle((String) model.get("icon"));
						}
						return IconHelper.createStyle("defaultleaf-icon");
					}
				});
		setLayout(new FitLayout());  
		tree.expandAll();
		tree.setAutoExpand(true);
		
		controller.setEastPanelContent(getInitFuncInfo());

		tree.addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {

			public void handleEvent(TreePanelEvent<ModelData> be) {
				VREFunctionalityModel sm = (VREFunctionalityModel) be.getItem();	
				if (be.getType() == Events.OnClick) {
					//do nothing
				}
			}
		});
	}

	private ContentPanel getInitFuncInfo() {
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		String toAdd = "<h3>Legend:</h3>";
		toAdd += "<div class=\"extres-icon\" style=\"padding-left:25px; margin-top: 10px; background-repeat: no-repeat;\">Selected Resource</div>";
		toAdd += "<div class=\"runninginstance-icon\" style=\"padding-left:25px; margin-top: 10px; background-repeat: no-repeat;\">Found Service</div>";
		toAdd += "<div class=\"missing-ri\" style=\"padding-left:25px; margin-top: 10px; background-repeat: no-repeat;\">Missing Service</div>";
		toAdd += "<div class=\"architecture-icon\" style=\"padding-left:25px; margin-top: 10px; background-repeat: no-repeat;\">gHN</div>";
		cp.add(new HTML(toAdd, true ));	
		cp.setStyleAttribute("margin", "10px");
		cp.setLayout(new FitLayout());
		return cp;
	}
	/**
	 * view for architecture step
	 * @param vDesc
	 */
	public void showGHNList(final List<GHNProfile> ghnodes) {  

		GhnGrid ghnGrid = new GhnGrid(controller, ghnodes);
		add(ghnGrid.getGrid());

		setLayout(new FitLayout());		
	}


	/**
	 * view for finalize step
	 * @param vDesc
	 */
	public void showFinalize(VREDescrBean desc, GHNBean[] ghns, boolean isCloudSelected) {  

		ContentPanel cp = new ContentPanel(new FitLayout());
		cp.setHeaderVisible(false);
		String toShow = "";
		if ( (ghns == null && (! isCloudSelected) ) ) {
			toShow = "<h3>Step Architecture Selection is Missing</h3>";
		}
		else {
			unmask();
			toShow = "<h3>" + desc.getName() + " is ready to be deployed.</h3>";
			toShow += "<br/><br/>selected nodes:";

			if (isCloudSelected) {
				toShow += "<p><img src=\""+ ImagesConstants.CLOUD_IMAGE + " \" /></p>";
			} else {
				toShow += "<p><ul>";

				for (int i = 0; i < ghns.length; i++) {
					if (ghns[i].isSelected())
						toShow += "<li>&nbsp; - &nbsp; <b>"+ ghns[i].getHost()+ "</b> (" + ghns[i].getCountry() + ")</li>" ;
				}						
				toShow += "</ul></p>";
			}
			toShow += "<br/><br/>click create button to start the creation process";

			cp.add(new HTML(toShow, true));
			cp.addButton(new Button("Create", new SelectionListener<ButtonEvent>() {  
				@Override  
				public void componentSelected(ButtonEvent ce) {  
					controller.createVreButtonClicked();	
				}  
			}));
		}
		cp.setStyleAttribute("margin", "10px");
		cp.add(new HTML(toShow, true));
		add(cp);
		setLayout(new FitLayout());	
		showingSummary = true;

	}

	/**
	 * view for finalize step
	 * @param vDesc
	 */
	public void showReport() {  
		if (showingSummary) {
			removeAll();
			layout();
			showingSummary = false;
		}
		this.remove(this.getHeader(), true);
		ContentPanel panel = new ContentPanel();  
		panel.setLayout(new RowLayout(Orientation.VERTICAL));  
		//panel.setSize(400, 300);  
		panel.setFrame(false);  
		panel.setCollapsible(false);  
		panel.setHeaderVisible(false);


		CloudDeployStatus cloudPanel = new CloudDeployStatus();
		ServicesDeployStatus resourceManagerPanel = new ServicesDeployStatus();


		CellPanel cellp = new HorizontalPanel();
		cellp.setWidth("100%");

		ContentPanel p1 = cloudPanel.getCloudDeployStatusPanel();
		ContentPanel p2 = resourceManagerPanel.getServicesDeployStatusPanel();

		cellp.add(p1);
		cellp.add(p2);

		cellp.setCellWidth(p1, "50%");
		cellp.setCellWidth(p2, "50%");
		cellp.setSpacing(5);

		//two rows

		panel.add(cellp, new RowData(1, -1, new Margins()));  

		CellPanel cellp2 = new HorizontalPanel();
		cellp2.setWidth("100%");
		ResourcesDeployStatus resourceAndFuncPanel = new ResourcesDeployStatus();
		OverallDeployStatus overPanel = new OverallDeployStatus();

		ContentPanel p3 = resourceAndFuncPanel.getResourcesDeployStatusPanel();
		ContentPanel p4 = overPanel.getOverallDeployStatusPanel();


		cellp2.add(p3);
		cellp2.add(p4);

		cellp2.setCellWidth(p3, "50%");
		cellp2.setCellWidth(p4, "50%");
		cellp2.setSpacing(5);


		panel.add(cellp, new RowData(1, -1, new Margins()));  
		panel.add(cellp2, new RowData(1, -1, new Margins()));  

		//two rows
		Button button = new Button("See textual report", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				mask("Retrieving Textual Report", "loading-indicator");	
				openTextualReport();
			}  
		});

		add(panel, new FlowData(0));  
		add(button, new FlowData(0));



		layout();

		/**
		 * starts the timer
		 */
		CheckTimer reportTimer = new CheckTimer(cloudPanel, resourceManagerPanel, resourceAndFuncPanel, overPanel);
		reportTimer.scheduleTimer(10);
	}


	private void openTextualReport() {
		new ReportPanel(this);
	}
}
