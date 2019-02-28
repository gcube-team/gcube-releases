package org.gcube.portlets.user.statisticalalgorithmsimporter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.AvailableProjectConfigJAXB;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.InformationSystemUtils;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.PoolManagerJAXB;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.SAIDescriptorJAXB;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectLanguageType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectSupportType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ISResourceTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(ISResourceTest.class);

	@Test
	public void testSaiDescriptor() {
		if (Constants.TEST_ENABLE) {

			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(SAIDescriptorJAXB.class);
				StringWriter sw = new StringWriter();

				SAIDescriptorJAXB saiDescriptor = new SAIDescriptorJAXB();
				
				PoolManagerJAXB poolManagerJAXB=new PoolManagerJAXB();
				poolManagerJAXB.setEnable(true);
				saiDescriptor.setPoolmanager(poolManagerJAXB);
				
				saiDescriptor.setRemotetemplatefile(Constants.REMOTE_TEMPLATE_FILE);
				
				
				List<AvailableProjectConfigJAXB> list = new ArrayList<>();
				AvailableProjectConfigJAXB a1 = new AvailableProjectConfigJAXB();
				a1.setLanguage(ProjectLanguageType.R.getId());
				a1.setSupport(ProjectSupportType.REDIT.getId());
				list.add(a1);
				a1 = new AvailableProjectConfigJAXB();
				a1.setLanguage(ProjectLanguageType.LINUX_COMPILED.getId());
				a1.setSupport(ProjectSupportType.BLACKBOX.getId());
				list.add(a1);
				a1 = new AvailableProjectConfigJAXB();
				a1.setLanguage(ProjectLanguageType.OCTAVE.getId());
				a1.setSupport(ProjectSupportType.BLACKBOX.getId());
				list.add(a1);
				saiDescriptor.setAvailableprojectconfiguration(list);
				
				jaxbContext.createMarshaller().marshal(saiDescriptor, sw);
				logger.debug(sw.toString());
				
				SAIDescriptorJAXB descr = (SAIDescriptorJAXB) jaxbContext.createUnmarshaller()
						.unmarshal(new StringReader(sw.toString()));
				logger.debug("SAIDescriptor: " + descr);
				
				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}
	
	
	@Test
	public void testSocialNotificationService() {
		if (Constants.TEST_ENABLE) {

			try {
				String socialNetworkingServiceURL=InformationSystemUtils.retrieveSocialNetworkingService(Constants.DEFAULT_SCOPE);
				logger.info("SocialNetworkingService: "+socialNetworkingServiceURL);
				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}
	
	@Test
	public void testDataMinerPoolManagerService() {
		if (Constants.TEST_ENABLE) {

			try {
				String dataMinerPoolManagerURL=InformationSystemUtils.retrieveDataMinerPoolManager(Constants.DEFAULT_SCOPE);
				logger.info("DataMinerPoolManager: "+dataMinerPoolManagerURL);
				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}

}
