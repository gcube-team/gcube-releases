package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;

import com.github.gwtbootstrap.client.ui.Button;
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

	private Map<String, String> organizations;
	private GCubeCkanDataCatalogPanel father;

	public CkanOrganizationsPanel(
			GCubeCkanDataCatalogPanel gCubeCkanDataCatalogPanel) {
		father = gCubeCkanDataCatalogPanel;
	}

	/**
	 * Set the organizations to show
	 * @param result
	 */
	public void setOrganizations(Map<String, String> result) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("horizontal-panel-organizations");
		VerticalPanel vPanel = new VerticalPanel();

		hPanel.add(vPanel);
		add(hPanel);

		this.organizations = result;

		//generate the list of organizations
		if(result == null || result.isEmpty()){

			Paragraph p = new Paragraph("You are not a member of any organizations.");
			p.setStyleName("no-organizations-found-paragraph");
			p.getElement().getStyle().setColor("#aaaaaa");
			vPanel.add(p);

		}else{

			UnorderedList list = new UnorderedList();
			Iterator<Entry<String, String>> iterator = organizations.entrySet().iterator();

			while (iterator.hasNext()) {
				final Map.Entry<String, String> entry = iterator
						.next();

				Button b = new Button();
				b.setType(ButtonType.LINK);
				b.setText(entry.getKey());
				b.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						String request = getCkanRequest(entry.getValue(), null);
						father.instanceCkanFrame(request);

					}
				});

				ListItem item = new ListItem(b);
				list.add(item);
			}

			list.addStyleName("list-panel-organizations-style");
			vPanel.add(list);
		}
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
