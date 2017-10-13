package org.gcube.portlets.user.td.resourceswidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.resourceswidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.mime.MimeTypeSupport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ResourcesListViewDetailPanel extends SimpleContainer {
	private static final String THUMBNAIL_WIDTH = "160px";
	private static final String THUMBNAIL_HEIGHT = "160px";
	private static final String DESCRIPTION_HEIGHT = "70px";
	
	private CommonMessages msgsCommon;
	private ResourcesMessages msgs;
	
	private Image thumbnail;
	private TextField name;
	private TextArea description;
	private TextField creationDate;
	private VerticalLayoutContainer v;

	private ResourceTDDescriptor descriptor;

	private TextButton btnDelete;

	private TextButton btnSave;

	private TextButton btnOpen;

	private ResourcesListViewPanel parent;

	private HBoxLayoutContainer buttonMenu;

	public ResourcesListViewDetailPanel(ResourcesListViewPanel parent) {
		super();
		this.parent = parent;
		initMessages();
		init();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ResourcesMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void init() {
		addStyleName(ThemeStyles.get().style().border());
		// getElement().getStyle().setBackgroundColor("white");

	}

	public void setDescriptor(ResourceTDDescriptor descriptor) {
		this.descriptor = descriptor;
		if (v != null) {
			remove(v);
		}

		if (descriptor == null) {
			forceLayout();
			return;
		}

		SafeUri thumbnailPath;

		ResourceTDType resourceTDType = descriptor.getResourceType();
		if (resourceTDType == null) {
			thumbnailPath = ResourceBundle.INSTANCE.resources160().getSafeUri();
		} else {

			switch (resourceTDType) {
			case CHART:
				thumbnailPath = ResourceBundle.INSTANCE.chart160().getSafeUri();
				break;
			case CODELIST:
				thumbnailPath = ResourceBundle.INSTANCE.codelist160()
						.getSafeUri();
				break;
			case CSV:
				thumbnailPath = ResourceBundle.INSTANCE.csv160().getSafeUri();
				break;
			case GENERIC_FILE:
				thumbnailPath = ResourceBundle.INSTANCE.file160().getSafeUri();
				break;
			case GENERIC_TABLE:
				thumbnailPath = ResourceBundle.INSTANCE.table160().getSafeUri();
				break;
			case GUESSER:
				thumbnailPath = ResourceBundle.INSTANCE.resources160()
						.getSafeUri();
				break;
			case JSON:
				thumbnailPath = ResourceBundle.INSTANCE.json160().getSafeUri();
				break;
			case MAP:
				thumbnailPath = ResourceBundle.INSTANCE.gis160().getSafeUri();
				break;
			case SDMX:
				thumbnailPath = ResourceBundle.INSTANCE.sdmx160().getSafeUri();
				break;
			default:
				thumbnailPath = ResourceBundle.INSTANCE.resources160()
						.getSafeUri();
				break;

			}

		}
		ResourceTD resourceTD = descriptor.getResourceTD();

		if (resourceTD instanceof InternalURITD) {
			InternalURITD internalURITD = (InternalURITD) resourceTD;
			if (internalURITD.getThumbnailTD() != null
					&& internalURITD.getThumbnailTD().getUrl() != null) {
				thumbnailPath = UriUtils.fromTrustedString(internalURITD
						.getThumbnailTD().getUrl());
			} else {
				if (internalURITD.getMimeType() != null) {
					if (internalURITD.getMimeType().compareTo(
							MimeTypeSupport._gif.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(
									MimeTypeSupport._jpg.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(
									MimeTypeSupport._png.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(
									MimeTypeSupport._bmp.getMimeName()) == 0) {
						thumbnailPath = ResourceBundle.INSTANCE.picture160()
								.getSafeUri();
					}
				}
			}
		}

		thumbnail = new Image();
		thumbnail.setWidth(THUMBNAIL_WIDTH);
		thumbnail.setHeight(THUMBNAIL_HEIGHT);

		name = new TextField();
		FieldLabel nameLabel = new FieldLabel(name, msgs.nameLabel());
		nameLabel.setLabelAlign(LabelAlign.TOP);

		description = new TextArea();
		description.setHeight(DESCRIPTION_HEIGHT);
		FieldLabel descriptionLabel = new FieldLabel(description, msgs.descriptionLabel());
		descriptionLabel.setLabelAlign(LabelAlign.TOP);

		creationDate = new TextField();
		FieldLabel creationDateLabel = new FieldLabel(creationDate,
				msgs.creationDateLabel());
		creationDateLabel.setLabelAlign(LabelAlign.TOP);

		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.setPack(BoxLayoutPack.CENTER);
		h.add(thumbnail);

		createMenu();

		v.add(h, new VerticalLayoutData(1, -1, new Margins(20, 0, 10, 0)));
		v.add(nameLabel, new VerticalLayoutData(1, -1, new Margins(1)));
		v.add(descriptionLabel, new VerticalLayoutData(1, -1, new Margins(1)));
		v.add(creationDateLabel, new VerticalLayoutData(1, -1, new Margins(1)));
		v.add(buttonMenu, new VerticalLayoutData(1, -1, new Margins(0)));

		thumbnail.setUrl(thumbnailPath);
		name.setValue(descriptor.getName());
		description.setValue(descriptor.getDescription());
		creationDate.setValue(descriptor.getCreationDate());
		add(v);

		forceLayout();

	}

	protected void createMenu() {
		if (descriptor == null || descriptor.getResourceType() == null) {
			return;
		}

		btnOpen = new TextButton(msgs.btnOpenText());
		btnOpen.setIcon(ResourceBundle.INSTANCE.resources());
		btnOpen.setIconAlign(IconAlign.RIGHT);
		btnOpen.setToolTip(msgs.btnOpenToolTip());
		SelectHandler openHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				requestOpen();

			}
		};
		btnOpen.addSelectHandler(openHandler);

		btnSave = new TextButton(msgsCommon.btnSaveText());
		btnSave.setIcon(ResourceBundle.INSTANCE.save());
		btnSave.setIconAlign(IconAlign.RIGHT);
		btnSave.setToolTip(msgsCommon.btnSaveToolTip());
		SelectHandler saveHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				requestSave();

			}
		};
		btnSave.addSelectHandler(saveHandler);

		btnDelete = new TextButton(msgs.btnDeleteText());
		btnDelete.setIcon(ResourceBundle.INSTANCE.delete());
		btnDelete.setIconAlign(IconAlign.RIGHT);
		btnDelete.setToolTip(msgs.btnDeleteToolTip());
		SelectHandler removeHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				requestRemove();

			}
		};
		btnDelete.addSelectHandler(removeHandler);

		ResourceTDType resourceTDType = descriptor.getResourceType();
		switch (resourceTDType) {
		case CHART:
			btnOpen.setIcon(ResourceBundle.INSTANCE.chart());
			btnOpen.setVisible(true);
			btnSave.setVisible(true);
			btnDelete.setVisible(true);
			break;
		case CODELIST:
			btnOpen.setVisible(false);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;
		case CSV:
			btnOpen.setVisible(false);
			btnSave.setVisible(true);
			btnDelete.setVisible(true);
			break;
		case GENERIC_FILE:
			btnOpen.setIcon(ResourceBundle.INSTANCE.file());
			btnOpen.setVisible(true);
			btnSave.setVisible(true);
			btnDelete.setVisible(true);
			break;
		case GENERIC_TABLE:
			btnOpen.setVisible(false);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;
		case GUESSER:
			btnOpen.setVisible(false);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;
		case JSON:
			btnOpen.setVisible(false);
			btnSave.setVisible(true);
			btnDelete.setVisible(true);
			break;
		case MAP:
			btnOpen.setIcon(ResourceBundle.INSTANCE.gis());
			btnOpen.setVisible(true);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;
		case SDMX:
			btnOpen.setVisible(true);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;
		default:
			btnOpen.setVisible(false);
			btnSave.setVisible(false);
			btnDelete.setVisible(true);
			break;

		}

		buttonMenu = new HBoxLayoutContainer();
		buttonMenu.setPack(BoxLayoutPack.CENTER);
		buttonMenu.add(btnOpen, new BoxLayoutData(new Margins(5, 2, 5, 2)));
		buttonMenu.add(btnSave, new BoxLayoutData(new Margins(5, 2, 5, 2)));
		buttonMenu
				.add(btnDelete, new BoxLayoutData(new Margins(5, 2, 5, 2)));

	}

	private void requestOpen() {
		parent.requestOpen(descriptor);
	}

	private void requestSave() {
		parent.requestSave(descriptor);

	}

	private void requestRemove() {
		ArrayList<ResourceTDDescriptor> resources=new ArrayList<ResourceTDDescriptor>();
		resources.add(descriptor);
		parent.requestRemove(resources);

	}

}
