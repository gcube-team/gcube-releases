package org.gcube.vremanagement.vremodeler.resources;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.InputSource;
import javax.xml.xpath.*;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.GenericResource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class GenericResources extends ResourceDefinition<GenericResource> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static GCUBELog logger = new GCUBELog(GenericResources.class);
	
	private static Dao<GenericResource, String> grDao;
		
	
	private String name=null;
	private String secondaryType = null;
	private String xpathToVerify=null;
	
	public GenericResources() throws Exception {	
		super();
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXpathToVerify() {
		return xpathToVerify;
	}

	public void setXpathToVerify(String xpathToVerify) {
		this.xpathToVerify = xpathToVerify;
	}

	public String getSecondaryType() {
		return secondaryType;
	}

	public void setSecondaryType(String secondaryType) {
		this.secondaryType = secondaryType;
	}

	@Override
	public List<GenericResource> getResources() throws Exception {
		grDao = DaoManager.createDao(DBInterface.connect(), GenericResource.class);
		QueryBuilder<GenericResource, String> query = grDao.queryBuilder();
		Where<GenericResource, String> where = query.where().eq("type", this.getSecondaryType());
		if (this.getName()!=null) where.and().eq("name", this.getName());
		if (this.xpathToVerify!=null){
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile(this.getXpathToVerify());
			logger.trace("xpath is to verify "+this.xpathToVerify);
			List<GenericResource> resources = new ArrayList<GenericResource>();
			for (GenericResource genericResource : grDao.query(query.prepare())) {
				if (genericResource.getBody()==null ) continue;
				if ((Boolean)expr.evaluate(new InputSource(new StringReader(genericResource.getBody())),  XPathConstants.BOOLEAN))
					resources.add(genericResource);
			}
			return resources;
		}
		else return grDao.query(query.prepare());
	}
	
	
	
}
