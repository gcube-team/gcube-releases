package org.gcube.datacatalogue.grsf_manage_widget.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.shared.GRSFStatus;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ex.NoGRSFRecordException;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

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
	TextArea titleTextArea;

	//	@UiField
	//	TextBox productType;

	@UiField
	TextBox productGrsfType;

	//	@UiField
	//	TextBox productSemanticId;
	//
	//	@UiField
	//	TextBox productShortTitle;
	//
	//	@UiField
	//	TextBox productSource;

	@UiField
	TextBox currentStatus;

	@UiField
	ListBox listBoxStatus;

	@UiField
	TextArea annotationArea;
	
	@UiField
	TextArea descriptionTextArea;

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
	private final static List<GRSFStatus> STATUS = new ArrayList<GRSFStatus>(Arrays.asList(GRSFStatus.values()));
	private final static String STATUS_UPDATE_SUCCESS = "The item has been correctly updated. Thanks for your collaboration!";
	private final static String STATUS_UPDATE_ERROR = "Sorry, there was a problem while trying to update the status of this item";
	protected static final String ERROR_ON_RETRIEVING_BEAN = "It seems there was a problem while contacting the service...";
	protected static final String NO_GRSF_RECORD_BEAN = "This item is not a GRSF record, thus it cannot be managed";

	protected static final String NO_ADMIN_ROLE = "Sorry but it seems you do not have the rights to manage items."
			+ " You are suggested to contact the VRE Manager if something is wrong.";
	private ManageProductBean bean;

	public ManageProductWidget(String productIdentifier) {
		initWidget(uiBinder.createAndBindUi(this));

		if(productIdentifier == null || productIdentifier.isEmpty()){
			GWT.log("The received item identifier is null..");
			return;
		}

		GWT.log("item identifier is " + productIdentifier);

		// start loader service 
		loadingImage.setUrl(LOADING_IMAGE_URL);
		loadingImage.setVisible(true);

		manageProductModal.show();

		// async request to fetch the product
		retrieveProductBean(productIdentifier);
	}

	private void retrieveProductBean(final String productIdentifier) {

		// check if it is an administrator
		service.isAdminUser(new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				if(!result){

					showInfo(NO_ADMIN_ROLE, AlertType.ERROR);

					// hide the form and disable the send button
					formUpdate.setVisible(false);
					confirmButton.setEnabled(false);
					loadingImage.setVisible(false);

				}else{
					service.getProductBeanById(productIdentifier, new AsyncCallback<ManageProductBean>() {

						@Override
						public void onSuccess(ManageProductBean result) {

							if(result != null){
								bean = result;
								annotationArea.setText("");
								infoBlock.setVisible(false);

								titleTextArea.setText(bean.getItemTitle());
								currentStatus.setText(bean.getCurrentStatus().toString());
								productGrsfType.setText(bean.getGrsfType());
								//								productType.setText(bean.getType());
								//								productSemanticId.setText(bean.getSemanticId());
								//								productShortTitle.setText(bean.getShortTitle());
								//								productSource.setText(bean.getSource());
								descriptionTextArea.setText(bean.getDescription());

								// check if we need to show more
								if(bean.getExtrasIfAvailable() != null && !bean.getExtrasIfAvailable().isEmpty())
									addExtrasAfter(bean, productGrsfTypeGroup);

								List<GRSFStatus> statusToShow = new ArrayList<GRSFStatus>(STATUS);
								statusToShow.remove(bean.getCurrentStatus());
								listBoxStatus.addItem("Select the new status");
								listBoxStatus.getElement().<SelectElement>cast().getOptions().getItem(0).setDisabled(true);
								for (GRSFStatus availableStatus : statusToShow) {
									listBoxStatus.addItem(availableStatus.toString());
								}
								listBoxStatus.setSelectedIndex(0);
							}
							else{
								showInfo(ERROR_ON_RETRIEVING_BEAN, AlertType.ERROR);
								formUpdate.setVisible(false);
								confirmButton.setEnabled(false);
							}

							loadingImage.setVisible(false);
						}

						@Override
						public void onFailure(Throwable caught) {

							if(caught instanceof NoGRSFRecordException)
								showInfo(NO_GRSF_RECORD_BEAN, AlertType.WARNING);
							else
								showInfo(caught.getMessage(), AlertType.ERROR);

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

	@UiHandler("cancelButton")
	void onCancelButton(ClickEvent ce){
		manageProductModal.hide();
	}

	@UiHandler("confirmButton")
	void onSaveButton(ClickEvent ce){

		if(bean == null)
			return;

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
		bean.setNewStatus(GRSFStatus.fromString(listBoxStatus.getSelectedItemText()));

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
