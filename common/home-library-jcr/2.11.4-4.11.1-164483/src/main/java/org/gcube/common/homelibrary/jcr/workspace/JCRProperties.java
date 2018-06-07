package org.gcube.common.homelibrary.jcr.workspace;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
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
		return getProperties().get(propertyName);
	}


	@Override
	public Map<String, String> getProperties() throws InternalErrorException {

		if (map!=null)
			return map;

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(portalLogin, false);	
			map = itemDelegate.getMetadata();
		} catch (Exception e) {
			logger.error("Error getting properties " , e);
		}finally {
			servlets.releaseSession();
		}	
		return map;
	}

	@Deprecated
	@Override
	public void addProperty(String name, String value) throws InternalErrorException {

		Validate.notNull(name,"Name property must be not null");
		Validate.notNull(value,"Value property must be not null");

		try {
			String escapeName = Text.escapeIllegalJcrChars(name);
			getProperties().put(escapeName, value);
			update();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public void addProperties(Map<String,String> properties) throws InternalErrorException {
		Validate.notNull(properties,"Properties map must be not null");
		try {
			Set<String> keys = properties.keySet();
			for (String key: keys){
				String value = properties.get(key);
				String escapeName = Text.escapeIllegalJcrChars(key);
				getProperties().put(escapeName, value);
			}
			update();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}



	@Override
	public void update() throws InternalErrorException {

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(portalLogin, false);	
			itemDelegate.setMetadata(getProperties());
			servlets.saveItem(itemDelegate);
		} catch (Exception e) {
			logger.error("Error updating properties " , e);
		}finally {
			servlets.releaseSession();
		}

	}


	@Override
	public boolean hasProperty(String property) throws InternalErrorException {

		if (getProperties().containsKey(property))
			return true;

		return false;
	}

}
