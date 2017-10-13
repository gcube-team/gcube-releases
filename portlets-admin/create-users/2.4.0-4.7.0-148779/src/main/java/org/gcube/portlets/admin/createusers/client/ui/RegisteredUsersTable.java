package org.gcube.portlets.admin.createusers.client.ui;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.admin.createusers.client.HandleUsersServiceAsync;
import org.gcube.portlets.admin.createusers.client.event.AddUserEvent;
import org.gcube.portlets.admin.createusers.client.event.AddUserEventHandler;
import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

/**
 * Show already registered users list.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class RegisteredUsersTable extends Composite {

	//CellTable custom UI resource
	private CellTable.Resources tableRes = GWT.create(TableResources.class);
	protected ListDataProvider<VreUserBean> dataProvider = new ListDataProvider<VreUserBean>();
	private CellTable<VreUserBean> table = new CellTable<VreUserBean>(1, tableRes);
	private final HandlerManager eventBus;

	public RegisteredUsersTable(List<VreUserBean> registeredUsers, HandlerManager eventBus, final HandleUsersServiceAsync userServices) {

		super();
		initWidget(table);

		// bind event
		this.eventBus = eventBus;
		bind();

		dataProvider.setList(registeredUsers);
		dataProvider.addDataDisplay(table);
		table.setStriped(true);
		table.setWidth("95%", false);
		table.addStyleName("table-style");
		table.setVisibleRange(new Range(0, registeredUsers.size()));
		table.setBordered(true);
		table.setRowCount(registeredUsers.size(), true);

		// column for mail and sorting handler
		Column<VreUserBean, String> emailCol = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				return user.getEmail();
			}
		});

		ListHandler<VreUserBean> emailColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		emailColHandler.setComparator(emailCol, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {

				return o1.getEmail().compareTo(o2.getEmail());

			}
		});
		emailCol.setSortable(true);	
		emailCol.setDefaultSortAscending(false);
		table.addColumnSortHandler(emailColHandler);

		// name column plus handler for sorting
		Column<VreUserBean, String> nameCol = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				return user.getName();
			}
		});

		ListHandler<VreUserBean> nameColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		nameColHandler.setComparator(nameCol, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {

				return o1.getName().compareTo(o2.getName());

			}
		});
		nameCol.setSortable(true);	
		nameCol.setDefaultSortAscending(false);
		table.addColumnSortHandler(nameColHandler);

		// surname column plus handler for sorting
		Column<VreUserBean, String> surnameCol = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				return user.getSurname();
			}
		});

		ListHandler<VreUserBean> surnameColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		nameColHandler.setComparator(surnameCol, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {

				return o1.getSurname().compareTo(o2.getSurname());

			}
		});
		surnameCol.setSortable(true);	
		surnameCol.setDefaultSortAscending(false);
		table.addColumnSortHandler(surnameColHandler);

		// institution/organization and handler for sorting
		Column<VreUserBean, String> institutionCol = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				return user.getInstitution();
			}
		});

		ListHandler<VreUserBean> institutionColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		institutionColHandler.setComparator(institutionCol, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {

				return o1.getInstitution().compareTo(o2.getInstitution());

			}
		});
		institutionCol.setSortable(true);	
		institutionCol.setDefaultSortAscending(false);
		table.addColumnSortHandler(institutionColHandler);

		// password and handler for sorting
		Column<VreUserBean, String> passwordChanged = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				return (user.isPasswordChanged() ? "True" : "False");
			}
		});

		ListHandler<VreUserBean> passwordChangedColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		passwordChangedColHandler.setComparator(passwordChanged, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {

				String o1PasswordChangedString = o1.isPasswordChanged() ? "True" : "False";
				String o2PasswordChangedString = o2.isPasswordChanged() ? "True" : "False";
				return o1PasswordChangedString.compareTo(o2PasswordChangedString);

			}
		});
		passwordChanged.setSortable(true);	
		passwordChanged.setDefaultSortAscending(false);
		table.addColumnSortHandler(passwordChangedColHandler);

		// registration column plus handler for sorting
		Column<VreUserBean, String> registrationDate = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(VreUserBean user) {
				DateTimeFormat formatter = DateTimeFormat.getFormat("MMM d yyyy");
				return formatter.format(new Date(user.getRegisrationDate()));
			}
		});

		ListHandler<VreUserBean> registrationDateColHandler = new ListHandler<VreUserBean>(dataProvider.getList());
		registrationDateColHandler.setComparator(registrationDate, new Comparator<VreUserBean>() {
			@Override
			public int compare(VreUserBean o1, VreUserBean o2) {
				return new Long(o1.getRegisrationDate()).compareTo(new Long(o2.getRegisrationDate()));

			}
		});
		registrationDate.setDefaultSortAscending(false);
		registrationDate.setSortable(true);	
		table.addColumnSortHandler(registrationDateColHandler);
		table.getColumnSortList().push(registrationDate);

		// delete option
		Column<VreUserBean, String> deleteUser = new Column<VreUserBean, String>(new ButtonCell()) {

			@Override
			public String getValue(VreUserBean object) {
				return object.isPasswordChanged() ? "True" : "False"; // useless
			}

			@Override
			public void render(Cell.Context context, VreUserBean value, SafeHtmlBuilder sb){

				if(value == null)
					return;

				if(!value.isPasswordChanged())
					sb.appendHtmlConstant("<Button>Delete User</Button>");
				else
					sb.appendHtmlConstant("<Button disabled>Delete User</Button>");

			}

			@Override
			public void onBrowserEvent(Cell.Context context, final Element parent, final VreUserBean user, NativeEvent event) {
				event.preventDefault();

				if(!"click".equals(event.getType()))
					return;

				EventTarget eventTarget = event.getEventTarget();

				if(parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))){

					// get the button and disable it
					parent.getFirstChildElement().setPropertyBoolean("disabled", true);

					userServices.deleteInvitedUser(user.getEmail(), new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							// delete this row too
							if(result){

								dataProvider.getList().remove(user);
								table.setVisibleRange(new Range(0, dataProvider.getList().size()));
								table.setRowCount(dataProvider.getList().size(), true);
								dataProvider.refresh();

								Window.alert("Deleted user with email " + user.getEmail());

							}else
								Window.alert("Unable to delete this user, sorry!");

							// enable the button again
							parent.getFirstChildElement().setPropertyBoolean("disabled", false);

						}

						@Override
						public void onFailure(Throwable caught) {

							Window.alert("Unable to delete this user, sorry!");

							// enable the button again
							parent.getFirstChildElement().setPropertyBoolean("disabled", false);

						}
					});
				}
			};
		};

		// send email option
		Column<VreUserBean, String> sendWelcomeMessage = new Column<VreUserBean, String>(new ButtonCell()) {

			@Override
			public String getValue(VreUserBean object) {
				return "Send Welcome"; // useless
			}

			@Override
			public void render(Cell.Context context, VreUserBean value, SafeHtmlBuilder sb){

				if(value == null)
					return;
				if(!value.isPasswordChanged())
					sb.appendHtmlConstant("<Button>Send Welcome</Button>");
				else
					sb.appendHtmlConstant("<Button disabled>Send Welcome</Button>");

			}

			@Override
			public void onBrowserEvent(Cell.Context context, final Element parent, final VreUserBean user, NativeEvent event) {
				event.preventDefault();

				if(!"click".equals(event.getType()))
					return;

				EventTarget eventTarget = event.getEventTarget();

				if(parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))){

					// get the button and disable it
					parent.getFirstChildElement().setPropertyBoolean("disabled", true);

					userServices.sendEmailToUser(user.getEmail(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {

							Window.alert("Welcome message sent to " + user.getEmail());

							// get the button and enable it
							parent.getFirstChildElement().setPropertyBoolean("disabled", false);

						}

						@Override
						public void onFailure(Throwable caught) {

							Window.alert("Unable to send the welcome message to " + user.getEmail());

							// get the button and enable it
							parent.getFirstChildElement().setPropertyBoolean("disabled", false);

						}
					});
				}
			}
		};


		// add columns
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Registered user's email"+ "\">");
		builder.appendEscaped("Email");
		builder.appendHtmlConstant("</span>");
		table.addColumn(emailCol, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Registered user's name"+ "\">");
		builder.appendEscaped("Name");
		builder.appendHtmlConstant("</span>");
		table.addColumn(nameCol, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Registered user's surname"+ "\">");
		builder.appendEscaped("Surname");
		builder.appendHtmlConstant("</span>");
		table.addColumn(surnameCol, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Registered user's institution/organization"+ "\">");
		builder.appendEscaped("Institution / Organisation");
		builder.appendHtmlConstant("</span>");
		table.addColumn(institutionCol, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Did he/she change the default password?"+ "\">");
		builder.appendEscaped("Password Changed");
		builder.appendHtmlConstant("</span>");
		table.addColumn(passwordChanged, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Registration date"+ "\">");
		builder.appendEscaped("Registration date");
		builder.appendHtmlConstant("</span>");
		table.addColumn(registrationDate, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Delete User"+ "\">");
		builder.appendEscaped("Delete");
		builder.appendHtmlConstant("</span>");
		table.addColumn(deleteUser, builder.toSafeHtml());
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span title=\"" + "Send Welcome Message" + "\">");
		builder.appendEscaped("Send Welcome");
		builder.appendHtmlConstant("</span>");
		table.addColumn(sendWelcomeMessage, builder.toSafeHtml());
	}

	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(AddUserEvent.TYPE, new AddUserEventHandler() {

			@Override
			public void onAddUser(AddUserEvent user) {

				addUserToTable(user);

			}
		});
	}

	/**
	 * Update the list of registered users
	 * @param event
	 */
	private void addUserToTable(AddUserEvent event) {

		VreUserBean userToAdd = event.getAddedUserBean();
		GWT.log("Adding " + userToAdd);
		dataProvider.getList().add(userToAdd);
		table.setVisibleRange(new Range(0, dataProvider.getList().size()));
		table.setRowCount(dataProvider.getList().size(), true);
		dataProvider.refresh();

	}

	/**
	 * Check if a user whit this email already exists
	 * @param actualEmail
	 * @return
	 */
	public boolean isUserPresent(String actualEmail) {
		List<VreUserBean> users = dataProvider.getList();

		for (VreUserBean vreUserBean : users) {
			if(vreUserBean.getEmail().equals(actualEmail))
				return true;
		}

		return false;
	}

	/**
	 * Interface for getting a cell value
	 * @param <C>
	 */
	private static interface GetValue<C> {
		C getValue(VreUserBean user);
	}

	/**
	 * get a column 
	 * 
	 * @param <C> the cell type
	 * @param cell the cell used to render the column
	 * @param getter the value getter for the cell
	 */
	private <C> Column<VreUserBean, C> getColumn(Cell<C> cell, final GetValue<C> getter) {
		Column<VreUserBean, C> column = new Column<VreUserBean, C>(cell) {
			@Override
			public C getValue(VreUserBean object) {
				return getter.getValue(object);
			}
		};		
		return column;
	}
}
