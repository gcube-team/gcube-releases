package org.gcube.datacatalogue.grsf_manage_widget.client.view;

import java.util.Date;

import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageRevertOperationWidget extends Composite {

	private static ManageRevertOperationWidgetUiBinder uiBinder = GWT
			.create(ManageRevertOperationWidgetUiBinder.class);

	interface ManageRevertOperationWidgetUiBinder extends
	UiBinder<Widget, ManageRevertOperationWidget> {
	}

	public ManageRevertOperationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	VerticalPanel moreInfoAboutOperation;

	@UiField
	Modal revertOperationModal;

	@UiField
	Icon loaderIcon;

	@UiField
	Image loadingImage;

	@UiField
	AlertBlock infoBlock;

	@UiField
	Button revertButton;

	@UiField
	Button cancelButton;

	@UiField
	TextArea requestAuthor;

	@UiField
	TextArea requestTypeBox;

	@UiField
	TextArea requestRecordUUID;

	@UiField
	TextArea requestTimestamp;

	private static GRSFManageWidgetServiceAsync service = GWT.create(GRSFManageWidgetService.class);

	public static final String LOADING_IMAGE_URL = GWT.getModuleBaseURL() + "../images/loader.gif";
	private RevertableOperationInfo revertableOperation = null;

	public ManageRevertOperationWidget(String encryptedUrlOperation) {
		initWidget(uiBinder.createAndBindUi(this));

		GWT.log("Encrypted url is " + encryptedUrlOperation);

		if(encryptedUrlOperation == null || encryptedUrlOperation.isEmpty())
			return;

		// start loader service 
		loadingImage.setUrl(LOADING_IMAGE_URL);
		loadingImage.setVisible(true);

		// show modal
		revertOperationModal.addStyleName("management-metadata-modal-style");
		//		revertOperationModal.getElement().getStyle().setWidth(60, Unit.PCT);
		revertOperationModal.show();
		loaderIcon.getElement().getStyle().setMarginRight(10, Unit.PX);
		moreInfoAboutOperation.getElement().getStyle().setMarginBottom(50, Unit.PX);

		// async request to fetch the product
		loadModalContent(encryptedUrlOperation);

	}

	/**
	 * Validate the parameters of the url and ask the editor/reviewer what he/she wants to do.
	 * @param encryptedUrlOperation
	 */
	private void loadModalContent(String encryptedUrlOperation) {

		revertButton.setEnabled(false);

		service.validateRevertOperation(encryptedUrlOperation, new AsyncCallback<RevertableOperationInfo>() {

			@Override
			public void onSuccess(RevertableOperationInfo result) {

				loadingImage.setVisible(false);

				if(result != null){
					revertableOperation = result;
					String dateString = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss").format(new Date(revertableOperation.getTimestamp()));					
					requestAuthor.setText(revertableOperation.getFullNameOriginalAdmin() + "(" + revertableOperation.getUserNameOriginalAdmin() + ")");
					requestTypeBox.setText(revertableOperation.getOperation().toString().toUpperCase());
					requestRecordUUID.setText(revertableOperation.getUuid());
					requestTimestamp.setText(dateString);

					Anchor viewRecord = new Anchor();
					viewRecord.setText("View Record");
					viewRecord.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					viewRecord.setHref(revertableOperation.getRecordUrl());
					viewRecord.setTarget("_blank");

					moreInfoAboutOperation.add(viewRecord);
					moreInfoAboutOperation.setVisible(true);
					revertButton.setEnabled(true);
				}else
					displayError(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				loadingImage.setVisible(false);
				displayError(caught);
			}
		});

	}

	@UiHandler("revertButton")
	void onSaveButton(ClickEvent ce){

		loaderIcon.setVisible(true);
		revertButton.setEnabled(false);
		cancelButton.setEnabled(false);
		
		service.performRevertOperation(revertableOperation, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				revertButton.setEnabled(true);
				loaderIcon.setVisible(false);

				if(!result)
					displayError(null);
				else{
					infoBlock.setVisible(true);
					infoBlock.setType(AlertType.SUCCESS);
					infoBlock.setText("The request has been processed successfully!");
					revertButton.removeFromParent();
					cancelButton.setText("Ok");
					cancelButton.setType(ButtonType.INFO);
					cancelButton.setEnabled(true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				displayError(caught);
			}
		});

	}

	@UiHandler("cancelButton")
	void onCancelButton(ClickEvent ce){
		revertOperationModal.hide();
	}

	protected void displayError(Throwable caught) {

		infoBlock.setVisible(true);
		infoBlock.setType(AlertType.ERROR);
		infoBlock.setText("Unable to perform this operation. " + (caught != null ? "Error was " + caught : ""));

	}
}
