package org.gcube.datacatalogue.catalogue.entities;

import javax.ws.rs.core.MultivaluedMap;

import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.ContextUtils;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

public class CatalogueItem {
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogueItem.class);
	
	protected String id;
	protected JSONObject item;
	
	public CatalogueItem() {}
	
	public CatalogueItem(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		this.item = (JSONObject) parser.parse(jsonString);
	}
	
	private void applicationChecks(String authorizationErroMessage) throws Exception {
		if(ContextUtils.isApplication()) {
			logger.debug("Application Token Request");
			DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
			CkanDataset dataset = dataCatalogue.getDataset(id, CatalogueUtils.fetchSysAPI());
			
			String organization = CatalogueUtilMethods.getCKANOrganization();
			if(organization.equalsIgnoreCase(dataset.getOrganization().getName())
					&& ContextUtils.getUsername().equals(dataset.getAuthor())) {
				return;
			}
			throw new Exception(authorizationErroMessage);
		}
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String create() {
		return null;
	}
	
	public String read() {
		return Delegator.delegateGet(Constants.ITEM_SHOW, (MultivaluedMap<String,String>) null);
	}
	
	public String update() {
		return null;
	}
	
	public String delete() {
		return null;
	}
	
}
