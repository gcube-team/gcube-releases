package org.gcube.portlets.user.td.openwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.openwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.openwidget.client.resources.ResourceBundleTDOpen;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabResourceInfoDialog extends Dialog {
	private static final int HEIGHT = 500;
	private static final int WIDTH = 500;
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	private TabResource tabResource;
	private TabResourcesInfoDialogMessages msgs;
	private EventBus eventBus;
	private ArrayList<LicenceData> licencesList;
	
	public TabResourceInfoDialog(TabResource tabResource, EventBus eventBus) {
		this.tabResource = tabResource;
		this.eventBus=eventBus;
		this.msgs = GWT.create(TabResourcesInfoDialogMessages.class);
		initWindow();
		retrieveLicencesList();
	}

	protected void initWindow() {
		setModal(true);
		setHeadingText(msgs.tabResourceInfoDialogHeadingText());
		getHeader().setIcon(ResourceBundleTDOpen.INSTANCE.information());
		setPredefinedButtons(PredefinedButton.OK);
		setHideOnButtonClick(true);
		setButtonAlign(BoxLayoutPack.CENTER);
		setWidth(WIDTH);
		setHeight(HEIGHT);
		

	}

	protected void create() {
		FieldSet configurationFieldSet = new FieldSet();
		configurationFieldSet.setHeadingText(msgs.tabResourceDetailFieldSet());
		configurationFieldSet.setCollapsible(false);
		configurationFieldSet.setBorders(true);

		VerticalLayoutContainer configurationFieldSetLayout = new VerticalLayoutContainer();
		configurationFieldSet.add(configurationFieldSetLayout,
				new MarginData(0));

		TextField trName = new TextField();
		trName.setValue(tabResource.getName());
		trName.setReadOnly(true);
		FieldLabel trNameLabel = new FieldLabel(trName, msgs.nameLabel());
		configurationFieldSetLayout.add(trNameLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));
		
		IconButton trBtnLock = new IconButton();
		if(tabResource.isLocked()){
			trBtnLock.setIcon(ResourceBundleTDOpen.INSTANCE.lock());
		} else {
			trBtnLock.setIcon(ResourceBundleTDOpen.INSTANCE.lockOpen());
		}
		
		FieldLabel trLockLabel = new FieldLabel(trBtnLock, msgs.lockLabel());
		
		configurationFieldSetLayout.add(trLockLabel, new VerticalLayoutData(
				-1, -1, new Margins(0)));
		
		
		TextArea trDescription = new TextArea();
		trDescription.setValue(tabResource.getDescription());
		trDescription.setReadOnly(true);
		FieldLabel trDescriptionLabel = new FieldLabel(trDescription,
				msgs.descriptionLabel());
		configurationFieldSetLayout.add(trDescriptionLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));
		

		TextField trType = new TextField();
		trType.setValue(tabResource.getTabResourceType());
		trType.setReadOnly(true);
		FieldLabel trTypeLabel = new FieldLabel(trType, msgs.typeLabel());
		configurationFieldSetLayout.add(trTypeLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));

		TextField trAgency = new TextField();
		trAgency.setValue(tabResource.getAgency());
		trAgency.setReadOnly(true);
		FieldLabel trAgencyLabel = new FieldLabel(trAgency, msgs.agencyLabel());
		configurationFieldSetLayout.add(trAgencyLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));		

		TextField trCreationDate = new TextField();
		try {
		trCreationDate.setValue(sdf.format(tabResource.getDate()));
		} catch(Throwable e){
			Log.error("Error parsing creation date: "+e.getLocalizedMessage());
		}
		
		trCreationDate.setReadOnly(true);
		FieldLabel trCreationDateLabel = new FieldLabel(trCreationDate,
				msgs.dateLabel());
		configurationFieldSetLayout.add(trCreationDateLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		
		TextField trTableType = new TextField();
		trTableType.setValue(tabResource.getTableTypeName());
		trTableType.setReadOnly(true);
		FieldLabel trTableTypeLabel = new FieldLabel(trTableType, msgs.tableTypeNameLabel());
		configurationFieldSetLayout.add(trTableTypeLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));
		
		TextArea trRight = new TextArea();
		trRight.setValue(tabResource.getRight());
		trRight.setReadOnly(true);
		FieldLabel trRightLabel = new FieldLabel(trRight, msgs.rightLabel());
		configurationFieldSetLayout.add(trRightLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));
		
		DateField trValidFromField = new DateField();
		trValidFromField.setReadOnly(true);
		FieldLabel trValidFromLabel=new FieldLabel(trValidFromField, msgs.validFromLabel());
		trValidFromField.clear();
		if (tabResource.getValidFrom() == null) {
			Log.debug("ValidFrom null or empty");
		} else {
			trValidFromField.setValue(tabResource.getValidFrom());
		}
		configurationFieldSetLayout.add(trValidFromLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));
		
		
		DateField trValidUntilToField = new DateField();
		trValidUntilToField.setReadOnly(true);
		FieldLabel trValidUntilToLabel= new FieldLabel(trValidUntilToField,
				msgs.validUntilToLabel());
		trValidUntilToField.clear();
		if (tabResource.getValidUntilTo() == null) {
			Log.debug("ValidUntilTo null or empty");
		} else {
			trValidUntilToField.setValue(tabResource.getValidUntilTo());
		}
		configurationFieldSetLayout.add(trValidUntilToLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));
		
		String licence="";
		for(LicenceData licenceData:licencesList){
			if(licenceData.getLicenceId().compareTo(tabResource.getLicence())==0){
				licence=licenceData.getLicenceName();
				break;
			}
		}
		
		TextField trLicence = new TextField();
		trLicence.setValue(licence);
		trLicence.setReadOnly(true);
		FieldLabel trLicenceLabel = new FieldLabel(trLicence, msgs.licencesLabel());
		configurationFieldSetLayout.add(trLicenceLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));
		
		
		TextField trOwner = new TextField();
		trOwner.setValue(tabResource.getOwnerLogin());
		trOwner.setReadOnly(true);
		FieldLabel trOwnerLabel = new FieldLabel(trOwner, msgs.ownerLabel());
		configurationFieldSetLayout.add(trOwnerLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));
		
		
		
		IconButton trBtnShare = new IconButton();
		trBtnShare.setIcon(ResourceBundleTDOpen.INSTANCE.share());
		FieldLabel trShareLabel = new FieldLabel(trBtnShare, msgs.shareLabel());
		ArrayList<Contacts> contacts = tabResource.getContacts();
		if (contacts != null && contacts.size() > 0) {
			trShareLabel.setVisible(true);
		} else {
			trShareLabel.setVisible(false);
		}
		configurationFieldSetLayout.add(trShareLabel, new VerticalLayoutData(
				-1, -1, new Margins(0)));
		
		
		CheckBox trValidField = new CheckBox();
		trValidField.setValue(tabResource.isValid());
		trValidField.setReadOnly(true);
		FieldLabel trValidLabel=new FieldLabel(trValidField, msgs.validLabel());
		configurationFieldSetLayout.add(trValidLabel, new VerticalLayoutData(
				-1, -1, new Margins(0)));

		
		CheckBox trFinalizedField = new CheckBox();
		trFinalizedField.setValue(tabResource.isFinalized());
		trFinalizedField.setReadOnly(true);
		FieldLabel trFinalizedLabel=new FieldLabel(trFinalizedField, msgs.finalizedLabel());
		configurationFieldSetLayout.add(trFinalizedLabel, new VerticalLayoutData(
				-1, -1, new Margins(0)));
		
		add(configurationFieldSet, new MarginData(0));

	}
	
	protected void retrieveLicencesList() {
		TDGWTServiceAsync.INSTANCE
				.getLicences(new AsyncCallback<ArrayList<LicenceData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error retrieving licences:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving licences.");
						}

					}

					public void onSuccess(ArrayList<LicenceData> result) {
						Log.trace("loaded " + result.size() + " LicenceData");
						licencesList = result;
						create();
						
					}

				});

	}
	

}
