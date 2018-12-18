package org.gcube.portlets_widgets.catalogue_sharing_widget.client;

import org.gcube.portlets_widgets.catalogue_sharing_widget.shared.ItemUrls;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ShareCatalogueWidget extends Composite {

	private static ShareCatalogueWidgetUiBinder uiBinder = GWT
			.create(ShareCatalogueWidgetUiBinder.class);

	interface ShareCatalogueWidgetUiBinder extends
	UiBinder<Widget, ShareCatalogueWidget> {
	}

	public static final ShareServicesAsync ckanServices = GWT.create(ShareServices.class);

	public static final String SERVICE_UNAVAILABLE = "The service is currently unavailable, retry later";

	@UiField
	Icon loadingIcon;

	@UiField
	AlertBlock errorBlock;

	@UiField
	Form  formWithInformation;

	@UiField
	TextBox itemShortUrl;
	//
	//	@UiField
	//	TextBox itemTitle;
	//
	//	@UiField
	//	TextBox itemName;

	@UiField
	TextArea itemLongUrl;

	@UiField
	Modal modalShareLink;

	public ShareCatalogueWidget(String itemUUID) {
		initWidget(uiBinder.createAndBindUi(this));
		modalShareLink.show();
		modalShareLink.setKeyboard(true);

		ckanServices.getPackageUrl(itemUUID, new AsyncCallback<ItemUrls>() {

			@Override
			public void onSuccess(ItemUrls result) {

				if(result == null){

					onError(null);

				}else{

					loadingIcon.setVisible(false);
					formWithInformation.setVisible(true);
					itemShortUrl.setText(result.getShortUrl() == null ? "" : result.getShortUrl());
					itemLongUrl.setText(result.getUrl() == null ? "" : result.getUrl());
					//					itemTitle.setText(result.getProductTitle() == null ? "" : result.getProductTitle());
					//					itemName.setText(result.getProductName() == null ? "" : result.getProductName());

					itemShortUrl.getElement().getStyle().setCursor(Cursor.DEFAULT);
					itemLongUrl.getElement().getStyle().setCursor(Cursor.DEFAULT);


					itemLongUrl.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							itemLongUrl.selectAll();

						}
					});

					itemLongUrl.addDoubleClickHandler(new DoubleClickHandler() {
						@Override
						public void onDoubleClick(DoubleClickEvent event) {
							itemLongUrl.selectAll();
						}
					});

					itemShortUrl.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							itemShortUrl.selectAll();

						}
					});

					itemShortUrl.addDoubleClickHandler(new DoubleClickHandler() {
						@Override
						public void onDoubleClick(DoubleClickEvent event) {
							itemShortUrl.selectAll();
						}
					});

					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
						@Override
						public void execute() {	
							itemLongUrl.setVisible(true);
							itemLongUrl.setFocus(true);
							itemLongUrl.selectAll();
						}
					});

				}

			}

			@Override
			public void onFailure(Throwable caught) {

				onError(caught.getMessage());

			}
		});
	}

	private void onError(String msg) {

		loadingIcon.setVisible(false);
		formWithInformation.setVisible(false);
		errorBlock.setText(SERVICE_UNAVAILABLE + (msg == null ? "" : "(" + msg + ")"));
		errorBlock.setVisible(true);

	}

}
