package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherServiceAsync;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteCustomFieldEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteCustomFieldEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection.TwinColumnSelectionMainPanel;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources.AddResourceContainer;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources.AddResourceToDataset;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources.AddedResourcesSummary;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources.TagsPanel;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.InfoIconsLabels;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.GroupBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
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
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Create metadata form for ckan product.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateDatasetForm extends Composite{

	private static EditMetadataFormUiBinder uiBinder = GWT
			.create(EditMetadataFormUiBinder.class);

	interface EditMetadataFormUiBinder extends
	UiBinder<Widget, CreateDatasetForm> {
	}

	@UiField HTMLPanel createDatasetMainPanel;
	@UiField TextBox titleTextBox;
	@UiField TextArea descriptionTextarea;
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
	@UiField ListBox metadataTypeListbox;
	@UiField Form formFirstStep;
	@UiField Form formSecondStep;
	@UiField Form formThirdStep;
	@UiField Button continueButton;
	@UiField Button goBackButtonSecondStep;
	@UiField Paragraph selectedProfile;
	@UiField Button goToDatasetButton;
	@UiField FlowPanel goToDatasetButtonPanel;
	@UiField Button addResourcesButton;
	@UiField SimplePanel workspaceResourcesContainer;
	@UiField Button continueThirdStep;
	@UiField Button goBackButtonFirstStep;
	@UiField Anchor licenseUrlAnchor;
	@UiField Paragraph unavailableUrl;
	@UiField TagsPanel tagsPanel;
	@UiField ListBox groupsListbox;

	// info panels
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
	@UiField Icon infoIconTypes;
	@UiField FocusPanel focusPanelTypes;
	@UiField Popover popoverTypes;
	@UiField Icon infoIconMaintainer;
	@UiField FocusPanel focusPanelMaintainer;
	@UiField Popover popoverMaintainer;
	@UiField Icon infoIconCustomFields;
	@UiField FocusPanel focusPanelCustomFields;
	@UiField Popover popoverCustomFields;
	@UiField Icon infoIconResources;
	@UiField FocusPanel focusPanelResources;
	@UiField Popover popoverResources;
	@UiField Icon infoIconTitle;
	@UiField FocusPanel focusPanelTitle;
	@UiField Popover popoverGroups;
	@UiField Icon infoIconGroups;
	@UiField FocusPanel focusPanelGroups;
	@UiField Popover popoverTitle;
	@UiField Icon infoIconDescription;
	@UiField Popover popoverDescription;
	@UiField FocusPanel focusPanelDescription;
	@UiField ControlGroup metadataTypesControlGroup;
	@UiField ControlGroup productTitleGroup;
	@UiField ControlGroup maintainerControlGroup;
	@UiField ControlGroup versionControlGroup;
	@UiField ControlGroup organizationsGroup;
	@UiField ControlGroup groupsControlGroup;

	// Create a remote service proxy to talk to the server-side ckan service.
	private final CKanPublisherServiceAsync ckanServices = GWT.create(CKanPublisherService.class);

	private static final String REGEX_TITLE_PRODUCT_SUBWORD = "[^a-zA-Z0-9_.-]";
	private static final String REGEX_MAIL = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";
	private static final String NONE_PROFILE = "none";

	// error/info messages
	protected static final String ERROR_PRODUCT_CREATION = "There was an error while trying to publish your item.";
	protected static final String PRODUCT_CREATED_OK = "Item correctly published!";
	private static final String TRYING_TO_CREATE_PRODUCT = "Trying to publish the item, please wait...";
	protected static final String MISSING_PUBLISH_RIGHTS = "It seems you are not authorized to publish on catalogue. Request it to the VRE manager or the portal administrator.";

	// tab panel 
	private TabPanel tabPanel; 

	// add resource form
	private AddResourceToDataset resourceForm;

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
	private TwinColumnSelectionMainPanel resourcesTwinPanel;

	// List of opened popup'ids
	private List<String> popupOpenedIds = new ArrayList<String>();

	// map of organization name title
	private Map<String, String> nameTitleOrganizationMap = new HashMap<String, String>();

	/**
	 * Invoked in the most general case
	 * @param eventBus the event bus
	 */
	public CreateDatasetForm(HandlerManager eventBus) {
		createDatasetFormBody(false, null, eventBus);
	}

	/**
	 * Invoked when the workspace is used
	 * @param idFolderWorkspace
	 * @param eventBus the event bus
	 */
	public CreateDatasetForm(String idFolderOrFileWorkspace, HandlerManager eventBus) {
		createDatasetFormBody(true, idFolderOrFileWorkspace, eventBus);
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
				InfoIconsLabels.closeDialogBox(popupOpenedIds);
			}
		});
	}

	/**
	 * The real constructor
	 * @param isWorkspaceRequest
	 * @param idFolderWorkspace
	 * @param owner
	 * @param eventBus
	 */
	private void createDatasetFormBody(final boolean isWorkspaceRequest, final String idFolderOrFileWorkspace, final HandlerManager eventBus){

		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		this.isWorkspaceRequest = isWorkspaceRequest;
		bind();
		prepareInfoIcons();

		// disable continue button
		continueButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		// hide tags panel TODO
		tagsPanel.setVisible(false);

		// check if the user has publishing rights
		setAlertBlock("Checking your permissions, please wait...", AlertType.INFO, true);

		ckanServices.isPublisherUser(isWorkspaceRequest, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				if(result){

					// set info block
					setAlertBlock("Retrieving information, please wait...", AlertType.INFO, true);

					// get back the licenses and the metadata information
					ckanServices.getDatasetBean(idFolderOrFileWorkspace, new AsyncCallback<DatasetMetadataBean>() {

						@Override
						public void onFailure(Throwable caught) {

							setAlertBlock("Error while retrieving information", AlertType.ERROR, true);

						}

						@Override
						public void onSuccess(final DatasetMetadataBean bean) {

							if(bean == null){

								setAlertBlock("Error while retrieving information", AlertType.ERROR, true);
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
								Map<String, List<String>> customFieldsMap = bean.getCustomFields();	
								
								List<String> vocabularyTags = bean.getTagsVocabulary();
								
								// TODO vocabulary list of tags has preemption
								if(vocabularyTags != null && !vocabularyTags.isEmpty()){
									
									tagsPanel.setVocabulary(vocabularyTags);
									
								}
								else if(customFieldsMap != null){

									// get the keys and put them as tags 
									Iterator<Entry<String, List<String>>> iteratorOverCustomField = customFieldsMap.entrySet().iterator();

									while (iteratorOverCustomField.hasNext()) {
										Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iteratorOverCustomField
												.next();

										List<String> values = entry.getValue();

										for (String value : values) {
											// these are fixed key, variable value custom fields
											CustomFieldEntry toAdd = new CustomFieldEntry(eventBus, entry.getKey(), value, false);
											customFieldEntriesList.add(toAdd);
											customFields.add(toAdd);

											// add as tag
											tagsPanel.addTagElement(entry.getKey());
										}
									}
								}
								
								// set it as visible anyway
								tagsPanel.setVisible(true);

								if(isWorkspaceRequest){
									// if there are not resources, for now just checked it ( and hide so that the step will be skipped)
									if(hideManageResources()){
										alertNoResources.setType(AlertType.WARNING);
										alertNoResources.setVisible(true);
									}else
										resourcesTwinPanel = new TwinColumnSelectionMainPanel(bean.getResourceRoot());
								}

								// set organizations
								List<OrganizationBean> organizations = bean.getOrganizationList();

								for (OrganizationBean organization : organizations) {
									organizationsListbox.addItem(organization.getTitle());
									nameTitleOrganizationMap.put(organization.getTitle(), organization.getName());
								}

								// force the selection of the first one, and retrieve the list of profiles 
								organizationsListbox.setSelectedIndex(0);

								// add change handler to dynamically retrieve the list of profiles
								organizationsListbox.addChangeHandler(new ChangeHandler() {

									@Override
									public void onChange(ChangeEvent event) {
										event.preventDefault();
										organizationsListboxChangeHandlerBody();
									}
								});

								// try to retrieve the profiles
								setAlertBlock("Retrieving types, please wait...", AlertType.INFO, true);

								// get the name of the organization from the title
								final String orgName = nameTitleOrganizationMap.get(organizationsListbox.getSelectedItemText());

								// perform remote request of profiles for the selected organization
								ckanServices.getProfiles(orgName, new AsyncCallback<List<MetaDataProfileBean>>() {

									@Override
									public void onFailure(Throwable caught) {
										setAlertBlock("Error while retrieving types, retry later", AlertType.ERROR, true);
									}

									@Override
									public void onSuccess(final List<MetaDataProfileBean> profiles) {

										if(profiles == null){
											setAlertBlock("Error while retrieving types, retry later", AlertType.ERROR, true);
										}
										else{

											receivedBean.setMetadataListTypes(profiles);
											prepareMetadataList(receivedBean);
											organizationsListbox.setEnabled(true);
											metadataTypeListbox.setEnabled(true);

											// try to retrieve the licenses
											setAlertBlock("Retrieving licenses, please wait...", AlertType.INFO, true);
											ckanServices.getLicenses(new AsyncCallback<LicensesBean>() {

												@Override
												public void onFailure(Throwable caught){
													setAlertBlock("Error while retrieving licenses, try later", AlertType.ERROR, true);
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

														// try to retrieve the licenses
														setAlertBlock("Retrieving groups, please wait...", AlertType.INFO, true);

														// request groups
														ckanServices.getUserGroups(orgName, new AsyncCallback<List<GroupBean>>() {

															@Override
															public void onSuccess(List<GroupBean> groups) {
																if(groups == null){
																	setAlertBlock("Error while retrieving groups", AlertType.ERROR, true);
																}else{
																	if(groups.isEmpty()){
																		groupsControlGroup.setVisible(false);
																	}
																	else{

																		// add groups
																		for (GroupBean group : groups) {
																			groupsListbox.addItem(group.getGroupTitle(), group.getGroupName());
																		}
																		hideGroupsAlreadyInProfile(profiles);
																	}
																	// everything went ok
																	setAlertBlock("", AlertType.ERROR, false);
																	continueButton.setEnabled(true);
																	resetButton.setEnabled(true);
																}
															}

															@Override
															public void onFailure(Throwable caught) {
																setAlertBlock("Error while retrieving groups, try later", AlertType.ERROR, true);
															}
														});

													}else{
														setAlertBlock("Error while retrieving licenses, try later", AlertType.ERROR, true);
													}
												}
											});
										}
									}
								});
							}
						}
					});

				}else{
					setAlertBlock(MISSING_PUBLISH_RIGHTS, AlertType.ERROR, true);
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				setAlertBlock(MISSING_PUBLISH_RIGHTS, AlertType.ERROR, true);
			}
		});



	}


	/**
	 * When the organization name is changed we need to retrieve the list of profiles and groups
	 */
	private void organizationsListboxChangeHandlerBody() {

		// remove any other product profiles
		metadataTypeListbox.clear();

		// add "none" item again
		metadataTypeListbox.addItem(NONE_PROFILE);

		// select "none"
		metadataTypeListbox.setSelectedIndex(0);

		// get the name of the organization from the title
		String selectedOrganizationTitle = organizationsListbox.getSelectedItemText();	
		final String orgName = nameTitleOrganizationMap.get(selectedOrganizationTitle);

		// try to retrieve the profiles
		setAlertBlock("Retrieving types, please wait...", AlertType.INFO, true);

		// disable the list of organizations name so that the user doesn't change it again
		// also disable the profiles and the list of groups
		organizationsListbox.setEnabled(false);
		metadataTypeListbox.setEnabled(false);
		groupsListbox.clear();
		groupsControlGroup.setVisible(false);

		// perform remote request of profiles for the selected organization
		ckanServices.getProfiles(orgName, new AsyncCallback<List<MetaDataProfileBean>>() {

			@Override
			public void onSuccess(final List<MetaDataProfileBean> profiles) {

				if(profiles != null){

					receivedBean.setMetadataListTypes(profiles);
					prepareMetadataList(receivedBean);
					organizationsListbox.setEnabled(true);
					metadataTypeListbox.setEnabled(true);

					// try to retrieve the licenses
					setAlertBlock("Retrieving groups, please wait...", AlertType.INFO, true);

					// request groups
					ckanServices.getUserGroups(orgName, new AsyncCallback<List<GroupBean>>() {

						@Override
						public void onSuccess(List<GroupBean> groups) {

							if(groups == null){
								setAlertBlock("Error while retrieving groups, try later", AlertType.ERROR, true);
							}else{
								if(groups.isEmpty()){
									groupsControlGroup.setVisible(false);
								}
								else{

									// add groups
									for (GroupBean group : groups) {
										groupsListbox.addItem(group.getGroupTitle(), group.getGroupName());
									}
									groupsListbox.setEnabled(true);
									hideGroupsAlreadyInProfile(profiles);
								}
								setAlertBlock("", AlertType.ERROR, false);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							setAlertBlock("Error while retrieving groups, try later", AlertType.ERROR, true);
						}
					});

				}else
					setAlertBlock("Error while retrieving types, sorry", AlertType.ERROR, true);

			}

			@Override
			public void onFailure(Throwable caught) {

				setAlertBlock("Error while retrieving types, sorry", AlertType.ERROR, true);

			}
		});

	}

	/**
	 * Add the items to the listbox and put data into the metadataPanel
	 * @param receivedBean
	 */
	private void prepareMetadataList(final DatasetMetadataBean receivedBean) {

		List<MetaDataProfileBean> profiles = receivedBean.getMetadataListTypes();

		if(profiles != null && !profiles.isEmpty()){
			for(MetaDataProfileBean metadataBean: profiles){

				metadataTypeListbox.addItem(metadataBean.getType().getName());		

				// add handler on select
				metadataTypeListbox.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {

						String selectedItemText = metadataTypeListbox.getSelectedItemText();

						if(selectedItemText.equals(NONE_PROFILE)){
							metadataFieldsPanel.clear();
							metadataFieldsPanel.setVisible(false);
							receivedBean.setChosenType(null);
						}else{
							receivedBean.setChosenType(selectedItemText);
							metadataFieldsPanel.clear();
							addFields(selectedItemText);
						}
					}
				});
			}

			// hide elements or show them if needed (groups in profiles cannot be present again in groups listbox)
			if(groupsControlGroup.isVisible()){
				List<String> groupsToHide = new ArrayList<String>();
				for(MetaDataProfileBean profile: profiles)
					groupsToHide.add(profile.getType().toString());

				SelectElement se = groupsListbox.getElement().cast();

				for (int i = 0; i < groupsListbox.getItemCount(); i++) {
					if(groupsToHide.contains(groupsListbox.getItemText(i))){
						se.getOptions().getItem(i).getStyle().setProperty("display", "none");
					}else
						se.getOptions().getItem(i).getStyle().setProperty("display", "");
				}
			}

			metadataTypesControlGroup.setVisible(true);
		}else{ 
			// just hide this listbox
			metadataTypesControlGroup.setVisible(false);
			metadataFieldsPanel.clear();
			listOfMetadataFields.clear();
			receivedBean.setChosenType(null);
		}
	}

	/**
	 * Add fields of the selected metadata profile to the widget
	 * @param selectedItem
	 */
	protected void addFields(String selectedItem) {

		for(MetaDataProfileBean bean: receivedBean.getMetadataListTypes()){

			if(bean.getType().getName().equals(selectedItem)){

				// prepare the data
				List<MetadataFieldWrapper> fields = bean.getMetadataFields();

				// clear old data
				listOfMetadataFields.clear();

				for (MetadataFieldWrapper field : fields) {
					MetaDataFieldSkeleton fieldWidget;
					try {
						fieldWidget = new MetaDataFieldSkeleton(field, eventBus);
						metadataFieldsPanel.add(fieldWidget);
						listOfMetadataFields.add(fieldWidget);
					} catch (Exception e) {
						GWT.log("Unable to build such widget", e);
					}
				}
				metadataFieldsPanel.setVisible(true);		
			}
		}
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
		final String errorMsg = validateDataOnContinue();

		if(errorMsg != null){

			alertOnContinue("Please check inserted data [" + errorMsg + "]", AlertType.ERROR);
			return;

		}else{

			// better check for title (only if the dataset was not created.. if it is the case, fields are not frozen)
			if(!titleTextBox.isEnabled())
				actionsAfterOnContinue();
			else{
				alertOnContinue("Checking if a item with such title already exists, please wait...", AlertType.INFO);

				ckanServices.datasetIdAlreadyExists(titleTextBox.getText(), new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							alertOnContinue("Sorry but an item with such title already exists, try to change it", AlertType.WARNING);
						}else{
							actionsAfterOnContinue();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						alertOnContinue("Sorry but there was a problem while checking if the inserted data are correct", AlertType.ERROR);
					}
				});
			}
		}
	}

	/**
	 * After onContinue ...
	 */
	private void actionsAfterOnContinue(){

		// check what to do 
		if(isWorkspaceRequest){

			// we need to show the page to handle resources one by one from the workspace
			formFirstStep.setVisible(false);
			formSecondStep.setVisible(!hideManageResources());
			formThirdStep.setVisible(hideManageResources());

			// add the resources to the container panel
			if(workspaceResourcesContainer.getWidget() == null){
				workspaceResourcesContainer.getElement().getStyle().setMarginLeft(20, Unit.PX);
				workspaceResourcesContainer.add(resourcesTwinPanel);
			}

		}else{

			// this is not a workspace request
			formFirstStep.setVisible(false);
			formThirdStep.setVisible(true);

		}

		if(metadataTypeListbox.getSelectedItemText().equals(NONE_PROFILE))
			selectedProfile.setText("");
		else
			selectedProfile.setText("Selected Type is " + metadataTypeListbox.getSelectedItemText());

	}


	@UiHandler("goBackButtonFirstStep")
	void onGoBackButtonFirstStep(ClickEvent e){

		// swap forms
		formFirstStep.setVisible(true);
		formSecondStep.setVisible(false);
		formThirdStep.setVisible(false);

	}


	@UiHandler("goBackButtonSecondStep")
	void onGoBackButton(ClickEvent e){

		// swap forms
		if(isWorkspaceRequest){
			formFirstStep.setVisible(hideManageResources());
			formSecondStep.setVisible(!hideManageResources());
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

			alertOnCreate("Please check the inserted values and the mandatory fields [" + errorMessage +"]", AlertType.ERROR, true);

		}
		else{

			String title = titleTextBox.getValue().trim();
			String description = descriptionTextarea.getText().trim();
			String selectedLicense = licenseListbox.getSelectedItemText();
			String visibility = visibilityListbox.getSelectedItemText();
			long version = Long.valueOf(versionTextbox.getValue().trim());
			String author = authorTextbox.getValue();
			String authorEmail = authorEmailTextbox.getValue();
			String maintainer = maintainerTextbox.getValue().trim();
			String maintainerEmail = maintainerEmailTextbox.getValue().trim();
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

			List<GroupBean> groups = new ArrayList<GroupBean>();

			// get groups, if any
			int items = groupsListbox.getItemCount();
			for (int i = 0; i < items; i++) {
				String groupTitle = groupsListbox.getItemText(i);
				String groupName = groupsListbox.getValue(i);
				if(groupsListbox.isItemSelected(i)){
					groups.add(new GroupBean(groupTitle, groupName));
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
			receivedBean.setTags(tagsPanel.getTags());
			receivedBean.setSelectedOrganization(chosenOrganization);
			receivedBean.setGroups(groups);
			if(resourcesTwinPanel != null)
				receivedBean.setResourceRoot(resourcesTwinPanel.getResourcesToPublish());

			Map<String, List<String>> customFieldsMap = new HashMap<String, List<String>>();

			// prepare custom fields
			for (MetaDataFieldSkeleton field : listOfMetadataFields) {

				String value = field.getFieldCurrentValue();

				if(!value.isEmpty()){
					String key = field.getFieldName();
					List<String> valuesForThisField = null;
					if(customFieldsMap.containsKey(key))
						valuesForThisField = customFieldsMap.get(key);
					else
						valuesForThisField = new ArrayList<String>();

					valuesForThisField.add(value);

					customFieldsMap.put(key, valuesForThisField);
				}

			}

			for(CustomFieldEntry customEntry : customFieldEntriesList){

				String key = customEntry.getKey();
				String value = customEntry.getValue();

				if(value != null && !value.isEmpty()){
					List<String> valuesForThisField = null;
					if(customFieldsMap.containsKey(key))
						valuesForThisField = customFieldsMap.get(key);
					else
						valuesForThisField = new ArrayList<String>();

					valuesForThisField.add(value);

					customFieldsMap.put(key, valuesForThisField);
				}

			}

			receivedBean.setCustomFields(customFieldsMap);

			// alert 
			alertOnCreate(TRYING_TO_CREATE_PRODUCT, AlertType.INFO, false);

			// invoke the create method
			createButton.setEnabled(false);
			goBackButtonSecondStep.setEnabled(false);

			ckanServices.createCKanDataset(receivedBean, new AsyncCallback<DatasetMetadataBean>() {

				@Override
				public void onSuccess(final DatasetMetadataBean createdDatasetBean) {

					if(createdDatasetBean != null){

						alertOnCreate(PRODUCT_CREATED_OK, AlertType.SUCCESS, false);

						// disable dataset fields
						disableDatasetFields();

						// disable reset
						resetButton.setEnabled(false);

						// show the go to dataset button
						final String datasetUrl = createdDatasetBean.getSource();
						goToDatasetButtonPanel.setVisible(true);
						goToDatasetButton.setVisible(true);
						goToDatasetButton.setText(
								(datasetUrl.length() > 100 ? 
										datasetUrl.substring(0, 100) + "..." : datasetUrl)
								);
						//						goToDatasetButton.setHref(datasetUrl);
						goToDatasetButton.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.open(datasetUrl, "_blank", "");
								//Window.Location.assign(datasetUrl);
							}
						});

						// if we are in the "general case" we need to show a form for adding resources
						if(isWorkspaceRequest){
							// leave to back button, but remove create and add go to dataset
							createButton.removeFromParent();

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
									addedResources.add(new AddedResourcesSummary(eventBus));
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
						
						alertOnCreate(ERROR_PRODUCT_CREATION, AlertType.ERROR, true);
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					alertOnCreate(ERROR_PRODUCT_CREATION + " Error message is : " + caught.getMessage(), AlertType.ERROR, true);
				}
			});
		}
	}

	/**
	 * Prepare the info icons of all core metadata info
	 */
	private void prepareInfoIcons() {

		// tags
		tagsPanel.prepareIcon(popupOpenedIds);

		// licenses
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.LICENSES_INFO_ID_POPUP,
				InfoIconsLabels.LICENSES_INFO_TEXT,
				InfoIconsLabels.LICENSES_INFO_CAPTION,
				infoIconLicenses,
				popoverLicenses,
				focusPanelLicenses,
				popupOpenedIds
				);

		// visibility
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.VISIBILITY_INFO_ID_POPUP,
				InfoIconsLabels.VISIBILITY_INFO_TEXT,
				InfoIconsLabels.VISIBILITY_INFO_CAPTION,
				infoIconVisibility,
				popoverVisibility,
				focusPanelVisibility,
				popupOpenedIds
				);

		// author
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.AUTHOR_INFO_ID_POPUP,
				InfoIconsLabels.AUTHOR_INFO_TEXT,
				InfoIconsLabels.AUTHOR_INFO_CAPTION,
				infoIconAuthor,
				popoverAuthor,
				focusPanelAuthor,
				popupOpenedIds
				);

		// author's email
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.AUTHOR_EMAIL_INFO_ID_POPUP,
				InfoIconsLabels.AUTHOR_EMAIL_INFO_TEXT,
				InfoIconsLabels.AUTHOR_EMAIL_INFO_CAPTION,
				infoIconAuthorEmail,
				popoverAuthorEmail,
				focusPanelAuthorEmail,
				popupOpenedIds
				);

		// maintainer
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.MAINTAINER_INFO_ID_POPUP,
				InfoIconsLabels.MAINTAINER_INFO_TEXT,
				InfoIconsLabels.MAINTAINER_INFO_CAPTION,
				infoIconMaintainer,
				popoverMaintainer,
				focusPanelMaintainer,
				popupOpenedIds
				);

		// maintainer's email
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_ID_POPUP,
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_TEXT,
				InfoIconsLabels.MAINTAINER_EMAIL_INFO_CAPTION,
				infoIconMaintainerEmail,
				popoverMaintainerEmail,
				focusPanelMaintainerEmail,
				popupOpenedIds
				);

		// profiles (or types)
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.PROFILES_INFO_ID_POPUP,
				InfoIconsLabels.PROFILES_INFO_TEXT,
				InfoIconsLabels.PROFILES_INFO_CAPTION,
				infoIconTypes,
				popoverTypes,
				focusPanelTypes,
				popupOpenedIds
				);

		// custom fields
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.CUSTOM_FIELDS_INFO_ID_POPUP,
				InfoIconsLabels.CUSTOM_FIELDS_INFO_TEXT,
				InfoIconsLabels.CUSTOM_FIELDS_INFO_CAPTION,
				infoIconCustomFields,
				popoverCustomFields,
				focusPanelCustomFields,
				popupOpenedIds
				);

		// resources field
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.RESOURCES_INFO_ID_POPUP,
				InfoIconsLabels.RESOURCES_INFO_TEXT,
				InfoIconsLabels.RESOURCES_INFO_CAPTION,
				infoIconResources,
				popoverResources,
				focusPanelResources,
				popupOpenedIds
				);

		// title
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.TITLE_INFO_ID_POPUP,
				InfoIconsLabels.TITLE_INFO_TEXT,
				InfoIconsLabels.TITLE_INFO_CAPTION,
				infoIconTitle,
				popoverTitle,
				focusPanelTitle,
				popupOpenedIds
				);

		// description
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.DESCRIPTION_INFO_ID_POPUP,
				InfoIconsLabels.DESCRIPTION_INFO_TEXT,
				InfoIconsLabels.DESCRIPTION_INFO_CAPTION,
				infoIconDescription,
				popoverDescription,
				focusPanelDescription,
				popupOpenedIds
				);

		// groups
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.GROUPS_INFO_ID_POPUP,
				InfoIconsLabels.GROUPS_INFO_TEXT,
				InfoIconsLabels.GROUPS_INFO_CAPTION,
				infoIconGroups,
				popoverGroups,
				focusPanelGroups,
				popupOpenedIds
				);
	}

	/**
	 * Test if profile data are valid
	 * @return
	 */
	private String areProfileDataValid() {

		for (MetaDataFieldSkeleton field : listOfMetadataFields) {

			field.removeError();

			String error = field.isFieldValueValid();
			if(error != null){
				field.showError();
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
	private void alertOnCreate(String text, AlertType type, boolean hideAfterAWhile){

		onCreateAlertBlock.setText(text);
		onCreateAlertBlock.setType(type);
		onCreateAlertBlock.setVisible(true);
		createButton.setEnabled(true);
		goBackButtonSecondStep.setEnabled(true);

		if(hideAfterAWhile){
			// hide after some seconds
			Timer t = new Timer() {

				@Override
				public void run() {

					onCreateAlertBlock.setVisible(false);

				}
			};
			t.schedule(10000);
		}
	}

	/**
	 * Validate data
	 * @return true on success, false otherwise
	 */
	private String validateDataOnContinue() {

		// remove errors
		productTitleGroup.setType(ControlGroupType.NONE);
		maintainerControlGroup.setType(ControlGroupType.NONE);
		versionControlGroup.setType(ControlGroupType.NONE);
		metadataTypesControlGroup.setType(ControlGroupType.NONE);
		organizationsGroup.setType(ControlGroupType.NONE);
		tagsPanel.setGroupPanelType(ControlGroupType.NONE);

		String title = titleTextBox.getText().trim();
		if(title.isEmpty()){
			productTitleGroup.setType(ControlGroupType.ERROR);
			return "Missing title";
		}

		// better check for the title		
		String[] splittedTitle = title.split(" "); 

		for (String word : splittedTitle) {
			String replaced = word.replaceAll(REGEX_TITLE_PRODUCT_SUBWORD, "");
			if(!replaced.equals(word)){
				productTitleGroup.setType(ControlGroupType.ERROR);
				return  "Please note not all characters are allowed for the title";
			}
		}

		// email reg expression
		String maintainerMail = maintainerEmailTextbox.getText();
		if(!maintainerMail.isEmpty() && !maintainerMail.matches(REGEX_MAIL)){
			maintainerControlGroup.setType(ControlGroupType.ERROR);
			return "Not valid maintainer email";
		}

		// check if version is a number
		try{
			int number = Integer.valueOf(versionTextbox.getText().trim());
			if(number <= 0)
				throw new Exception();
		}catch(Exception e){
			versionControlGroup.setType(ControlGroupType.ERROR);
			return "Version must be a natural number greater than zero";
		}

		// check if metadata profile is different from none and its mandatory fields have been fulfilled
		if(checkSelectedMetaDataProfile()){
			metadataTypesControlGroup.setType(ControlGroupType.ERROR);
			return "You must select a Type different frome none";
		}

		if(organizationsListbox.getSelectedItemText() == null){
			organizationsGroup.setType(ControlGroupType.ERROR);
			return "You must select an organization in which you want to publish";
		}

		// at least one tag..
		if(tagsPanel.getTags().isEmpty()){
			tagsPanel.setGroupPanelType(ControlGroupType.ERROR);
			return "Please add at least one meaningful tag for the item";
		}

		return null;
	}

	/**
	 * Checks if a metadata profile has been chosen and its fields have been fulfilled
	 * @return
	 */
	private boolean checkSelectedMetaDataProfile() {
		return metadataTypeListbox.getSelectedItemText().equals(NONE_PROFILE) && (metadataTypeListbox.getItemCount() != 1);
	}

	@UiHandler("resetButton")
	void resetFormEvent(ClickEvent e){

		// reset main fields
		titleTextBox.setText("");
		descriptionTextarea.setText("");
		versionTextbox.setText("");
		maintainerTextbox.setText("");
		maintainerEmailTextbox.setText("");
		tagsPanel.removeTags();

		// unselect all groups
		for(int i = 0; i < groupsListbox.getItemCount(); i++)
			groupsListbox.setItemSelected(i, false);

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
		tagsPanel.freeze();
		licenseListbox.setEnabled(false);
		organizationsListbox.setEnabled(false);
		addCustomFieldButton.setEnabled(false);
		metadataTypeListbox.setEnabled(false);
		groupsListbox.setEnabled(false);

		for(CustomFieldEntry ce: customFieldEntriesList)
			ce.freeze();

		// disable profile fields
		for (MetaDataFieldSkeleton field : listOfMetadataFields) {
			field.freeze();
		}

		// freeze table of resources
		if(resourcesTwinPanel != null)
			resourcesTwinPanel.freeze();
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
	 * Hide the groups that are already listed in the profiles page
	 * @param profiles
	 */
	private void hideGroupsAlreadyInProfile(List<MetaDataProfileBean> profiles) {

		List<String> groupsToHide = new ArrayList<String>();
		for(MetaDataProfileBean profile: profiles)
			groupsToHide.add(profile.getType().getName());

		SelectElement se = groupsListbox.getElement().cast();

		int hiddenElements = 0;
		for (int i = 0; i < groupsListbox.getItemCount(); i++) {
			if(groupsToHide.contains(groupsListbox.getItemText(i))){
				se.getOptions().getItem(i).getStyle().setProperty("display", "none");
				hiddenElements++;
			}
		}

		if(hiddenElements == groupsListbox.getItemCount())
			groupsControlGroup.setVisible(false);
		else
			groupsControlGroup.setVisible(true);

	}

	/**
	 * Check if resource(s) are missing
	 * @return
	 */
	private boolean hideManageResources(){

		return receivedBean.getResourceRoot() == null || (receivedBean.getResourceRoot().isFolder() && (receivedBean.getResourceRoot().getChildren() == null ||
				receivedBean.getResourceRoot().getChildren().isEmpty()));

	}
}
