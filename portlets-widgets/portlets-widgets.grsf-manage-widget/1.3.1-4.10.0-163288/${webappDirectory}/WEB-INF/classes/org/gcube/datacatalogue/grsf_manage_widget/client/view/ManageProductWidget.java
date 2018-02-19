package org.gcube.datacatalogue.grsf_manage_widget.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.datacatalogue.common.enums.Status;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.EnableConfirmButtonEvent;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.EnableConfirmButtonEventHandler;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.HideManagementPanelEvent;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.ConnectToWidget;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.SimilarGRSFRecordWidget;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.SourceWidget;
import org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets.SuggestMerges;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ConnectedBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.HashTagsOnUpdate;
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
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
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
import com.google.gwt.user.client.Window;
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

	private static ManageProductWidgetUiBinder uiBinder = GWT.create(ManageProductWidgetUiBinder.class);

	interface ManageProductWidgetUiBinder extends
	UiBinder<Widget, ManageProductWidget> {
	}

	@UiField
	VerticalPanel container;

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
	ControlGroup similarGRSFRecordGroup;

	@UiField
	VerticalPanel panelForSimilarGRSFRecords;

	@UiField
	VerticalPanel panelForFurtherMerges;

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
	private final static String STATUS_UPDATE_SUCCESS = "The record has been correctly updated. Thanks for your collaboration!";
	private final static String STATUS_UPDATE_ERROR = "Sorry, there was a problem while trying to update the status of this record";
	protected static final String ERROR_ON_RETRIEVING_BEAN = "It seems there was a problem while contacting the service...";
	protected static final String NO_GRSF_RECORD_BEAN = "This record is not a GRSF record, thus it cannot be managed";
	protected static final String NO_ADMIN_ROLE = "Sorry but it seems you do not have the rights to manage records."
			+ " You are suggested to contact the VRE Manager if something is wrong with this.";

	// event bus shared with the portlet
	private HandlerManager eventBus = null;

	// the objects to be managed
	private ManageProductBean bean;
	private final static List<Status> STATUS = new ArrayList<Status>(Arrays.asList(Status.values()));

	// similar records and to connect widgets references
	private SimilarGRSFRecordWidget similarRecordPanel;
	private SuggestMerges suggestedMergesPanel;
	private ConnectToWidget connectWidget;

	private boolean updateSucceeded = false;

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
		loaderIcon.getElement().getStyle().setMarginRight(10, Unit.PX);

		// show modal
		manageProductModal.addStyleName("management-metadata-modal-style");
		//		manageProductModal.getElement().getStyle().setWidth(60, Unit.PCT);
		manageProductModal.show();

		manageProductModal.addHiddenHandler(new HiddenHandler() {

			@Override
			public void onHidden(HiddenEvent hiddenEvent) {
				if(updateSucceeded)
					Window.Location.reload();

			}
		});

		// async request to fetch the product
		retrieveProductBean(productIdentifier);
		listenEvents(this.eventBus);
	}


	/**
	 * Liste events
	 * @param eventBus
	 */
	private void listenEvents(HandlerManager eventBus) {
		eventBus.addHandler(EnableConfirmButtonEvent.TYPE, new  EnableConfirmButtonEventHandler() {

			@Override
			public void onEvent(EnableConfirmButtonEvent event) {
				confirmButton.setEnabled(true);
			}
		});
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

				if(!result){

					showInfo(NO_ADMIN_ROLE, AlertType.ERROR);

					// hide the form and disable the send button
					formUpdate.setVisible(false);
					confirmButton.setEnabled(false);
					loadingImage.setVisible(false);

					// ask to hide management panel
					if(eventBus != null){
						eventBus.fireEvent(new HideManagementPanelEvent());
						GWT.log("Hide management panel event sent");
					}
				}else{
					service.getProductBeanById(productIdentifier, new AsyncCallback<ManageProductBean>() {

						@Override
						public void onSuccess(ManageProductBean resBean) {

							if(resBean == null){
								showInfo(ERROR_ON_RETRIEVING_BEAN, AlertType.ERROR);
								formUpdate.setVisible(false);
								confirmButton.setEnabled(false);
							}else{
								bean = resBean;
								infoBlock.setVisible(false);

								// top: more or less fixed information
								GRSFNameTexBox.setText(bean.getTitle());
								shortNameTextBox.setText(bean.getShortName());
								shortNameTextBox.addChangeHandler(new ChangeHandler() {

									@Override
									public void onChange(ChangeEvent event) {
										eventBus.fireEvent(new EnableConfirmButtonEvent());
									}
								});

								semanticIdentifierTextBox.setText(bean.getSemanticIdentifier());
								productGrsfType.setText(bean.getGrsfType());
								currentStatus.setText(bean.getCurrentStatus().toString());
								traceabilityFlag.setValue(bean.isTraceabilityFlag());
								traceabilityFlag.setText("Traceability");
								traceabilityFlag.setTitle("Current value for this flag in the Knowledge Base is " + bean.isTraceabilityFlag());

								traceabilityFlag.addClickHandler(new ClickHandler() {

									@Override
									public void onClick(ClickEvent event) {
										eventBus.fireEvent(new EnableConfirmButtonEvent());
									}
								});

								// manage sources
								List<SourceRecord> availableSources = bean.getSources();
								panelForSourceItems.add(new SourceWidget(availableSources));

								// manage similar GRSF records, if any
								if(bean.getSimilarGrsfRecords() != null && !bean.getSimilarGrsfRecords().isEmpty()){
									List<SimilarGRSFRecord> availableGRSFSimilarRecords = bean.getSimilarGrsfRecords();
									similarRecordPanel = new SimilarGRSFRecordWidget(availableGRSFSimilarRecords, eventBus);
									panelForSimilarGRSFRecords.add(similarRecordPanel);
								}else
									similarGRSFRecordGroup.setVisible(false);

								// further suggested merges
								suggestedMergesPanel = new SuggestMerges(service, bean.getDomain(), eventBus);
								panelForFurtherMerges.add(suggestedMergesPanel);

								// prepare "connect" panel
								connectWidget = new ConnectToWidget(bean, service, eventBus);
								panelForConnectOtherRecords.add(connectWidget);

								// check for new status box
								List<Status> statusToShow = new ArrayList<Status>(STATUS);
								statusToShow.remove(bean.getCurrentStatus());
								statusToShow.remove(Status.To_be_Merged); // it cannot be set by a user

								// if the record isn't approved, then remove also archived
								if(!bean.getCurrentStatus().equals(Status.Approved))
									statusToShow.remove(Status.Archived);

								listBoxStatus.addItem("Select a new status");
								listBoxStatus.getElement().<SelectElement>cast().getOptions().getItem(0).setDisabled(true);
								for (Status availableStatus : statusToShow) {
									listBoxStatus.addItem(availableStatus.toString(), availableStatus.toString());
								}
								listBoxStatus.setSelectedIndex(0);

								listBoxStatus.addChangeHandler(new ChangeHandler() {

									@Override
									public void onChange(ChangeEvent event) {
										eventBus.fireEvent(new EnableConfirmButtonEvent());
									}
								});

								annotationArea.addChangeHandler(new ChangeHandler() {

									@Override
									public void onChange(ChangeEvent event) {
										eventBus.fireEvent(new EnableConfirmButtonEvent());
									}
								});

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

	@UiHandler("confirmButton")
	void onSaveButton(ClickEvent ce){

		String report = "";
		Set<String> hashtags = new HashSet<>();

		manageProductModal.setCloseVisible(false);
		cancelButton.setEnabled(false);
		confirmButton.setEnabled(false);
		loaderIcon.setVisible(true);

		// get short name
		bean.setShortNameUpdated(shortNameTextBox.getText());

		if(!bean.getShortName().equals(bean.getShortNameUpdated())){
			report += "\n- The GRSF Short Name has been changed to '" + bean.getShortNameUpdated() + "', from '" + bean.getShortName() + "'";
			hashtags.add(HashTagsOnUpdate.SHORTNAME_UPDATED.getString());
		}

		// evaluate the connections and the actions on them
		bean.setConnections(connectWidget.getConnectList());

		// add the connections for the report
		if(!bean.getConnections().isEmpty()){
			report += "\n- Suggested connections:";
			boolean addConnectionHashtag = false;
			for(ConnectedBean cb: bean.getConnections()){
				if(cb.isRemove())
					report += "\n\t - remove connection with record " + cb.getKnowledgeBaseId() + ";";
				else if(cb.isConnect()){
					addConnectionHashtag = true;
					report += "\n\t - add connection with record " + cb.getKnowledgeBaseId() + ";";
				}else
					report += "\n\t - keep this suggestion " + cb.getKnowledgeBaseId() + ";";
			}
			
			if(addConnectionHashtag)
				hashtags.add(HashTagsOnUpdate.CONNECT.getString());
		}

		// update similar records and to connect
		if(similarRecordPanel != null)
			bean.setSimilarGrsfRecords(similarRecordPanel.getSimilarRecords());
		else
			bean.setSimilarGrsfRecords(new ArrayList<SimilarGRSFRecord>(0));		

		// add the suggested ones, if any
		bean.getSimilarGrsfRecords().addAll(suggestedMergesPanel.getSimilarRecords());

		// set the merge operator on the bean if there is at least one merge to be done
		if(!bean.getSimilarGrsfRecords().isEmpty()){
			report += "\n- Suggested merges:";
			for(SimilarGRSFRecord sR: bean.getSimilarGrsfRecords()){
				if(sR.isSuggestedMerge()){
					bean.setMergesInvolved(true);
					report += "\n\t - merge the current record with record " + sR.getKnowledgeBaseId() + ";";
				}
			}
			if(bean.isMergesInvolved()){
				report += "\n- The update involves a merge operation;";
				hashtags.add(HashTagsOnUpdate.MERGE.getString());
			}
		}
		
		// set new values
		bean.setAnnotation(new HTML(annotationArea.getText().trim()).getText());

		if(bean.getAnnotation() != null && !bean.getAnnotation().isEmpty())
			report += "\n- Annotation message is: " + bean.getAnnotation() + ";";

		// traceability flag
		Boolean traceabilityNewValue = traceabilityFlag.getValue();
		boolean currentTraceabilitFlag = bean.isTraceabilityFlag();
		if(!traceabilityNewValue.equals(currentTraceabilitFlag)){
			report += "\n- Traceability flag has been changed to: '" + traceabilityNewValue + "';";
			if(traceabilityNewValue)
				hashtags.add(HashTagsOnUpdate.TRACEABILITY_FLAG_SET.getString());
			else
				hashtags.add(HashTagsOnUpdate.TRACEABILITY_FLAG_UNSET.getString());
		}
		
		// update the traceability flag
		bean.setTraceabilityFlag(traceabilityNewValue);
		
		// force the new status in the listbox
		if(bean.isMergesInvolved()){
			bean.setNewStatus(Status.To_be_Merged);
			report += "\n- The Status has been changed to '" + bean.getNewStatus().getOrigName() + "'.";
		}
		else if(listBoxStatus.getSelectedIndex() <= 0){
			// if the status has not be changed ...
			bean.setNewStatus(bean.getCurrentStatus());
			report += "\n- The Status is unchanged.";
		}
		else{
			bean.setNewStatus(Status.fromString(listBoxStatus.getSelectedItemText()));
			report += "\n- The Status has been changed to '" + bean.getNewStatus().getOrigName() + "'.";
			hashtags.add(bean.getNewStatus().getOrigName());
		}

		// set the report
		bean.setReport(report);

		// set hashtags
		bean.setHashtags(hashtags);

		GWT.log("Report is:\n" + report);

		service.notifyProductUpdate(bean, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void v) {

				showInfo(STATUS_UPDATE_SUCCESS, AlertType.SUCCESS);
				confirmButton.removeFromParent();
				formUpdate.setVisible(false);
				manageProductModal.setCloseVisible(true);
				cancelButton.setEnabled(true);
				cancelButton.setText("Ok");
				cancelButton.setType(ButtonType.INFO);
				loaderIcon.setVisible(false);
				updateSucceeded = true;
			}

			@Override
			public void onFailure(Throwable caught) {
				manageProductModal.setCloseVisible(true);
				cancelButton.setEnabled(true);
				confirmButton.setEnabled(true);
				loaderIcon.setVisible(false);
				showInfo(STATUS_UPDATE_ERROR + ": " + caught.getMessage(), AlertType.ERROR);
			}
		});

	}

	@UiHandler("cancelButton")
	void onCancelButton(ClickEvent ce){
		manageProductModal.hide();

		if(updateSucceeded)
			Window.Location.reload();
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
