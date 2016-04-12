package org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.GenericComponentInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PortType;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class GCubeWebServiceMainPackageDataCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private MainPackageInfoFieldSet mainPackageInfoFieldSet = new MainPackageInfoFieldSet();
	

	private String packageId;

	public GCubeWebServiceMainPackageDataCard(String packageId) {
		super("Edit Service Profile Data - Main Package");

		this.packageId = packageId;

		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_GCubeWebServiceMainPackageData().getText());

		this.add(stepInfo);
		this.add(mainPackageInfoFieldSet, formData);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	@Override
	public void performNextStepLogic() {
		saveDataAndFireEvent(new GoAheadEvent(this));
	}

	@Override
	public void performBackStepLogic() {
		saveDataAndFireEvent(new GoBackEvent(this));
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new GetPackageData(packageId), new AsyncCallback<GetPackageDataResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetPackageDataResult result) {
				PackageData packageData = result.getData();
				
				mainPackageInfoFieldSet.setName(packageData.getName());
				mainPackageInfoFieldSet.setDescription(packageData
						.getDescription());
				mainPackageInfoFieldSet.setVersion(packageData.getVersion());
				
				mainPackageInfoFieldSet.setPortTypes(packageData.getPortTypes());
				
				GCubeWebServiceMainPackageDataCard.this.unmask();
				Log.debug("Data loaded.");
			}
			
		});
	}

	@Override
	public boolean isValid(boolean preventMark) {
		return super.isValid(preventMark) && mainPackageInfoFieldSet.isValid();
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {

		PackageData packageData = new PackageData(PackageType.Software);
		packageData.setName(mainPackageInfoFieldSet.getName());
		packageData.setDescription(mainPackageInfoFieldSet.getDescription());
		packageData.setVersion(mainPackageInfoFieldSet.getVersion());
		packageData.setPortTypes(mainPackageInfoFieldSet.getPortTypes());

		Log.debug("Saving data");
		dispatchAsync.execute(new SetPackageData(packageId, packageData),
				new AsyncCallback<SetPackageDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetPackageDataResult result) {
						Log.debug("Data saved succesfully");						
						binding.removeButton(window.getNextButton());
						eventBus.fireEvent(event);
					}
				});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_GCubeWebServiceMainPackageData().getText();
	}

	private class MainPackageInfoFieldSet extends GenericComponentInfoFieldSet {
		
		private PortTypePanel portTypePanel = new PortTypePanel();
		
		public MainPackageInfoFieldSet() {
			super("Main Package");
			this.add(portTypePanel,new FormData("100%"));
		}

		private boolean isValid(){
			return portTypePanel.isValid();
		}
		
		public void setPortTypes(ArrayList<PortType> value){
			portTypePanel.setPortTypes(value);
		}
		
		public ArrayList<PortType> getPortTypes(){
			return portTypePanel.getPortTypes();
		}

		private class PortTypePanel extends ContentPanel {

			private ListStore<PortTypeModel> store = new ListStore<PortTypeModel>();
			private final CheckBoxSelectionModel<PortTypeModel> sm = new CheckBoxSelectionModel<PortTypeModel>();
			private EditorGrid<PortTypeModel> editorGrid;

			private Button addButton = new Button("Add",
					AbstractImagePrototype.create(Resources.INSTANCE.addIcon()));
			private Button removeButton = new Button("Remove",
					AbstractImagePrototype.create(Resources.INSTANCE
							.deleteIcon()));

			public PortTypePanel() {
				FormData formData = new FormData("100%");

				this.setLayout(new FitLayout());
				this.setHeading("PortTypes**");
				this.setHeight(200);

				// Url column
				ColumnConfig urlColumn = new ColumnConfig(
						PortTypeModel.URL_CODE, "URL", 300);
				TextField<String> urlText = new TextField<String>();
				urlText.setAllowBlank(false);
				urlColumn.setEditor(new CellEditor(urlText));

				ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
				columns.add(sm.getColumn());
				columns.add(urlColumn);

				ColumnModel columnModel = new ColumnModel(columns);

				editorGrid = new EditorGrid<PortTypeModel>(store, columnModel);
				editorGrid.setSelectionModel(sm);
				editorGrid.setAutoExpandColumn(PortTypeModel.URL_CODE);

				editorGrid.addPlugin(sm);
				editorGrid.getView().setShowDirtyCells(false);

				this.add(editorGrid, formData);

				ToolBar toolBar = new ToolBar();
				toolBar.setAlignment(HorizontalAlignment.RIGHT);

				toolBar.add(addButton);
				toolBar.add(removeButton);

				this.setBottomComponent(toolBar);

				bind();
			}

			public boolean isValid() {
				if (store.getCount() == 0)
					return false;
				for (PortTypeModel m : store.getModels()) {
					if (m.getUrl() == null || m.getUrl().isEmpty() || ! m.getUrl().matches("^/.+"))
						return false;
				}
				return true;
			}

			private void bind() {
				addButton
						.addSelectionListener(new SelectionListener<ButtonEvent>() {

							@Override
							public void componentSelected(ButtonEvent ce) {
								PortTypeModel entryPoint = new PortTypeModel(
										"/");
								PortTypePanel.this.editorGrid.stopEditing(true);
								store.insert(entryPoint, 0);
								PortTypePanel.this.editorGrid.startEditing(
										store.indexOf(entryPoint), 1);
							}
						});

				removeButton
						.addSelectionListener(new SelectionListener<ButtonEvent>() {

							@Override
							public void componentSelected(ButtonEvent ce) {
								for (PortTypeModel entryPoint : sm
										.getSelectedItems())
									store.remove(entryPoint);
							}
						});
			}

			public ArrayList<PortType> getPortTypes() {
				ArrayList<PortType> result = new ArrayList<PortType>();
				for (PortTypeModel model : store.getModels()) {
					result.add(new PortType(model.getUrl()));
				}
				return result;
			}

			public void setPortTypes(ArrayList<PortType> value) {
				store.removeAll();
				for (PortType pt : value) {
					store.add(new PortTypeModel(pt.getName()));
				}
				store.commitChanges();
			}

			private class PortTypeModel extends BaseModelData {

				public final static String URL_CODE = "URL";

				public PortTypeModel(String url) {
					set(URL_CODE, url);
				}

				public String getUrl() {
					return get(URL_CODE);
				}
				
			}

		}
	}

}
