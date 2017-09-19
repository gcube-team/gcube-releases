package org.gcube.portlets.user.contactinformation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * EntryPoint module for ContactInfo project.
 * @author Massimiliano Assante (massimiliano.assante@isti.cnr.it) CNR-ISTI
 * @author Costantino Perciante (costantino.perciante@isti.cnr.it) CNR-ISTI
 */
public class ContactInfo implements EntryPoint {

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	// page bus adapter
	private final PageBusAdapter pageBusAdapter = new PageBusAdapter();

	// waiting image
	public static final String spinner = GWT.getModuleBaseURL() + "../images/loader.gif";

	// remote services
	private final ContactInfoServiceAsync contactService = GWT.create(ContactInfoService.class);

	// main panel
	private VerticalPanel mainPanel = new VerticalPanel();

	// panel for save/cancel buttons
	private  FlowPanel actionsPanel = new FlowPanel();

	// context information
	private UserContext userContext;

	// list of contact information
	List<InfoPart> contactInfo = new ArrayList<InfoPart>();

	// edit button
	private Button edit = new Button("Edit Contact Information");

	// save button
	private Button save = new Button("Save Contact Information");

	// cancel button
	private Button cancel = new Button("Cancel");

	// Alert block for errors/success after editing
	private Alert alertBlock = new Alert();

	public void onModuleLoad() {

		// add main panel into the div
		RootPanel.get("ContactProfileDiv").add(mainPanel);

		// mainpanel style
		mainPanel.addStyleName("contact-mainpanel-style");

		// retrieve current information
		contactService.getUserContext(getUserToShowId(), new AsyncCallback<UserContext>() {
			@Override
			public void onSuccess(UserContext context) {
				userContext = context;
				displayUserContact(context);	
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			}
		});

		// swap to the editable fields
		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				swapToEdit();				
			}
		});

		// try to save new data and swap to uneditable fields
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				collectNewInfoAndSave();
				swapToUnEdit();
			}
		});

		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				resetTextBoxes();
				swapToUnEdit();
			}
		});

		// buttons style
		edit.addStyleName("save-edit-button");
		save.addStyleName("save-edit-button");

		// alert block style
		alertBlock.addStyleName("contact-alert-block");

		subscribeToLinkedInImport();
	}

	private void displayUserContact(UserContext userContext) {
		mainPanel.clear();		
		HashMap<ContactType, String> userInfo = userContext.getInformations();
		boolean hasHangout = false;
		for (ContactType type : userInfo.keySet()) {
			if (type == ContactType.GOOGLE)
				hasHangout = true;
			else{
				InfoPart info = new InfoPart(userInfo.get(type), type);
				mainPanel.add(info);
				contactInfo.add(info);

				// hide if value is empty
				if(userInfo.get(type).isEmpty())
					info.setVisible(false);
			}
		}

		if(hasHangout){
			InfoPart info = new InfoPart(userInfo.get(ContactType.GOOGLE), ContactType.GOOGLE);
			mainPanel.add(info);
			contactInfo.add(info);

			// hide if value is empty
			if(userInfo.get(ContactType.GOOGLE).isEmpty())
				info.setVisible(false);
		}

		if(showAsOwner(userContext)) { //its him seeing his profile

			// alert block
			mainPanel.add(alertBlock);
			alertBlock.setVisible(false);
			alertBlock.setClose(false);

			// edit button
			mainPanel.add(edit);

			// other actions
			actionsPanel.add(save);
			actionsPanel.add(cancel);
			save.setVisible(false);
			cancel.setVisible(false);

			// add margin left to save button
			save.getElement().getStyle().setMarginRight(5, Unit.PX);

			// add to main panel
			mainPanel.add(actionsPanel);
		}else{
			if (userInfo.isEmpty()) {
				mainPanel.add(new HTML(userContext.getUserInfo().getFullName() + " has not entered any contact information yet."));
			}
		}
	}
	
	/**
	 * Check if the profile must be shown as belonging to the user or not
	 * @param result
	 * @return
	 */
	private boolean showAsOwner(UserContext result) {

		if(getUserToShowId()== null  && result.isOwner()) 
			return true;

		return false;
	}

	/**
	 * When the user pushes Cancel, replace the modified textboxes' values with the 
	 * labels' ones.
	 */
	private void resetTextBoxes() {
		for (InfoPart infoPart : contactInfo) {
			infoPart.replaceTextBoxValueWithLabel();
		}
	}

	/**
	 * Retrieve the current textbox values and try to update the user contact info
	 */
	private void collectNewInfoAndSave() {

		final HashMap<ContactType, String> contactInfoMap = new HashMap<ContactType, String>();

		// collect
		for (InfoPart infoPart : contactInfo) {
			// Sanitize html
			HTML sanitize = new HTML(infoPart.getTextBoxValue());
			contactInfoMap.put(infoPart.getContactType(), sanitize.getText());
		}

		// disable button
		save.setEnabled(false);

		// change alert message
		alertBlock.setText("Updating information please wait...");
		alertBlock.setType(AlertType.INFO);

		// try to save
		contactService.updateContactInformation(contactInfoMap, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if(result){

					// we need an iterator
					Iterator<InfoPart> newValuesIterator = contactInfo.iterator();
					while (newValuesIterator.hasNext()) {

						// get the element
						InfoPart infoPart = (InfoPart) newValuesIterator.next();

						// get the type
						ContactType type = infoPart.getContactType();

						if(contactInfoMap.containsKey(type)){
							String newValue = contactInfoMap.get(type);
							// check if its new value is not empty
							if(!newValue.isEmpty())
								infoPart.updateInformation(contactInfoMap.get(type), type);
							else{
								// hide
								infoPart.setVisible(false);
							}
						}
					}

					// show success message
					alertBlock.setText("Information updated");
					alertBlock.setType(AlertType.SUCCESS);

				}else{
					// on error just show old values and an alert message 
					alertBlock.setText("Unable to updated information");
					alertBlock.setType(AlertType.ERROR);
				}
				alertBlock.setVisible(true);
				save.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				save.setEnabled(true);
				alertBlock.setText("Unable to updated information");
				alertBlock.setType(AlertType.ERROR);
				alertBlock.setVisible(true);
			}
		});

		// hide alert message after a while
		Timer t =  new Timer() {
			@Override
			public void run() {
				alertBlock.setVisible(false);
			}
		};
		t.schedule(2000);

	}

	/**
	 * Show the editable textboxes
	 */
	private void swapToEdit() {

		for (InfoPart infoPart : contactInfo) {
			infoPart.showTextBox(true);
			infoPart.setVisible(true);
		}

		// hide the edit contact button and show the Save Contact Information one
		edit.setVisible(false);
		save.setVisible(true);
		cancel.setVisible(true);
	}

	/**
	 * Swap to uneditable text
	 */
	private void swapToUnEdit() {

		// hide textboxes
		for (InfoPart infoPart : contactInfo) {
			infoPart.showTextBox(false);
			if(infoPart.getTextBoxValue().isEmpty())
				infoPart.setVisible(false);
		}

		// hide the edit contact/cancel button and show the Save Contact Information one
		edit.setVisible(true);
		save.setVisible(false);
		cancel.setVisible(false);
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
