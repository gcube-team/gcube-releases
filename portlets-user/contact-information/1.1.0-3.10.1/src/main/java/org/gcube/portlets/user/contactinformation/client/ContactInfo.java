package org.gcube.portlets.user.contactinformation.client;

import java.util.HashMap;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.PersonJsonizer;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.SubscriberData;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.SubscriberDataJsonizer;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portlets.user.contactinformation.client.ui.InfoPart;
import org.gcube.portlets.user.contactinformation.shared.ContactType;
import org.gcube.portlets.user.contactinformation.shared.UserContext;
import org.jsonmaker.gwt.client.Jsonizer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class ContactInfo implements EntryPoint {

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private final PageBusAdapter pageBusAdapter = new PageBusAdapter();
	public static final String spinner = GWT.getModuleBaseURL() + "../images/loader.gif";
	private final ContactInfoServiceAsync contactService = GWT.create(ContactInfoService.class);

	private VerticalPanel mainPanel = new VerticalPanel();
	private UserContext userContext;

	private Button edit = new Button("Edit Contact Information");

	public void onModuleLoad() {
		RootPanel.get("ContactProfileDiv").add(mainPanel);
		contactService.getUserContext(getUserToShowId(), new AsyncCallback<UserContext>() {
			@Override
			public void onSuccess(UserContext context) {
				if (!context.isInfrastructure())
					RootPanel.get("ContactProfileDiv").setStyleName("frame");
				userContext = context;
				displayUserContact(context);	
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			}
		});

		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Location.assign("/group/control_panel/manage?p_p_id=2#_LFR_FN_socialNetwork	");					
			}
		});
		edit.getElement().getStyle().setPadding(8, Unit.PX);

		subscribeToLinkedInImport();
	}

	private void displayUserContact(UserContext userContext) {
		mainPanel.clear();		
		HashMap<ContactType, String> userInfo = userContext.getInformations();
		boolean hasHangout = false;
		for (ContactType type : userInfo.keySet()) {
			if (type == ContactType.GOOGLE)
				hasHangout = true;
			else
				mainPanel.add(new InfoPart(userInfo.get(type), type));
		}
		if (hasHangout) {
			mainPanel.add(new InfoPart(userInfo.get(ContactType.GOOGLE), ContactType.GOOGLE));
		}
		if (getUserToShowId() == null) { //its him seeing his profile
			mainPanel.add(edit);
		} else {
			if (userInfo.isEmpty()) {
				mainPanel.add(new HTML(userContext.getUserInfo().getFullName() + " has not entered any contact information yet."));
			}
		}

	}

	/**
	 * decode the userid from the location param
	 * @return the decoded (base64) userid
	 */
	public static String getUserToShowId() {
		String encodedOid = Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID);
		if (Window.Location.getParameter(encodedOid) == null)
			return null;
		String encodedUserId = Window.Location.getParameter(encodedOid);
		return Encoder.decode(encodedUserId);
	}

	private void subscribeToLinkedInImport() {

		SubscriberData subscriberData = new SubscriberData();
		subscriberData.setHeader("myHeader");
		subscriberData.setBody("myHeader");

		//Subscribe to message and associate subsequent receptions with custom subscriber data
		try {
			pageBusAdapter.PageBusSubscribe("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", null, null, subscriberData, (Jsonizer)GWT.create(SubscriberDataJsonizer.class));
			// register listener
			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener() {
				public void onPageBusSubscriptionCallback(PageBusEvent event) {					
					try {
						Person message = (Person) event.getMessage((Jsonizer)GWT.create(PersonJsonizer.class));
						if (message != null) {
							String inPublicProfileURL = message.getName();
							userContext.getInformations().put(ContactType.IN, inPublicProfileURL);
							displayUserContact(userContext);
						}

					} catch (PageBusAdapterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				@Override
				public String getName() {
					return null;
				}
			}
					);
		} catch (PageBusAdapterException e) {
			e.printStackTrace();
		}
	}
}
