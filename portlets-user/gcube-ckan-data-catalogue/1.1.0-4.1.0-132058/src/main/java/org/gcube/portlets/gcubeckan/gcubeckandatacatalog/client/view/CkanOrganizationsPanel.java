package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import java.util.List;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Footer;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The ckan panel that shows the user organizations
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CkanOrganizationsPanel extends VerticalPanel{

	private List<BeanUserInOrgRole> organizations;
	private GCubeCkanDataCatalogPanel father;

	public CkanOrganizationsPanel(
			GCubeCkanDataCatalogPanel gCubeCkanDataCatalogPanel) {
		father = gCubeCkanDataCatalogPanel;
	}

	/**
	 * Set the organizations to show. If a null list is passed, it is an error
	 * @param result
	 */
	public void setOrganizations(List<BeanUserInOrgRole> result) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("horizontal-panel-organizations");
		VerticalPanel vPanel = new VerticalPanel();

		hPanel.add(vPanel);
		add(hPanel);

		this.organizations = result;

		//generate the list of organizations
		if(result == null){
			Paragraph p = new Paragraph("There was an error while retrieving your organizations, sorry.");
			p.setStyleName("no-organizations-found-paragraph");
			p.getElement().getStyle().setColor("#aaaaaa");
			vPanel.add(p);
		}
		else if(result.isEmpty()){

			Paragraph p = new Paragraph("You are not a member of any organizations.");
			p.setStyleName("no-organizations-found-paragraph");
			p.getElement().getStyle().setColor("#aaaaaa");
			vPanel.add(p);

		}else{

			UnorderedList list = new UnorderedList();

			for (final BeanUserInOrgRole org : organizations) {
				
				Paragraph line = new Paragraph();
				Button b = new Button();
				b.setType(ButtonType.LINK);
				b.setText(org.getOrgName());
				b.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						String request = getCkanRequest(org.getOrgUrl(), null);
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
		String html = "Powered by <a href=\"http://www.gcube-system.org\" target=\"_blank\">gCube</a> | <a href=\"http://ckan.org\" target=\"_blank\">CKAN</a>";
		Footer footer = new Footer(html);
		footer.setStyleName("footer-organizations");
		add(footer);
		setWidth("100%");
		setStyleName("my-organizations-container-style");
	}

	/**
	 * Request the correct url to the ckan connector
	 * @param pathInfo
	 * @param query
	 * @return
	 */
	private String getCkanRequest(String pathInfo, String query){
		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(father.getBaseURLCKANConnector(), "");
		ckan.addGubeToken(father.getGcubeTokenValueToCKANConnector());
		pathInfo = CkanConnectorAccessPoint.checkNullString(pathInfo);
		query = CkanConnectorAccessPoint.checkNullString(query);
		ckan.addPathInfo(pathInfo);
		ckan.addQueryString(query);
		return ckan.buildURI();
	}

}
