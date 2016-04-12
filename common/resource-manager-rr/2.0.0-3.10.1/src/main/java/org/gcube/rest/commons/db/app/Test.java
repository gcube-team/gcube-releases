package org.gcube.rest.commons.db.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.gcube.rest.commons.db.dao.app.GeneralResourceModelDao;
import org.gcube.rest.commons.db.dao.app.HostNodeModelDao;
import org.gcube.rest.commons.db.dao.app.ResourceModelDao;
import org.gcube.rest.commons.db.dao.app.RunInstanceModelDao;
import org.gcube.rest.commons.db.dao.app.SerInstanceModelDao;
import org.gcube.rest.commons.db.model.app.GeneralResourceModel;
import org.gcube.rest.commons.db.model.app.HostNodeModel;
import org.gcube.rest.commons.db.model.app.ResourceModel;
import org.gcube.rest.commons.db.model.app.RunInstanceModel;
import org.gcube.rest.commons.db.model.app.SerInstanceModel;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.information.collector.rr.RRInformationCollector;
import org.gcube.rest.commons.publisher.resourceregistry.PublisherRRimpl;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance.Profile;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance.NodeProperties;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;

public class Test {
	final private static ApplicationInitializer ai;
	final private static Injector injector;
	
	static {
		injector = Guice.createInjector(
			new JpaPersistModule("myapp-db"),
			new AbstractModule() {
				
				@Override
				protected void configure() {
					bind(ResourceModelDao.class);
					bind(RunInstanceModelDao.class);
					bind(SerInstanceModelDao.class);
					bind(HostNodeModelDao.class);
					bind(InformationCollector.class).to(RRInformationCollector.class);
					
					bind(new TypeLiteral<ResourcePublisher<GeneralResource>>(){})
						.to(new TypeLiteral<PublisherRRimpl<GeneralResource>>(){});
				}
			}
			);
		ai = injector.getInstance(ApplicationInitializer.class);
	}

