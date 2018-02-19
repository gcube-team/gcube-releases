package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager;

import java.util.Set;

import org.sdmxsource.sdmx.api.exception.CrossReferenceException;
import org.sdmxsource.sdmx.api.manager.retrieval.SdmxBeanRetrievalManager;
import org.sdmxsource.sdmx.api.model.ResolutionSettings.RESOLVE_CROSS_REFERENCES;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencyBean;
import org.sdmxsource.sdmx.api.model.beans.base.IdentifiableBean;
import org.sdmxsource.sdmx.api.model.beans.base.MaintainableBean;
import org.sdmxsource.sdmx.api.model.beans.reference.CrossReferenceBean;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.api.model.beans.reference.StructureReferenceBean;
import org.sdmxsource.sdmx.api.model.query.RESTStructureQuery;

public class ISRESTSdmxBeanRetrievalManager implements SdmxBeanRetrievalManager {


	
	
	@Override
	public AgencyBean getAgency(String id) 
	{
		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getAgency(id);
	}

	@Override
	public IdentifiableBean getIdentifiableBean(CrossReferenceBean crossReferenceBean) throws CrossReferenceException {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getIdentifiableBean(crossReferenceBean);
	}

	@Override
	public Set<? extends IdentifiableBean> getIdentifiableBeans(StructureReferenceBean sRef) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getIdentifiableBeans(sRef);
	}

	@Override
	public <T> T getIdentifiableBean(CrossReferenceBean crossReferenceBean, Class<T> structureType)
			throws CrossReferenceException {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getIdentifiableBean(crossReferenceBean,structureType);
	}

	@Override
	public <T> T getIdentifiableBean(StructureReferenceBean crossReferenceBean, Class<T> structureType)
			throws CrossReferenceException {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getIdentifiableBean(crossReferenceBean,structureType);
	}

	@Override
	public SdmxBeans getMaintainables(RESTStructureQuery restquery) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainables(restquery);
	}

	@Override
	public SdmxBeans getSdmxBeans(StructureReferenceBean sRef, RESOLVE_CROSS_REFERENCES resolveCrossReferences) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getSdmxBeans(sRef, resolveCrossReferences);
	}

	@Override
	public MaintainableBean getMaintainableBean(StructureReferenceBean sRef) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBean(sRef);
	}

	@Override
	public MaintainableBean getMaintainableBean(StructureReferenceBean sRef, boolean returnStub, boolean returnLatest) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBean(sRef,returnStub,returnLatest);
	}

	@Override
	public <T extends MaintainableBean> T getMaintainableBean(Class<T> structureType, MaintainableRefBean ref) {
	
		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBean(structureType,ref);
	}

	@Override
	public <T extends MaintainableBean> T getMaintainableBean(Class<T> structureType, MaintainableRefBean ref,
			boolean returnStub, boolean returnLatest) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBean(structureType,ref,returnStub,returnLatest);
	}

	@Override
	public <T extends MaintainableBean> Set<T> getMaintainableBeans(Class<T> structureType) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBeans(structureType);
	}

	@Override
	public <T extends MaintainableBean> Set<T> getMaintainableBeans(Class<T> structureType, MaintainableRefBean ref) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBeans(structureType,ref);
	}

	@Override
	public <T extends MaintainableBean> Set<T> getMaintainableBeans(Class<T> structureType, MaintainableRefBean ref,
			boolean returnLatest, boolean returnStub) {

		return BeanRetrievalManagerProvider.getInstance().getRESTSdmxBeanRetrievalManager().getMaintainableBeans(structureType,ref);
	}

	
	
}
