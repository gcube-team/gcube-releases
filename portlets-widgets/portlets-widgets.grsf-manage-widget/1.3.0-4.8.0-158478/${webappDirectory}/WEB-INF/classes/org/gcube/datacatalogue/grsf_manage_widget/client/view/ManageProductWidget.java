package org.gcube.datacatalogue.grsf_manage_widget.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.datacatalogue.common.enums.Status;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.HideManagementPanelEvent;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.ConnectToWidget;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.FormEntryModel;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.SimilarGRSFRecordWidget;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.SourceWidget;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SourceRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ex.NoGRSFRecordException;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Management widget main panel.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductWidget extends Composite{

	private static GRSFManageWidgetServiceAsync service = GWT.create(GRSFManageWidgetService.class);

	private static ManageProductWidgetUiBinder uiBinder = GWT
			.create(ManageProductWidgetUiBinder.class);

	interface ManageProductWidgetUiBinder extends
	UiBinder<Widget, ManageProductWidget> {
	}

	@UiField
	Modal manageProductModal;

	@UiField
	AlertBlock infoBlock;

	@UiField
	TextArea shortNameTextBox;

	@UiField
	TextBox productGrsfType;

	@UiField
	CheckBox traceabilityFlag;

	@UiField
	TextArea GRSFNameTexBox;

	@UiField
	TextArea semanticIdentifierTextBox;

	@UiField
	VerticalPanel panelForSourceItems;

	@UiField
	VerticalPanel panelForSimilarGRSFRecords;

	@UiField
	VerticalPanel panelForConnectOtherRecords;

	@UiField
	TextBox currentStatus;

	@UiField
	ListBox listBoxStatus;

	@UiField
	TextArea annotationArea;

	@UiField
	Button cancelButton;

	@UiField
	Button confirmButton;

	@UiField
	Icon loaderIcon;

	@UiField
	ControlGroup listBoxStatusGroup;

	@UiField
	ControlGroup annotationAreaGroup;

	@UiField
	ControlGroup productGrsfTypeGroup;

	@UiField
	Form formUpdate;

	@UiField
	Image loadingImage;

	public static final String LOADING_IMAGE_URL = GWT.getModuleBaseURL() + "../images/loader.gif";

	// messages used here and there
	private final static String STATUS_UPDATE_SUCCESS = "The item has been correctly updated. Thanks for your collaboration!";
	private final static String STATUS_UPDATE_ERROR = "Sorry, there was a problem while trying to update the status of this item";
	protected static final String ERROR_ON_RETRIEVING_BEAN = "It seems there was a problem while contacting the service...";
	protected static final String NO_GRSF_RECORD_BEAN = "This item is not a GRSF record, thus it cannot be managed";

	protected static final String NO_ADMIN_ROLE = "Sorry but it seems you do not have the rights to manage items."
			+ " You are suggested to contact the VRE Manager if something is wrong with this";

	// event bus shared with the portlet
	private HandlerManager eventBus = null;

	// the objects to be managed
	private ManageProductBean bean;
	private final static List<Status> STATUS = new ArrayList<Status>(Arrays.asList(Status.values()));


	/**
	 * Build a ManageProduct widget for the product with the specified id.
	 * @param productIdentifier
	 * @param eventBus
	 */
	public ManageProductWidget(String productIdentifier, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;

		GWT.log("item identifier is " + productIdentifier);

		if(productIdentifier == null || productIdentifier.isEmpty())
			return;

		// start loader service 
		loadingImage.setUrl(LOADING_IMAGE_URL);
		loadingImage.setVisible(true);
		formUpdate.setVisible(false);

		// show modal
		manageProductModal.show();
		manageProductModal.setWidth("50%");

		// async request to fetch the product
		retrieveProductBean(productIdentifier);
	}


	/**
	 * Actually builds the widget... asks for details about the record
	 * @param productIdentifier
	 */
	private void retrieveProductBean(final String productIdentifier) {

		// check if it is an administrator
		service.isAdminUser(new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				GWT.log("Get " + result);
				
				if(!result){

					showInfo(NO_ADMIN_ROLE, AlertType.ERROR);

					// hide the form and disable the send button
					formUpdate.setVisible(false);
					confirmButton.setEnabled(false);
					loadingImage.setVisible(false);

					// ask to hide management panel
					if(eventBus != null)
						eventBus.fireEvent(new HideManagementPanelEvent());

				}else{
					service.getProductBeanById(productIdentifier, new AsyncCallback<ManageProductBean>() {

						@Override
						public void onSuccess(ManageProductBean result) {

							GWT.log("Get " + result);
							
							if(result == null){
								showInfo(ERROR_ON_RETRIEVING_BEAN, AlertType.ERROR);
								formUpdate.setVisible(false);
								confirmButton.setEnabled(false);
							}else{
								bean = result;
								infoBlock.setVisible(false);
								
								// top: more or less fixed information
								GRSFNameTexBox.setText(bean.getGrsfName());
								shortNameTextBox.setText(bean.getShortName());
								semanticIdentifierTextBox.setText(bean.getSemanticIdentifier());
								productGrsfType.setText(bean.getGrsfType());
								currentStatus.setText(bean.getCurrentStatus().toString());
								traceabilityFlag.setValue(bean.isTraceabilityFlag());
								traceabilityFlag.setTitle("Current value for the record is " + bean.isTraceabilityFlag());

								// manage sources
								List<SourceRecord> availableSources = bean.getSources();
								panelForSourceItems.add(new SourceWidget(availableSources));

								// manage similar GRSF records, if any
								List<SimilarGRSFRecord> availableGRSFSimilarRecords = bean.getSimilarGrsfRecords();
								panelForSimilarGRSFRecords.add(new SimilarGRSFRecordWidget(availableGRSFSimilarRecords));

								// prepare "connect" panel
								panelForConnectOtherRecords.add(new ConnectToWidget());

								// check if we need to show more
								if(bean.getExtrasIfAvailable() != null && !bean.getExtrasIfAvailable().isEmpty())
									addExtrasAfter(bean, productGrsfTypeGroup);

								// check for new status box
								List<Status> statusToShow = new ArrayList<Status>(STATUS);
								statusToShow.remove(bean.getCurrentStatus());

								// if the record isn't approved, then remove also archived
								if(!bean.getCurrentStatus().equals(Status.Approved))
									statusToShow.remove(Status.Archived);

								listBoxStatus.addItem("Select the new status");
								listBoxStatus.getElement().<SelectElement>cast().getOptions().getItem(0).setDisabled(true);
								for (Status availableStatus : statusToShow) {
									listBoxStatus.addItem(availableStatus.toString());
								}
								listBoxStatus.setSelectedIndex(0);

								formUpdate.setVisible(true);
							}

							loadingImage.setVisible(false);
						}

						@Override
						public void onFailure(Throwable caught) {

							if(caught instanceof NoGRSFRecordException)
								showInfo(NO_GRSF_RECORD_BEAN, AlertType.WARNING);
							else
								showInfo("Error is " + caught, AlertType.ERROR);

							// hide the form and disable the send button
							formUpdate.setVisible(false);
							confirmButton.setEnabled(false);
							loadingImage.setVisible(false);
						}
					});
				}

			}

			@Override
			public void onFailure(Throwable caught) {

				showInfo(NO_ADMIN_ROLE, AlertType.ERROR);

				// hide the form and disable the send button
				formUpdate.setVisible(false);
				confirmButton.setEnabled(false);
				loadingImage.setVisible(false);

			}
		});

	}

	/**
	 * Add extras if available after controlGroupBefore
	 * @param bean
	 * @param productTypeGroup
	 */
	private void addExtrasAfter(ManageProductBean bean,
			ControlGroup controlGroupBefore) {

		int index = formUpdate.getWidgetIndex(controlGroupBefore);

		Map<String, String> extras = bean.getExtrasIfAvailable();
		Iterator<Entry<String, String>> iterator = extras.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) iterator
					.next();

			formUpdate.insert(new FormEntryModel(entry.getKey(), entry.getValue()), index);
			index++;

		}

	}

	@UiHandler("confirmButton")
	void onSaveButton(ClickEvent ce){

		listBoxStatusGroup.setType(ControlGroupType.NONE);

		if(listBoxStatus.getSelectedIndex() <= 0){
			listBoxStatusGroup.setType(ControlGroupType.ERROR);
			return;
		}

		annotationAreaGroup.setType(ControlGroupType.NONE);

		if(annotationArea.getText() == null || annotationArea.getText().isEmpty()){
			annotationArea.setPlaceholder("An annotation message to send along the update (mandatory)");
			annotationAreaGroup.setType(ControlGroupType.ERROR);
			return;
		}

		manageProductModal.setCloseVisible(false);
		cancelButton.setEnabled(false);
		confirmButton.setEnabled(false);
		loaderIcon.setVisible(true);

		// set new values
		bean.setAnnotation(new HTML(annotationArea.getText().trim()).getText());
		bean.setNewStatus(Status.fromString(listBoxStatus.getSelectedItemText()));

		service.notifyProductUpdate(bean, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {

				if(result == null){
					showInfo(STATUS_UPDATE_SUCCESS, AlertType.SUCCESS);
					confirmButton.removeFromParent();
					formUpdate.setVisible(false);

				}else{
					showInfo(STATUS_UPDATE_ERROR + "(" + result + ")", AlertType.ERROR);
					confirmButton.setEnabled(true);
				}

				manageProductModal.setCloseVisible(true);
				cancelButton.setEnabled(true);
				loaderIcon.setVisible(false);
			}

			@Override
			public void onFailure(Throwable caught) {

				manageProductModal.setCloseVisible(true);
				cancelButton.setEnabled(true);
				confirmButton.setEnabled(true);
				loaderIcon.setVisible(false);
				showInfo(STATUS_UPDATE_ERROR, AlertType.ERROR);
			}
		});

	}
	
	@UiHandler("cancelButton")
	void onCancelButton(ClickEvent ce){
		// just hide the panel
		manageProductModal.hide();
	}

	/**
	 * Show information
	 * @param statusUpdateError
	 */
	protected void showInfo(String statusUpdateError, AlertType type) {

		infoBlock.setText(statusUpdateError);
		infoBlock.setType(type);
		infoBlock.setVisible(true);

	}

}
