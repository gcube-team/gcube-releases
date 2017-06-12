package org.gcube.portlets.user.td.columnwidget.client.dimension;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.properties.TabResourcePropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class ConnectCodelistDialog extends Window implements
		CodelistSelectionListener {
	private static final String WIDTH = "500px";
	private static final String HEIGHT = "150px";
	private ArrayList<ConnectCodelistListener> listeners;

	private ComboBox<TabResource> comboDimensionType = null;
	private FieldLabel comboDimensionTypeLabel;
	private ListStore<TabResource> storeComboDimensionType;

	private ComboBox<ColumnData> comboColumnReferenceType = null;
	private FieldLabel comboColumnReferenceTypeLabel;
	private ListStore<ColumnData> storeComboColumnReferenceType;

	private EventBus eventBus;

	private TextButton btnConnect;
	private TextButton btnClose;
	private ConnectCodelistMessages msgs;
	private CommonMessages msgsCommon;

	public ConnectCodelistDialog(EventBus eventBus) {
		listeners = new ArrayList<ConnectCodelistListener>();
		this.eventBus = eventBus;
		initMessages();
		initWindow();
		create();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ConnectCodelistMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	

	protected void create() {
		final FramedPanel panel = new FramedPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);

		// Attach Codelist
		// comboDimensionType
		TabResourcePropertiesCombo propsDimensionType = GWT
				.create(TabResourcePropertiesCombo.class);
		storeComboDimensionType = new ListStore<TabResource>(
				propsDimensionType.id());

		comboDimensionType = new ComboBox<TabResource>(storeComboDimensionType,
				propsDimensionType.label());
		Log.trace("ComboDimensionType created");

		addHandlersForComboDimensionType(propsDimensionType.label());

		comboDimensionType.setEmptyText(msgs.comboDimensionTypeEmptyText());
		comboDimensionType.setWidth(191);
		comboDimensionType.setEditable(false);
		comboDimensionType.setTriggerAction(TriggerAction.ALL);

		comboDimensionTypeLabel = new FieldLabel(comboDimensionType, msgs.comboDimensionTypeLabel());

		// ColumnReferenceType
		ColumnDataPropertiesCombo propsColumnReferenceType = GWT
				.create(ColumnDataPropertiesCombo.class);
		storeComboColumnReferenceType = new ListStore<ColumnData>(
				propsColumnReferenceType.id());

		comboColumnReferenceType = new ComboBox<ColumnData>(
				storeComboColumnReferenceType, propsColumnReferenceType.label());
		Log.trace("ComboColumnReferenceType created");

		addHandlersForComboColumnReferenceType(propsColumnReferenceType.label());

		comboColumnReferenceType.setEmptyText(msgs.comboColumnReferenceTypeEmptyText());
		comboColumnReferenceType.setWidth(191);
		comboColumnReferenceType.setEditable(false);
		comboColumnReferenceType.setTriggerAction(TriggerAction.ALL);

		comboColumnReferenceTypeLabel = new FieldLabel(
				comboColumnReferenceType, msgs.comboColumnReferenceTypeLabel());

		// Buttons
		btnConnect = new TextButton(msgs.btnConnectText());
		btnConnect.setIcon(ResourceBundle.INSTANCE.codelistLink());
		btnConnect.setIconAlign(IconAlign.RIGHT);
		btnConnect.setToolTip(msgs.btnConnectToolTip());
		btnConnect.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Connect");
				connectCodelist();

			}

		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		BoxLayoutData boxLayoutData=new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnConnect, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(comboDimensionTypeLabel, new VerticalLayoutData(1, -1));
		v.add(comboColumnReferenceTypeLabel, new VerticalLayoutData(1, -1));
		v.add(flowButton, new VerticalLayoutData(1, 36,
				new Margins(5, 2, 5, 2)));

		panel.add(v);

		add(panel);

		comboColumnReferenceTypeLabel.setVisible(false);

	}

	protected void addHandlersForComboDimensionType(
			final LabelProvider<TabResource> labelProvider) {

		comboDimensionType.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboDimension TriggerClickEvent");
				callDialogCodelistSelection();
				comboDimensionType.collapse();

			}

		});

	}

	protected void addHandlersForComboColumnReferenceType(
			final LabelProvider<ColumnData> labelProvider) {

		comboColumnReferenceType
				.addSelectionHandler(new SelectionHandler<ColumnData>() {
					public void onSelection(SelectionEvent<ColumnData> event) {
						Log.debug("ComboColumnReferenceType selected: "
								+ event.getSelectedItem());

					}

				});

	}

	private void connectCodelist() {
		TabResource codelist = comboDimensionType.getCurrentValue();
		if (codelist == null) {
			Log.debug("No codelist selected");
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAValidCodelist());
		} else {
			ColumnData connection = comboColumnReferenceType.getCurrentValue();
			if (connection == null) {
				Log.debug("No connection selected");
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAValidColumn());
			} else {
				fireCompleted(connection);
			}
		}
	}

	protected void callDialogCodelistSelection() {
		CodelistSelectionDialog dialogCodelistSelection = new CodelistSelectionDialog(
				eventBus);
		dialogCodelistSelection.addListener(this);
		dialogCodelistSelection.show();
	}

	@Override
	public void selected(TabResource tabResource) {
		Log.debug("Selected Codelist: " + tabResource);
		storeComboDimensionType.clear();
		storeComboDimensionType.add(tabResource);
		storeComboDimensionType.commitChanges();
		comboDimensionType.setValue(tabResource);
		retrieveConnectedColumnData(tabResource);
	}

	@Override
	public void aborted() {
		Log.debug("Select Codelist Aborted");

	}

	@Override
	public void failed(String reason, String detail) {
		Log.error("Select Codelist Failed[reason: " + reason + " , detail:"
				+ detail + "]");

	}

	protected void retrieveConnectedColumnData(TabResource tabResource) {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(
				tabResource.getTrId(),
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving columns: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingColumnsHead(),
													msgs.errorRetrievingColumns());
								}
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						storeComboColumnReferenceType.clear();
						storeComboColumnReferenceType.addAll(result);
						storeComboColumnReferenceType.commitChanges();
						
						comboColumnReferenceTypeLabel.setVisible(true);
						comboColumnReferenceType.setMinListWidth(191);
						comboColumnReferenceTypeLabel.forceLayout();
						
					}
				});

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		setModal(true);
		setClosable(true);
		getHeader().setIcon(ResourceBundle.INSTANCE.codelistLink());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		fireAborted();
		hide();
	}

	public void addListener(ConnectCodelistListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ConnectCodelistListener listener) {
		listeners.remove(listener);
	}

	public void fireCompleted(ColumnData connection) {
		for (ConnectCodelistListener listener : listeners)
			listener.selectedConnectCodelist(connection);
		hide();
	}

	public void fireAborted() {
		for (ConnectCodelistListener listener : listeners)
			listener.abortedConnectCodelist();
		hide();
	}

	public void fireFailed(String reason, String details) {
		for (ConnectCodelistListener listener : listeners)
			listener.failedConnectCodelist(reason, details);
		hide();
	}

}
