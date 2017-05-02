package org.gcube.portlet.user.my_vres.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NoVresPanel extends Composite {

	private static NoVresPanelUiBinder uiBinder = GWT
			.create(NoVresPanelUiBinder.class);

	private final MyVREsServiceAsync myVREsService = GWT.create(MyVREsService.class);

	interface NoVresPanelUiBinder extends UiBinder<Widget, NoVresPanel> {
	}
	@UiField Anchor joinLink;
	@UiField Anchor availableLink;
	public NoVresPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		myVREsService.getSiteLandingPagePath(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				joinLink.setHref(result);
				availableLink.setHref(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
			
			}
		});
	}
}
