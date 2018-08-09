package org.gcube.portlets.widgets.wsthreddssync.client.view.binder;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.client.WsThreddsWidget;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoUnSyncEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.view.FormatUtil;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;



// TODO: Auto-generated Javadoc
/**
 * The Class ShowThreddsFolderInfoView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public abstract class ShowThreddsFolderInfoView extends Composite {


	/** The ui binder. */
	private static ShowThreddsFolderInfoViewUiBinder uiBinder =
		GWT.create(ShowThreddsFolderInfoViewUiBinder.class);

	/**
	 * The Interface ShowThreddsFolderInfoViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 15, 2018
	 */
	interface ShowThreddsFolderInfoViewUiBinder
		extends UiBinder<Widget, ShowThreddsFolderInfoView> {
	}


	@UiField
	TextBox field_folder_status;

	@UiField
	ListBox field_select_scope;


	@UiField
	TextBox field_catalogue_name;


	@UiField
	TextBox field_remote_path;

	@UiField
	TextBox field_folder_path;


	@UiField
	TextBox field_folder_locked;

	@UiField
	Button button_do_unsync;


	/*@UiField
	TextBox field_last_sync;
	*/


	@UiField
	ControlGroup cg_catalogue_name;


	@UiField
	ControlGroup cg_remote_path;

	@UiField
	HTMLPanel form_unit_fields;

	@UiField
	Pager pager;



	/** The folder id. */
	private String folderId;

	/** The map VR es. */
	private Map<String, GcubeScope> mapScopes = new HashMap<String, GcubeScope>();


	private boolean isCreateConfiguration;


	/**
	 * The Enum SUBMIT_ACTION.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 12, 2018
	 */
	public static enum SUBMIT_ACTION {CREATE_UPDATE_CONFIGURATION, DO_UNSYNC, DO_SYNC};

	/**
	 * Submit handler.
	 *
	 * @param action the action
	 */
	public abstract void submitHandler(SUBMIT_ACTION action);

	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);


	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 *
	 * @param folderId the folder id
	 * @param isCreateConfiguration the is create configuration
	 */
	public ShowThreddsFolderInfoView(String folderId, boolean isCreateConfiguration) {
		this.folderId = folderId;
		this.isCreateConfiguration = isCreateConfiguration;

		initWidget(uiBinder.createAndBindUi(this));

		pager.getLeft().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				submitHandler(SUBMIT_ACTION.CREATE_UPDATE_CONFIGURATION);
			}
		});

		pager.getRight().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				//boolean isValid = validateSubmit();
				//if(isValid)
				submitHandler(SUBMIT_ACTION.DO_SYNC);

			}
		});

		if(isCreateConfiguration) {
			WsThreddsWidget.wsThreddsSyncService.getListOfScopesForLoggedUser(new AsyncCallback<List<GcubeScope>>() {

				@Override
				public void onSuccess(List<GcubeScope> result) {

					for (GcubeScope gcubeScope : result) {
						String toValue = FormatUtil.toScopeValue(gcubeScope);
						mapScopes.put(gcubeScope.getScopeName(), gcubeScope);
						field_select_scope.addItem(toValue, gcubeScope.getScopeName());

					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	/**
	 * Update view to result.
	 *
	 * @param folder the folder
	 * @param syncFolderDesc the sync folder desc
	 */
	public void updateViewToResult(final WsFolder folder, WsThreddsSynchFolderDescriptor syncFolderDesc) {

		this.field_select_scope.clear();

		if(syncFolderDesc==null) {
			pager.getRight().setVisible(false);
			return;
		}

		if(syncFolderDesc.getSyncStatus()!=null) {
			this.field_folder_status.setValue(syncFolderDesc.getSyncStatus().toString());
		}

		if(syncFolderDesc.getSelectedScope()!=null) {
			String toValue = FormatUtil.toScopeValue(syncFolderDesc.getSelectedScope());
			this.field_select_scope.addItem(toValue, syncFolderDesc.getSelectedScope().getScopeName());
			this.field_select_scope.setValue(0, syncFolderDesc.getSelectedScope().getScopeName());
			this.field_select_scope.setSelectedValue(syncFolderDesc.getSelectedScope().getScopeName());
		}

		ThSyncFolderDescriptor sfd = syncFolderDesc.getServerFolderDescriptor();
		if(sfd!=null) {

			this.field_folder_path.setValue(sfd.getFolderPath());
			this.field_folder_path.setTitle(sfd.getFolderPath());
			this.field_folder_locked.setValue(sfd.isLocked()+"");

			ThSynchFolderConfiguration config = sfd.getConfiguration();

			if(config!=null) {
				this.field_catalogue_name.setValue(config.getToCreateCatalogName());
				this.field_catalogue_name.setTitle(config.getToCreateCatalogName());
				this.field_remote_path.setValue(config.getRemotePath());
				this.field_remote_path.setTitle(config.getRemotePath());
			}


			ThProcessDescriptor lpd = sfd.getLocalProcessDescriptor();
			if(lpd!=null) {
				//this.field_last_sync.setValue(DateTimeFormat.getFormat(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date(lpd.getLaunchTime())));
				ThSynchFolderConfiguration sc = lpd.getSynchConfiguration();
				if(sc!=null) {

					//this.field_select_vre.setValue(sc.get, value);

				}
			}
		}


		button_do_unsync.setVisible(true);

		button_do_unsync.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				WsThreddsWidget.eventBus.fireEvent(new PerformDoUnSyncEvent(folder));
			}
		});
	}

	/**
	 * Checks if is creates the configuration.
	 *
	 * @return true, if is creates the configuration
	 */
	public boolean isCreateConfiguration() {
		return isCreateConfiguration;
	}



	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	protected boolean validateSubmit() {
		cg_catalogue_name.setType(ControlGroupType.NONE);
		cg_remote_path.setType(ControlGroupType.NONE);

		if(field_catalogue_name.getValue()==null || field_catalogue_name.getValue().isEmpty()){
			cg_catalogue_name.setType(ControlGroupType.ERROR);
			setError(true, "Unit Title field is required");
			return false;
		}

		if(field_remote_path.getValue()==null || field_remote_path.getValue().isEmpty()){
			cg_remote_path.setType(ControlGroupType.ERROR);
			setError(true, "Folder Name field is required");
			return false;
		}

		return true;
	}


	/**
	 * Gets the remote path.
	 *
	 * @return the remote path
	 */
	public String getRemotePath() {
		return field_remote_path.getValue();
	}

	/**
	 * Gets the catalogue name.
	 *
	 * @return the catalogue name
	 */
	public String getCatalogueName(){
		return field_catalogue_name.getValue();
	}



	/**
	 * Gets the selected scope.
	 *
	 * @return the selected scope
	 */
	public GcubeScope getSelectedScope(){
		//String item = field_select_scope.getSelectedItemText();
		String scope = field_select_scope.getSelectedValue();
		return mapScopes.get(scope);
	}


	/**
	 * Gets the pager.
	 *
	 * @return the pager
	 */
	public Pager getPager() {
		return pager;
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public HTMLPanel getMainPanel(){
		return form_unit_fields;
	}



}
