package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.client.JoinVRE;
import org.gcube.portlets.user.joinvre.shared.UserBelonging;
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
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class VreThumbnail extends Composite {

	private static VreThumbnailUiBinder uiBinder = GWT
			.create(VreThumbnailUiBinder.class);

	interface VreThumbnailUiBinder extends UiBinder<Widget, VreThumbnail> {
	}
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	private final static String PUBLIC_VRE_BUTTON_LABEL= "Access this VRE";
	private final static String PRIVATE_VRE_BUTTON_LABEL= "Private";
	private final static String RESTRICTED_VRE_BUTTON_LABEL= "Request Access";
	
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
		switch (vre.getMembershipType()) {
		case RESTRICTED:
			joinButton.setType(ButtonType.DEFAULT);
			joinButton.setText(RESTRICTED_VRE_BUTTON_LABEL);
			if (vre.getUserBelonging() == UserBelonging.PENDING) {
				joinButton.setText("Waiting approval");
				joinButton.setType(ButtonType.WARNING);
				joinButton.setEnabled(false);
			}
			break;
		case OPEN:
			joinButton.setText(PUBLIC_VRE_BUTTON_LABEL);
			break;
		default:
			joinButton.setEnabled(false);
			joinButton.setText(PRIVATE_VRE_BUTTON_LABEL);
			break;
		}	
		if (vre.getUserBelonging() == UserBelonging.BELONGING) {
			joinButton.setType(ButtonType.SUCCESS);
			joinButton.setText("Enter this VRE");
			joinButton.setEnabled(true);
		}
		vreImage.setUrl(vre.getImageURL());
	}

	@UiHandler("joinButton")
	void handleClick(ClickEvent e) {
		if (myVre.getUserBelonging() != UserBelonging.PENDING) {
			joinService.joinVRE(myVre.getId(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					String errorDescription = "Error while trying to join to" 
							+ myVre.getName() + " VRE. Please Try again later. "
							+ "If the problem persist please contact us at www.gcube-system.org";
					Window.alert(errorDescription);
				}

				@Override
				public void onSuccess(String siteLandingPagePath) {
					Location.assign(siteLandingPagePath +"/explore?"+JoinVRE.GET_OID_PARAMETER+"="+myVre.getId());
				}
			});
		}

	}


	@UiHandler("vreInfoButton")
	void infoClick(ClickEvent e) {
		if (myVre.getPublicURL() == null) {
			InfoPanel modal = new InfoPanel(myVre);
			modal.show();
		} else {
			Window.open(myVre.getPublicURL(),"_blank","");
		}
	}

	public void setPending() {
		joinButton.setText("Waiting approval");
		joinButton.setType(ButtonType.WARNING);
		joinButton.setEnabled(false);
	}

	public VRE getMyVre() {
		return myVre;
	}

}
