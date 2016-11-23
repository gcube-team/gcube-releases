package org.gcube.portlets.admin.vredefinition.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.ui.PageHeader;
import org.gcube.portlets.admin.vredefinition.client.ui.ResourcesTable;
import org.gcube.portlets.admin.vredefinition.client.ui.SummaryPanel;
import org.gcube.portlets.admin.vredefinition.client.ui.VRECreationFormSkeleton;
import org.gcube.portlets.admin.vredefinition.shared.Functionality;
import org.gcube.portlets.admin.vredefinition.shared.Resource;
import org.gcube.portlets.admin.vredefinition.shared.ResourceCategory;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.AppendButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The VREDefinitionPanel class contains the form to create/edit a VRE.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class VREDefinitionPanel extends Composite {

	// async services
	VREDefinitionServiceAsync rpcService = GWT.create(VREDefinitionService.class);

	// main panel
	private VerticalPanel mainPanel;

	// skeleton
	private VRECreationFormSkeleton skeleton = new VRECreationFormSkeleton();

	// summary panel
	private SummaryPanel summary = new SummaryPanel();

	// main tab
	private TabPanel mainTabPanel = new TabPanel(Tabs.LEFT);

	// tab panel for VRE information
	private Tab vreInformationTab = new Tab();

	// the create vre button
	private Button createVREButton = new Button("Upload");

	// retrieving information alert block
	private AlertBlock retrievingInformation = new AlertBlock(AlertType.INFO);

	// hashmap to keep track of the selection of the subfunctionalities
	protected class SubFunctionalityClientBean{

		private CheckBox checkbox;
		private Functionality subfunctionality;

		SubFunctionalityClientBean(CheckBox checkbox, Functionality subfunctionality){

			this.checkbox = checkbox;
			this.subfunctionality = subfunctionality;

		}

		public CheckBox getCheckbox() {
			return checkbox;
		}


		public Functionality getSubfunctionality() {
			return subfunctionality;
		}
	}

	HashMap<String, List<SubFunctionalityClientBean>> subFunctionalityBeansMap = 
			new HashMap<String, List<VREDefinitionPanel.SubFunctionalityClientBean>>();

	// keys of the hashmap returned by some rpc services
	private static final String DESIGNER = "Designer";
	private static final String MANAGER = "Manager";
	private static final String VRE_NAME_FIELD = "vreName";
	private static final String VRE_MANAGER_FIELD = "vreManager";
	private static final String VRE_DESIGNER_FIELD = "vreDesigner";
	private static final String VRE_DESCRIPTION_FIELD = "vreDescription";
	private static final String VRE_START_TIME_FIELD = "vreStartTime";
	private static final String VRE_END_TIME_FIELD = "vreEndTime";
	private static final String EDIT_MODE = "edit";

	public VREDefinitionPanel(){

		super();

		// create main panel
		mainPanel = new VerticalPanel();

		// init
		initWidget(mainPanel);

		// set its style
		mainPanel.addStyleName("vre-definition-main-panel");

		// add the main tab panel to the main panel
		mainPanel.add(mainTabPanel);

		// add skeleton as tab
		vreInformationTab.setHeading("VRE Information");
		vreInformationTab.add(skeleton);

		// add and set as selected
		mainTabPanel.add(vreInformationTab);
		mainTabPanel.selectTab(0);

		// add loader to skeletonTab
		retrievingInformation.setClose(false);
		retrievingInformation.setText("Retrieving information, please wait...");
		vreInformationTab.add(retrievingInformation);

		// require designers/managers information or retrieve a previous bean saved into section
		rpcService.getVRE(new AsyncCallback<Map<String,Serializable>>() {

			@SuppressWarnings("unchecked")
			public void onSuccess(Map<String, Serializable> result) {

				// remove loader
				vreInformationTab.remove(retrievingInformation);

				if(result != null && !result.isEmpty()){

					retrievingInformation.setText("Retrieving functionalities, please wait...");
					vreInformationTab.add(retrievingInformation);

					boolean editMode = (Boolean) result.get(EDIT_MODE);

					// check edit mode
					if(editMode){

						skeleton.setVREDesigner((String)result.get(VRE_DESIGNER_FIELD));
						skeleton.setVREManagers((List<String>)result.get(MANAGER), (String)result.get(VRE_MANAGER_FIELD)); // pass all the managers plus the selected one
						skeleton.setVREName((String)result.get(VRE_NAME_FIELD));
						skeleton.setVREDescription((String)result.get(VRE_DESCRIPTION_FIELD));
						skeleton.setVREFromDate((Date)result.get(VRE_START_TIME_FIELD));
						skeleton.setVREToDate((Date)result.get(VRE_END_TIME_FIELD));

					}else{
						skeleton.setVREDesigner((String)result.get(DESIGNER));
						skeleton.setVREManagers((List<String>)result.get(MANAGER), null);
					}

					// require functionalities
					requireFunctionalities(editMode, mainTabPanel);

				}else
					showErrors("Unable to retrieve the current designer and managers for this Virtual Organization");

			}

			public void onFailure(Throwable caught) {

				// remove loader
				vreInformationTab.remove(retrievingInformation);
				showErrors("Unable to retrieve the current designer and managers for this Virtual Organization");

			}
		});

	}

	/**
	 * Require the list of functionalities
	 * @param mainTabPanel 
	 */
	private void requireFunctionalities(boolean isEditMode, final TabPanel mainTabPanel) {
		// require the list of functionalities
		rpcService.getFunctionality(isEditMode, new AsyncCallback<ArrayList<Functionality>>() {

			public void onSuccess(final ArrayList<Functionality> result) {

				// remove info
				vreInformationTab.remove(retrievingInformation);

				if(result != null){

					// put these widgets into the skeleton as rows
					for (final Functionality functionality : result) {

						// create a tab
						Tab functionalityTab = new Tab();
						functionalityTab.setHeading(functionality.getName());

						// the header row of each macro functionality will look like
						// Title - Subtitle
						Row mainRow = new Row();

						// title and subtitle of the macrofunctionality
						PageHeader header = new PageHeader();
						header.setText(functionality.getName(), functionality.getDescription());
						mainRow.add(header);

						// get subfunctionalities
						List<Functionality> children = functionality.getSubFunctionalities();

						// if there are more than two, we add a button to select/deselect them all
						if(children.size() >= 2){

							// Select/Deselect sub-functionalities button  
							final Button selectDeselectAll = new Button("Select All");
							selectDeselectAll.setTitle("Select all the sub-functionalities");
							selectDeselectAll.setType(ButtonType.LINK);
							selectDeselectAll.setSize(ButtonSize.LARGE);
							selectDeselectAll.addStyleName("select-deselect-button-style");

							final Boolean[] selectedAll = new Boolean[0];
							selectedAll[0] = false;

							// add an handler to the button Select/Deselect All
							selectDeselectAll.addClickHandler(new ClickHandler() {

								public void onClick(ClickEvent event) {

									// get the list from the hashmap
									List<VREDefinitionPanel.SubFunctionalityClientBean> listSubfs =
											subFunctionalityBeansMap.get(functionality.getName());

									// select/deselect all the subfunctionalities within the list for this macrofunctionality
									if(!selectedAll[0]){

										// swap to deselect all
										selectDeselectAll.setTitle("Deselect all the sub-functionalities");
										selectDeselectAll.setText("Deselect All");

										for(VREDefinitionPanel.SubFunctionalityClientBean subBean : listSubfs){
											subBean.getCheckbox().setValue(true);
											subBean.getSubfunctionality().setSelected(true);
										}

									}else{

										// swap to select all
										selectDeselectAll.setTitle("Select all the sub-functionalities");
										selectDeselectAll.setText("Select All");

										for(VREDefinitionPanel.SubFunctionalityClientBean subBean : listSubfs){
											subBean.getCheckbox().setValue(false);
											subBean.getSubfunctionality().setSelected(false);
										}

									}

									// swap status
									selectedAll[0] = !selectedAll[0];
								}
							});

							mainRow.add(selectDeselectAll);

						}
						// append the whole row to the container
						functionalityTab.add(mainRow);

						mainTabPanel.add(functionalityTab);

						if(children != null){
							for(final Functionality child: children){

								// create a row for the subfunctionality
								Row subFunctionalityRow = new Row();

								// put all the stuff into a form
								Form subFunctionalityForm = new Form();
								subFunctionalityForm.addStyleName("subfunctionality-form");

								// select subfunctionality checkbox
								CheckBox checkBoxSubfunctionality = new CheckBox();
								checkBoxSubfunctionality.setText(child.getName());
								checkBoxSubfunctionality.setValue(child.isSelected()); // Auth for example is already set
								checkBoxSubfunctionality.addStyleName("subfunctionality-textbox");

								// add handler to this checkbox
								checkBoxSubfunctionality.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

									public void onValueChange(ValueChangeEvent<Boolean> event) {

										child.setSelected(event.getValue());

									}
								});

								// subfunctionality description
								HelpInline helpBlock = new HelpInline();
								helpBlock.setText(child.getDescription());


								subFunctionalityForm.add(checkBoxSubfunctionality);
								subFunctionalityForm.add(helpBlock);

								// add form to row
								subFunctionalityRow.add(subFunctionalityForm);

								// insert this entry <MacroFunc, <SubFunc, CheckBox>> to the map
								if(subFunctionalityBeansMap.containsKey(functionality.getName())){

									// we need to update the list
									List<VREDefinitionPanel.SubFunctionalityClientBean> oldList =
											subFunctionalityBeansMap.get(functionality.getName());

									oldList.add(new SubFunctionalityClientBean(checkBoxSubfunctionality, child));

								}else{

									// there is not yet such macro functionality
									List<VREDefinitionPanel.SubFunctionalityClientBean> newList = new ArrayList<SubFunctionalityClientBean>();
									newList.add(new SubFunctionalityClientBean(checkBoxSubfunctionality, child));

									// put into the hashmap
									subFunctionalityBeansMap.put(functionality.getName(), newList);

								}

								// append to the main row
								functionalityTab.add(subFunctionalityRow);

								// iterate over resources (of the subfunctionality)
								List<ResourceCategory> categoryResources = child.getResources();

								if(categoryResources != null){
									for (ResourceCategory resourceCategory : categoryResources) {

										// skip the category and go to its children (i.e. the resources)
										List<Resource> resources = resourceCategory.getItems();

										// if there are resources, add a filter widget to the form and a table
										if(resources.size() > 0){

											AppendButton apButton = new AppendButton();

											TextBox searchBox = new TextBox();
											searchBox.setPlaceholder("Filter by name");
											Button searchButton = new Button();
											searchButton.setIcon(IconType.SEARCH);
											searchButton.setIconSize(IconSize.DEFAULT);
											searchButton.setType(ButtonType.DEFAULT);

											apButton.add(searchBox);
											apButton.add(searchButton);

											apButton.addStyleName("filter-functionalities-box");
											subFunctionalityForm.add(apButton);

											// create a table whose rows are
											//<Name, Description, Select> and show the first 5 rows
											ResourcesTable table = new ResourcesTable(resources, apButton, checkBoxSubfunctionality);

											// append to the form
											subFunctionalityForm.add(table);

										}

									}
								}
							}
						}
					}

					// add SummaryTabPane
					final Tab summaryTab = new Tab();
					summaryTab.setHeading("Summary");
					summaryTab.add(summary);
					summaryTab.addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {

							onGenerateSummarySubmit(result, summaryTab);

						}
					});

					// add to the tab panel
					mainTabPanel.add(summaryTab);

				}else{

					// remove info
					vreInformationTab.remove(retrievingInformation);
					showErrors("Unable to retrieve VRE functionalities");

				}
			}

			public void onFailure(Throwable caught) {

				// remove info
				vreInformationTab.remove(retrievingInformation);
				showErrors("Unable to retrieve VRE functionalities");

			}
		});
	}

	/**
	 * Generate summary
	 * @param result 
	 * @param summaryTab 
	 */
	private void onGenerateSummarySubmit(final ArrayList<Functionality> result, final Tab summaryTab) {

		// we need to generate the current summary and check if something wrong is present (e.g., vre's name missing)
		final String vreName = skeleton.getCurrentVREName();
		String vreDesigner = skeleton.getCurrentVREDesigner();
		String vreSelectedManager = skeleton.getCurrentVREManager();
		String vreDescription = skeleton.getCurrentVREDescription();
		Date vreStartDate = skeleton.getCurrentVREStartDate();
		Date vreToDate = skeleton.getCurrentVREToDate();

		// check errors: vreName, vreDesigner, vreDescription cannot be empty
		if(vreName.isEmpty() || vreName.contains(" ")){

			Timer t = new Timer() {

				@Override
				public void run() {

					mainTabPanel.selectTab(0);

				}
			};
			t.schedule(100);
			skeleton.showAlertBlockVREName();
			return;
		}

		if(vreDescription.isEmpty() || vreDescription.trim().length() == 0){
			skeleton.showAlertBlockVREDescritption();
			Timer t = new Timer() {

				@Override
				public void run() {

					mainTabPanel.selectTab(0);

				}
			};
			t.schedule(100);
			return;
		}

		// we can show the panel
		summary.setVisible(true);

		// ok, we can create the bean
		final VREDescriptionBean vreDescriptionBean = new VREDescriptionBean(vreName, vreDescription, vreDesigner, vreSelectedManager, vreStartDate, vreToDate);

		// clear functionalities panel
		summary.clearFunctionalitiesPanel();

		// collect information on functionalities/subfunctionalities/resources selected and show them
		for(Functionality macroFunctionality: result){

			// get its name
			String name = macroFunctionality.getName();

			// start building a tree having as root this macro functionality
			Tree t = new Tree();
			TreeItem root = new TreeItem();
			root.setText(name);
			t.addItem(root);

			// panel to add later to the summary for this macrofunctionality
			VerticalPanel partialSummaryFunctionality = new VerticalPanel();
			partialSummaryFunctionality.getElement().getStyle().setMarginBottom(10, Unit.PX);

			// get the child list
			List<Functionality> subfunctionalities = macroFunctionality.getSubFunctionalities();

			for (Functionality subfunctionality : subfunctionalities) {

				// set as name the subfunctionality
				TreeItem subfuncItem = new TreeItem();
				subfuncItem.setText(subfunctionality.getName());

				if(subfunctionality.isSelected()){

					// add to the root
					root.addItem(subfuncItem);

					// we need the categories but we need not to put theme into the tree
					List<ResourceCategory> categories = subfunctionality.getResources();

					if(categories == null)
						continue;

					for (ResourceCategory resourceCategory : categories) {

						// iterate over its resources	
						ArrayList<Resource> resources = resourceCategory.getItems();

						for (Resource resource : resources) {
							if(resource.isSelected()){

								TreeItem resourceItem = new TreeItem();
								resourceItem.setText(resource.getName());

								// add to the subfunctionality
								subfuncItem.addItem(resourceItem);

							}
						}
					}
				}

			}

			// add the tree to the summary
			partialSummaryFunctionality.add(t);
			summary.addFunctionality(partialSummaryFunctionality);

		}

		// fill the summary panel with main information
		summary.setVreMainInformation(vreName, vreSelectedManager, vreDesigner, vreDescription, vreStartDate, vreToDate);

		// add the summary
		summaryTab.add(summary);

		// add the buttons into a flow panel
		final FlowPanel summaryButtons = new FlowPanel();
		summaryButtons.addStyleName("summary-buttons-panel");
		summaryButtons.add(createVREButton);
		summaryTab.add(summaryButtons);

		// add style to goBackToDefinition button

		// set as primary the create one
		createVREButton.setType(ButtonType.PRIMARY);

		// make them large
		createVREButton.setSize(ButtonSize.LARGE);


		// add handler to createVRE button
		createVREButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				boolean confirmed = Window.confirm("Do you want to create " + vreName  + " ?");

				// disable buttons
				createVREButton.setEnabled(false);

				if(confirmed){

					// add waiting block
					final AlertBlock waitingCreation = new AlertBlock(AlertType.INFO);
					waitingCreation.setText("Please wait...");
					waitingCreation.setClose(false);
					summaryTab.add(waitingCreation);

					// try to set the vre
					rpcService.setVRE(vreDescriptionBean, result, new AsyncCallback<Boolean>() {

						public void onSuccess(Boolean result) {

							// remove waiting
							summaryTab.remove(waitingCreation);

							// add alert block
							addAlertOnCreation(vreName, result, summaryTab);

							// remove the buttons
							if(result)
								summaryTab.remove(summaryButtons);
							else{
								// enable again
								createVREButton.setEnabled(true);
							}

						}

						public void onFailure(Throwable caught) {

							// remove waiting
							summaryTab.remove(waitingCreation);

							// add alert block
							addAlertOnCreation(vreName, false, summaryTab);

							// enable again
							createVREButton.setEnabled(true);
						}
					});
				}

				// enable again
				createVREButton.setEnabled(true);
			}
		});

	}

	/**
	 * Show errors on async request
	 */
	private void showErrors(String msg){

		skeleton.removeLoader();

		// error block
		AlertBlock errorMsg = new AlertBlock();
		errorMsg.setType(AlertType.ERROR);
		errorMsg.setClose(false);
		errorMsg.setText(msg);

		// put it into a column of width 12
		Row errorRow = new Row();
		Column errorCol = new Column(12);
		errorCol.add(errorMsg);
		errorRow.add(errorCol);

		// pass to the skeleton
		skeleton.appendRow(errorRow);
	}

	/**
	 * After creation show an alert message
	 * @param vreName
	 * @param success
	 * @param summaryTab
	 */
	private void addAlertOnCreation(String vreName, boolean success, final Tab summaryTab){

		String msg = "VRE with name "  + vreName;
		if(success)
			msg += " created correctly!";
		else
			msg += " not created. Please retry later.";

		final AlertBlock confirmation = new AlertBlock(msg);

		if(success)
			confirmation.setType(AlertType.SUCCESS);
		else
			confirmation.setType(AlertType.ERROR);

		// add to the panel
		summaryTab.add(confirmation);

		// remove after a while
		Timer t = new Timer() {

			@Override
			public void run() {

				summaryTab.remove(confirmation);

			}
		};

		// remove after two seconds
		t.schedule(2000);

	}

}
