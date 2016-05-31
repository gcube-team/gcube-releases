package org.gcube.rest.commons.db.dao.app;

import java.util.List;

import javax.inject.Singleton;

import org.gcube.rest.commons.db.model.app.SerInstanceModel;
import org.gcube.rest.commons.db.model.core.GenericDaoImpl;
import org.gcube.rest.commons.db.model.core.IGenericDAO;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@Singleton
public class SerInstanceModelDao extends GenericDaoImpl<SerInstanceModel> implements IGenericDAO<SerInstanceModel> {

	@Override
	public Class<SerInstanceModel> getClazz() {
		return SerInstanceModel.class;
	}
	
	public List<SerInstanceModel> getByServiceClassAndServiceNameAndScope(
			String serviceClass,
			String serviceName,
			String scope
			){
		
		List<SerInstanceModel> results = this.findByCriteria(
				Restrictions.eq("serviceClass", serviceClass),
				Restrictions.eq("serviceName", serviceName)
				);
		
		
		return results;
		
	}
	
	
	public static final Function<SerInstanceModel, SerInstance> converterModelToBase = new Function<SerInstanceModel, SerInstance>() {
		@Override
		public SerInstance apply(SerInstanceModel input) {
			return input.copyTo();
		}
	};
	
	public static final Function<SerInstance, SerInstanceModel> converterBaseToModel = new Function<SerInstance, SerInstanceModel>() {
		@Override
		public SerInstanceModel apply(SerInstance input) {
			return new SerInstanceModel(input);
		}
	};
	
	public static List<SerInstance> convertToSerInstanceList(List<SerInstanceModel> list){
		return Lists.newArrayList(
				Collections2.transform(list, SerInstanceModelDao.converterModelToBase)
		);
	}
}


