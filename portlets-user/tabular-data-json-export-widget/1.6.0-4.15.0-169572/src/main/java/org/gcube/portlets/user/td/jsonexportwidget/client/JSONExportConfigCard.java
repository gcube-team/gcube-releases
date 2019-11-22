package org.gcube.portlets.user.td.jsonexportwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.destination.WorkspaceDestination;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.jsonexportwidget.client.grid.ColumnDataGridPanel;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author Giancarlo Panichi
 *         
 * 
 */
public class JSONExportConfigCard extends WizardCard {
	private static JSONExportWizardTDMessages msgs = GWT
			.create(JSONExportWizardTDMessages.class);
	private static final int LABEL_WIDTH = 128;
	private static final int LABEL_PAD_WIDTH = 2;
	private CommonMessages msgsCommon;
	
	private JSONExportSession exportSession;

	private Radio radioViewColumnExportTrue;
	private Radio radioViewColumnExportFalse;

	private ColumnDataGridPanel jsonColumnGridPanel;
	

	public JSONExportConfigCard(final JSONExportSession exportSession) {
		super(msgs.jsonExportConfigCardHead(), "");
		initMessages();
		if (exportSession == null) {
			Log.error("JSONExportSession is null");
		}
		this.exportSession = exportSession;

		FormPanel panel = createPanel();
		setContent(panel);

	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		// Export View Column
		radioViewColumnExportTrue = new Radio();
		radioViewColumnExportTrue.setBoxLabel(msgs
				.radioViewColumnExportTrueLabel());
		radioViewColumnExportTrue.setValue(true);

		radioViewColumnExportFalse = new Radio();
		radioViewColumnExportFalse.setBoxLabel(msgs
				.radioViewColumnExportFalseLabel());

		ToggleGroup exportViewColumnGroup = new ToggleGroup();
		exportViewColumnGroup.add(radioViewColumnExportTrue);
		exportViewColumnGroup.add(radioViewColumnExportFalse);

		HorizontalPanel viewColumnExportPanel = new HorizontalPanel();
		viewColumnExportPanel.add(radioViewColumnExportTrue);
		viewColumnExportPanel.add(radioViewColumnExportFalse);

		new ToolTip(viewColumnExportPanel, new ToolTipConfig(
				msgs.viewColumnExportPanelToolTip()));
		FieldLabel viewColumnExportPanelLabel = new FieldLabel(
				viewColumnExportPanel, msgs.viewColumnExportPanelLabel());
		viewColumnExportPanelLabel.setLabelWidth(LABEL_WIDTH);
		viewColumnExportPanelLabel.setLabelPad(LABEL_PAD_WIDTH);
		content.add(viewColumnExportPanelLabel);

		// Column Selection Grid
		jsonColumnGridPanel = new ColumnDataGridPanel(this);

		jsonColumnGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {

					}

				});

		content.add(jsonColumnGridPanel);

		return panel;
	}

	protected boolean getExportViewColumns() {
		if (radioViewColumnExportTrue.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setup() {
		Log.debug("JSONExportConfigCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("JSONExportConfigCard Call sayNextCard");
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);
		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		ArrayList<ColumnData> columns = jsonColumnGridPanel.getSelectedItems();
		if (columns.size() == 0) {
			d = new AlertMessageBox(msgsCommon.attention(),
					msgs.noColumnSelected());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			exportSession.setColumns(columns);
			exportSession.setExportViewColumns(getExportViewColumns());
			useWorkspaceDestination();
		}

	}

	protected void useWorkspaceDestination() {
		final WorkspaceDestination workspaceDestination = WorkspaceDestination.INSTANCE;
		exportSession.setDestination(workspaceDestination);
		retrieveTabularResource();
	}

	protected void retrieveTabularResource() {
		TDGWTServiceAsync.INSTANCE
				.getTabResourceInformation(new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						exportSession.setTabResource(result);
						goNext();
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {

							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								showErrorAndHide(msgsCommon.error(), msgs
										.errorRetrievingTabularResourceInfo(),
										caught.getLocalizedMessage(), caught);

							}
						}

					}

				});

	}

	protected void goNext() {
		try {
			/*
			 * DestinationSelectionCard destCard = new DestinationSelectionCard(
			 * exportSession); getWizardWindow().addCard(destCard);
			 * getWizardWindow().nextCard();
			 */

			Log.info("NextCard JSONWorkspaceSelectionCard");
			JSONWorkSpaceSelectionCard jsonWorkspaceSelectionCard = new JSONWorkSpaceSelectionCard(
					exportSession);
			getWizardWindow().addCard(jsonWorkspaceSelectionCard);
			getWizardWindow().nextCard();

		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

}
