package org.gcube.rest.commons.db.dao.app;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.gcube.rest.commons.db.model.app.HostNodeModel;
import org.gcube.rest.commons.db.model.core.GenericDaoImpl;
import org.gcube.rest.commons.db.model.core.IGenericDAO;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Singleton
public class HostNodeModelDao extends GenericDaoImpl<HostNodeModel> implements IGenericDAO<HostNodeModel> {
	private static final Logger logger = LoggerFactory.getLogger(HostNodeModelDao.class);

	private final static String SCOPES_FIELD_NAME = "scopes";
	private final static String SCOPES_ALIAS = "s";
	private final static Map<String, String> aliases = ImmutableMap.of(
			SCOPES_FIELD_NAME, SCOPES_ALIAS);

	@Override
	public Class<HostNodeModel> getClazz() {
		return HostNodeModel.class;
	}

	public List<HostNodeModel> getByScope(String scope) {

		Criterion criterion = Restrictions.eq(SCOPES_ALIAS + "."
				+ CollectionPropertyNames.COLLECTION_ELEMENTS, scope);

		List<HostNodeModel> results = this.findByCriteria(aliases, criterion);

		return results;
	}

	public static final Function<HostNodeModel, HostNode> converterModelToBase = new Function<HostNodeModel, HostNode>() {
		@Override
		public HostNode apply(HostNodeModel input) {
			return input.copyTo();
		}
	};

	public static final Function<HostNode, HostNodeModel> converterBaseToModel = new Function<HostNode, HostNodeModel>() {
		@Override
		public HostNodeModel apply(HostNode input) {
			return new HostNodeModel(input);
		}
	};

	public static List<HostNode> convertToHostNodeList(List<HostNodeModel> list) {
		return Lists.newArrayList(Collections2.transform(list, HostNodeModelDao.converterModelToBase));
	}
}
