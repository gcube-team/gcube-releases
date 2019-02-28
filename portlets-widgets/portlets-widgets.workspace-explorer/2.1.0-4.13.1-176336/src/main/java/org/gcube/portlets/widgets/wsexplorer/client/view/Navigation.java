/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.event.CreateFolderClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadMySpecialFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEvent;
import org.gcube.portlets.widgets.wsexplorer.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory;

import com.github.gwtbootstrap.client.ui.NavWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.ListStyleType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 24, 2015
 */
public class Navigation extends Composite{

	private static NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);

	@UiField
	FlowPanel navigation;

	@UiField
	NavWidget home;

	@UiField
	NavWidget vre_folder;

	@UiField
	NavWidget new_folder;

	private HandlerManager eventBus;

	/**
	 * The Interface BreadcrumbsUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jun 23, 2015
	 */
	interface NavigationUiBinder extends UiBinder<Widget, Navigation> {
	}

	/**
	 * @param eventbus
	 *
	 */
	public Navigation(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
//		getElement().getStyle().setMarginLeft(5.0, Unit.PX);
		home.setActive(true);
		getElement().getStyle().setListStyleType(ListStyleType.NONE);
		WorkspaceExplorerConstants.workspaceNavigatorService.getItemByCategory(ItemCategory.HOME, new AsyncCallback<Item>() {

			@Override
			public void onSuccess(Item result) {
				home.setText(result.getName());

			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
				home.setText(WorkspaceExplorerConstants.HOME_LABEL);
			}
		});


		home.setBaseIcon(WorkspaceExplorerResources.CustomIconType.home);
		home.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Navigation.this.eventBus.fireEvent(new LoadRootEvent());
			}
		});

		vre_folder.setActive(true);
		vre_folder.setBaseIcon(WorkspaceExplorerResources.CustomIconType.vre_folder);

		WorkspaceExplorerConstants.workspaceNavigatorService.getItemByCategory(ItemCategory.VRE_FOLDER, new AsyncCallback<Item>() {

			@Override
			public void onSuccess(Item result) {
				vre_folder.setText(result.getName());

			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
				vre_folder.setText(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL);

			}
		});

		vre_folder.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Navigation.this.eventBus.fireEvent(new LoadMySpecialFolderEvent());
			}
		});

		new_folder.setActive(false);
		new_folder.setBaseIcon(WorkspaceExplorerResources.CustomIconType.new_folder);
		new_folder.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Navigation.this.eventBus.fireEvent(new CreateFolderClickEvent());
			}
		});
	}

	public void setVisibleNewFolderFacility(boolean bool){
		new_folder.setVisible(bool);
	}
}
