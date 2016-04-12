package org.gcube.rest.commons.db.dao.app;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.gcube.rest.commons.db.model.app.ResourceModel;
import org.gcube.rest.commons.db.model.core.GenericDaoImpl;
import org.gcube.rest.commons.db.model.core.IGenericDAO;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Singleton
public class ResourceModelDao extends GenericDaoImpl<ResourceModel> implements
		IGenericDAO<ResourceModel> {

	private final static String SCOPES_FIELD_NAME = "scopes";
	private final static String SCOPES_ALIAS = "s";
	private final static Map<String, String> aliases = ImmutableMap.of(
			SCOPES_FIELD_NAME, SCOPES_ALIAS);

	@Override
	public Class<ResourceModel> getClazz() {
		return ResourceModel.class;
	}

	public List<ResourceModel> getGenericResourcesByID(String resourceID,
			String scope) {
		Criterion criterion1 = Restrictions.eq("resourceID", resourceID);
		Criterion criterion2 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<ResourceModel> results = this.findByCriteria(aliases, criterion1,
				criterion2);

		return results;
	}

	public List<ResourceModel> getGenericResourcesByName(String name,
			String scope) {
		Criterion criterion1 = Restrictions.eq("name", name);
		Criterion criterion2 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<ResourceModel> results = this.findByCriteria(aliases, criterion1,
				criterion2);

		return results;
	}

	public List<ResourceModel> getGenericResourcesByType(String type,
			String scope) {
		Criterion criterion1 = Restrictions.eq("type", type);
		Criterion criterion2 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<ResourceModel> results = this.findByCriteria(aliases, criterion1,
				criterion2);

		return results;
	}

	public List<ResourceModel> getGenericResourcesByTypeAndName(String type,
			String name, String scope) {
		Criterion criterion1 = Restrictions.eq("type", type);
		Criterion criterion2 = Restrictions.eq("name", name);
		Criterion criterion3 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<ResourceModel> results = this.findByCriteria(aliases, criterion1,
				criterion2, criterion3);

		return results;
	}

	public List<String> listGenericResourceIDsByType(String type, String scope) {
		Criterion criterion1 = Restrictions.eq("type", type);
		Criterion criterion2 = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<ResourceModel> results = this.findByCriteria(aliases, criterion1,
				criterion2);

		List<String> ids = Lists.newArrayList();
		for (ResourceModel result : results) {
			ids.add(result.getResourceID());
		}

		return ids;
	}

	// //

	public static final Function<ResourceModel, Resource> converterResourceModelToResource = new Function<ResourceModel, Resource>() {
		@Override
		public Resource apply(ResourceModel input) {
			return input.copyTo();
		}
	};

	public static final Function<Resource, ResourceModel> converterResourceResourceModel = new Function<Resource, ResourceModel>() {
		@Override
		public ResourceModel apply(Resource input) {
			return new ResourceModel(input);
		}
	};

	public static List<Resource> convertToResourceList(List<ResourceModel> list) {
		return Lists.newArrayList(Collections2.transform(list,
				ResourceModelDao.converterResourceModelToResource));
	}

}
