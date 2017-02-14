/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowDatasetsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowGroupsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowManageProductWidgetEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowOrganizationsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEvent;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;


/**
 * The Class CkanMetadataManagementPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 * Jun 9, 2016
 */
public class CkanMetadataManagementPanel extends FlowPanel{

	private AlertBlock nav = new AlertBlock();

	// generic
	private Button home = new Button("Home");
	private Button organizations = new Button("Organizations");
	private Button groups = new Button("Groups");
	private Button products = new Button("Products");

	// user's own
	private InlineHTML separatorMyInfo = null;
	private InlineHTML separatorAdminButtons = null;
	private Button myDatasets = new Button("My Products");
	private Button myOrganizations = new Button("My Organizations");
	private Button myGroups = new Button("My Groups");

	// statistics
	private Button statistics = new Button("Statistics");

	// other stuff
	private Button insertMeta = new Button("Publish Product");
	private Button editMeta = new Button("Edit Product");
	private Button manageProduct = new Button("Manage Product");
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
		organizations.setType(ButtonType.LINK);
		groups.setType(ButtonType.LINK);
		products.setType(ButtonType.LINK);

		myDatasets.setType(ButtonType.LINK);
		myOrganizations.setType(ButtonType.LINK);
		myGroups.setType(ButtonType.LINK);

		statistics.setType(ButtonType.LINK);

		insertMeta.setType(ButtonType.LINK);
		editMeta.setType(ButtonType.LINK);
		manageProduct.setType(ButtonType.LINK);
		manageProduct.getElement().getStyle().setFloat(Float.RIGHT);

		// set icons
		home.setIcon(IconType.HOME);
		organizations.setIcon(IconType.BUILDING);
		groups.setIcon(IconType.GROUP);
		products.setIcon(IconType.SITEMAP);
		myDatasets.setIcon(IconType.SITEMAP);
		myOrganizations.setIcon(IconType.BUILDING);
		myGroups.setIcon(IconType.GROUP);
		insertMeta.setIcon(IconType.FILE);
		editMeta.setIcon(IconType.EDIT_SIGN);
		statistics.setIcon(IconType.BAR_CHART);
		manageProduct.setIcon(IconType.CHECK_SIGN); 

		// hide edit and insert
		editMeta.setVisible(false);
		insertMeta.setVisible(false);
		manageProduct.setVisible(false);
		manageProduct.setEnabled(false);

		// add to navigation bar
		nav.add(home);
		nav.add(organizations);
		nav.add(groups);
		nav.add(products);
		nav.add(statistics);
		separatorMyInfo = new InlineHTML("<span style=\"font-weight:bold;vertical-alignment:middle;\">|</span>");
		separatorMyInfo.setVisible(true);
		nav.add(separatorMyInfo);
		nav.add(myOrganizations);
		nav.add(myGroups);
		nav.add(myDatasets);
		separatorAdminButtons = new InlineHTML("<span style=\"font-weight:bold;vertical-alignment:middle;\">|</span>");
		separatorAdminButtons.setVisible(false);
		nav.add(separatorAdminButtons);
		nav.add(insertMeta);
		nav.add(editMeta);
		nav.add(manageProduct);
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

		organizations.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowOrganizationsEvent(false));

			}
		});

		groups.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowGroupsEvent(false));

			}
		});

		products.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowDatasetsEvent(false));

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

				eventBus.fireEvent(new ShowDatasetsEvent(true));

			}
		});

		myOrganizations.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowOrganizationsEvent(true));

			}
		});


		myGroups.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowGroupsEvent(true));

			}
		});

		statistics.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowStatisticsEvent());

			}
		});

		manageProduct.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowManageProductWidgetEvent(GCubeCkanDataCatalogPanel.getLatestSelectedProductIdentifier()));

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
	 * Those buttons can be only visible when the logged user has role edit/admin/sysadmin
	 * @param show
	 */
	public void showInsertAndEditProductButtons(boolean show){

		//editMeta.setVisible(show); TODO
		separatorAdminButtons.setVisible(show);
		insertMeta.setVisible(show);

	}

	/**
	 * Button to manage the product.. for example in grsf case
	 * @param value true or false
	 * @param isManageProductEnabled 
	 */
	public void showManageProductButton(boolean value){
		manageProduct.setVisible(value);
	}

	/**
	 * Enable or disable the manage product button
	 */
	public void enableManageProductButton(boolean value){
		manageProduct.setEnabled(value);
	}

	/**
	 * Show only home/statistics buttons
	 */
	public void doNotShowUserRelatedInfo(){

		separatorMyInfo.setVisible(false);
		insertMeta.setVisible(false);
		editMeta.setVisible(false);
		myDatasets.setVisible(false);
		myOrganizations.setVisible(false);
		myGroups.setVisible(false);
		manageProduct.setVisible(false);

	}
}
