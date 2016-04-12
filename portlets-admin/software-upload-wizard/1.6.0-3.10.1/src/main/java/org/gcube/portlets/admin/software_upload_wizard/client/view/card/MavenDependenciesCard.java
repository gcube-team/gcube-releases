package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import java.util.ArrayList;
import java.util.Collection;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ArtifactIdTextField;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ArtifactVersionTextField;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.GroupIdTextField;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddMavenDependenciesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenDependenciesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenRepositories;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenRepositoriesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemoveMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemoveMavenDependenciesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenRepositoryInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class MavenDependenciesCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private ArtifactIdTextField artifactIdTextField = new ArtifactIdTextField();
	private GroupIdTextField groupIdTextField = new GroupIdTextField();
	private ArtifactVersionTextField versionField = new ArtifactVersionTextField();
	private FormButtonBinding formButtonBinding = new FormButtonBinding(this);
	private Button addDependencyButton = new Button("Add");
	private InfoPanel linksPanel = new InfoPanel();

	private String packageId;

	private MavenDependenciesPanel mavenDepsPanel = new MavenDependenciesPanel();

	public MavenDependenciesCard(String packageId) {
		super("Edit Maven Dependencies");

		this.packageId = packageId;
		
		buildUI();

		bind();
	}
	
	public MavenDependenciesCard(String packageId, String additionalTitle){
		super("Edit Maven Dependencies - " + additionalTitle);

		this.packageId = packageId;
		
		buildUI();

		bind();
	}
	
	private void buildUI() {
		formButtonBinding.addButton(addDependencyButton);

		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_MavenDependenciesData()
				.getText());

		this.add(stepInfo);
		this.add(groupIdTextField);
		this.add(artifactIdTextField);
		this.add(versionField);
		this.add(addDependencyButton);
		this.add(mavenDepsPanel, formData);
		this.add(linksPanel);
	}


	
	

	private void bind() {
		addDependencyButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						addDependency();
					}
				});

	}

	@Override
	public void setup() {
		window.setBackButtonEnabled(true);
		loadData();
	}

	private void loadData() {
		mavenDepsPanel.refreshContent();
		loadMavenReposLinks();
	}

	private void loadMavenReposLinks() {
		Log.debug("Retrieving Maven repositories and links ...");
		dispatchAsync.execute(new GetMavenRepositories(),
				new AsyncCallback<GetMavenRepositoriesResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetMavenRepositoriesResult result) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder
								.append("<p>Browse Maven repositories on Nexus for artifacts coordinates:</p>");
						stringBuilder.append("<ul>");
						for (MavenRepositoryInfo tmp : result.getRepos())
							stringBuilder.append("<li><a href='" + tmp.getUrl()
									+ "' target='_blank' >" + tmp.getId()
									+ "</a></li>");
						stringBuilder.append("</ul>");
						linksPanel.setText(stringBuilder.toString());
						Log.debug("Maven repositories links updated");
					}
				});
	}

	private void addDependency() {
		addDependencyButton.disable();
		// Check if dependency is already defined
		MavenCoordinates dependency = new MavenCoordinates(
				groupIdTextField.getValue(), artifactIdTextField.getValue(),
				versionField.getValue());
		for (MavenCoordinates tmp : mavenDepsPanel.getDependencies()) {
			if (tmp.equals(dependency)) {
				MessageBox.alert("Invalid data",
						"The specified dependency is already declared", null);
				addDependencyButton.enable();
				return;
			}
		}

		// Add dependency
		ArrayList<MavenCoordinates> dependencies = new ArrayList<MavenCoordinates>();
		dependencies.add(dependency);
		Log.debug("Adding dependency...");
		dispatchAsync.execute(new AddMavenDependencies(packageId, dependencies),
				new AsyncCallback<AddMavenDependenciesResult>() {

					@Override
					public void onFailure(Throwable caught) {
						addDependencyButton.enable();
					}

					@Override
					public void onSuccess(AddMavenDependenciesResult result) {
						Log.debug("Dependency added");
						addDependencyButton.enable();
						mavenDepsPanel.refreshContent();
					}
				});
	}

	@Override
	public void performNextStepLogic() {
		eventBus.fireEvent(new GoAheadEvent(this));

	}

	@Override
	public void performBackStepLogic() {
		eventBus.fireEvent(new GoBackEvent(this));
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_MavenDependencies().getText();
	}

	private class MavenDependenciesPanel extends ContentPanel {

		private ListStore<MavenDependencyModelData> depsStore = new ListStore<MavenDependencyModelData>();

		private Grid<MavenDependencyModelData> grid;
		private Button deleteFileButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE.deleteIcon()));
		private Button refreshButton = new Button("Refresh",
				AbstractImagePrototype.create(Resources.INSTANCE
						.tableRefreshIcon()));
		private ColumnModel cm;

		public MavenDependenciesPanel() {
			this.setHeading("Declared maven dependencies");

			// Columns configuration
			ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();
			CheckBoxSelectionModel<MavenDependencyModelData> sm = new CheckBoxSelectionModel<MavenDependencyModelData>();

			configs.add(sm.getColumn());

			ColumnConfig column = new ColumnConfig(MavenDependencyModelData.ID,
					"ArtifactId", 200);
			configs.add(column);

			column = new ColumnConfig(MavenDependencyModelData.GROUP_ID,
					"GroupId", 200);
			configs.add(column);

			column = new ColumnConfig(MavenDependencyModelData.VERSION,
					"Version", 200);
			configs.add(column);

			cm = new ColumnModel(configs);

			// create grid
			grid = new Grid<MavenDependencyModelData>(depsStore, cm);

			grid.setHeight(150);
			grid.setSelectionModel(sm);
			grid.setAutoExpandColumn(MavenDependencyModelData.ID);
			grid.setAutoExpandColumn(MavenDependencyModelData.GROUP_ID);
			grid.setAutoExpandColumn(MavenDependencyModelData.VERSION);
			grid.setColumnReordering(false);
			grid.setBorders(true);
			grid.addPlugin(sm);

			ToolBar bottomBar = new ToolBar();

			this.add(grid);
			bottomBar.add(deleteFileButton);
			bottomBar.add(refreshButton);
			this.setBottomComponent(bottomBar);

			this.bind();
		}

		private void bind() {
			deleteFileButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							removeSelectedDependencies();
						}
					});

			refreshButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							refreshContent();
						}
					});
		}

		public void refreshContent() {
			Log.debug("Retrieving declared dependencies...");
			this.mask();
			dispatchAsync.execute(new GetMavenDependencies(packageId),
					new AsyncCallback<GetMavenDependenciesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.error(
									"Error occurred while retrieving declared dependencies",
									caught);
						}

						@Override
						public void onSuccess(GetMavenDependenciesResult result) {
							Log.debug("Dependencies retrieved");
							setDependencies(result.getDependencies());
							MavenDependenciesPanel.this.unmask();
						}
					});
		}

		public void removeSelectedDependencies() {
			Log.debug("Removing selected dependencies...");
			dispatchAsync.execute(
					new RemoveMavenDependencies(packageId,
							new ArrayList<MavenCoordinates>(
									getSelectedDependencies())),
					new AsyncCallback<RemoveMavenDependenciesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.error(
									"Error occurred while removing selected dependencies",
									caught);
						}

						@Override
						public void onSuccess(
								RemoveMavenDependenciesResult result) {
							Log.debug("Dependencies removed");
							refreshContent();
						}
					});
		}

		public Collection<MavenCoordinates> getDependencies() {
			Collection<MavenCoordinates> result = new ArrayList<MavenCoordinates>();
			for (MavenDependencyModelData data : depsStore.getModels())
				result.add(data.getMavenCoordinates());
			return result;
		}

		public Collection<MavenCoordinates> getSelectedDependencies() {
			Collection<MavenCoordinates> result = new ArrayList<MavenCoordinates>();
			for (MavenDependencyModelData data : grid.getSelectionModel()
					.getSelectedItems()) {
				result.add(data.getMavenCoordinates());
			}
			return result;
		}

		public void setDependencies(Collection<MavenCoordinates> dependencies) {
			depsStore.removeAll();
			for (MavenCoordinates coords : dependencies) {
				depsStore.add(new MavenDependencyModelData(coords));
			}
			depsStore.commitChanges();
			grid.getSelectionModel().setSelection(depsStore.getModels());
		}
		
		@Override
		public El mask() {
		
			return super.mask("Loading...", "loading");
		}

		private class MavenDependencyModelData extends BaseModelData {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3968246916645713917L;

			public final static String ID = "ID";
			public final static String GROUP_ID = "GROUP_ID";
			public final static String VERSION = "VERSION";

			public MavenDependencyModelData(MavenCoordinates coordinates) {
				set(ID, coordinates.getArtifactId());
				set(GROUP_ID, coordinates.getGroupId());
				set(VERSION, coordinates.getVersion());
			}

			public MavenCoordinates getMavenCoordinates() {
				return new MavenCoordinates((String) get(GROUP_ID),
						(String) get(ID), (String) get(VERSION));
			}

		}
	}

}
