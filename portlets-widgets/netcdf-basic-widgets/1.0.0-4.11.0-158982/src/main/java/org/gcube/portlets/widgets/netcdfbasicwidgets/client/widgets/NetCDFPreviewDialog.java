package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import java.util.ArrayList;
import java.util.Set;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEventType;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.model.NetCDFDataModel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource.NetCDFBasicResources;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.util.GWTMessages;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFPreviewDialog extends DialogBox implements SelectVariableEvent.HasSelectVariableEventHandler {
	
	private static final String TABPANEL_HEIGHT = "400px";
	private static final String TABPANEL_WIDTH = "700px";

	private static final NetCDFPreviewMessages messages = GWT.create(NetCDFPreviewMessages.class);

	private HandlerRegistration resizeHandlerRegistration;
	private Node closeEventTarget = null;
	private int zIndex = -1;
	private NetCDFDataModel netCDFDataModel;

	private VariablesPanel variablesPanel;
	private DetailPanel detailPanel;
	private SamplePanel samplePanel;
	private InfoPanel infoPanel;
	private String url;

	// private DataGrid<VariableData> dataGrid;


	public NetCDFPreviewDialog(String url) {
		try {
			GWT.log("PublicLink: " + url);
			this.url = url;
			initWindow();
			initHandler();
			addToolIcon();
			create();
		} catch (Throwable e) {
			GWT.log(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void initWindow() {
		GWT.log(messages.dialogTitle());
		NetCDFBasicResources.INSTANCE.netCDFBasicCSS().ensureInjected();
		setModal(true);
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setText(messages.dialogTitle());
		// setHeight(DIALOG_HEIGHT);
		// setWidth(DIALOG_WIDTH);
	}

	private void initHandler() {
		resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				center();

			}
		});

	}

	private void create() {

		netCDFDataModel = new NetCDFDataModel(url);
		// Create a tab panel
		final TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
		tabPanel.setAnimationDuration(1000);
		tabPanel.setAnimationVertical(true);
		tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
		tabPanel.setHeight(TABPANEL_HEIGHT);
		tabPanel.setWidth(TABPANEL_WIDTH);

		// Variable TabPanel
		variablesPanel = new VariablesPanel(netCDFDataModel);
		tabPanel.add(variablesPanel, messages.variablesTab());

		// Detail TabPanel
		detailPanel = new DetailPanel(netCDFDataModel);
		tabPanel.add(detailPanel, messages.detailTab());

		// Sample TabPanel
		samplePanel = new SamplePanel(netCDFDataModel);
		tabPanel.add(samplePanel, messages.sampleTab());

		// Info TabPanel
		infoPanel = new InfoPanel(netCDFDataModel);
		tabPanel.add(infoPanel, messages.infoTab());

		// Return the content
		tabPanel.selectTab(0);
		tabPanel.ensureDebugId("netcdfTabPanel");

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				int tabId = event.getSelectedItem();
				Widget tabWidget = tabPanel.getWidget(tabId);
				if (tabWidget != null) {
					if (tabWidget instanceof VariablesPanel) {
						VariablesPanel variablesPanel = (VariablesPanel) tabWidget;
						variablesPanel.refresh();
					} else {
						if (tabWidget instanceof DetailPanel) {
							DetailPanel detailPanel = (DetailPanel) tabWidget;
							detailPanel.refresh();
						} else {
							if (tabWidget instanceof InfoPanel) {
								InfoPanel infoPanel = (InfoPanel) tabWidget;
								infoPanel.refresh();
							} else {

							}
						}
					}
				}
			}
		});

		// Create a table to layout the content
		// VerticalPanel dialogContents = new VerticalPanel();
		// dialogContents.setSpacing(4);
		// dialogContents.add(tabPanel);

		// Add Button
		Button btnSave = new Button("Save");
		btnSave.getElement().getStyle().setMarginLeft(4, Unit.PX);
		btnSave.getElement().getStyle().setMarginRight(4, Unit.PX);
		btnSave.setTabIndex(10003);
		btnSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				btnSavePressed();

			}
		});

		Button btnClose = new Button("Close");
		btnClose.getElement().getStyle().setMarginLeft(4, Unit.PX);
		btnClose.getElement().getStyle().setMarginRight(4, Unit.PX);
		btnClose.setTabIndex(10004);
		btnClose.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				btnClosePressed();

			}
		});

		FlowPanel buttonPack = new FlowPanel();
		buttonPack.setWidth("100%");
		// buttonPack.add(btnSave);
		buttonPack.add(btnClose);

		/*
		 * HorizontalPanel hp=new HorizontalPanel(); hp.add(buttonPack);
		 * hp.setCellHorizontalAlignment(buttonPack,
		 * HasHorizontalAlignment.ALIGN_CENTER);
		 */

		DockPanel dockPanel = new DockPanel();
		dockPanel.setSpacing(4);

		dockPanel.add(tabPanel, DockPanel.CENTER);
		dockPanel.add(buttonPack, DockPanel.SOUTH);
		dockPanel.setCellHorizontalAlignment(buttonPack, DockPanel.ALIGN_CENTER);

		dockPanel.setWidth("100%");
		setWidget(dockPanel);
		center();

	}

	@Override
	public void show() {
		super.show();
		center();
	};

	private void closeOnServer() {
		if (netCDFDataModel != null) {
			netCDFDataModel.close();
		}
	}

	private void btnClosePressed() {
		SelectVariableEvent event = new SelectVariableEvent(SelectVariableEventType.Aborted);
		fireEvent(event);
		hide();

	}

	private void btnSavePressed() {
		Set<VariableData> selected = variablesPanel.seleceted();
		if (selected == null || selected.isEmpty()) {
			GWTMessages.alert("Attention", "Select a layer!", zIndex);
		} else {
			SelectVariableEvent event = new SelectVariableEvent(SelectVariableEventType.Completed);
			event.setVariables(new ArrayList<VariableData>(selected));
			fireEvent(event);
			hide();
		}

	}

	private void addToolIcon() {

		// get the "dialogTopRight" class td
		Element dialogTopRight = getCellElement(0, 2);

		// close button image html
		dialogTopRight.setInnerHTML("<div  class='"
				+ NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getDialogToolButtonText() + "'>" + "<img src='"
				+ NetCDFBasicResources.INSTANCE.toolButtonClose20().getSafeUri().asString() + "' class='"
				+ NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getDialogToolButtonIcon() + "' /></div>");

		// set the event target
		closeEventTarget = dialogTopRight.getChild(0).getChild(0);
	}

	@Override
	public void hide() {
		closeOnServer();
		if (resizeHandlerRegistration != null) {
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide();
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK) && isCloseEvent(nativeEvent)) {
			final SelectVariableEvent selectVariableEvent = new SelectVariableEvent(SelectVariableEventType.Aborted);
			fireEvent(selectVariableEvent);
			this.hide();
		}
		super.onPreviewNativeEvent(event);
	}

	// see if the click target is the close button
	private boolean isCloseEvent(NativeEvent event) {
		return event.getEventTarget().equals(closeEventTarget); // compares
																// equality of
																// the
																// underlying
																// DOM elements
	}

	@Override
	public HandlerRegistration addSelectVariableEventHandler(SelectVariableEvent.SelectVariableEventHandler handler) {
		return addHandler(handler, SelectVariableEvent.getType());
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		getGlassElement().getStyle().setZIndex(zIndex);
		getElement().getStyle().setZIndex(zIndex + 1);

	}

	public int getZIndex() {
		return zIndex;
	}

}
