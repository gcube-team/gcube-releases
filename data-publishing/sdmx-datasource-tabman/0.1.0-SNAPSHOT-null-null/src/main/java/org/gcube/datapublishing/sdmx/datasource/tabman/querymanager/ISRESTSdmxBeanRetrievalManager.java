package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;
import org.sdmxsource.sdmx.api.constants.SDMX_STRUCTURE_TYPE;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_QUERY_DETAIL;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_REFERENCE_DETAIL;
import org.sdmxsource.sdmx.api.exception.SdmxException;
import org.sdmxsource.sdmx.api.factory.ReadableDataLocationFactory;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;
import org.sdmxsource.sdmx.api.model.ResolutionSettings.RESOLVE_CROSS_REFERENCES;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.MaintainableBean;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.api.model.beans.reference.StructureReferenceBean;
import org.sdmxsource.sdmx.api.model.query.RESTStructureQuery;
import org.sdmxsource.sdmx.api.util.ReadableDataLocation;
import org.sdmxsource.sdmx.querybuilder.builder.StructureQueryBuilderRest;
import org.sdmxsource.sdmx.sdmxbeans.model.beans.reference.RESTStructureQueryImpl;
import org.sdmxsource.sdmx.structureretrieval.manager.BaseSdmxBeanRetrievalManager;
import org.sdmxsource.sdmx.util.beans.reference.StructureReferenceBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ISRESTSdmxBeanRetrievalManager extends BaseSdmxBeanRetrievalManager {

	private Logger logger;

	@Autowired
	private StructureQueryBuilderRest restQueryBuilder;
	
	@Autowired
	private StructureParsingManager spm;
	
	@Autowired
	private ReadableDataLocationFactory rdlFactory;

	private RegistryInformationCache registryInformationCache;
	
	public ISRESTSdmxBeanRetrievalManager(long cacheTimeout) {
		super ();
		this.registryInformationCache = new RegistryInformationCache();
		this.registryInformationCache.setDuration(cacheTimeout);
		this.logger = LoggerFactory.getLogger(ISRESTSdmxBeanRetrievalManager.class);
	}
	
	@Override
	public SdmxBeans getMaintainables(RESTStructureQuery sQuery) {
		String registryUrl = this.registryInformationCache.getRegistryUrl(ScopeProvider.instance.get());
		String restQuery = registryUrl+"/"+restQueryBuilder.buildStructureQuery(sQuery);
		this.logger.debug("REST Query "+restQuery);
		URL restURL;
		try {
			restURL = new URL(restQuery);
		} catch (MalformedURLException e) {
			throw new SdmxException(e, "Could not open a conneciton to URL: " + restQuery);
		}
		ReadableDataLocation  rdl = rdlFactory.getReadableDataLocation(restURL);
		return spm.parseStructures(rdl).getStructureBeans(false);
	}
	
	@Override
	public SdmxBeans getSdmxBeans(StructureReferenceBean sRef, RESOLVE_CROSS_REFERENCES resolveCrossReferences) {
		STRUCTURE_REFERENCE_DETAIL refDetail;
		switch (resolveCrossReferences) {
		case DO_NOT_RESOLVE:
			refDetail = STRUCTURE_REFERENCE_DETAIL.NONE;
			break;
		default:
			refDetail = STRUCTURE_REFERENCE_DETAIL.DESCENDANTS;
			break;
		}
		STRUCTURE_QUERY_DETAIL queryDetail = STRUCTURE_QUERY_DETAIL.FULL;
		RESTStructureQuery query = new RESTStructureQueryImpl(queryDetail, refDetail, null, sRef, false);
		return getMaintainables(query);
	}

	@SuppressWarnings("unchecked")
	public <T extends MaintainableBean> Set<T> getMaintainableBeans(Class<T> structureType, MaintainableRefBean ref, boolean returnLatest, boolean returnStub) {
		SDMX_STRUCTURE_TYPE type = SDMX_STRUCTURE_TYPE.ANY;
		if(structureType != null) {
			type = SDMX_STRUCTURE_TYPE.parseClass(structureType);
		}
		
		StructureReferenceBean sRef = new StructureReferenceBeanImpl(ref, type);
		STRUCTURE_REFERENCE_DETAIL refDetail = STRUCTURE_REFERENCE_DETAIL.NONE;
		STRUCTURE_QUERY_DETAIL queryDetail = returnStub ? STRUCTURE_QUERY_DETAIL.ALL_STUBS : STRUCTURE_QUERY_DETAIL.FULL;
		RESTStructureQuery query = new RESTStructureQueryImpl(queryDetail, refDetail, null, sRef, returnLatest);
		return (Set<T>)getMaintainables(query).getMaintainables(sRef.getMaintainableStructureType());
	}
	
}
