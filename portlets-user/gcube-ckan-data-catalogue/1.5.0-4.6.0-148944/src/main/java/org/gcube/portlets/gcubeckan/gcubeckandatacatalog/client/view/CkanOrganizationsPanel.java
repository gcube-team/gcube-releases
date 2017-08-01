package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import java.util.List;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.resource.CkanPortletResources;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgGroupRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Footer;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The ckan panel that shows the user organizations
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CkanOrganizationsPanel extends VerticalPanel{

	private List<BeanUserInOrgGroupRole> organizations;
	private GCubeCkanDataCatalogPanel father;
	private Image loading = new Image(CkanPortletResources.ICONS.loading());

	public CkanOrganizationsPanel(
			GCubeCkanDataCatalogPanel gCubeCkanDataCatalogPanel) {
		father = gCubeCkanDataCatalogPanel;
		this.setHeight("500px");
		this.setWidth("100%");
		this.add(loading);
		this.setCellHorizontalAlignment(loading, HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * Set the organizations to show. If a null list is passed, it is an error
	 * @param result
	 */
	public void setOrganizations(List<BeanUserInOrgGroupRole> result) {

		this.organizations = result;
		
		// prepare panels
		this.remove(loading);
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("horizontal-panel-organizations");
		VerticalPanel vPanel = new VerticalPanel();
		hPanel.add(vPanel);
		this.add(hPanel);

		//generate the list of organizations
		if(result == null){
			Paragraph p = new Paragraph("There was an error while retrieving your organizations, sorry.");
			p.setStyleName("no-organizations-found-paragraph");
			p.getElement().getStyle().setColor("#aaaaaa");
			vPanel.add(p);
		}
		else if(result.isEmpty()){

			Paragraph p = new Paragraph("You are not a member of any organization.");
			p.setStyleName("no-organizations-found-paragraph");
			p.getElement().getStyle().setColor("#aaaaaa");
			vPanel.add(p);

		}else{

			UnorderedList list = new UnorderedList();

			for (final BeanUserInOrgGroupRole org : organizations) {
				
				Paragraph line = new Paragraph();
				Button b = new Button();
				b.setType(ButtonType.LINK);
				b.setText(org.getName());
				b.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						String request = getCkanRequest(org.getUrl(), null);
						father.instanceCkanFrame(request);

					}
				});

				line.add(b);
				Button role = new Button();
				role.setType(ButtonType.LINK);
				role.setText("as Catalogue-" + org.getRole().toString().toLowerCase());
				role.addStyleName("button-as-role-style");
				role.getElement().getStyle().setProperty("pointer-events", "none");
				line.add(role);
				ListItem item = new ListItem(line);
				list.add(item);
			}

			list.addStyleName("list-panel-organizations-style");
			vPanel.add(list);
		}

		// add the footer
		String html = "<a class=\"d4s-hide-text d4science-footer-logo\" href=\"http://www.gcube-system.org\" target=\"_blank\" title=\"Powered by gCube\">Powered by gCube</a>" +
				"<a class=\"d4s-hide-text d4s-ckan-footer-logo\" href=\"http://ckan.org\" title=\"CKAN\">CKAN</a>";
		Footer footer = new Footer(html);
		footer.setStyleName("footer-organizations");
		this.add(footer);
		this.setWidth("100%");
		this.setStyleName("my-organizations-container-style");
	}

	/**
	 * Request the correct url to the ckan connector
	 * @param pathInfo
	 * @param query
	 * @return
	 */
	private String getCkanRequest(String pathInfo, String query){
		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(father.getCatalogueUrl(), "");
		//ckan.addGubeToken(father.getGcubeTokenValueToCKANConnector());
		pathInfo = CkanConnectorAccessPoint.checkNullString(pathInfo);
		query = CkanConnectorAccessPoint.checkNullString(query);
		ckan.addPathInfo(pathInfo);
		ckan.addQueryString(query);
		return ckan.buildURI();
	}

}
