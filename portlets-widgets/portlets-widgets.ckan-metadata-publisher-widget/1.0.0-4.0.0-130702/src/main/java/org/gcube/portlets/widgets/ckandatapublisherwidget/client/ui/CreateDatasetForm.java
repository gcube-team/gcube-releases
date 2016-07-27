package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherServiceAsync;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteCustomFieldEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteCustomFieldEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.GcubeDialogExtended;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.InfoIconsLabels;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Create metadata form for ckan dataset.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateDatasetForm extends Composite{

	/**
	 * Create a remote service proxy to talk to the server-side ckan service.
	 */
	private final CKanPublisherServiceAsync ckanServices = GWT.create(CKanPublisherService.class);

	private static EditMetadataFormUiBinder uiBinder = GWT
			.create(EditMetadataFormUiBinder.class);

	interface EditMetadataFormUiBinder extends
	UiBinder<Widget, CreateDatasetForm> {
	}

	@UiField HTMLPanel createDatasetMainPanel;
	@UiField TextBox titleTextBox;
	@UiField TextArea descriptionTextarea;
	@UiField TextBox tagsEnterTextBox;
	@UiField FlowPanel tagsPanel;
	@UiField ListBox licenseListbox;
	@UiField ListBox visibilityListbox;
	@UiField ListBox organizationsListbox;
	@UiField TextBox versionTextbox;
	@UiField TextBox authorTextbox;
	@UiField TextBox authorEmailTextbox;
	@UiField TextBox maintainerTextbox;
	@UiField TextBox maintainerEmailTextbox;
	@UiField ControlGroup customFields;
	@UiField Button addCustomFieldButton;
	@UiField Button createButton;
	@UiField Button resetButton;
	@UiField AlertBlock infoBlock;
	@UiField AlertBlock alertNoResources;
	@UiField AlertBlock onContinueAlertBlock;
	@UiField AlertBlock onCreateAlertBlock;
	@UiField VerticalPanel metadataFieldsPanel;
	@UiField ListBox metadataProfilesFormatListbox;
	@UiField Form formFirstStep;
	@UiField Form formSecondStep;
	@UiField Form formThirdStep;
	@UiField Button continueButton;
	@UiField Button goBackButtonFirstOrSecondStep;
	@UiField Paragraph selectedProfile;
	@UiField Button goToDatasetButton;
	@UiField Button addResourcesButton;
	@UiField CheckBox addResourcesCheckBox;
	@UiField ControlGroup resourcesControlGroup;
	@UiField SimplePanel workspaceResourcesContainer;
	@UiField Button continueThirdStep;
	@UiField Button goBackButtonFirstStep;
	@UiField Anchor licenseUrlAnchor;
	@UiField Paragraph unavailableUrl;

	// info panels
	@UiField Icon infoIconTags;
	@UiField FocusPanel focusPanelTags;
	@UiField Popover popoverTags;
	@UiField Icon infoIconLicenses;
	@UiField FocusPanel focusPanelLicenses;
	@UiField Popover popoverLicenses;
	@UiField Icon infoIconVisibility;
	@UiField FocusPanel focusPanelVisibility;
	@UiField Popover popoverVisibility;
	@UiField Icon infoIconAuthor;
	@UiField FocusPanel focusPanelAuthor;
	@UiField Popover popoverAuthor;
	@UiField Icon infoIconMaintainerEmail;
	@UiField FocusPanel focusPanelMaintainerEmail;
	@UiField Popover popoverMaintainerEmail;
	@UiField Icon infoIconAuthorEmail;
	@UiField FocusPanel focusPanelAuthorEmail;
	@UiField Popover popoverAuthorEmail;
	@UiField Icon infoIconProfiles;
	@UiField FocusPanel focusPanelProfiles;
	@UiField Popover popoverProfiles;
	@UiField Icon infoIconMaintainer;
	@UiField FocusPanel focusPanelMaintainer;
	@UiField Popover popoverMaintainer;
	@UiField Icon infoIconCustomFields;
	@UiField FocusPanel focusPanelCustomFields;
	@UiField Popover popoverCustomFields;
	@UiField Icon infoIconResources;
	@UiField FocusPanel focusPanelResources;
	@UiField Popover popoverResources;
	@UiField ControlGroup metadataProfilesControlGroup;

	// error message
	protected static final String ERROR_PRODUCT_CREATION = "There was an error while trying to publish your product, sorry.. Retry later";

	// tab panel 
	private TabPanel tabPanel; 

	// add resource form
	private AddResourceToDataset resourceForm;

	// tags list
	private List<String> tagsList = new ArrayList<String>();

	// the licenses
	private LicensesBean licenseBean;

	// event bus
	private HandlerManager eventBus;

	// added custom field entries 
	private List<CustomFieldEntry> customFieldEntriesList = new ArrayList<CustomFieldEntry>();

	// dataset metadata bean
	private DatasetMetadataBean receivedBean;

	// the owner
	private String owner;

	// workspace request?
	private boolean isWorkspaceRequest = false;

	// the list of MetaDataFieldSkeleton added
	private List<MetaDataFieldSkeleton> listOfMetadataFields = new ArrayList<MetaDataFieldSkeleton>();

	// resource table
	private ResourcesTable resourcesTable;

	// List of opened popup'ids
	private List<String> popupOpenedIds = new ArrayList<String>();

	// map of organization name title
	private Map<String, String> nameTitleOrganizationMap = new HashMap<String, String>();

	/**
	 * Invoked in the most general case
	 * @param owner
	 */
	public CreateDatasetForm(String owner, HandlerManager eventBus) {

		createDatasetFormBody(false, null, owner, eventBus);

	}

	/**
	 * Invoked when the workspace is used.
	 * @param idFolderWorkspace
	 * @param owner
	 */
	public CreateDatasetForm(String idFolderWorkspace, String owner, HandlerManager eventBus) {

		createDatasetFormBody(true, idFolderWorkspace, owner, eventBus);

	}


	/**
	 * The real constructor
	 * @param isWorkspaceRequest
	 * @param idFolderWorkspace
	 * @param owner
	 * @param eventBus
	 */
	private void createDatasetFormBody(final boolean isWorkspaceRequest, String idFolderWorkspace, String owner, final HandlerManager eventBus){

		initWidget(uiBinder.createAndBindUi(this));

		this.owner = owner;

		// save event bus
		this.eventBus = eventBus;

		// workspace request
		this.isWorkspaceRequest = isWorkspaceRequest;

		// bind on events
		bind();

		// prepare info icons
		prepareInfoIcons();

		// disable continue button
		continueButton.setEnabled(false);
		resetButton.setEnabled(false);

		// set info block
		setAlertBlock("Retrieving information, please wait...", AlertType.INFO, true);

		// get back the licenses and the metadata information
		ckanServices.getDatasetBean(idFolderWorkspace, owner, new AsyncCallback<DatasetMetadataBean>() {

			@Override
			public void onFailure(Throwable caught) {

				setAlertBlock("Error while retrieving information, try to refresh the page", AlertType.ERROR, true);

			}

			@Override
			public void onSuccess(final DatasetMetadataBean bean) {

				if(bean == null){

					setAlertBlock("Error while retrieving information, try to refresh the page", AlertType.ERROR, true);
				}
				else{

					// save it
					receivedBean = bean;

					// fill the form
					titleTextBox.setText(bean.getTitle());
					descriptionTextarea.setText(bean.getDescription());
					versionTextbox.setText(String.valueOf(bean.getVersion()));
					authorTextbox.setText(bean.getAuthorSurname() + " " + bean.getAuthorName());
					authorEmailTextbox.setText(bean.getAuthorEmail());
					maintainerTextbox.setText(bean.getAuthorSurname() + " " + bean.getAuthorName());
					maintainerEmailTextbox.setText(bean.getMaintainerEmail());

					// retrieve custom fields
					Map<String, String> customFieldsMap = bean.getCustomFields();	

					if(customFieldsMap != null){

						// get the keys and put them as tags
						Iterator<Entry<String, String>> iteratorOverCustomField = customFieldsMap.entrySet().iterator();

						while (iteratorOverCustomField.hasNext()) {
							Map.Entry<String, String> entry = (Map.Entry<String, String>) iteratorOverCustomField
									.next();

							// these are fixed key, variable value custom fields
							CustomFieldEntry toAdd = new CustomFieldEntry(eventBus, entry.getKey(), entry.getValue(), false);
							customFieldEntriesList.add(toAdd);
							customFields.add(toAdd);

							// add as tag
							addTagElement(entry.getKey());

						}
					}

					if(isWorkspaceRequest){

						// enable manage resources checkbox
						resourcesControlGroup.setVisible(true);
						addResourcesCheckBox.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
						resourcesTable = new ResourcesTable(bean.getResources());

						// if there are not resources, for now just checked it ( and hide so that the step will be skipped) TODO
						if(bean.getResources() == null || bean.getResources().isEmpty()){

							resourcesControlGroup.setVisible(false);
							alertNoResources.setType(AlertType.WARNING);
							alertNoResources.setVisible(true);
						}

					}

					// set organizations
					List<OrganizationBean> organizations = bean.getOrganizationList();

					for (OrganizationBean organization : organizations) {
						organizationsListbox.addItem(organization.getTitle());
						nameTitleOrganizationMap.put(organization.getTitle(), organization.getName());
					}

					// force the selection of the first one, and retrieve the list of profiles 
					organizationsListbox.setSelectedIndex(0);

					// add change handler to dinamycally retrieve the list of profiles
					organizationsListbox.addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							event.preventDefault();
							organizationsListboxChangeHandlerBody();

						}
					});

					// try to retrieve the profiles
					setAlertBlock("Retrieving profiles, please wait...", AlertType.INFO, true);

					// get the name of the organization from the title
					String orgName = nameTitleOrganizationMap.get(organizationsListbox.getSelectedItemText());

					// perform remote request of profiles for the selected organization
					ckanServices.getProfiles(orgName, new AsyncCallback<List<MetaDataProfileBean>>() {

						@Override
						public void onFailure(Throwable caught) {

							setAlertBlock("Error while retrieving profiles, try to refresh the page", AlertType.ERROR, true);

						}

						@Override
						public void onSuccess(List<MetaDataProfileBean> result) {

							if(result == null){

								setAlertBlock("Error while retrieving profiles, try to refresh the page", AlertType.ERROR, true);

							}
							else{

								receivedBean.setMetadataList(result);
								prepareMetadataList(receivedBean);
								organizationsListbox.setEnabled(true);
								metadataProfilesFormatListbox.setEnabled(true);

								// try to retrieve the licenses
								setAlertBlock("Retrieving licenses, please wait...", AlertType.INFO, true);
								ckanServices.getLicenses(new AsyncCallback<LicensesBean>() {

									@Override
									public void onFailure(Throwable caught){

										setAlertBlock("Error while retrieving licenses, try to refresh the page", AlertType.ERROR, true);

									}

									@Override
									public void onSuccess(LicensesBean lBean) {

										if(lBean != null && !lBean.getLicenseTitles().isEmpty()){

											licenseBean = lBean;

											// sort the list
											List<String> listOfNames = new ArrayList<String>();
											Collections.copy(listOfNames, licenseBean.getLicenseTitles());
											Collections.sort(listOfNames);

											// fill the listbox
											for(int i = 0; i < listOfNames.size(); i++){
												licenseListbox.addItem(listOfNames.get(i));
											}

											// set the url of the license, if any
											showLicenseUrl();

											// everything went ok
											setAlertBlock("", AlertType.ERROR, false);
											continueButton.setEnabled(true);
											resetButton.setEnabled(true);

										}else{

											setAlertBlock("Error while retrieving licenses, try to refresh the page", AlertType.ERROR, true);

										}
									}
								});
							}
						}
					});
				}
			}
		});

	}

	/**
	 * When the organization name is changed we need to retrieve the list of profiles
	 */
	private void organizationsListboxChangeHandlerBody() {

		// remove any other product profiles
		int presentItems = metadataProfilesFormatListbox.getItemCount();
		for (int i = presentItems - 1; i >= 0; i--) {
			metadataProfilesFormatListbox.removeItem(i);
		}

		// add "none" item again
		metadataProfilesFormatListbox.addItem("none");

		// select "none"
		metadataProfilesFormatListbox.setSelectedIndex(0);

		// get the name of the organization from the title
		String selectedOrganizationTitle = organizationsListbox.getSelectedItemText();	
		String orgName = nameTitleOrganizationMap.get(selectedOrganizationTitle);

		// try to retrieve the profiles
		setAlertBlock("Retrieving profiles, please wait...", AlertType.INFO, true);

		// disable the list of organizations name so that the user doesn't change it again
		organizationsListbox.setEnabled(false);
		metadataProfilesFormatListbox.setEnabled(false);

		// perform remote request of profiles for the selected organization
		ckanServices.getProfiles(orgName, new AsyncCallback<List<MetaDataProfileBean>>() {

			@Override
			public void onSuccess(List<MetaDataProfileBean> result) {

				if(result != null){

					receivedBean.setMetadataList(result);
					prepareMetadataList(receivedBean);
					organizationsListbox.setEnabled(true);
					metadataProfilesFormatListbox.setEnabled(true);

					// everything went ok
					setAlertBlock("", AlertType.ERROR, false);

				}else
					setAlertBlock("Error while retrieving profiles, sorry", AlertType.ERROR, true);

			}

			@Override
			public void onFailure(Throwable caught) {

				setAlertBlock("Error while retrieving profiles, sorry", AlertType.ERROR, true);

			}
		});

	}

	/**
	 * Add the items to the listbox and put data into the metadataPanel
	 * @param receivedBean
	 */
	private void prepareMetadataList(DatasetMetadataBean receivedBean) {

		List<MetaDataProfileBean> beans = receivedBean.getMetadataList();

		if(beans != null && !beans.isEmpty()){
			for(MetaDataProfileBean metadataBean: beans){

				metadataProfilesFormatListbox.addItem(metadataBean.getType().getName());		

				// add handler on select
				metadataProfilesFormatListbox.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {

						String selectedItem = metadataProfilesFormatListbox.getSelectedItemText();

						if(selectedItem.equals("none")){
							// hide the panel
							metadataFieldsPanel.clear();
							metadataFieldsPanel.setVisible(false);
						}else{

							metadataFieldsPanel.clear();
							addFields(selectedItem);
						}
					}
				});
			}
		}else{ 
			// just hide this listbox
			metadataProfilesControlGroup.setVisible(false);
		}
	}

	protected void addFields(String selectedItem) {

		for(MetaDataProfileBean bean: receivedBean.getMetadataList()){

			if(bean.getType().getName().equals(selectedItem)){

				// prepare the data
				List<MetadataFieldWrapper> fields = bean.getMetadataFields();

				// clear old data
				listOfMetadataFields.clear();

				for (MetadataFieldWrapper field : fields) {
					MetaDataFieldSkeleton fieldWidget = new MetaDataFieldSkeleton(field, eventBus);
					metadataFieldsPanel.add(fieldWidget);
					listOfMetadataFields.add(fieldWidget);
				}

				metadataFieldsPanel.setVisible(true);		
			}
		}
	}

	/**
	 * Bind on events
	 */
	private void bind() {

		// when a custom field is removed, remove it from the list
		eventBus.addHandler(DeleteCustomFieldEvent.TYPE, new DeleteCustomFieldEventHandler() {

			@Override
			public void onRemoveEntry(DeleteCustomFieldEvent event) {

				customFieldEntriesList.remove(event.getRemovedEntry());
				customFields.remove(event.getRemovedEntry());

			}
		});

		// on close form
		eventBus.addHandler(CloseCreationFormEvent.TYPE, new CloseCreationFormEventHandler() {

			@Override
			public void onClose(CloseCreationFormEvent event) {

				closeDialogBox();

			}

		});

	}

	@UiHandler("addCustomFieldButton")
	void addCustomFieldEvent(ClickEvent e){

		CustomFieldEntry toAdd = new CustomFieldEntry(eventBus, "", "", true);
		customFieldEntriesList.add(toAdd);
		customFields.add(toAdd);

	}

	@UiHandler("continueButton")
	void onContinueButton(ClickEvent e){

		// validate data
		String errorMsg = validateDataOnContinue();

		if(errorMsg == null){
			// check what to do 
			if(isWorkspaceRequest){
				if(!addResourcesCheckBox.getValue()){

					// we need to show the page to handle resources one by one from the workspace
					formFirstStep.setVisible(false);
					formSecondStep.setVisible(true);
					formThirdStep.setVisible(false);

					// add the resources to the container panel
					if(workspaceResourcesContainer.getWidget() == null)
						workspaceResourcesContainer.add(resourcesTable);

				}else{

					// resources will be added automatically and we can show the  page of the profiles
					formFirstStep.setVisible(false);
					formThirdStep.setVisible(true);
				}
			}else{

				// this is not a workspace request
				formFirstStep.setVisible(false);
				formThirdStep.setVisible(true);

			}

			if(metadataProfilesFormatListbox.getSelectedItemText().equals("none"))
				selectedProfile.setText("");
			else
				selectedProfile.setText("Selected Profile is " + metadataProfilesFormatListbox.getSelectedItemText());

		}else{
			alertOnContinue("Please check inserted data [" + errorMsg + "]", AlertType.ERROR);
		}

	}


	@UiHandler("goBackButtonFirstStep")
	void onGoBackButtonFirstStep(ClickEvent e){

		// swap forms
		formFirstStep.setVisible(true);
		formSecondStep.setVisible(false);
		formThirdStep.setVisible(false);

	}


	@UiHandler("goBackButtonFirstOrSecondStep")
	void onGoBackButton(ClickEvent e){

		// swap forms
		if(isWorkspaceRequest && !addResourcesCheckBox.getValue()){
			formFirstStep.setVisible(false);
			formSecondStep.setVisible(true);
		}else{
			formFirstStep.setVisible(true);
			formSecondStep.setVisible(false);
		}
		formThirdStep.setVisible(false);

	}

	@UiHandler("continueThirdStep")
	void onContinueThirdStep(ClickEvent e){

		// swap forms
		formSecondStep.setVisible(false);
		formThirdStep.setVisible(true);

	}


	@UiHandler("createButton")
	void createDatasetEvent(ClickEvent e){

		String errorMessage = areProfileDataValid();

		if(errorMessage != null){

			alertOnCreate("Please check the inserted values and the mandatory fields [" + errorMessage +"]", AlertType.ERROR);

		}
		else{

			String title = titleTextBox.getValue();
			String description = descriptionTextarea.getText();
			String selectedLicense = licenseListbox.getSelectedItemText();
			String visibility = visibilityListbox.getSelectedItemText();
			long version = Long.valueOf(versionTextbox.getValue());
			String author = authorTextbox.getValue();
			String authorEmail = authorEmailTextbox.getValue();
			String maintainer = maintainerTextbox.getValue();
			String maintainerEmail = maintainerEmailTextbox.getValue();
			String chosenOrganizationTitle = organizationsListbox.getSelectedItemText();

			//we need to retrieve the organization's name from this title
			List<OrganizationBean> orgs = receivedBean.getOrganizationList();
			String chosenOrganization = null;
			for (OrganizationBean organizationBean : orgs) {
				if(chosenOrganizationTitle.equals(organizationBean.getTitle())){
					chosenOrganization = organizationBean.getName();
					break;
				}
			}

			// fill the bean
			receivedBean.setAuthorFullName(author);
			receivedBean.setAuthorEmail(authorEmail);
			receivedBean.setDescription(description);
			receivedBean.setLicense(selectedLicense);
			receivedBean.setMaintainer(maintainer);
			receivedBean.setMaintainerEmail(maintainerEmail);
			receivedBean.setVersion(version);
			receivedBean.setVisibility(visibility.equals("Public"));
			receivedBean.setTitle(title);
			receivedBean.setTags(tagsList);
			receivedBean.setSelectedOrganization(chosenOrganization);

			Map<String, String> customFieldsMap = new HashMap<String, String>();

			// prepare custom fields
			for (MetaDataFieldSkeleton field : listOfMetadataFields) {

				customFieldsMap.put(field.getFieldName(), field.getFieldCurrentValue());

			}

			for(CustomFieldEntry customEntry : customFieldEntriesList){

				String key = customEntry.getKey();
				String value = customEntry.getValue();
				customFieldsMap.put(key, value);

			}

			receivedBean.setCustomFields(customFieldsMap);

			// alert 
			alertOnCreate("Trying to create product, please wait", AlertType.INFO);

			// invoke the create method
			createButton.setEnabled(false);
			goBackButtonFirstOrSecondStep.setEnabled(false);

			ckanServices.createCKanDataset(receivedBean, isWorkspaceRequest, new AsyncCallback<DatasetMetadataBean>() {

				@Override
				public void onSuccess(final DatasetMetadataBean createdDatasetBean) {

					if(createdDatasetBean != null){

						alertOnCreate("Product correctly created!", AlertType.SUCCESS);

						// disable dataset fields
						disableDatasetFields();

						// disable reset
						resetButton.setEnabled(false);

						// show the go to dataset button
						final String datasetUrl = createdDatasetBean.getSource();
						goToDatasetButton.setVisible(true);
						goToDatasetButton.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign(datasetUrl);
							}
						});

						// if we are in the "general case" we need to show a form for adding resources
						if(isWorkspaceRequest)
						{

							// leave to back button, but remove create and add go to dataset
							createButton.removeFromParent();

							// set go to dataset as primary
							goToDatasetButton.setType(ButtonType.PRIMARY);

						}else{

							// remove create button
							createButton.removeFromParent();

							// show the add resources button
							addResourcesButton.setVisible(true);

							addResourcesButton.addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {

									// remove content of the main panel
									createDatasetMainPanel.clear();

									// TabPanel
									tabPanel = new TabPanel(Tabs.ABOVE);
									tabPanel.setWidth("100%");

									// add the form
									resourceForm = new AddResourceToDataset(eventBus, createdDatasetBean.getId(), createdDatasetBean.getSelectedOrganization(), owner, datasetUrl);

									// tab for the form
									Tab formContainer = new Tab();
									formContainer.add(resourceForm);
									formContainer.setHeading("Add New Resource");
									tabPanel.add(formContainer);

									// tab for the added resources
									Tab addedResources = new Tab();
									addedResources.add(new AddedResourcesSummary(eventBus, owner));
									addedResources.setHeading("Added Resource");
									tabPanel.add(addedResources);

									// add tabs to resources panel
									tabPanel.selectTab(0);


									// form container
									AddResourceContainer container = new AddResourceContainer(datasetUrl);
									container.add(tabPanel);

									// add the new content of the main panel
									createDatasetMainPanel.add(container);
								}
							});	
						}

					}else{
						alertOnCreate(ERROR_PRODUCT_CREATION, AlertType.ERROR);
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					alertOnCreate(ERROR_PRODUCT_CREATION, AlertType.ERROR);
				}
			});
		}
	}

	/**
	 * Prepare the info icons of all core metadata info
	 */
	private void prepareInfoIcons() {

		// tags
		preparePopupPanelAndPopover(
				InfoIconsLabels.TAGS_INFO_ID_POPUP,
				InfoIconsLabels.TAGS_INFO_TEXT,
				InfoIconsLabels.TAGS_INFO_CAPTION,
				infoIconTags,
				popoverTags,
				focusPanelTags
				);

		// licenses
		preparePopupPanelAndPopover(
				InfoIconsLabels.LICENSES_INFO_ID_POPUP,
				InfoIconsLabels.LICENSES_INFO_TEXT,
				InfoIconsLabels.LICENSES_INFO_CAPTION,
				infoIconLicenses,
				popoverLicenses,
				focusPanelLicenses
				);

		// visibility
		preparePopupPanelAndPopover(
				InfoIconsLabels.VISIBILITY_INFO_ID_POPUP,
				InfoIconsLabels.VISIBILITY_INFO_TEXT,
				InfoIconsLabels.VISIBILITY_INFO_CAPTION,
				infoIconVisibility,
				popoverVisibility,
				focusPanelVisibility
				);

		// author
		preparePopupPanelAndPopover(
				InfoIconsLabels.AUTHOR_INFO_ID_POPUP,
				InfoIconsLabels.AUTHOR_INFO_TEXT,
				InfoIconsLabels.AUTHOR_INFO_CAPTION,
				infoIconAuthor,
				popoverAuthor,
				focusPanelAuthor
				);

		// author's email
		preparePopupPanelAndPopover(
				InfoIconsLabels.AUTHOR_EMAIL_INFO_ID_POPUP,
				InfoIconsLabels.AUTHOR_EMAIL_INFO_TEXT,
				InfoIconsLabels.AUTHOR_EMAIL_INFO_CAPTION,
				infoIconAuthorEmail,
				popoverAuthorEmail,
				focusPanelAuthorEmail
				);

		// maintainer
		preparePopupPanelAndPopover(
				InfoIconsLabels.MAINTAINER_INFO_ID_POPUP,
				InfoIconsLabels.MAINTAINER_INFO_TEXT,
				InfoIconsLabels.MAINTAINER_INFO_CAPTION,
				infoIconMaintainer,
				popoverMaintainer,
				focusPanelMaintainer
				);

		// maintainer's email
		preparePopupPanelAndPopover(
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_ID_POPUP,
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_TEXT,
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_CAPTION,
				infoIconMaintainerEmail,
				popoverMaintainerEmail,
				focusPanelMaintainerEmail
				);

		// profiles
		preparePopupPanelAndPopover(
				InfoIconsLabels.PROFILES_INFO_ID_POPUP,
				InfoIconsLabels.PROFILES_INFO_TEXT,
				InfoIconsLabels.PROFILES_INFO_CAPTION,
				infoIconProfiles,
				popoverProfiles,
				focusPanelProfiles
				);

		// custom fields
		preparePopupPanelAndPopover(
				InfoIconsLabels.CUSTOM_FIELDS_INFO_ID_POPUP,
				InfoIconsLabels.CUSTOM_FIELDS_INFO_TEXT,
				InfoIconsLabels.CUSTOM_FIELDS_INFO_CAPTION,
				infoIconCustomFields,
				popoverCustomFields,
				focusPanelCustomFields
				);

		// resources field
		preparePopupPanelAndPopover(
				InfoIconsLabels.RESOURCES_INFO_ID_POPUP,
				InfoIconsLabels.RESOURCES_INFO_TEXT,
				InfoIconsLabels.RESOURCES_INFO_CAPTION,
				infoIconResources,
				popoverResources,
				focusPanelResources
				);

	}

	/**
	 * Test if profile data are valid
	 * @return
	 */
	private String areProfileDataValid() {

		for (MetaDataFieldSkeleton field : listOfMetadataFields) {

			String error = field.isFieldValueValid();
			if(error != null){
				return field.getFieldName() + " is not valid. Suggestion: " + error;
			}
		}

		return null;
	}

	/**
	 * On continue show alert box and enable buttons
	 * @param text
	 * @param type
	 */
	private void alertOnContinue(String text, AlertType type){

		onContinueAlertBlock.setText(text);
		onContinueAlertBlock.setType(type);
		onContinueAlertBlock.setVisible(true);
		continueButton.setEnabled(true);
		resetButton.setEnabled(true);

		// hide after some seconds
		Timer t = new Timer() {

			@Override
			public void run() {

				onContinueAlertBlock.setVisible(false);

			}
		};

		t.schedule(4000);
	}

	/**
	 * On continue show alert box and enable buttons
	 * @param text
	 * @param type
	 */
	private void alertOnCreate(String text, AlertType type){

		onCreateAlertBlock.setText(text);
		onCreateAlertBlock.setType(type);
		onCreateAlertBlock.setVisible(true);
		createButton.setEnabled(true);
		goBackButtonFirstOrSecondStep.setEnabled(true);

		// hide after some seconds
		Timer t = new Timer() {

			@Override
			public void run() {

				onCreateAlertBlock.setVisible(false);

			}
		};

		t.schedule(10000);
	}

	/**
	 * Validate data
	 * @return true on success, false otherwise
	 */
	private String validateDataOnContinue() {

		String errorMessage = null;

		if(titleTextBox.getText().isEmpty()){
			errorMessage = "Missing title";
			return errorMessage;
		}

		// better check for the title
		String regexTitleSubWord = "^[a-zA-Z0-9_]+$";
		String[] splittedTitle = titleTextBox.getText().split(" ");

		for (String word : splittedTitle) {

			if(!word.matches(regexTitleSubWord))
				return "Please note that only alphanumeric characters are allowed for the title";

		}

		// email reg expression
		String regexMail = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";
		if(!validateByRegExpression(maintainerEmailTextbox.getText(), regexMail)){
			errorMessage = "Not valid maintainer email";
			return errorMessage;
		}

		// check if version is a number
		try{
			Integer.valueOf(versionTextbox.getText());
		}catch(Exception e){
			return errorMessage = "Version must be a natural number";
		}

		// check if metadata profile is different from none and its mandatory fields have been fulfilled
		if(checkSelectedMetaDataProfile()){
			errorMessage = "You must select a metadata profile different frome none";
		}

		if(organizationsListbox.getSelectedItemText() == null){
			errorMessage = "You must select an organization in which you want to publish";
		}

		return errorMessage;
	}

	/**
	 * Checks if a metadata profile has been chosen and its fields have been fulfilled
	 * @return
	 */
	private boolean checkSelectedMetaDataProfile() {
		return metadataProfilesFormatListbox.getSelectedItemText().equals("none") && (metadataProfilesFormatListbox.getItemCount() != 1);
	}

	/**
	 * Validate a text against a regular expression.
	 * @param textToValidate
	 * @param regex
	 * @return
	 */
	private boolean validateByRegExpression(String textToValidate, String regex){
		return textToValidate.matches(regex);
	}

	@UiHandler("resetButton")
	void resetFormEvent(ClickEvent e){

		// reset main fields
		titleTextBox.setText("");
		descriptionTextarea.setText("");
		versionTextbox.setText("");
		maintainerTextbox.setText("");
		maintainerEmailTextbox.setText("");
		removeTags();

		// delete custom fields
		for (CustomFieldEntry customField : customFieldEntriesList) {
			customField.removeFromParent();
		}
		customFieldEntriesList.clear();

	}

	/**
	 * Disable dataset editable fields once the dataset has been
	 * Successfully created.
	 */
	protected void disableDatasetFields() {

		titleTextBox.setEnabled(false);
		descriptionTextarea.setEnabled(false);
		versionTextbox.setEnabled(false);
		maintainerTextbox.setEnabled(false);
		maintainerEmailTextbox.setEnabled(false);
		visibilityListbox.setEnabled(false);
		tagsEnterTextBox.setEnabled(false);
		licenseListbox.setEnabled(false);
		organizationsListbox.setEnabled(false);
		addCustomFieldButton.setEnabled(false);
		addResourcesCheckBox.setEnabled(false);

		//	freeze tags
		for(int i = 0; i < tagsList.size(); i++){

			// get tag widget
			ListItem tagWidget = (ListItem)tagsPanel.getWidget(i);

			// get the "x" span
			tagWidget.getWidget(1).removeFromParent();

		}

		// disable profile fields
		for (MetaDataFieldSkeleton field : listOfMetadataFields) {

			field.freeze();

		}

		// freeze table of resources
		if(resourcesTable != null)
			resourcesTable.freezeTable();
	}

	/**
	 * change alert block behavior.
	 * @param textToShow
	 * @param type
	 * @param visible
	 */
	private void setAlertBlock(String textToShow, AlertType type, boolean visible){

		infoBlock.setText(textToShow);
		infoBlock.setType(type);
		infoBlock.setVisible(visible);

	}

	@UiHandler("tagsEnterTextBox")
	void onAddTag(KeyDownEvent event){

		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if (!"".equals(tagsEnterTextBox.getValue().trim())) {

				addTagElement(tagsEnterTextBox);

			}
		}
	}

	@UiHandler("addResourcesCheckBox")
	void onAddResourcesCheckboxPress(ClickEvent e){

		// we need to set all resources to be added if checkbox value is true
		if(addResourcesCheckBox.getValue()){

			GWT.log("Set all resources to be add");
			resourcesTable.checkAllResources(true);

		}

	}

	@UiHandler("licenseListbox")
	void onSelectedLicenseChange(ChangeEvent c){

		showLicenseUrl();
	}

	/**
	 * The body of the onSelectedLicenseChange
	 */
	private void showLicenseUrl(){

		List<String> titles = licenseBean.getLicenseTitles();
		String selectedLicense = licenseListbox.getSelectedItemText();
		GWT.log("Selected license is " + selectedLicense);
		for (int i = 0; i < titles.size(); i++) {
			if(selectedLicense.equals(titles.get(i))){

				if(licenseBean.getLicenseUrls().get(i).isEmpty())
					break;

				GWT.log("URL is " + licenseBean.getLicenseUrls().get(i));

				licenseUrlAnchor.setText(licenseBean.getLicenseUrls().get(i));
				licenseUrlAnchor.setHref(licenseBean.getLicenseUrls().get(i));
				licenseUrlAnchor.setVisible(true);
				unavailableUrl.setVisible(false);
				return;
			}
		}
		licenseUrlAnchor.setVisible(false);
		unavailableUrl.setVisible(true);

	}

	/**
	 * Add the tag as an element (inserted by the user)
	 */
	private void addTagElement(TextBox itemBox){

		if (itemBox.getValue() != null && !"".equals(itemBox.getValue().trim())) {

			if(tagsList.contains(itemBox.getValue())){
				itemBox.setValue("");
				return;
			}

			// ckan accepts only alphanumeric values
			String[] subTags = itemBox.getValue().split(" ");
			if(subTags.length == 1){
				if(!subTags[0].matches("^[a-zA-Z0-9]*$"))
					return;
				if(subTags[0].length() <= 1)
					return;
			}else{
				for (int i = 0; i < subTags.length; i++) {
					String subTag = subTags[i];
					if(!subTag.matches("^[a-zA-Z0-9]*$"))
						return;
				}
			}

			final String value = itemBox.getValue();
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("tag-style");
			Span tagText = new Span(itemBox.getValue());

			Span tagRemove = new Span("x");
			tagRemove.setTitle("Remove this tag");
			tagRemove.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeTag(displayItem, value);
				}
			});

			tagRemove.setStyleName("tag-style-x");
			displayItem.add(tagText);
			displayItem.add(tagRemove);
			itemBox.setValue("");
			itemBox.setFocus(true);
			tagsPanel.add(displayItem);
			tagsList.add(value);
		}
	}

	/**
	 * Add the tag as an element (when publishing from workspace)
	 */
	private void addTagElement(final String tag){

		if(tagsList.contains(tag))
			return;

		// ckan accepts only alphanumeric values
		String[] subTags = tag.split(" ");
		if(subTags.length == 1){
			if(!subTags[0].matches("^[a-zA-Z0-9]*$"))
				return;
			if(subTags[0].length() <= 1)
				return;
		}else{
			for (int i = 0; i < subTags.length; i++) {
				String subTag = subTags[i];
				if(!subTag.matches("^[a-zA-Z0-9]*$"))
					return;
			}
		}

		final ListItem displayItem = new ListItem();
		displayItem.setStyleName("tag-style");
		Span p = new Span(tag);

		Span span = new Span("x");
		span.setTitle("Remove this tag");
		span.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent clickEvent) {
				removeTag(displayItem, tag);
			}
		});

		span.setStyleName("tag-style-x");
		displayItem.add(p);
		displayItem.add(span);
		tagsPanel.add(displayItem);
		tagsList.add(tag);
	}

	/**
	 * Remove a tag from the list
	 * @param displayItem
	 */
	private void removeTag(ListItem displayItem, String value) {

		tagsList.remove(value);
		tagsPanel.remove(displayItem);

	}

	/**
	 * Remove all inserted tags
	 */
	private void removeTags(){

		tagsList.clear();
		tagsPanel.clear();

	}

	/**
	 * Close any dialog box opened
	 */
	private void closeDialogBox() {

		for (String popupid : popupOpenedIds) {
			GcubeDialogExtended popup = null;
			try{
				Element element = DOM.getElementById(popupid);
				popup = (GcubeDialogExtended) Widget.asWidgetOrNull(getWidget(element));
				popup.hide();
			}catch(Exception e){
				GWT.log("ERROR", e);
			}	
		}
	}

	/**
	 * Prepare the popover and the gcube popup panel for information.
	 * @param text
	 * @param captionText
	 * @param iconElement
	 * @param popover
	 * @param focusPanel
	 */
	private void preparePopupPanelAndPopover(
			final String popupId, 
			final String text, 
			final String captionText, 
			Icon iconElement, 
			Popover popover, 
			FocusPanel focusPanel){

		// prepare the popover
		popover.setText(new HTML("<p style='color:initial'>" + text +"</p>").getHTML());
		popover.setHeading(new HTML("<b>" + captionText +"</b>").getHTML());

		// set icon cursor
		iconElement.getElement().getStyle().setCursor(Cursor.HELP);

		// prepare the gcube dialog
		focusPanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// Retrieve elemnt that should have this id
				GcubeDialogExtended popup = null;
				try{
					Element element = DOM.getElementById(popupId);
					popup = (GcubeDialogExtended) Widget.asWidgetOrNull(getWidget(element));
				}catch(Exception e){
					GWT.log("ERROR", e);
				}

				// if it doesn't exist, create it
				if(popup == null){

					popup = new GcubeDialogExtended(captionText, text);
					popup.getElement().setId(popupId);
					popup.setModal(false);

					// add its id
					popupOpenedIds.add(popupId);

				}

				// then center and show
				popup.center();
				popup.show();

			}
		});

	}

	/**
	 * Check if an element of such type is actually a widget
	 * @param element
	 * @return
	 */
	public static IsWidget getWidget(Element element) {
		EventListener listener = DOM
				.getEventListener(element);
		// No listener attached to the element, so no widget exist for this
		// element
		if (listener == null) {
			GWT.log("Widget is NULL");
			return null;
		}
		if (listener instanceof Widget) {
			// GWT uses the widget as event listener
			GWT.log("Widget is " + listener);
			return (Widget) listener;
		}
		return null;
	}
}
