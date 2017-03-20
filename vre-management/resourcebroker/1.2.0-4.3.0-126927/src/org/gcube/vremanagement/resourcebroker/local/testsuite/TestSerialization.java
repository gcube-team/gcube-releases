/****************************************************************************
 *  This software is part of the gCube Project.

 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: TestSerialization.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.DeployNode;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.FeedbackStatus;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementElemPath;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementRelationType;


/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TestSerialization {

	public static void main(final String[] args)
	throws GCUBEFault {

		System.out.println("*** Creating a PlanRequest object");
		// REQUEST
		PlanRequest planReq = new PlanRequest("/gcube/devsec");

		PackageGroup group = planReq.createPackageGroup("service1");
		// Defines a package group with a pre-defined ghn
		group.addPackage(new PackageElem(true, "PkgServiceClass", "PkgServiceName", "PkgServiceVersion", "PkgPackageName", "PkgPackageVersion"));
		group.addPackage(new PackageElem(true, "PkgServiceClass", "PkgServiceName", "PkgServiceVersion", "PkgPackageName", "PkgPackageVersion"));
		group.setGHN("d5a5af20-ac50-11de-a928-ab32081f9f00");

		PackageGroup group1 = planReq.createPackageGroup("service2");
		// Defines a package group without a pre-defined ghn
		group1.addPackage(new PackageElem(true, "PkgServiceClass2", "PkgServiceName2", "PkgServiceVersion2", "PkgPackageName2", "PkgPackageVersion2"));

		PackageGroup group2 = planReq.createPackageGroup("service1");
		// Defines a package group without a pre-defined ghn
		group2.addPackage(new PackageElem(true, "PkgServiceClass2", "PkgServiceName2", "PkgServiceVersion2", "PkgPackageName2", "PkgPackageVersion2"));
		group2.addPackage(new PackageElem(true, "PkgServiceClass2", "PkgServiceName2", "PkgServiceVersion2", "PkgPackageName2", "PkgPackageVersion2"));
		group2.addPackage(new PackageElem(true, "PkgServiceClass2", "PkgServiceName2", "PkgServiceVersion2", "PkgPackageName2", "PkgPackageVersion2"));
		group2.addPackage(new PackageElem(true, "PkgServiceClass2", "PkgServiceName2", "PkgServiceVersion2", "PkgPackageName2", "PkgPackageVersion2"));
		Requirement[] reqs = {
				new Requirement(RequirementElemPath.OS , RequirementRelationType.EQUAL, "Linux"),
				new Requirement(RequirementElemPath.PLATFORM, RequirementRelationType.EQUAL, "i386"),
				new Requirement(RequirementElemPath.MEM_RAM_AVAILABLE, RequirementRelationType.GREATER_OR_EQUAL, "200"),
				new Requirement(RequirementElemPath.MEM_RAM_SIZE, RequirementRelationType.GREATER, "3000"),
				new Requirement(RequirementElemPath.MEM_VIRTUAL_AVAILABLE, RequirementRelationType.GREATER, "280"),
				new Requirement(RequirementElemPath.MEM_VIRTUAL_SIZE, RequirementRelationType.GREATER, "300"),
				new Requirement(RequirementElemPath.HOST, RequirementRelationType.CONTAINS, "dlib29"),
				new Requirement(RequirementElemPath.DISK_SPACE, RequirementRelationType.GREATER, "800"),
				new Requirement(RequirementElemPath.LOAD1MIN, RequirementRelationType.LESS, "1"),
				new Requirement(RequirementElemPath.LOAD5MIN, RequirementRelationType.LESS, "1"),
				new Requirement(RequirementElemPath.LOAD15MIN, RequirementRelationType.LESS, "0.02"),
				new Requirement(RequirementElemPath.PROCESSOR_NUM, RequirementRelationType.GREATER_OR_EQUAL, "2"),
				new Requirement(RequirementElemPath.PROCESSOR_BOGOMIPS, RequirementRelationType.GREATER_OR_EQUAL, "3000"),
				new Requirement(RequirementElemPath.SITE_LOCATION, RequirementRelationType.CONTAINS, "Pisa"),
				new Requirement(RequirementElemPath.SITE_COUNTRY, RequirementRelationType.CONTAINS, "it"),
				new Requirement(RequirementElemPath.SITE_DOMAIN, RequirementRelationType.CONTAINS, "research-infrastructures.eu"),
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "ANT_HOME", RequirementRelationType.CONTAINS, "/ant"),
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "ANT_HOME", RequirementRelationType.EQUAL, "/usr/share/ant"),
				// Here simply requires that the environment contains that key (the value is not relevant)
				new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "GLOBUS_OPTIONS", RequirementRelationType.EQUAL, null),
				new Requirement(RequirementElemPath.CUSTOM_REQUIREMENT, "/GHNDescription/Architecture[@PlatformType = 'i386']")
			};

		group2.addRequirements(reqs);

		planReq.getGHNList().addGHN("1f251d60-215f-11df-b9c5-8ea2ab6d6650");
		planReq.getGHNList().addGHN("09ee4a70-1723-11df-adce-80cd45adc17d");

		XStreamTransformer transformer = new XStreamTransformer();
		String xml = transformer.toXML(planReq);
		PlanRequest req = transformer.getRequestFromXML(xml, false);
		System.out.println("*** Transforming object into XML Representation");
		System.out.println(transformer.toXML(req));
		System.out.println("*** Validation: [OK]");

		// XML RESPONSE
		String xmlRequest =
			"<?xml version=\"1.0\" ?>\n" +
			"<PlanRequest xmlns=\"http://gcube-system.org/namespaces/resourcebroker/broker/xsd/deployRequest\">\n" +
			"<Scope>/gcube/devsec</Scope>\n" +
			"<PackageGroup service=\"service1\" ID=\"1\">\n" +
			"<Package reuse=\"true\"><ServiceClass>PkgServiceClass</ServiceClass><ServiceName>PkgServiceName</ServiceName><ServiceVersion>PkgServiceVersion</ServiceVersion><PackageName>PkgPackageName</PackageName><PackageVersion>PkgPackageVersion</PackageVersion></Package><Package reuse=\"true\"><ServiceClass>PkgServiceClass2</ServiceClass><ServiceName>PkgServiceName2</ServiceName><ServiceVersion>PkgServiceVersion2</ServiceVersion><PackageName>PkgPackageName2</PackageName><PackageVersion>PkgPackageVersion2</PackageVersion></Package><GHN>d5a5af20-ac50-11de-a928-ab32081f9f00</GHN>\n" +
			"</PackageGroup>\n" +
			"<PackageGroup ID=\"15\" service=\"service2\"><Package reuse=\"true\"><ServiceClass>PkgServiceClass3</ServiceClass><ServiceName>PkgServiceName3</ServiceName><ServiceVersion>PkgServiceVersion3</ServiceVersion><PackageName>PkgPackageName3</PackageName><PackageVersion>PkgPackageVersion3</PackageVersion></Package></PackageGroup><GHNList><GHN>1f251d60-215f-11df-b9c5-8ea2ab6d6650</GHN><GHN>09ee4a70-1723-11df-adce-80cd45adc17d</GHN></GHNList></PlanRequest>";
		System.out.println("*** Deserializing XML request");
		PlanRequest req1 = transformer.getRequestFromXML(xmlRequest, false);
		System.out.println(transformer.toXML(req1));

		// RESPONSE
		System.out.println("*** Creating a PlanResponse object");
		PlanResponse planResp = new PlanResponse(new PlanBuilderIdentifier(), "/gcube/devsec");
		PackageGroup resG1 = planResp.createPackageGroup("service1");
		resG1.setGHN("ResGHN1");
		resG1.addPackage(new PackageElem(true, "PkgServiceClass3",
				"PkgServiceName3", "PkgServiceVersion3", "PkgPackageName3",
				"PkgPackageVersion3"));
		resG1.addPackage(new PackageElem(true, "PkgServiceClass3",
				"PkgServiceName3", "PkgServiceVersion3", "PkgPackageName3",
				"PkgPackageVersion3"));

		PackageGroup resG2 = planResp.createPackageGroup("service2");
		resG2.setGHN("ResGHN2");
		resG2.addPackage(new PackageElem(true, "PkgServiceClass5",
				"PkgServiceName5", "PkgServiceVersion5", "PkgPackageName5",
				"PkgPackageVersion5"));
		resG2.addPackage(new PackageElem(true, "PkgServiceClass1",
				"PkgServiceName1", "PkgServiceVersion1", "PkgPackageName1",
				"PkgPackageVersion1"));

		String xmlResponse = transformer.toXML(planResp);
		System.out.println(xmlResponse);
		try {
			transformer.getResponseFromXML(xmlResponse, false);
		} catch (GCUBEFault e) {
			System.out.println("Response validation failure: " + e.getFaultMessage());
		}


		System.out.println("*** Creating a Feedback object");
		@SuppressWarnings("deprecation")
		Feedback feedback = new Feedback();
		feedback.setPlanID(planResp.getKey());
		feedback.setScope(planResp.getScope());

		try {
			PackageGroup resG1Clone = resG1.clone();
			for (PackageElem p : resG1Clone.getPackages()) {
				p.setStatus(FeedbackStatus.SUCCESS);
			}
			resG1Clone.addPackage(new PackageElem(
					false,
					"serviceClass",
					"serviceName",
					"serviceVersion",
					"packageName",
					"packageVersion",
					FeedbackStatus.PARTIAL));

			resG1Clone.addPackage(new PackageElem(
					false,
					"serviceClass",
					"serviceName",
					"serviceVersion",
					"packageName",
					"packageVersion",
					FeedbackStatus.PARTIAL));

			resG1Clone.addPackage(new PackageElem(
					false,
					"serviceClass",
					"serviceName",
					"serviceVersion",
					"packageName",
					"packageVersion",
					FeedbackStatus.FAILED));
			DeployNode dn1 = new DeployNode(resG1Clone);
			feedback.addDeployNode(dn1);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		try {
			PackageGroup resG2Clone = resG2.clone();
			for (PackageElem p : resG2Clone.getPackages()) {
				p.setStatus(FeedbackStatus.FAILED);
			}
			DeployNode dn2 = new DeployNode(resG2Clone);
			feedback.addDeployNode(dn2);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		System.out.println("*** Feedback (Using API)");
		System.out.println(transformer.toXML(feedback));
		{
			int i = 0;
			for (DeployNode dn : feedback.getDeployNodes()) {
				System.out.println("Score for deployNode(" + i + "): " + dn.getScore() + "%");
				i++;
			}
		}



		String xmlFeedback = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<Feedback planID=\"htire8h3dqfetoh0mtnnhiiqpd\" xmlns=\"http://gcube-system.org/namespaces/resourcebroker/broker/xsd/deployFeedback\">\n" +
				"  <Scope>/gcube/devsec</Scope>\n" +
				"  <DeployNode>\n" +
				"	<PackageGroup ID=\"2\" service=\"service2\">\n" +
				"	  <Package reuse=\"true\" status=\"PARTIAL\">\n" +
				"        <ServiceClass>PkgServiceClass1</ServiceClass>\n" +
				"        <ServiceName>PkgServiceName1</ServiceName>\n" +
				"        <ServiceVersion>PkgServiceVersion1</ServiceVersion>\n" +
				"        <PackageName>PkgPackageName1</PackageName>\n" +
				"        <PackageVersion>PkgPackageVersion1</PackageVersion>\n" +
				"      </Package>\n" +
				"	  <Package reuse=\"true\" status=\"PARTIAL\">\n" +
				"        <ServiceClass>PkgServiceClass1</ServiceClass>\n" +
				"        <ServiceName>PkgServiceName1</ServiceName>\n" +
				"        <ServiceVersion>PkgServiceVersion1</ServiceVersion>\n" +
				"        <PackageName>PkgPackageName1</PackageName>\n" +
				"        <PackageVersion>PkgPackageVersion1</PackageVersion>\n" +
				"      </Package>\n" +
				"	  <Package reuse=\"true\" status=\"FAILED\">\n" +
				"        <ServiceClass>PkgServiceClass1</ServiceClass>\n" +
				"        <ServiceName>PkgServiceName1</ServiceName>\n" +
				"        <ServiceVersion>PkgServiceVersion1</ServiceVersion>\n" +
				"        <PackageName>PkgPackageName1</PackageName>\n" +
				"        <PackageVersion>PkgPackageVersion1</PackageVersion>\n" +
				"      </Package>\n" +
				"      <GHN>ResGHN2</GHN>\n" +
				"    </PackageGroup>\n" +
				"  </DeployNode>\n" +
				"</Feedback>\n";
		System.out.println("\n\n*** Serializing a Feedback XML");
		Feedback fb1 = transformer.getFeedbackFromXML(xmlFeedback, false);
		System.out.println(transformer.toXML(fb1));
		{
			int i = 0;
			for (DeployNode dn : fb1.getDeployNodes()) {
				System.out.println("Score for deployNode(" + i + "): " + dn.getScore() + "%");
				i++;
			}
		}
	}

}
