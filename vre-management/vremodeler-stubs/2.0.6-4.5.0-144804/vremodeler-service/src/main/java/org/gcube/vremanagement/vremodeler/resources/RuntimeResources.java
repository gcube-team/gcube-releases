package org.gcube.vremanagement.vremodeler.resources;

import java.util.List;

import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RuntimeResource;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class RuntimeResources extends ResourceDefinition<RuntimeResource>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	private String name= null;
	private String category;
	
	public RuntimeResources() throws Exception {
		super();
	}
	

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	@Override
	public List<RuntimeResource> getResources() throws Exception{
		Dao<RuntimeResource, String> rrDao = DaoManager.createDao(DBInterface.connect(), RuntimeResource.class);
		QueryBuilder<RuntimeResource, String> query = rrDao.queryBuilder();
		Where<RuntimeResource, String> where = query.where().eq("type", this.getCategory());
		if (this.getName()!=null) where.and().eq("name", this.getName());
		return rrDao.query(query.prepare());
	}
	

		
	
	
}
