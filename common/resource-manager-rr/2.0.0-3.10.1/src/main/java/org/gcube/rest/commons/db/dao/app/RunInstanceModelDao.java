package org.gcube.rest.commons.db.dao.app;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.gcube.rest.commons.db.model.app.RunInstanceModel;
import org.gcube.rest.commons.db.model.core.GenericDaoImpl;
import org.gcube.rest.commons.db.model.core.IGenericDAO;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Singleton
public class RunInstanceModelDao extends GenericDaoImpl<RunInstanceModel> implements IGenericDAO<RunInstanceModel> {

	@Override
	public Class<RunInstanceModel> getClazz() {
		return RunInstanceModel.class;
	}
	
	public List<RunInstanceModel> getByServiceClassAndServiceNameAndScopeAndEndpointKey(
			String serviceClass,
			String serviceName,
			String scope,
			String endpointKey
			){
		
		List<RunInstanceModel> results = this.findByCriteria(
				Restrictions.eq("serviceClass", serviceClass),
				Restrictions.eq("serviceName", serviceName)
				);
		
		List<RunInstanceModel> runningInstances = Lists.newArrayList();
		
		for (RunInstanceModel result : results){
			String url = result.getEndpoints().get(endpointKey).toASCIIString();
			
			runningInstances.add(result);
		}
		
		return runningInstances;
		
	}
	
	public List<RunInstanceModel> getByServiceClassAndServiceNameAndScope(
			String serviceClass,
			String serviceName,
			String scope
			){
		
		List<RunInstanceModel> results = this.findByCriteria(
				Restrictions.eq("serviceClass", serviceClass),
				Restrictions.eq("serviceName", serviceName)
				);
		
		return results;
	}
	
	public static final Function<RunInstanceModel, String> resourceIDextractor = new Function<RunInstanceModel, String>() {
		@Override
		public String apply(RunInstanceModel input) {
			return input.getResourceId();
		}
	};
	
	public static final Function<RunInstanceModel, RunInstance> converterModelToBase = new Function<RunInstanceModel, RunInstance>() {
		@Override
		public RunInstance apply(RunInstanceModel input) {
			return input.copyTo();
		}
	};
	
	public static final Function<RunInstance, RunInstanceModel> converterBaseToModel = new Function<RunInstance, RunInstanceModel>() {
		@Override
		public RunInstanceModel apply(RunInstance input) {
			return new RunInstanceModel(input);
		}
	};
	
	public static List<RunInstance> convertToRunInstanceList(List<RunInstanceModel> list){
		return Lists.newArrayList(
				Collections2.transform(list, RunInstanceModelDao.converterModelToBase)
		);
	}
	
	public static Set<RunInstance> convertToRunInstanceSet(Collection<RunInstanceModel> list){
		return Sets.newHashSet(
				Collections2.transform(list, RunInstanceModelDao.converterModelToBase)
		);
	}
	
	
	public static Set<String> convertToResourceIDsSet(List<RunInstanceModel> list){
		return Sets.newHashSet(
				Collections2.transform(list, RunInstanceModelDao.resourceIDextractor)
		);
	}
}
