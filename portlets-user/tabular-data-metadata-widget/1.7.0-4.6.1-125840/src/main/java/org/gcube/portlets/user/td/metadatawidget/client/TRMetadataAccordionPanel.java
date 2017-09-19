package org.gcube.portlets.user.td.metadatawidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRAgencyMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRDescriptionMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRNameMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRRightsMetadata;
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

public class TRMetadataAccordionPanel extends ContentPanel {
	protected EventBus eventBus;
	protected TRId trId;
	protected AccordionLayoutContainer con;
	protected AccordionLayoutAppearance appearance;

	public TRMetadataAccordionPanel(String name, TRId trId, EventBus eventBus) {
		super();
		setId(name);
		this.eventBus = eventBus;
		this.trId = trId;
		forceLayoutOnResize = true;
		setResize(true);
		init();

	}

	protected void getTabResourceMetadata(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getTRMetadata(trId,
				new AsyncCallback<ArrayList<TRMetadata>>() {

					public void onSuccess(ArrayList<TRMetadata> result) {
						addContentPanel(result);
						Log.debug("Retrived Metadata:" + result.toString());

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
		addStyleName("margin-10");
		// getHeader().setIcon(ExampleImages.INSTANCE.accordion());

		con = new AccordionLayoutContainer();

		con.setExpandMode(ExpandMode.SINGLE_FILL);

		add(con);

		appearance = GWT
				.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);

		getTabResourceMetadata(trId);
	}

	protected void addContentPanel(ArrayList<TRMetadata> result) {
		ContentPanel cp;

		int i, size;
		size = result.size();
		for (i = 0; i < size; i++) {
			TRMetadata trMetadata = result.get(i);
			cp = new ContentPanel(appearance);
			cp.setAnimCollapse(false);
			cp.setCollapsible(true);
			cp.setResize(true);
			cp.collapse();
			cp.setHeadingText(trMetadata.getTitle());

			if (trMetadata instanceof TRDescriptionMetadata) {
				cp.add(layoutTRDescriptionMetadata((TRDescriptionMetadata) trMetadata));
			} else {
				if (trMetadata instanceof TRNameMetadata) {
					cp.add(layoutTRNameMetadata((TRNameMetadata) trMetadata));
				} else {
					if (trMetadata instanceof TRAgencyMetadata) {
						cp.add(layoutTRAgencyMetadata((TRAgencyMetadata) trMetadata));
					} else {

						if (trMetadata instanceof TRRightsMetadata) {
							cp.add(layoutTRRightsMetadata((TRRightsMetadata) trMetadata));
						} else {

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
	}

	public void init() {
		addAccordion();

	}

	protected VerticalLayoutContainer layoutTRAgencyMetadata(
			TRAgencyMetadata trMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField versionField = new TextField();
		versionField.setReadOnly(true);
		versionField.setValue(trMetadata.getValue());
		vMetadata.add(new FieldLabel(versionField, "Agency"),
				new VerticalLayoutData(-1, -1, new Margins(2)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTRNameMetadata(
			TRNameMetadata trMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField versionField = new TextField();
		versionField.setReadOnly(true);
		versionField.setValue(trMetadata.getValue());
		vMetadata.add(new FieldLabel(versionField, "Name"),
				new VerticalLayoutData(-1, -1, new Margins(2)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTRDescriptionMetadata(
			TRDescriptionMetadata trMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField versionField = new TextField();
		versionField.setReadOnly(true);
		versionField.setValue(trMetadata.getValue());
		vMetadata.add(new FieldLabel(versionField, "Description"),
				new VerticalLayoutData(-1, -1, new Margins(2)));
		return vMetadata;
	}

	protected VerticalLayoutContainer layoutTRRightsMetadata(
			TRRightsMetadata trMetadata) {
		VerticalLayoutContainer vMetadata = new VerticalLayoutContainer();
		TextField versionField = new TextField();
		versionField.setReadOnly(true);
		versionField.setValue(trMetadata.getValue());
		vMetadata.add(new FieldLabel(versionField, "Rights"),
				new VerticalLayoutData(-1, -1, new Margins(2)));
		return vMetadata;
	}
}
