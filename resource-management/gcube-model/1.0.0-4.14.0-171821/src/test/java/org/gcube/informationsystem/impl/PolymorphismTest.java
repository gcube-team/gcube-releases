/**
 * 
 */
package org.gcube.informationsystem.impl;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.resourcemanagement.model.impl.entity.resource.EServiceImpl;
import org.gcube.resourcemanagement.model.impl.entity.resource.HostingNodeImpl;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.ActivatesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.entity.resource.HostingNode;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Activates;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class PolymorphismTest {

	private static Logger logger = LoggerFactory
			.getLogger(PolymorphismTest.class);
	
	public static final String NYESERVICE = "{\"@class\":\"EService\",\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	public static final String MYESERVICE = "{\"@class\":\"EService\",\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"MySoftwareFacet\",\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"@superClasses\":[\"Facet\", \"Entity\"],\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	public static final String MYOTHERESERVICE = "{\"@class\":\"EService\",\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"MySoftwareFacet\",\"header\":null,\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	public static final String MYANOTHERESERVICE = "{\"@class\":\"EService\",\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	
	public static final String NYESERVICE2 = "{\"@class\":\"MyEService\",\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	public static final String MYANOTHERESERVICE2 = "{\"@class\":\"MyEService\",\"@superClasses\":[\"EService\",\"Service\",\"Resource\"],\"header\":{\"@class\":\"Header\",\"uuid\":\"3ace4bd0-e5cd-49a3-97a8-a0a9468ce6d4\",\"creator\":null, \"creationTime\":null, \"lastUpdateTime\":null},\"consistsOf\":[{\"@class\":\"IsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"whnmanager\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/vremanagement/ws/whnmanager\",\"protocol\":null,\"description\":null,\"authorization\": {\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}},{\"@class\":\"ConsistsOf\",\"header\":null,\"relationProperty\":null,\"target\":{\"@class\":\"AccessPointFacet\",\"header\":null,\"entryName\":\"WhnManager-remote-management\",\"endpoint\":\"http://pc-frosini.isti.cnr.it:8080/whn-manager/gcube/resource\",\"protocol\":null,\"description\":null,\"authorization\":{\"@class\":\"ValueSchema\",\"value\":\"gcube-token\",\"schema\":null},\"properties\":null}}],\"isRelatedTo\":[]}";
	
	public static final String MY_CONSISTS_OF = "{\"@superClasses\":[\"IsIdentifiedBy\", \"ConsistsOf\", \"Relation\"],\"@class\":\"MyIsIdentifiedBy\",\"header\":null,\"relationProperty\":null,\"target\":{\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}}";
	public static final String MY_TEST_FACET = "{\"@superClasses\":[\"SoftwareFacet\", \"Facet\", \"Entity\"],\"@class\":\"MySoftwareFacet\",\"header\":null,\"name\":\"WhnManager\",\"group\":\"VREManagement\",\"version\":\"2.0.0-SNAPSHOT\",\"description\":\"Web Hosting Node Service\",\"qualifier\":null,\"optional\":false}";
	
	
	@Test
	public void testER() throws Exception {
		
		Facet f = ISMapper.unmarshal(Facet.class, MY_TEST_FACET);
		logger.debug("\n\n{} {}\n\n", f.getClass().getSimpleName(), f);
		
		@SuppressWarnings("rawtypes")
		ConsistsOf c = ISMapper.unmarshal(ConsistsOf.class, MY_CONSISTS_OF);
		logger.debug("{} {}\n\n", c.getClass().getSimpleName(), c);
		
		String[] eServices = new String[]{NYESERVICE, MYESERVICE, MYOTHERESERVICE, MYANOTHERESERVICE, NYESERVICE2, MYANOTHERESERVICE2};
		//String[] eServices = new String[]{NYESERVICE2, MYANOTHERESERVICE2};
		//String[] eServices = new String[]{ESERVICE};
		for(String eService : eServices){
			Resource resource = ISMapper.unmarshal(Resource.class, eService);
			logger.debug("{} {}", resource.getClass().getSimpleName(), resource);
			
			List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfs = resource.getConsistsOf();
			for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfs){
				logger.debug("{}", consistsOf);
				Facet facet = consistsOf.getTarget();
				logger.debug("{} {}", facet.getClass().getSimpleName(), facet);
			}
			
			logger.debug("\n\n");
		}
		
	}
	
	// @Test
	public void testRelation() throws Exception {
		HostingNode hostingNode = new HostingNodeImpl();
		Header headerHn = new HeaderImpl(UUID.randomUUID());
		hostingNode.setHeader(headerHn);
		
		EService eService = new EServiceImpl();
		Header headerES = new HeaderImpl(UUID.randomUUID());
		eService.setHeader(headerES);
		
		Activates<HostingNode, EService> activates = new ActivatesImpl<HostingNode, EService>(hostingNode, eService, null);
		
		hostingNode.attachResource(activates);
		
		String string = ISMapper.marshal(activates);
		logger.debug(string);
		
	}
}