	public static void main(String[] args) throws URISyntaxException, JAXBException {

		
		ResourceModelDao dao = injector.getInstance(ResourceModelDao.class);
		
		System.out.println("before save");
		Resource resource = new Resource();
		resource.setDescription("dummy description");
		resource.setName("dummy name");
		resource.setScopes(Lists.newArrayList("scope1" , "scope2"));
		
		ResourceModel resourceModel1 = new ResourceModel(resource);
		
		resourceModel1 = dao.save(resourceModel1);
		System.out.println("after save");
		
		
		System.out.println("before findAll");
		System.out.println(dao.findAll());
		System.out.println("after findAll");
		
		System.out.println("before findById");
		ResourceModel resourceModel2 = dao.findById(resourceModel1.getId());
		System.out.println("after findById");
		
		System.out.println("rm : " + resourceModel2.getId());
		System.out.println("rm : " + resourceModel2.getDescription());
		System.out.println("rm : " + resourceModel2.getScopes());
//		
//		
		GeneralResource generalGeneral = new GeneralResource();
		generalGeneral.setResourceID("123456789-abcde");
		
		GeneralResourceModel generalResourceModel = new GeneralResourceModel(generalGeneral);
		
		
		GeneralResourceModelDao generalResourceModelDao  = injector.getInstance(GeneralResourceModelDao.class);
		System.out.println("before save");
		generalResourceModel = generalResourceModelDao.save(generalResourceModel);
		System.out.println("after save");
		
		System.out.println("before findAll");
		System.out.println(generalResourceModelDao.findAll());
		System.out.println("after findAll");
		
		
		System.out.println("before findById");
		GeneralResourceModel generalResourceModel2 = generalResourceModelDao.findById(generalResourceModel.getId());
		System.out.println("after findById");
		
		System.out.println("rm : " + generalResourceModel2.getId());
		System.out.println("rm : " + generalResourceModel2.getDescription());
		System.out.println("rm : " + generalResourceModel2.getResourceID());
		
		
		
		Map<String, URI> endpoints = Maps.newHashMap();
		endpoints.put("key", new URI("http://google.com"));

		RunInstance.Profile prof = new Profile("description", "version", "ghnId", "serviceId", "serviceName", "serviceClass", new Date(), "ready", endpoints , XMLConverter.stringToNode("<doc><specificData></specificData></doc>"));
		RunInstance runInstance = new RunInstance("id", Sets.newHashSet("RunInstScope1", "RunInstScope2"), prof);
		
		RunInstanceModel rim = new RunInstanceModel(runInstance);
		
		RunInstanceModelDao runInstanceModelDao  = injector.getInstance(RunInstanceModelDao.class);
		
		rim = runInstanceModelDao.save(rim);
		
		System.out.println(runInstanceModelDao.findAll());
		

		System.out.println("before findById");
		RunInstanceModel rim2 = runInstanceModelDao.findById(rim.getId());
		System.out.println("after findById");
		
		
		System.out.println("rim2 : " + rim2.getId());
		System.out.println("rim2" + rim2.getDescription());
		System.out.println("rim2 : " + rim2.getEndpoints());
		
		
		NodeProperties nodeProperties = new NodeProperties("nodeID", Lists.newArrayList("scopeA", "scopeB"), XMLConverter.stringToNode("<doc></doc>"));
		
		SerInstance serInstance = new SerInstance(new URI("http://google.com"), "my key", "serviceName", "serviceClass", nodeProperties);
		SerInstanceModel serInstanceModel = new SerInstanceModel(serInstance);
		
		SerInstanceModelDao serInstanceModelDao  = injector.getInstance(SerInstanceModelDao.class);
		
		
		System.out.println("before save");
		serInstanceModel = serInstanceModelDao.save(serInstanceModel);
		System.out.println("after save");
		
		System.out.println("before findAll");
		System.out.println(serInstanceModelDao.findAll());
		System.out.println("after findAll");
		
		
		System.out.println("before findById");
		SerInstanceModel serInstanceModel2 = serInstanceModelDao.findById(serInstanceModel.getId());
		System.out.println("after findById");
		
		System.out.println("rm : " + serInstanceModel2.getId());
		System.out.println("rm : " + serInstanceModel2.getDescription());
		System.out.println("rm : " + serInstanceModel2.getEndpoint());
		System.out.println("rm : " + serInstanceModel2.getNodeId());
		System.out.println("rm : " + serInstanceModel2.getScopes());
		System.out.println("rm : " + serInstanceModel2.getEndpoint());
		
		SerInstance serInstance2 = serInstanceModel2.copyTo();
		
		System.out.println("are equal ? : " + serInstance2.equals(serInstance));
		
		String str = "<Profile><GHNDescription><Name>meteora.di.uoa.gr:8080</Name><Type>Dynamic</Type><SecurityEnabled>false</SecurityEnabled><NetworkAdapter IPAddress=\"195.134.66.114\" MTU=\"0\" /></GHNDescription><Site><Location>Athens</Location><Country>gr</Country><Latitude>37.58</Latitude><Longitude>23.43</Longitude></Site><DeployedPackages><Package><PackageName>guice-persist</PackageName><PackageVersion>3.0.0</PackageVersion><ServiceName>guice-persist</ServiceName><ServiceClass>com.google.inject.persist</ServiceClass></Package><Package><PackageName>discovery-client</PackageName><PackageVersion>1.0.1-3.1.0</PackageVersion><ServiceName>discovery-client</ServiceName><ServiceClass>org.gcube.resources.discovery</ServiceClass></Package><Package><PackageName>common-smartgears</PackageName><PackageVersion>1.2.0-3.1.0</PackageVersion><ServiceName>common-smartgears</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>commons-logging</PackageName><PackageVersion>1.1.1</PackageVersion><ServiceName>commons-logging</ServiceName><ServiceClass>commons-logging</ServiceClass></Package><Package><PackageName>jboss-logging-annotations</PackageName><PackageVersion>1.2.0.Beta1</PackageVersion><ServiceName>jboss-logging-annotations</ServiceName><ServiceClass>org.jboss.logging</ServiceClass></Package><Package><PackageName>resource-manager-rr</PackageName><PackageVersion>2.0.0-SNAPSHOT</PackageVersion><ServiceName>resource-manager-rr</ServiceName><ServiceClass>org.gcube.common.rest</ServiceClass></Package><Package><PackageName>dom4j</PackageName><PackageVersion>1.6.1</PackageVersion><ServiceName>dom4j</ServiceName><ServiceClass>dom4j</ServiceClass></Package><Package><PackageName>javassist</PackageName><PackageVersion>3.18.1-GA</PackageVersion><ServiceName>javassist</ServiceName><ServiceClass>org.javassist</ServiceClass></Package><Package><PackageName>commons-lang</PackageName><PackageVersion>2.6</PackageVersion><ServiceName>commons-lang</ServiceName><ServiceClass>commons-lang</ServiceClass></Package><Package><PackageName>jackson-core-asl</PackageName><PackageVersion>1.9.13</PackageVersion><ServiceName>jackson-core-asl</ServiceName><ServiceClass>jackson-core-asl</ServiceClass></Package><Package><PackageName>common-scope-maps</PackageName><PackageVersion>1.0.0-3.1.0</PackageVersion><ServiceName>common-scope-maps</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>jackson-mapper-asl</PackageName><PackageVersion>1.9.13</PackageVersion><ServiceName>jackson-mapper-asl</ServiceName><ServiceClass>jackson-mapper-asl</ServiceClass></Package><Package><PackageName>common-scope</PackageName><PackageVersion>1.2.0-3.1.0</PackageVersion><ServiceName>common-scope</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>resteasy-jaxrs</PackageName><PackageVersion>3.0.6.Final</PackageVersion><ServiceName>resteasy-jaxrs</ServiceName><ServiceClass>org.jboss.resteasy</ServiceClass></Package><Package><PackageName>resource-aware-commons</PackageName><PackageVersion>2.0.0-SNAPSHOT</PackageVersion><ServiceName>resource-aware-commons</ServiceName><ServiceClass>org.gcube.common.rest</ServiceClass></Package><Package><PackageName>jboss-logging</PackageName><PackageVersion>3.1.3.GA</PackageVersion><ServiceName>jboss-logging</ServiceName><ServiceClass>org.jboss.logging</ServiceClass></Package><Package><PackageName>hibernate-core</PackageName><PackageVersion>4.3.5.final</PackageVersion><ServiceName>hibernate-core</ServiceName><ServiceClass>org.hibernate</ServiceClass></Package><Package><PackageName>log4j</PackageName><PackageVersion>1.2.16</PackageVersion><ServiceName>log4j</ServiceName><ServiceClass>log4j</ServiceClass></Package><Package><PackageName>ic-client</PackageName><PackageVersion>1.0.1-3.1.0</PackageVersion><ServiceName>ic-client</ServiceName><ServiceClass>org.gcube.resources.discovery</ServiceClass></Package><Package><PackageName>registry-publisher</PackageName><PackageVersion>1.2.3-3.1.0</PackageVersion><ServiceName>registry-publisher</ServiceName><ServiceClass>org.gcube.resources</ServiceClass></Package><Package><PackageName>commons-beanutils</PackageName><PackageVersion>1.8.3</PackageVersion><ServiceName>commons-beanutils</ServiceName><ServiceClass>commons-beanutils</ServiceClass></Package><Package><PackageName>slf4j-log4j12</PackageName><PackageVersion>1.6.4</PackageVersion><ServiceName>slf4j-log4j12</ServiceName><ServiceClass>org.slf4j</ServiceClass></Package><Package><PackageName>common-gcore-stubs</PackageName><PackageVersion>1.1.1-3.1.0</PackageVersion><ServiceName>common-gcore-stubs</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>hibernate-entitymanager</PackageName><PackageVersion>4.3.5.final</PackageVersion><ServiceName>hibernate-entitymanager</ServiceName><ServiceClass>org.hibernate</ServiceClass></Package><Package><PackageName>hibernate-commons-annotations</PackageName><PackageVersion>4.0.4.final</PackageVersion><ServiceName>hibernate-commons-annotations</ServiceName><ServiceClass>org.hibernate</ServiceClass></Package><Package><PackageName>httpcore</PackageName><PackageVersion>4.2.1</PackageVersion><ServiceName>httpcore</ServiceName><ServiceClass>org.apache.httpcomponents</ServiceClass></Package><Package><PackageName>resteasy-client</PackageName><PackageVersion>3.0.6.Final</PackageVersion><ServiceName>resteasy-client</ServiceName><ServiceClass>org.jboss.resteasy</ServiceClass></Package><Package><PackageName>javassist</PackageName><PackageVersion>3.12.1.GA</PackageVersion><ServiceName>javassist</ServiceName><ServiceClass>javassist</ServiceClass></Package><Package><PackageName>common-validator</PackageName><PackageVersion>1.0.0-3.1.0</PackageVersion><ServiceName>common-validator</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>slf4j-api</PackageName><PackageVersion>1.7.2</PackageVersion><ServiceName>slf4j-api</ServiceName><ServiceClass>org.slf4j</ServiceClass></Package><Package><PackageName>slf4j-api</PackageName><PackageVersion>1.7.5</PackageVersion><ServiceName>slf4j-api</ServiceName><ServiceClass>org.slf4j</ServiceClass></Package><Package><PackageName>common-smartgears-utils</PackageName><PackageVersion>1.0.0-3.1.0</PackageVersion><ServiceName>common-smartgears-utils</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>hibernate-c3p0</PackageName><PackageVersion>4.3.5.final</PackageVersion><ServiceName>hibernate-c3p0</ServiceName><ServiceClass>org.hibernate</ServiceClass></Package><Package><PackageName>guava</PackageName><PackageVersion>16.0.1</PackageVersion><ServiceName>guava</ServiceName><ServiceClass>com.google.guava</ServiceClass></Package><Package><PackageName>common-configuration-scanner</PackageName><PackageVersion>1.0.0-3.1.0</PackageVersion><ServiceName>common-configuration-scanner</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>jaxb-api</PackageName><PackageVersion>2.1</PackageVersion><ServiceName>jaxb-api</ServiceName><ServiceClass>javax.xml.bind</ServiceClass></Package><Package><PackageName>hibernate-jpa-2.1-api</PackageName><PackageVersion>1.0.0.final</PackageVersion><ServiceName>hibernate-jpa-2.1-api</ServiceName><ServiceClass>org.hibernate.javax.persistence.hibernate-jpa-2.1-api</ServiceClass></Package><Package><PackageName>xstream</PackageName><PackageVersion>1.4.3</PackageVersion><ServiceName>xstream</ServiceName><ServiceClass>com.thoughtworks.xstream</ServiceClass></Package><Package><PackageName>mysql-connector-java</PackageName><PackageVersion>5.1.30</PackageVersion><ServiceName>mysql-connector-java</ServiceName><ServiceClass>com.mysql.jdbc</ServiceClass></Package><Package><PackageName>logback-core</PackageName><PackageVersion>1.0.13</PackageVersion><ServiceName>logback-core</ServiceName><ServiceClass>ch.qos.logback</ServiceClass></Package><Package><PackageName>commons-codec</PackageName><PackageVersion>1.6</PackageVersion><ServiceName>commons-codec</ServiceName><ServiceClass>commons-codec</ServiceClass></Package><Package><PackageName>guice</PackageName><PackageVersion>3.0.0</PackageVersion><ServiceName>guice</ServiceName><ServiceClass>com.google.inject</ServiceClass></Package><Package><PackageName>common-events</PackageName><PackageVersion>1.0.1-3.1.0</PackageVersion><ServiceName>common-events</ServiceName><ServiceClass>org.gcube.core</ServiceClass></Package><Package><PackageName>commons-io</PackageName><PackageVersion>2.1</PackageVersion><ServiceName>commons-io</ServiceName><ServiceClass>commons-io</ServiceClass></Package><Package><PackageName>activation</PackageName><PackageVersion>1.1</PackageVersion><ServiceName>activation</ServiceName><ServiceClass>javax.activation</ServiceClass></Package><Package><PackageName>common-gcore-resources</PackageName><PackageVersion>1.2.0-3.1.0</PackageVersion><ServiceName>common-gcore-resources</ServiceName><ServiceClass>org.gcube.resources</ServiceClass></Package><Package><PackageName>logback-classic</PackageName><PackageVersion>1.0.13</PackageVersion><ServiceName>logback-classic</ServiceName><ServiceClass>ch.qos.logback</ServiceClass></Package><Package><PackageName>jaxrs-api</PackageName><PackageVersion>3.0.6.Final</PackageVersion><ServiceName>jaxrs-api</ServiceName><ServiceClass>org.jboss.resteasy</ServiceClass></Package><Package><PackageName>httpclient</PackageName><PackageVersion>4.2.1</PackageVersion><ServiceName>httpclient</ServiceName><ServiceClass>org.apache.httpcomponents</ServiceClass></Package><Package><PackageName>gson</PackageName><PackageVersion>2.2.4</PackageVersion><ServiceName>gson</ServiceName><ServiceClass>com.google.gson</ServiceClass></Package><Package><PackageName>c3p0</PackageName><PackageVersion>0.9.2.1</PackageVersion><ServiceName>c3p0</ServiceName><ServiceClass>com.mchange.v2.c3p0</ServiceClass></Package><Package><PackageName>resource-manager</PackageName><PackageVersion>2.0.0-SNAPSHOT</PackageVersion><ServiceName>resource-manager</ServiceName><ServiceClass>org.gcube.common.rest</ServiceClass></Package><Package><PackageName>resource-collector</PackageName><PackageVersion>0.0.1-SNAPSHOT</PackageVersion><ServiceName>resource-collector</ServiceName><ServiceClass>org.gcube.common.rest</ServiceClass></Package><Package><PackageName>scannotation</PackageName><PackageVersion>1.0.3</PackageVersion><ServiceName>scannotation</ServiceName><ServiceClass>org.scannotation</ServiceClass></Package><Package><PackageName>jboss-transaction-api_1.2_spec</PackageName><PackageVersion>1.0.0.Final</PackageVersion><ServiceName>jboss-transaction-api_1.2_spec</ServiceName><ServiceClass>org.jboss.spec.javax.transaction</ServiceClass></Package><Package><PackageName>jandex</PackageName><PackageVersion>1.1.0.Final</PackageVersion><ServiceName>jandex</ServiceName><ServiceClass>org.jboss</ServiceClass></Package><Package><PackageName>jboss-annotations-api_1.1_spec</PackageName><PackageVersion>1.0.1.Final</PackageVersion><ServiceName>jboss-annotations-api_1.1_spec</ServiceName><ServiceClass>org.jboss.spec.javax.annotation</ServiceClass></Package></DeployedPackages></Profile>";
		HostNode.Profile profile = XMLConverter.fromXML(str, HostNode.Profile.class);
		
		HostNode hostnode = new HostNode("id", Lists.newArrayList("HostNodeScope1", "HostNodeScope2"), profile);
		HostNodeModel hostNodeModel = new HostNodeModel(hostnode);
		
		HostNodeModelDao hostNodeModelDao  = injector.getInstance(HostNodeModelDao.class);
		
		
		System.out.println("before save");
		hostNodeModel = hostNodeModelDao.save(hostNodeModel);
		System.out.println("after save");
		
		System.out.println("before findAll");
		System.out.println(hostNodeModelDao.findAll());
		System.out.println("after findAll");
		
		
		System.out.println("before findById");
		HostNodeModel hostNodeModel2 = hostNodeModelDao.getByScope("HostNodeScope2").get(0);
		System.out.println("after findById");
		
		System.out.println("rm : " + hostNodeModel2.getResourceId());
		System.out.println("rm : " + hostNodeModel2.getDescription());
		System.out.println("rm : " + hostNodeModel2.getScopes());
		System.out.println("rm : " + hostNodeModel2.getProfile());
		
		HostNode hostNode2 = hostNodeModel2.copyTo();
		
		System.out.println("are equal ? : " + hostnode.equals(hostNode2));

		System.out.println("remove id: " + hostNodeModel2.getResourceId());

		final HostNodeModelDao hostNodeModelDao1 = injector.getInstance(HostNodeModelDao.class);
		final RunInstanceModelDao runInstanceModelDao1 = injector.getInstance(RunInstanceModelDao.class);

// hostNodeModelDao.deleteByResourceID("id");
//		hostNodeModelDao1.deleteByResourceID("27189ec6-b52c-3489-8c77-f2fa0ad809af");

//		runInstanceModelDao1.deleteByResourceID("id");
//		runInstanceModelDao1.deleteByResourceID("27189ec6-b52c-3489-8c77-f2fa0ad809af");
//		runInstanceModelDao1.deleteByResourceID("88485268-ed8c-326b-bfb7-e313acbbb2f2");

		ai.stop();
		
	}
	
}
