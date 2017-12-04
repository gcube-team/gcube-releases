/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShareLinkEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowDatasetsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowGroupsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowManageProductWidgetEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowOrganizationsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowTypesEvent;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
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

	private static final String MANAGE_ITEM_TOOLTIP = "By pushing on this button, you will be able to manage the item you are viewing."
			+ " Manageable items are the GRSF ones.";

	private AlertBlock nav = new AlertBlock();

	// generic
	private Button home = new Button("Home");
	private Button organizations = new Button("Organizations");
	private Button groups = new Button("Groups");
	private Button items = new Button("Items");
	private Button types = new Button("Types");

	// user's own
	private InlineHTML separatorMyInfo = null;
	private InlineHTML separatorAdminButtons = null;
	private Button myDatasets = new Button("My Items");
	private Button myOrganizations = new Button("My Organizations");
	private Button myGroups = new Button("My Groups");

	// statistics
	private Button statistics = new Button("Statistics");

	// other stuff
	private Button shareLink = new Button("Share Link");
	private Button insertMeta = new Button("Publish Item");
	private Button editMeta = new Button("Edit Item");
	private Button manageProduct = new Button("Manage Item");
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
		items.setType(ButtonType.LINK);
		types.setType(ButtonType.LINK);

		myDatasets.setType(ButtonType.LINK);
		myOrganizations.setType(ButtonType.LINK);
		myGroups.setType(ButtonType.LINK);

		statistics.setType(ButtonType.LINK);

		shareLink.setType(ButtonType.LINK);
		insertMeta.setType(ButtonType.LINK);
		editMeta.setType(ButtonType.LINK);
		manageProduct.setType(ButtonType.PRIMARY);
		manageProduct.getElement().getStyle().setFloat(Float.RIGHT);

		// set icons
		home.setIcon(IconType.HOME);
		organizations.setIcon(IconType.BUILDING);
		groups.setIcon(IconType.GROUP);
		items.setIcon(IconType.SITEMAP);
		types.setIcon(IconType.FILE_TEXT);
		shareLink.setIcon(IconType.SHARE);
		myDatasets.setIcon(IconType.SITEMAP);
		myOrganizations.setIcon(IconType.BUILDING);
		myGroups.setIcon(IconType.GROUP);
		insertMeta.setIcon(IconType.FILE);
		editMeta.setIcon(IconType.EDIT_SIGN);
		statistics.setIcon(IconType.BAR_CHART);
		manageProduct.setIcon(IconType.CHECK_SIGN); 

		// hide edit and insert
		shareLink.setEnabled(false);
		editMeta.setVisible(false);
		insertMeta.setVisible(false);
		manageProduct.setVisible(false);
		manageProduct.setEnabled(false);

		// manage item info
		manageProduct.setTitle(MANAGE_ITEM_TOOLTIP);
		manageProduct.getElement().getStyle().setFontWeight(FontWeight.BOLD);

		// add to navigation bar
		nav.add(home);
		nav.add(organizations);
		nav.add(groups);
		nav.add(items);
		nav.add(types);
		nav.add(statistics);
		separatorMyInfo = new InlineHTML("<span style=\"font-weight:bold;vertical-alignment:middle;\">|</span>");
		separatorMyInfo.setVisible(true);
		nav.add(separatorMyInfo);
		nav.add(myOrganizations);
		nav.add(myGroups);
		nav.add(myDatasets);
		separatorAdminButtons = new InlineHTML("<span style=\"font-weight:bold;vertical-alignment:middle;\">|</span>");
		separatorAdminButtons.setVisible(true);
		nav.add(separatorAdminButtons);
		nav.add(shareLink);
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

		items.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowDatasetsEvent(false));

			}
		});
		
		types.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShowTypesEvent(false));

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

		shareLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				eventBus.fireEvent(new ShareLinkEvent(GCubeCkanDataCatalogPanel.getLatestSelectedProductIdentifier()));

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
		//separatorAdminButtons.setVisible(show);
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
	 * Enable or disable the share link button
	 */
	public void enableShareItemButton(boolean value){
		shareLink.setEnabled(value);
	}

	/**
	 * Show only home/statistics buttons
	 */
	public void doNotShowUserRelatedInfo(){

		separatorMyInfo.setVisible(false);
		separatorAdminButtons.setVisible(false);
		shareLink.setVisible(false);
		insertMeta.setVisible(false);
		editMeta.setVisible(false);
		myDatasets.setVisible(false);
		myOrganizations.setVisible(false);
		myGroups.setVisible(false);
		manageProduct.setVisible(false);

	}

	public void removeGenericManagementButtons() {

		home.setVisible(false);
		organizations.setVisible(false);
		groups.setVisible(false);
		items.setVisible(false);
		types.setVisible(false);
		separatorMyInfo.setVisible(false);
		separatorAdminButtons.setVisible(false);
		myDatasets.setVisible(false);
		myOrganizations.setVisible(false);
		myGroups.setVisible(false);
		statistics.setVisible(false);
		manageProduct.setVisible(false);
		
	}
}
