/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbInitEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbInitEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.CreateFolderClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.CreateFolderClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadMySpecialFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadMySpecialFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.RootLoadedEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.RootLoadedEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.widgets.wsexplorer.client.view.Breadcrumbs;
import org.gcube.portlets.widgets.wsexplorer.client.view.CreateFolderForm;
import org.gcube.portlets.widgets.wsexplorer.client.view.Navigation;
import org.gcube.portlets.widgets.wsexplorer.client.view.WorkspaceExplorer;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;


/**
 * The Class WorkspaceExplorerController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class WorkspaceExplorerController implements EventHandler {

	public HandlerManager eventBus = new HandlerManager(null);
	private WorkspaceExplorerPanel workspaceExplorerPanel;
	private WorkspaceExplorer wsExplorer;
	private Breadcrumbs breadcrumbs = new Breadcrumbs(eventBus);
	private Navigation navigation = new Navigation(eventBus);

	/**
	 * Instantiates a new workspace explorer controller.
	 *
	 * As default all items are showable and selectable
	 * @param heightPanel the height panel
	 */
	public WorkspaceExplorerController(String heightPanel){
		bindEvents();
		//As default all items are showable and selectable
		wsExplorer = new WorkspaceExplorer(eventBus, ItemType.values(), ItemType.values());
		wsExplorer.initTable(new ListDataProvider<Item>());
		initExplorerPanel(heightPanel);
	}

	/**
	 * Instantiates a new workspace explorer controller.
	 *
	 * As default all items are showable and selectable
	 *
	 * @param filterCriteria the filter criteria
	 * @param heightPanel the height panel
	 */
	public WorkspaceExplorerController(FilterCriteria filterCriteria, String heightPanel) {
		bindEvents();
		//As default all items are showable and selectable
		wsExplorer = new WorkspaceExplorer(eventBus, filterCriteria, ItemType.values(), ItemType.values(), null, false, null);
		wsExplorer.initTable(new ListDataProvider<Item>());
		initExplorerPanel(heightPanel);
	}



	/**
	 * Instantiates a new workspace explorer controller.
	 *
	 * As default all items are showable and selectable
	 *
	 * @param filterCriteria the filter criteria
	 * @param showProperties the show properties
	 * @param heightPanel the height panel
	 */
	public WorkspaceExplorerController(FilterCriteria filterCriteria, List<String> showProperties, String heightPanel) {
		bindEvents();
		//As default all items are showable and selectable
		wsExplorer = new WorkspaceExplorer(eventBus, filterCriteria, ItemType.values(), ItemType.values(), showProperties, false, null);
		wsExplorer.initTable(new ListDataProvider<Item>());
		initExplorerPanel(heightPanel);
	}



	/**
	 * Inits the explorer panel.
	 *
	 * @param heightPanel the height panel
	 */
	private void initExplorerPanel(String heightPanel){
		workspaceExplorerPanel = new WorkspaceExplorerPanel(5, wsExplorer.getPanel(), breadcrumbs, navigation, heightPanel);

	}

	/**
	 * Bind events.
	 */
	private void bindEvents() {

		eventBus.addHandler(LoadFolderEvent.TYPE, new LoadFolderEventHandler() {

			@Override
			public <T> void onLoadFolder(LoadFolderEvent<T> loadFolderEvent) {

				if(loadFolderEvent.getTargetItem()!=null){

					if(loadFolderEvent.getTargetItem() instanceof Item){
						Item item = (Item) loadFolderEvent.getTargetItem();

						if(item.isFolder()){
							try {
								wsExplorer.loadFolder(item, false, -1, -1, true);
								loadParentBreadcrumbByItemId(item.getId(), item.getName(), true);

								if(item.isSpecialFolder())
									navigation.setVisibleNewFolderFacility(false);
								else
									navigation.setVisibleNewFolderFacility(true);

								clearMoreInfo();
							} catch (Exception e) {
								GWT.log(e.getMessage());
							}
						}
					}
				}
			}
		});

		eventBus.addHandler(RootLoadedEvent.TYPE, new RootLoadedEventHandler() {

			@Override
			public void onRootLoaded(RootLoadedEvent rootLoadedEvent) {

			}
		});

		eventBus.addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				eventBus.fireEvent(new LoadFolderEvent<Item>(breadcrumbClickEvent.getTargetItem()));
			}
		});

		eventBus.addHandler(LoadRootEvent.TYPE, new LoadRootEventHandler() {

			@Override
			public void onLoadRoot(LoadRootEvent loadRootEvent) {
				wsExplorer.loadRoot();

				WorkspaceExplorerConstants.workspaceNavigatorService.getItemByCategory(ItemCategory.HOME, new AsyncCallback<Item>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
						breadcrumbs.clear();
						navigation.setVisibleNewFolderFacility(false);
					}

					@Override
					public void onSuccess(Item result) {
						result.setName(WorkspaceExplorerConstants.HOME_LABEL); //FORCE SET NAME LIKE "HOME_LABEL"
						breadcrumbs.init(result);
						navigation.setVisibleNewFolderFacility(true);
						clearMoreInfo();
					}
				});
			}
		});

		eventBus.addHandler(BreadcrumbInitEvent.TYPE, new BreadcrumbInitEventHandler() {

			@Override
			public void onBreadcrumbInit(BreadcrumbInitEvent breadcrumbInitEvent) {
				navigation.setVisibleNewFolderFacility(true);
			}
		});

		eventBus.addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public <T> void onClick(final ClickItemEvent<T> clickItemEvent) {

				if(clickItemEvent.getItem()!=null){
					if (clickItemEvent.getItem() instanceof Item) {
						Item item = (Item) clickItemEvent.getItem();
						updateMoreInfo(item);
					}
				}
			}
		});

		eventBus.addHandler(LoadMySpecialFolderEvent.TYPE, new LoadMySpecialFolderEventHandler() {

			@Override
			public void onLoadMySpecialFolder(LoadMySpecialFolderEvent loadMySpecialFolderEvent) {
				wsExplorer.loadMySpecialFolder();

				WorkspaceExplorerConstants.workspaceNavigatorService.getItemByCategory(ItemCategory.VRE_FOLDER, new AsyncCallback<Item>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
						navigation.setVisibleNewFolderFacility(false);
					}

					@Override
					public void onSuccess(Item result) {
						breadcrumbs.init(result);
						navigation.setVisibleNewFolderFacility(false);
						clearMoreInfo();
					}
				});
			}
		});

		eventBus.addHandler(CreateFolderClickEvent.TYPE, new CreateFolderClickEventHandler() {

			@Override
			public void onClick(CreateFolderClickEvent createFolderClickEvent) {
				clearMoreInfo();
				CreateFolderForm createFolder = new CreateFolderForm() {

					@Override
					public void subtmitHandler(String folderName) {

						showMessage(AlertType.INFO, "Creating folder \""+folderName+"\"");

						WorkspaceExplorerConstants.workspaceNavigatorService.createFolder(folderName, "", breadcrumbs.getLastParent().getId(), new AsyncCallback<Item>() {

							@Override
							public void onFailure(Throwable caught) {
								hideMessage();
								clearMoreInfo();
								Window.alert(caught.getMessage());
								GWT.log(caught.getMessage());
							}

							@Override
							public void onSuccess(Item result) {
								hideMessage();
								if(result!=null){
									wsExplorer.addItemToExplorer(result);
									clearMoreInfo();
								}else
									Window.alert("Create folder error, Try again");
							}
						});
					}

					@Override
					public void closeHandler() {
						workspaceExplorerPanel.getSouthPanel().remove(this);
					}
				};

				workspaceExplorerPanel.getSouthPanel().add(createFolder);
				createFolder.getTextBoxFolderName().setFocus(true);
			}
		});
	}

	/**
	 * Load parent breadcrumb by item id.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 */
	protected void loadParentBreadcrumbByItemId(final String itemIdentifier, String itemName, boolean includeItemAsParent){

		GWT.log("Reload Parent Breadcrumb: [Item id: "+itemIdentifier+"]");

		WorkspaceExplorerConstants.workspaceNavigatorService.getBreadcrumbsByItemIdentifier(itemIdentifier,  itemName, includeItemAsParent, new AsyncCallback<ArrayList<Item>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
				wsExplorer.setAlert(caught.getMessage(), AlertType.ERROR);
			}

			@Override
			public void onSuccess(ArrayList<Item> result) {
				for (Item item : result) {
					GWT.log("->"+item.getName());
				}
				if(result!=null){
					breadcrumbs.setPath(result);
					clearMoreInfo();
				}
			}
		});
	}

	/**
	 * Clear more info.
	 */
	private void clearMoreInfo(){
		workspaceExplorerPanel.getSouthPanel().clear();
	}

	/**
	 * Update more info.
	 *
	 * @param item the item
	 */
	private void updateMoreInfo(final Item item){

		if(item!=null){
			workspaceExplorerPanel.getSouthPanel().clear();
			Alert alert = new Alert();
			alert.addStyleName("alert-custom");

			final HorizontalPanel hp = new HorizontalPanel();
			Image iconInfo = WorkspaceExplorerResources.getIconInfo().createImage();
			iconInfo.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			hp.add(iconInfo);
			final Label labelName = new Label("Name: "+item.getName());
			final double marginValue = 10.0;
			labelName.getElement().getStyle().setMarginLeft(marginValue, Unit.PX);
			labelName.getElement().getStyle().setMarginRight(marginValue, Unit.PX);
			final Label labelSize = new Label("Size: ");
			labelSize.getElement().getStyle().setMarginRight(marginValue, Unit.PX);
			final Label labelMime = new Label("Mime Type: ");
			labelMime.getElement().getStyle().setMarginRight(marginValue, Unit.PX);
			final Label labelACL = new Label("Rights: ");
			labelACL.getElement().getStyle().setMarginRight(marginValue, Unit.PX);
		
			hp.add(labelName);
			hp.add(labelSize);
			if(!item.isFolder())
				hp.add(labelMime);
			hp.add(labelACL);
		
			final Button showIdButton = new Button("more");
			showIdButton.setSize("50px", "22px");
			showIdButton.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					final Label labelitemId = new Label("Id: " + item.getId());
					hp.add(labelitemId);
					labelitemId.getElement().getStyle().setMarginRight(marginValue, Unit.PX);
					showIdButton.removeFromParent();
				}
			});	
			hp.add(showIdButton);
			
			WorkspaceExplorerConstants.workspaceNavigatorService.getReadableSizeByItemId(item.getId(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(String result) {
					labelSize.setText("Size: " +result);
				}
			});

			if(!item.isFolder()){
				WorkspaceExplorerConstants.workspaceNavigatorService.getMimeType(item.getId(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(String result) {
						if(result!=null)
							labelMime.setText("Mime Type: "+result);
					}
				});
			}

			WorkspaceExplorerConstants.workspaceNavigatorService.getUserACLForFolderId(item.getId(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(String result) {
					labelACL.setText("Rights: "+result);
				}
			});
			alert.add(hp);
			alert.setType(AlertType.DEFAULT);
			alert.setClose(false);
			workspaceExplorerPanel.getSouthPanel().add(alert);		}
	}

	/**
	 * Gets the breadcrumbs.
	 *
	 * @return the breadcrumbs
	 */
	public Breadcrumbs getBreadcrumbs() {
		return breadcrumbs;
	}

	/**
	 * Gets the workspace explorer panel.
	 *
	 * @return the workspaceExplorerPanel
	 */
	public WorkspaceExplorerPanel getWorkspaceExplorerPanel() {
		return workspaceExplorerPanel;
	}

	/**
	 * Gets the ws explorer.
	 *
	 * @return the wsExplorer
	 */
	public WorkspaceExplorer getWsExplorer() {
		return wsExplorer;
	}

	/**
	 * Sets the selectable types.
	 *
	 * @param selectableTypes the new selectable types
	 */
	public void setSelectableTypes(ItemType[] selectableTypes) {
		wsExplorer.setSelectableTypes(selectableTypes);
	}

	/**
	 * Gets the selectable types.
	 *
	 * @return the selectableTypes
	 */
	public List<ItemType> getSelectableTypes() {
		return wsExplorer.getSelectableTypes();
	}

	/**
	 * Gets the showable types.
	 *
	 * @return the showable types
	 */
	public List<ItemType> getShowableTypes() {
		return wsExplorer.getShowableTypes();
	}

	/**
	 * Sets the showable types.
	 *
	 * @param showableTypes the new showable types
	 */
	public void setShowableTypes(ItemType[] showableTypes) {
		wsExplorer.setShowableTypes(showableTypes);
	}

	/**
	 * Item is selectable.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean itemIsSelectable(Item item){
		GWT.log("Selectable type: "+wsExplorer.getSelectableTypes());
		GWT.log("item: "+item);
		if (item!=null){
			boolean selectable = wsExplorer.getSelectableTypes().contains(item.getType());
			return selectable?true:false;
		}
		return false;
	}

	/**
	 * Gets the event bus.
	 *
	 * @return the eventBus
	 */
	public HandlerManager getEventBus() {
		return eventBus;
	}

}
