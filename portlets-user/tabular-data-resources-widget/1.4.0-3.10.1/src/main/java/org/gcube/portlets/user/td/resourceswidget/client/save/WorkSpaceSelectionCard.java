/**
 * 
 */
package org.gcube.portlets.user.td.resourceswidget.client.save;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectPanel;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class WorkSpaceSelectionCard extends WizardCard {
	private static SaveResourceMessages msgs=GWT.create(SaveResourceMessages.class);
	private SaveResourceSession saveResourceSession;
	private WorkSpaceSelectionCard thisCard;
	private TextField fileName;
	private TextField fileDescription;
	
	private VerticalLayoutContainer p;
	private WorkspaceExplorerSelectPanel wpanel;
	private CommonMessages msgsCommon;

	public WorkSpaceSelectionCard(final SaveResourceSession saveResourceSession) {
		super(msgs.workspaceSelectionCardHead(), "");
		initMessages();
		this.saveResourceSession = saveResourceSession;
		thisCard = this;

		FramedPanel formPanel = new FramedPanel();
		formPanel.setHeaderVisible(false);
		p = new VerticalLayoutContainer();
		formPanel.setWidget(p);

		fileName = new TextField();
		fileName.setAllowBlank(false);
		fileName.addValidator(new EmptyValidator<String>());
		fileName.setWidth("410px");
		fileName.setValue(saveResourceSession.getFileName());
		
	
		p.add(new FieldLabel(fileName, msgs.nameLabel()), new VerticalLayoutData(1,
				-1));

		fileDescription = new TextField();
		fileDescription.setAllowBlank(false);
		fileDescription.addValidator(new EmptyValidator<String>());
		fileDescription.setWidth("410px");
		fileDescription.setValue(saveResourceSession.getFileDescription());
		p.add(new FieldLabel(fileDescription, msgs.descriptionLabel()),
				new VerticalLayoutData(1, -1));
		
		///
		Log.debug("Set Workspace Panel");
		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.FOLDER);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.add(ItemType.FOLDER);
		
		wpanel = new WorkspaceExplorerSelectPanel(
				msgs.workspaceExplorerSelectionPanelHead(),selectableTypes, showableTypes);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {
				Log.debug("Selected Item:" + item);
				if (item.getType() == ItemType.FOLDER) {
					thisCard.saveResourceSession.setItemId(item.getId());
				} else {
					thisCard.saveResourceSession.setItemId(null);
					Log.debug("Item type:" + item.getType());
				}
			}

			@Override
			public void onFailed(Throwable throwable) {
				Log.error("Error in workspaceExplorer: "
						+ throwable.getLocalizedMessage());
				throwable.printStackTrace();
			}

			@Override
			public void onAborted() {
				Log.debug("WorkspaceExplorer Aborted");

			}

			@Override
			public void onNotValidSelection() {
				thisCard.saveResourceSession.setItemId(null);
				
			}
		};

		wpanel.addWorkspaceExplorerSelectNotificationListener(handler);
		
		
		
		///
		p.add(new FieldLabel(wpanel, msgs.workspaceExplorerSelectionPanelLabel()), new VerticalLayoutData(1, 1));
	
		setContent(formPanel);

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}

	protected void checkExportData() {
		Log.debug("File Name:" + fileName.getCurrentValue() + " Item id: "
				+ saveResourceSession.getItemId());
		fileName.disable();
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);
				fileName.enable();
			}
		};
		if (fileName.getCurrentValue() != null
				&& !fileName.getCurrentValue().isEmpty() && fileName.validate()) {
			if (fileDescription.getCurrentValue() != null
					&& !fileDescription.getCurrentValue().isEmpty()
					&& fileDescription.validate()) {

				if (saveResourceSession.getItemId() != null) {
					saveResourceSession.setFileName(fileName.getCurrentValue());
					saveResourceSession.setFileDescription(fileDescription
							.getCurrentValue());
					goNext();
				} else {
					d = new AlertMessageBox(msgsCommon.attention(), msgs.attentionNoFolderSelected());
					d.addHideHandler(hideHandler);
					d.setModal(false);
					d.show();

				}
			} else {
				d = new AlertMessageBox(msgsCommon.attention(),
						msgs.attentionNoValidFileDescription());
				d.addHideHandler(hideHandler);
				d.setModal(false);
				d.show();

			}
		} else {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.attentionNoValidFileName());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		}

	}

	@Override
	public void setup() {
		Log.debug("WorkSpaceSelectionCard Call Setup ");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("WorkSpaceSelectionCard Call sayNextCard:" + wpanel);
				checkExportData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.debug("Remove WorkSpaceSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		setBackButtonVisible(true);
		setEnableBackButton(true);
		setEnableNextButton(true);
	
	}

	protected void goNext() {
		OperationInProgressCard csvOperationInProgressCard = new OperationInProgressCard(
				saveResourceSession);
		getWizardWindow().addCard(csvOperationInProgressCard);
		Log.info("NextCard OperationInProgressCard");
		getWizardWindow().nextCard();
	}

}
