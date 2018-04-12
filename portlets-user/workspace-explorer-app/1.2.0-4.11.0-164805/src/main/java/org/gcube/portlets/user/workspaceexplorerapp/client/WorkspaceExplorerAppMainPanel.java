/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialNavSection;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.client.download.DownloadType;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.DownloadItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.grid.DisplayField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;



/**
 * The Class WorkspaceExplorerAppMainPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 3, 2017
 */
public class WorkspaceExplorerAppMainPanel extends Composite {

	private static WorkspaceExplorerAppMainPanelUiBinder uiBinder =
		GWT.create(WorkspaceExplorerAppMainPanelUiBinder.class);


	/**
	 * The Interface WorkspaceExplorerAppMainPanelUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jul 3, 2017
	 */
	interface WorkspaceExplorerAppMainPanelUiBinder
		extends UiBinder<Widget, WorkspaceExplorerAppMainPanel> {
	}

	@UiField
	HTMLPanel explorer_main_container;

	@UiField
	HTMLPanel we_html_base_panel;

	@UiField
	MaterialNavBar we_nav_bar;

	@UiField
	MaterialNavSection we_nav_right;

	@UiField
	MaterialLink download_we;

	@UiField
	MaterialLink show_we;

	@UiField
	Image d4science_workspace_logo_180;

	private HandlerManager handlerManager;


	/**
	 * Instantiates a new workspace explorer app main panel.
	 *
	 * @param handlerManager the handler manager
	 * @param displayFields the display fields
	 */
	public WorkspaceExplorerAppMainPanel(HandlerManager handlerManager, DisplayField[] displayFields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.handlerManager = handlerManager;
		we_nav_bar.getElement().setId("we_nav_bar");
		we_nav_bar.getElement().setAttribute("id", "we_nav_bar");

		final MaterialLink order = new MaterialLink("Order by");
		order.setTitle("Order data for item selected");
		order.setActivates("order_list");
		order.setTextColor("white");
		order.setIconType(IconType.ARROW_DROP_DOWN);
		order.setIconPosition(IconPosition.RIGHT);

		final List<MaterialLink> links = new ArrayList<MaterialLink>(displayFields.length);
		for (final DisplayField field : displayFields) {
			if(field.isSortable()){
				MaterialLink link = new MaterialLink(field.getLabel());
				link.addStyleName("under-line-onhover");
				links.add(link);
			}
		}

		final SortByContextMenu scm = new SortByContextMenu(this.handlerManager, links);

		order.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GWT.log("clicked : "+event.getSource().toString());
				scm.showRelativeTo(order);
			}
		});

		we_nav_right.add(order);
		download_we.addClickHandler(new ClickHandler() {

			/* (non-Javadoc)
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent event) {
				WorkspaceExplorerAppMainPanel.this.handlerManager.fireEvent(new DownloadItemEvent(null, DownloadType.DOWNLOAD));
			}
		});

		show_we.addClickHandler(new ClickHandler() {

			/* (non-Javadoc)
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent event) {
				WorkspaceExplorerAppMainPanel.this.handlerManager.fireEvent(new DownloadItemEvent(null, DownloadType.OPEN));
			}
		});

		d4science_workspace_logo_180.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Window.open("https://www.d4science.org", "_blank", null);
			}
		});
	}

	/**
	 * Update to explorer panel.
	 *
	 * @param workspaceExplorerAppPanel the workspace explorer app panel
	 */
	public void updateToExplorerPanel(WorkspaceExplorerAppPanel workspaceExplorerAppPanel){
		explorer_main_container.clear();
		explorer_main_container.add(workspaceExplorerAppPanel);
	}



	/**
	 * Update to error.
	 *
	 * @param error the error
	 */
	public void updateToError(Widget error){
		explorer_main_container.clear();
		explorer_main_container.add(error);
	}


}
