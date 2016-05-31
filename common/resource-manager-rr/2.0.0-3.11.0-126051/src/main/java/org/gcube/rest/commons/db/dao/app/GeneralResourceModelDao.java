package org.gcube.rest.commons.db.dao.app;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.gcube.rest.commons.db.model.app.GeneralResourceModel;
import org.gcube.rest.commons.db.model.core.GenericDaoImpl;
import org.gcube.rest.commons.db.model.core.IGenericDAO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;

import com.google.common.collect.ImmutableMap;

@Singleton
public class GeneralResourceModelDao extends GenericDaoImpl<GeneralResourceModel> implements IGenericDAO<GeneralResourceModel> {

	private final static String SCOPES_FIELD_NAME = "scopes";
	private final static String SCOPES_ALIAS = "s";
	private final static Map<String, String> aliases = ImmutableMap.of(
			SCOPES_FIELD_NAME, SCOPES_ALIAS);
	
	@Override
	public Class<GeneralResourceModel> getClazz() {
		return GeneralResourceModel.class;
	}
	
	public GeneralResourceModel getByResourceID(String resourceID, String scope){
		Criterion criterion1 = Restrictions.eq("resourceID", resourceID);
		Criterion criterion2 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<GeneralResourceModel> results = this.findByCriteria(aliases, criterion1, criterion2);
		
		if (results.size() != 1){
			throw new IllegalStateException("expected result was 1. found : " + results.size());
		}
		
		return results.get(0);
	}
}


