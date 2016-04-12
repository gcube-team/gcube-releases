package org.gcube.datapublishing.sdmx.impl.registry;

import javax.inject.Inject;

import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.impl.model.SDMXRegistryDescriptorImpl;
import org.gcube.datapublishing.sdmx.impl.reports.OperationStatus;
import org.gcube.datapublishing.sdmx.impl.reports.SubmissionReport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdmxsource.sdmx.api.constants.SDMX_STRUCTURE_TYPE;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencySchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.api.model.beans.reference.StructureReferenceBean;
import org.sdmxsource.sdmx.api.model.beans.registry.ProvisionAgreementBean;
import org.sdmxsource.sdmx.api.model.beans.registry.RegistrationBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencyMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencySchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.DataProviderMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.DataProviderSchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.DataSourceMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DataflowMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.registry.ProvisionAgreementMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.registry.RegistrationMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.AgencyMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.AgencySchemeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.DataProviderMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.DataProviderSchemeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.DataSourceMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.metadatastructure.DataflowMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.registry.ProvisionAgreementMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.registry.RegistrationMutableBeanImpl;
import org.sdmxsource.sdmx.structureretrieval.manager.InMemoryRetrievalManager;
import org.sdmxsource.sdmx.util.beans.reference.MaintainableRefBeanImpl;
import org.sdmxsource.sdmx.util.beans.reference.StructureReferenceBeanImpl;
import org.sdmxsource.util.io.ReadableDataLocationTmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class FusionRegistryClientTest {

	private static Logger logger = LoggerFactory
			.getLogger(FusionRegistryClientTest.class);
	
	@Inject @FusionRegistry
	private SDMXRegistryClient client;

	private static InMemoryRetrievalManager retrievalManager;

	@BeforeClass
	public static void setUp() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext-test.xml");
		retrievalManager = context.getBean(InMemoryRetrievalManager.class);
	}
	
	@Before
	public void before(){
		SDMXRegistryDescriptorImpl descriptor = new SDMXRegistryDescriptorImpl();
		descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2_1, "http://pc-fortunati.isti.cnr.it:8080/FusionRegistry/ws/rest/");
		client.setRegistry(descriptor);
	}
	
	private void publishAgencyScheme(AgencySchemeBean bean) {
		try {
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			logger.error("Unable to complete test.", e);
			Assert.fail();
		}
	}

	private void publishCodelistFromLocalRepository(String agency, String id,
			String version) {
		
		try {
			SdmxBeans beans = client.getCodelist(agency, id, version,
					Detail.allstubs, References.none);
			if (beans.getCodelists().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			
			logger.info("Data not present in Registry");

			// Retrieve data from file
			logger.info("Retrieving Structural Metadata from file");
			retrievalManager.invoke(new ReadableDataLocationTmp(
					"src/test/resources/Repository/StructuralMetadata/codelist/"
							+ agency + "/" + id + "/" + version + "/resource.xml"));
			MaintainableRefBean ref = new MaintainableRefBeanImpl(agency, id,
					version);

			CodelistBean bean = retrievalManager.getCodelistBeans(ref, false, false).iterator().next();

			Assert.assertNotNull(bean);
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}

	}

	private void publishConceptSchemeFromLocalRepository(String agency,
			String id, String version) {
		
		try {
			SdmxBeans beans = client.getConceptScheme(agency, id, version,
					Detail.allstubs, References.none);
			if (beans.getConceptSchemes().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			
			logger.info("Data not present in Registry");
			logger.info("Retrieving Structural Metadata from file");
			retrievalManager.invoke(new ReadableDataLocationTmp(
					"src/test/resources/Repository/StructuralMetadata/conceptscheme/"
							+ agency + "/" + id + "/" + version + "/resource.xml"));
			MaintainableRefBean ref = new MaintainableRefBeanImpl(agency, id,
					version);

			ConceptSchemeBean bean = retrievalManager.getConceptSchemeBeans(ref, false, false).iterator().next();

			Assert.assertNotNull(bean);
			
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

	private void publishDatastructure(String agency, String id, String version) {
		
		try {
			SdmxBeans beans = client.getDataStructure(agency, id, version,
					Detail.full, References.none);
			if (beans.getDataStructures().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			;
			logger.info("Data not present in Registry");
			logger.info("Retrieving Structural Metadata from file");
			retrievalManager.invoke(new ReadableDataLocationTmp(
					"src/test/resources/Repository/StructuralMetadata/datastructure/"
							+ agency + "/" + id + "/" + version + "/resource.xml"));
			MaintainableRefBean ref = new MaintainableRefBeanImpl(agency, id,
					version);

			DataStructureBean bean = retrievalManager.getDataStructureBeans(ref, false, false).iterator().next();

			Assert.assertNotNull(bean);
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

	private void publishDataflow(DataflowBean bean) {
		try {
			SdmxBeans beans = client.getDataFlow(bean.getAgencyId(), bean.getId(),
					bean.getVersion(), Detail.full, References.none);
			if (beans.getDataflows().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			
			logger.info("Data not present in Registry");
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

	private void publishDataProviderScheme(DataProviderSchemeBean bean) {
		
		try {
			SdmxBeans beans = client.getDataProviderScheme(bean.getAgencyId(),
					bean.getId(), bean.getVersion(), Detail.full, References.none);
			if (beans.getDataProviderSchemes().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			
			logger.info("Data not present in Registry");
			SubmissionReport report = client.publish(bean);
			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

	private void publishProvisionAgreement(ProvisionAgreementBean bean) {
		
		try {
			SdmxBeans beans = client.getProvisionAgreement(bean.getAgencyId(),
					bean.getId(), bean.getVersion(), Detail.full, References.none);
			if (beans.getProvisionAgreements().size() > 0) {
				logger.info("Structural metadata already present, skipping submission.");
				return;
			}
			
			logger.info("Data not present in Registry");
			client.publish(bean);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

	private void publishRegistration(RegistrationBean bean) {
		try {
			client.getAllDataSetRegistrations();
		} catch (Exception e){
			logger.warn("Catched Exception while getting registrations",e);
		} 
		
		try {
			client.publish(bean);
//			Assert.assertTrue(report.getStatus()==OperationStatus.Success);
		} catch (SDMXRegistryClientException e) {
			logger.error("Got SdmxRegistryClientException while publishing registration", e);
			Assert.fail();
		}
	}
	
	@Test
	public void getAllCodelistStubsTest(){
		try {
			SdmxBeans beans = client.getCodelist("all", "all", "LATEST", Detail.allstubs, References.none);
			for (CodelistBean codelist: beans.getCodelists()){
				logger.info(String.format("Retrieved codelist (%s,%s,%s)", codelist.getAgencyId(),codelist.getId(), codelist.getVersion()));
			}
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void publishStructuralMetadata() {

		AgencySchemeBean agencyScheme = ArtifactsBuilder.buildTestAgencyScheme();

		MaintainableRefBean dsdRef = new MaintainableRefBeanImpl("FAO",
				"CAPTURE_DATASTRUCTURE", "0.1");

		DataflowBean dataflow = ArtifactsBuilder.buildTestDataflow(new StructureReferenceBeanImpl(
				dsdRef, SDMX_STRUCTURE_TYPE.DSD));
		DataProviderMutableBean dataProvider = ArtifactsBuilder.buildTestDataProvider();
		DataProviderSchemeBean dataProviderScheme = ArtifactsBuilder.buildTestDataProviderScheme(dataProvider);

		StructureReferenceBean dataproviderRef = dataProviderScheme
				.getItems().get(0).asReference();
		StructureReferenceBean dataflowRef = dataflow
				.asReference();

		ProvisionAgreementBean provisionAgreement = ArtifactsBuilder.buildTestProvisionAgreement(
				dataproviderRef, dataflowRef);

		publishAgencyScheme(agencyScheme);
		publishCodelistFromLocalRepository("FAO", "CL_ASFIS_TAX", "0.1");
		publishCodelistFromLocalRepository("FAO", "CL_UNIT_MULTIPLIER", "0.1");
		publishCodelistFromLocalRepository("FAO", "CL_SPECIES", "1.0");
		publishCodelistFromLocalRepository("FAO", "CL_UNIT", "0.1");
		publishCodelistFromLocalRepository("FAO", "CL_UN_COUNTRY", "0.1");
		publishCodelistFromLocalRepository("FAO", "CL_FAO_MAJOR_AREA", "0.1");
		publishCodelistFromLocalRepository("FAO", "CL_DIVISION", "0.1");
		publishConceptSchemeFromLocalRepository("FAO", "CS_FISHSTAT", "1.0");
		publishConceptSchemeFromLocalRepository("FAO",
				"GENERAL_CONCEPT_SCHEME", "0.1");
		publishDatastructure(dsdRef.getAgencyId(), dsdRef.getMaintainableId(),
				dsdRef.getVersion());
		publishDatastructure("FAO", "CAPTURE_DATASTRUCTURE", "0.1");
		publishDatastructure("FAO", "REGIONAL_CAPTURE_DATASTRUCTURE", "0.1");
		publishDataflow(dataflow);
		publishDataProviderScheme(dataProviderScheme);
		publishProvisionAgreement(provisionAgreement);
		publishRegistration(ArtifactsBuilder.buildTestRegistration(provisionAgreement
				.asReference()));
		logger.info("End of test");
	}

	static class ArtifactsBuilder {
		private static AgencySchemeBean buildTestAgencyScheme() {
			// Create Agency Scheme from scratch
			AgencySchemeMutableBean bean = new AgencySchemeMutableBeanImpl();
			bean.setId("AGENCIES");
			bean.setAgencyId("SDMX");
			bean.setVersion("1.0");
			bean.addName("en", "SDMX Agency Scheme");
			// ROOT Agency
			AgencyMutableBean sdmxAgency = new AgencyMutableBeanImpl();
			sdmxAgency.setId("SDMX");
			sdmxAgency.addName("en", "SDMX");
			bean.addItem(sdmxAgency);
			// FAO Agency
			AgencyMutableBean faoAgency = new AgencyMutableBeanImpl();
			faoAgency.setId("FAO");
			faoAgency.addName("en", "Food And Agriculture");
			bean.addItem(faoAgency);
			return bean.getImmutableInstance();
		}

		private static DataProviderMutableBean buildTestDataProvider() {
			DataProviderMutableBean dp = new DataProviderMutableBeanImpl();
			dp.setId("FAO_DATAPROVIDER");
			dp.addName("en", "FAO data provider");
			return dp;
		}

		private static DataProviderSchemeBean buildTestDataProviderScheme(
				DataProviderMutableBean... dps) {
			DataProviderSchemeMutableBean bean = new DataProviderSchemeMutableBeanImpl();
			bean.setAgencyId("FAO");
			bean.setId("DATA_PROVIDERS");
			bean.setVersion("1.0");
			bean.addName("en", "TEST Data Providers");
			for (DataProviderMutableBean dataProviderMutableBean : dps) {
				bean.addItem(dataProviderMutableBean);
			}
			return bean.getImmutableInstance();
		}

		private static DataflowBean buildTestDataflow(
				StructureReferenceBean dsdRef) {
			DataflowMutableBean bean = new DataflowMutableBeanImpl();
			MaintainableRefBean ref = new MaintainableRefBeanImpl("FAO",
					"TEST_DATAFLOW", "1.0");
			bean.setAgencyId(ref.getAgencyId());
			bean.setId(ref.getMaintainableId());
			bean.setVersion(ref.getVersion());
			bean.addName("en", "Dataflow for test purposes");
			bean.setDataStructureRef(dsdRef);
			return bean.getImmutableInstance();
		}

		private static ProvisionAgreementBean buildTestProvisionAgreement(
				StructureReferenceBean dataproviderRef,
				StructureReferenceBean dataflowRef) {
			ProvisionAgreementMutableBean bean = new ProvisionAgreementMutableBeanImpl();
			bean.setAgencyId("FAO");
			bean.setId("TEST_AGREEMENT");
			bean.setVersion("1.0");
			bean.addName("en", "Test Provision Agreement");
			bean.setDataproviderRef(dataproviderRef);
			bean.setStructureUsage(dataflowRef);
			return bean.getImmutableInstance();
		}

		private static RegistrationBean buildTestRegistration(
				StructureReferenceBean provisionAgreementRef) {
			RegistrationMutableBean bean = new RegistrationMutableBeanImpl();
			bean.setAgencyId("FAO");
			bean.setId("TEST_DATASOURCE");
			bean.setVersion("1.0");
			bean.addName("en", "Test registration");
			DataSourceMutableBean dataSource = new DataSourceMutableBeanImpl();
			dataSource.setRESTDatasource(false);
			dataSource.setWebServiceDatasource(false);
			dataSource.setSimpleDatasource(true);
			dataSource
					.setDataUrl("http://127.0.0.1/sdmx-datasource/CAPTURE-formatted-modified.xml");
			bean.setDataSource(dataSource);
			bean.setProvisionAgreementRef(provisionAgreementRef);
			return bean.getImmutableInstance();
		}
	}

}
