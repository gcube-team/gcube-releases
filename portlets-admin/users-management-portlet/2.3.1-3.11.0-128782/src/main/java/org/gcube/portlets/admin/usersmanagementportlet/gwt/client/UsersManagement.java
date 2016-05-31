package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.HashMap;
import java.util.Iterator;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.interfaces.UsersManagementService;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.interfaces.UsersManagementServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

/**
 * EntryPoint class
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UsersManagement implements EntryPoint {

	public static UsersManagementServiceAsync userService = (UsersManagementServiceAsync) GWT.create(UsersManagementService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) userService;
	private static final String mainTitle = "Users Management";
	private Panel mainPanel = new Panel(mainTitle);

	private static FormPanel formPanel = new FormPanel(Position.LEFT);
	private static TextField mailSubject = new TextField("Email's subject", "msubject");
	private static TextField mailTo = new TextField("Email's recipients", "mto");
	private static HtmlEditor mailBody = new HtmlEditor("Email's body", "mbody");
	private HashMap<String,String> availableTemplates = new HashMap<String,String>();
	private String currentSelectedTemplate = null;

	private static LoadMask loadMask = new LoadMask("UsersDiv", "Sending email, please wait...");


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "UsersManagementServlet");
		RootPanel root = RootPanel.get("UsersDiv");
		final UsersInfoGrid usersInfoUI = new UsersInfoGrid();
		final UserRequestsGrid usersReqUI = new UserRequestsGrid(usersInfoUI);

		formPanel.setPaddings(10);
		formPanel.setSize(800, 400);
		formPanel.setTitle("Email Notification");

		Store store = new SimpleStore("mtemplates", getEmailTemplates());  
		store.load();  
		final ComboBox emailTemplates = new ComboBox();  
		emailTemplates.setFieldLabel("Email Templates");  
		emailTemplates.setHiddenName("mtemplates");  
		emailTemplates.setStore(store);  
		emailTemplates.setDisplayField("mtemplates");  
		emailTemplates.setTypeAhead(true);  
		emailTemplates.setMode(ComboBox.LOCAL);  
		emailTemplates.setTriggerAction(ComboBox.ALL);  
		emailTemplates.setEmptyText("Select a template...");  
		emailTemplates.setSelectOnFocus(true);  
		emailTemplates.setWidth(300);

		mailTo.setGrow(true);


		AsyncCallback<Integer> getNumOfUsersCallback = new AsyncCallback<Integer>() {

			public void onFailure(Throwable caught) {


			}

			public void onSuccess(Integer result) {
				if (result != -1)
					mailTo.setValue(result.toString() + " registered users will receive this email");
				else
					mailTo.setValue("Number of recipients is not yet available");
			}

		};UsersManagement.userService.getNumberOfUserThatReceiveNots(getNumOfUsersCallback);



		formPanel.add(emailTemplates);
		mailTo.setDisabled(true);
		mailSubject.setGrow(true);
		mailSubject.setGrowMax(650);
		mailSubject.setGrowMin(250);
		formPanel.add(mailTo);
		formPanel.add(mailSubject);
		mailBody.setPixelSize(550, 200);
		mailBody.setValue("Email's message body");
		mailBody.focus(false, 1000);
		formPanel.add(mailBody, new AnchorLayoutData("98%"));

		final Button clearBtn= new Button("Clear", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				mailSubject.setValue("");
				mailBody.setValue("");
				emailTemplates.clearValue();
			}
		});

		final Button sendBtn= new Button("Send", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Confirm Changes", "Are you sure you want to send this email to all registered users?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")) {

							AsyncCallback<Void> authorizeUsersCallback = new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									loadMask.hide();
									MessageBox.alert("Failed to send the email. Please try again");

								}

								public void onSuccess(Void result) {
									loadMask.hide();
									MessageBox.alert("The email has been sent to all registered users");


								}

							};UsersManagement.userService.sendEmailToRegisteredUsers(mailSubject.getText(), mailBody.getRawValue(), authorizeUsersCallback);
							loadMask.show();
						}

					}});
			}
		});

		emailTemplates.addListener(new ComboBoxListenerAdapter(){
			public void onSelect(ComboBox comboBox, Record record, int index) {
				currentSelectedTemplate = comboBox.getValue();
				mailSubject.setValue(comboBox.getValue());
				mailBody.setValue(availableTemplates.get(currentSelectedTemplate));
			}
		});



		formPanel.addButton(sendBtn); 
		formPanel.addButton(clearBtn);

		//TODO
		mainPanel.setWidth(1200);
		//mainPanel.setAutoWidth(true);
		mainPanel.setAutoHeight(true);
		//mainPanel.setAutoScroll(true);
		// This is the main panel which will host all the tabs
		final TabPanel headerPanel = new TabPanel();
		//headerPanel.setWidth(1200);
		headerPanel.setAutoHeight(true);



		// first tab for the Users Management
		Panel usersTabPanel = new Panel();
		usersTabPanel.setTitle("Manage Registered Users");
		usersTabPanel.setAutoWidth(true);
		usersTabPanel.setAutoScroll(true);
		usersTabPanel.add(usersInfoUI);
		headerPanel.add(usersTabPanel);

		// second tab for the Requests Management
		Panel requestsTabPanel = new Panel();
		requestsTabPanel.setTitle("Manage Users' Requests");
		requestsTabPanel.setAutoWidth(true);
		requestsTabPanel.setAutoScroll(true);
		requestsTabPanel.add(usersReqUI);
		headerPanel.add(requestsTabPanel);

		Panel emailNotTabPanel = new Panel();
		emailNotTabPanel.setTitle("Email Notifications");
		emailNotTabPanel.setAutoWidth(true);
		emailNotTabPanel.setAutoScroll(true);
		emailNotTabPanel.add(formPanel);
		headerPanel.add(emailNotTabPanel);

		headerPanel.setActiveTab(0);
		headerPanel.setActiveItem(0);
		mainPanel.add(headerPanel);

		mainPanel.addTool(new Tool(Tool.REFRESH, new Function() {  
			public void execute() {  
				usersInfoUI.refreshGrid();
				usersReqUI.refreshGrid();
			}  
		}, "Refresh")); 
		
		// add the mainPanel to the root panel
		root.add(mainPanel);
		
		updateWindowSize();

		/* Add a listener for the resizing of the window */
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){
			public void onResize(ResizeEvent event) {
				updateWindowSize();
			}
		});
	}

	private void updateWindowSize() {
		RootPanel root = RootPanel.get("UsersDiv");
		int leftBorder = root.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		mainPanel.setWidth(rootWidth);
	}

	private String[] getEmailTemplates() {
		EmailTemplatesData temp = new EmailTemplatesData();
		availableTemplates = temp.getEmailTemplates();
		String templatesSubjects[] = new String[availableTemplates.size()];
		Iterator<String> it = availableTemplates.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			templatesSubjects[i] = it.next();
			i++;
		}

		return templatesSubjects;
	}
}
