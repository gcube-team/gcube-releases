package org.gcube.common.homelibrary.jcr.workspace;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRProperties implements Properties {

	private static Logger logger = LoggerFactory.getLogger(JCRProperties.class);


	private Map<String,String> map;
	private ItemDelegate itemDelegate;
	private String portalLogin;

	public JCRProperties(ItemDelegate itemDelegate, String portalLogin) throws RepositoryException, InternalErrorException {
		this.portalLogin = portalLogin;
		this.itemDelegate = itemDelegate;
		this.map = itemDelegate.getMetadata();
		if (this.map == null)
			this.map = new HashMap<String, String>();
	}


	@Override
	public String getId() throws InternalErrorException {
		return itemDelegate.getId();
	}

	@Override
	public String getPropertyValue(String propertyName) throws InternalErrorException {
		return map.get(propertyName);
	}

	@Override
	public Map<String, String> getProperties() throws InternalErrorException {
		return map;
	}

	@Override
	public void addProperty(String name, String value) throws InternalErrorException {

		Validate.notNull(name,"Name property must be not null");
		Validate.notNull(value,"Value property must be not null");

		try {
			String escapeName = Text.escapeIllegalJcrChars(name);
			map.put(escapeName, value);
			update();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public void update() throws InternalErrorException {

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(portalLogin, false);	
			itemDelegate.setMetadata(map);
			servlets.saveItem(itemDelegate);
		} catch (Exception e) {
			logger.error("Error updating properties " , e);
		}finally {
			servlets.releaseSession();
		}

	}

}
