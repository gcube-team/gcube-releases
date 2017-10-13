package org.gcube.portlets.user.td.metadatawidget.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabDescriptionsMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabExportMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabGenericMapMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabImportMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabNamesMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabVersionMetadata;
import org.gcube.portlets.user.td.metadatawidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class TableMetadataAccordionPanel extends ContentPanel {
	protected EventBus eventBus;
	protected TRId trId;
	protected AccordionLayoutContainer con;
	protected AccordionLayoutAppearance appearance;

	public TableMetadataAccordionPanel(String name, TRId trId, EventBus eventBus) {
		super();
		setId(name);
		this.eventBus = eventBus;
		this.trId = trId;
		forceLayoutOnResize = true;
		setResize(true);
		init();

	}

	protected void getTabResourceMetadata(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getTableMetadata(trId,
				new AsyncCallback<ArrayList<TabMetadata>>() {

					public void onSuccess(ArrayList<TabMetadata> result) {
						Log.debug("Retrived Metadata:" + result.toString());
						addContentPanel(result);

					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error retrienving tabular resource metadata: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Error retrienving tabular resource metadata: "
												+ caught.getLocalizedMessage());

							}
						}
					}

				});
	}

	protected void addAccordion() {
		setHeadingText("Metadata");
		setBodyBorder(false);

		getTabResourceMetadata(trId);
	}

	protected void addContentPanel(ArrayList<TabMetadata> result) {
		con = new AccordionLayoutContainer();

		con.setExpandMode(ExpandMode.SINGLE_FILL);

		appearance = GWT
				.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);

		ContentPanel cp;

		int i, size;
		size = result.size();
		for (i = 0; i < size; i++) {
			TabMetadata tabMetadata = result.get(i);
			cp = new ContentPanel(appearance);
			cp.setAnimCollapse(false);
			cp.setCollapsible(true);
			cp.setResize(true);
			cp.collapse();
			cp.setHeadingText(tabMetadata.getTitle());
			cp.setBorders(false);
			cp.setBodyStyle("margin:0px;");
			cp.setBodyBorder(false);

			if (tabMetadata instanceof TabDescriptionsMetadata) {
				cp.add(layoutTabDescriptionsMapMetadata((TabDescriptionsMetadata) tabMetadata));
			} else {
				if (tabMetadata instanceof TabNamesMetadata) {
					cp.add(layoutTabNamesMetadata((TabNamesMetadata) tabMetadata));
				} else {
					if (tabMetadata instanceof TabImportMetadata) {
						cp.add(layoutTabImportMetadata((TabImportMetadata) tabMetadata));
					} else {
						if (tabMetadata instanceof TabExportMetadata) {
							cp.add(layoutTabExportMetadata((TabExportMetadata) tabMetadata));
						} else {
							if (tabMetadata instanceof TabVersionMetadata) {
								cp.add(layoutTabVersionMetadata((TabVersionMetadata) tabMetadata));
							} else {
								if (tabMetadata instanceof TabGenericMapMetadata) {
									cp.add(layoutTabGenericMapMetadata((TabGenericMapMetadata) tabMetadata));
								} else {
								}
							}
						}

					}
				}
			}
			if (i == 0) {
				cp.expand();
				con.setActiveWidget(cp);
			}
			con.add(cp);
		}
		// doLayout();
		add(con);

	}

	public void init() {
		addAccordion();

	}

	protected VerticalLayoutContainer layoutTabVersionMetadata(
			TabVersionMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField versionField = new TextField();
		versionField.setReadOnly(true);
		versionField.setValue(tabMetadata.getVersion());
		vMetadata.add(new FieldLabel(versionField, "Version"),
				new VerticalLayoutData(1, -1, new Margins(1)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTabNamesMetadata(
			TabNamesMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TabNamesMetadataGrid namesMetadataGrid = new TabNamesMetadataGrid(
				tabMetadata);
		vMetadata.add(namesMetadataGrid.getGrid(), new VerticalLayoutData(1,
				-1, new Margins(1)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTabDescriptionsMapMetadata(
			TabDescriptionsMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TabDescriptionsMetadataGrid descriptionsMetadataGrid = new TabDescriptionsMetadataGrid(
				tabMetadata);
		vMetadata.add(descriptionsMetadataGrid.getGrid(),
				new VerticalLayoutData(1, -1, new Margins(1)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTabGenericMapMetadata(
			TabGenericMapMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();

		HashMap<String, String> genericMap = tabMetadata.getMetadataMap();
		for (String key : genericMap.keySet()) {
			TextField pointField = new TextField();
			pointField.setReadOnly(true);
			pointField.setValue(genericMap.get(key));
			vMetadata.add(new FieldLabel(pointField, key),
					new VerticalLayoutData(1, -1, new Margins(1)));
		}
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTabImportMetadata(
			TabImportMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField pointField = new TextField();
		pointField.setReadOnly(true);
		pointField.setValue(tabMetadata.getSourceType());
		vMetadata.add(new FieldLabel(pointField, "Source"),
				new VerticalLayoutData(1, -1, new Margins(1)));

		TextField urlField = new TextField();
		urlField.setReadOnly(true);
		urlField.setValue(((TabImportMetadata) tabMetadata).getUrl());
		vMetadata.add(new FieldLabel(urlField, "Url"), new VerticalLayoutData(
				1, -1, new Margins(1)));

		TextField dateField = new TextField();
		dateField.setReadOnly(true);
		dateField.setValue(((TabImportMetadata) tabMetadata).getImportDate());
		vMetadata.add(new FieldLabel(dateField, "Date"),
				new VerticalLayoutData(1, -1, new Margins(1)));

		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTabExportMetadata(
			TabExportMetadata tabMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();

		TextField pointField = new TextField();
		pointField.setReadOnly(true);
		pointField.setValue(((TabExportMetadata) tabMetadata)
				.getDestinationType());
		vMetadata.add(new FieldLabel(pointField, "Destination"),
				new VerticalLayoutData(1, -1, new Margins(1)));

		TextField urlField = new TextField();
		urlField.setReadOnly(true);
		urlField.setValue(((TabExportMetadata) tabMetadata).getUrl());
		vMetadata.add(new FieldLabel(urlField, "Url"), new VerticalLayoutData(
				1, -1, new Margins(1)));

		TextField dateField = new TextField();
		dateField.setReadOnly(true);
		dateField.setValue(((TabExportMetadata) tabMetadata).getExportDate());
		vMetadata.add(new FieldLabel(dateField, "Date"),
				new VerticalLayoutData(1, -1, new Margins(1)));
		return vMetadata;
	}
}
