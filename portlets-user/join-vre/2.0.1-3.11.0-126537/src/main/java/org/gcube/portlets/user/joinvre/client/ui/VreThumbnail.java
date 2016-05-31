package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class VreThumbnail extends Composite {

	private static VreThumbnailUiBinder uiBinder = GWT
			.create(VreThumbnailUiBinder.class);

	interface VreThumbnailUiBinder extends UiBinder<Widget, VreThumbnail> {
	}
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);

	@UiField Heading vreName;
	@UiField Image vreImage;
	@UiField Button joinButton;
	@UiField Button vreInfoButton;

	private VRE myVre;

	public VreThumbnail(VRE vre) {
		initWidget(uiBinder.createAndBindUi(this));
		this.myVre = vre;
		String name = vre.getName();
		if (name.length() > 22)
			name = name.substring(0, 17) + "...";
		vreName.setText(name);
		if (vre.isUponRequest()) {
			joinButton.setType(ButtonType.DEFAULT);
			joinButton.setText("Access");
		} else {
			joinButton.setType(ButtonType.PRIMARY);
			joinButton.setText("Enter this VRE");
		}
		vreImage.setUrl(vre.getImageURL());
	}

	@UiHandler("joinButton")
	void handleClick(ClickEvent e) {
		if (myVre.isExternal()) {
			RedirectPanel modal = new RedirectPanel(myVre);
			modal.show();
		}
		else {
			joinService.joinVRE(myVre.getId(), new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					String errorDescription = "Error while trying to join to" 
							+ myVre.getName() + " VRE. Please Try again later. "
							+ "If the problem persist contact system administrator";
					Window.alert(errorDescription);
				}

				@Override
				public void onSuccess(Boolean result) {
					Window.open("/group/data-e-infrastructure-gateway/join-new?orgid="+myVre.getId(), "_self", "");
				}

			});
		}
	}


	@UiHandler("vreInfoButton")
	void infoClick(ClickEvent e) {
		InfoPanel modal = new InfoPanel(myVre);
		modal.show();
	}

}
