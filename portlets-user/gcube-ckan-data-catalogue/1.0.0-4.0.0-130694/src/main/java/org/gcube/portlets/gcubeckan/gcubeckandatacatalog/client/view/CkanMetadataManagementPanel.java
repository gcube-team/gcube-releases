/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserDatasetsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserGroupsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserOrganizationsEvent;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * The Class CkanMetadataManagementPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 * Jun 9, 2016
 */
public class CkanMetadataManagementPanel extends FlowPanel{

	private AlertBlock nav = new AlertBlock();
	private Button home = new Button("Home");
	private Button insertMeta = new Button("Publish Product");
	private Button editMeta = new Button("Edit Product");
	private Button myDatasets = new Button("My Products");
	private Button myOrganizations = new Button("My Organizations");
	private Button myGroups = new Button("My Groups");
	private Button statistics = new Button("Statistics");
	private HandlerManager eventBus;

	/**
	 * Instantiates a new ckan metadata management panel.
	 * @param eventBus
	 */
	public CkanMetadataManagementPanel(HandlerManager eventBus){
		this.eventBus = eventBus;
		//		this.getElement().getStyle().setPaddingTop(H_OFFSET, Unit.PX);
		//		this.getElement().getStyle().setPaddingBottom(H_OFFSET, Unit.PX);

		// set link style buttons
		home.setType(ButtonType.LINK);
		myDatasets.setType(ButtonType.LINK);
		myOrganizations.setType(ButtonType.LINK);
		myGroups.setType(ButtonType.LINK);
		insertMeta.setType(ButtonType.LINK);
		editMeta.setType(ButtonType.LINK);
		statistics.setType(ButtonType.LINK);

		// set icons
		home.setIcon(IconType.HOME);
		myDatasets.setIcon(IconType.SITEMAP);
		myOrganizations.setIcon(IconType.BUILDING);
		myGroups.setIcon(IconType.GROUP);
		insertMeta.setIcon(IconType.FILE);
		editMeta.setIcon(IconType.EDIT_SIGN);
		statistics.setIcon(IconType.BAR_CHART);

		// hide edit and insert
		editMeta.setVisible(false);
		insertMeta.setVisible(false);

		// add to navigation bar
		nav.add(home);
		nav.add(myDatasets);
		nav.add(myOrganizations);
		nav.add(myGroups);
		nav.add(statistics);
		nav.add(insertMeta);
		nav.add(editMeta);
		nav.setClose(false);
		nav.setType(AlertType.INFO);
		nav.getElement().getStyle().setMarginBottom(0, Unit.PX);
		nav.getElement().getStyle().setBackgroundColor("#FFF");
		addHandlers();
		add(nav);
	}

	/**
	 *
	 */
	private void addHandlers() {

		home.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowHomeEvent());

			}
		});

		insertMeta.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new InsertMetadataEvent());
			}
		});

		editMeta.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new EditMetadataEvent());
			}
		});

		myDatasets.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowUserDatasetsEvent());

			}
		});

		myOrganizations.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowUserOrganizationsEvent());

			}
		});


		myGroups.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowUserGroupsEvent());

			}
		});

		statistics.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowStatisticsEvent());

			}
		});

	}

	/**
	 * Gets the current height.
	 *
	 * @return the current height
	 */
	public int getCurrentHeight(){
		return this.getOffsetHeight();
	}

	/**
	 * Those buttons can be only visible when the logged user has role editr/admin/sysadmin
	 * @param show
	 */
	public void showInsertAndEditProductButtons(boolean show){

		//editMeta.setVisible(show); TODO
		insertMeta.setVisible(show);

	}

	/**
	 * Show only home/statistics buttons
	 */
	public void doNotShowUserRelatedInfo(){

		insertMeta.setVisible(false);
		editMeta.setVisible(false);
		myDatasets.setVisible(false);
		myOrganizations.setVisible(false);
		myGroups.setVisible(false);

	}
}
