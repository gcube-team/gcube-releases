package org.gcube.portlets.admin.wfroleseditor.client.presenter;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfroleseditor.client.WfRolesServiceAsync;
import org.gcube.portlets.admin.wfroleseditor.client.event.AddRoleEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleEvent;

public class WfRolesPresenter implements Presenter {  

	private List<WfRoleDetails> wfRoleDetails;

	public interface Display {
		HasClickHandlers getAddButton();
		HasClickHandlers getDeleteButton();
		HasClickHandlers getList();
		void setData(List<String> data);
		int getClickedRow(ClickEvent event);
		List<Integer> getSelectedRows();
		void updateSize();
		void showLoading(boolean show);
		Widget asWidget();
	}

	private final WfRolesServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;

	public WfRolesPresenter(WfRolesServiceAsync rpcService, HandlerManager eventBus, Display view) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}

	public void bind() {
		display.getAddButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new AddRoleEvent());
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				deleteSelectedRoles();
			}
		});

		display.getList().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int selectedRow = display.getClickedRow(event);

				if (selectedRow >= 0) {
					String id = wfRoleDetails.get(selectedRow).getId();
					eventBus.fireEvent(new EditRoleEvent(id));
				}
			}
		});
	}

	public void go(final HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		fetchRoleDetails();
	}

	public void sortRoleDetails() {
		for (int i = 0; i < wfRoleDetails.size(); ++i) {
			for (int j = 0; j < wfRoleDetails.size() - 1; ++j) {
				if (wfRoleDetails.get(j).getDisplayName().compareToIgnoreCase(wfRoleDetails.get(j + 1).getDisplayName()) >= 0) {
					WfRoleDetails tmp = wfRoleDetails.get(j);
					wfRoleDetails.set(j, wfRoleDetails.get(j + 1));
					wfRoleDetails.set(j + 1, tmp);
				}
			}
		}
	}

	public void setRoleDetails(List<WfRoleDetails> wfRoleDetails) {
		this.wfRoleDetails = wfRoleDetails;
	}

	public WfRoleDetails getRoleDetail(int index) {
		return wfRoleDetails.get(index);
	}

	private void fetchRoleDetails() {	  
		display.showLoading(true);
		rpcService.getRoleDetails(new AsyncCallback<ArrayList<WfRoleDetails>>() {
			public void onSuccess(ArrayList<WfRoleDetails> result) {
				wfRoleDetails = result;
				sortRoleDetails();
				List<String> data = new ArrayList<String>();

				for (int i = 0; i < result.size(); ++i) {
					data.add(wfRoleDetails.get(i).getDisplayName());
				}
				display.showLoading(false);
				display.setData(data);
			}

			public void onFailure(Throwable caught) {
				Window.alert("Error fetching role details");
			}
		});
	}

	private void deleteSelectedRoles() {
		List<Integer> selectedRows = display.getSelectedRows();
		ArrayList<String> ids = new ArrayList<String>();

		for (int i = 0; i < selectedRows.size(); ++i) {
			ids.add(wfRoleDetails.get(selectedRows.get(i)).getId());
		}

		rpcService.deleteRoles(ids, new AsyncCallback<ArrayList<WfRoleDetails>>() {
			public void onSuccess(ArrayList<WfRoleDetails> result) {
				wfRoleDetails = result;
				sortRoleDetails();
				List<String> data = new ArrayList<String>();

				for (int i = 0; i < result.size(); ++i) {
					data.add(wfRoleDetails.get(i).getDisplayName());
				}

				display.setData(data);

			}

			public void onFailure(Throwable caught) {
				Window.alert("Error deleting selected roles");
			}
		});
	}
}
