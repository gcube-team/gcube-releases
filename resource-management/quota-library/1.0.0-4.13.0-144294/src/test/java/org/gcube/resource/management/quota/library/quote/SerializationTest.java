package org.gcube.resource.management.quota.library.quote;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.Quota;
import org.gcube.resource.management.quota.library.quotalist.ServicePackage;
import org.gcube.resource.management.quota.library.quotalist.ServicePackageDetail;
import org.gcube.resource.management.quota.library.quotalist.StorageQuota;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SerializationTest {

	static JAXBContext context;
	
	@BeforeClass
	public static void before() throws Exception{
		// context = JAXBContext.newInstance(Quota.class);
	}
	
	@Test
	public void serializeQuota() throws Exception{
		context = JAXBContext.newInstance(Quota.class);
		String currentContext="/gcube";
		String identifier="alessandro.pieve";
		//ManagerType managerType=ManagerType.STORAGE;
		TimeInterval timeInterval=TimeInterval.DAILY;
		Double quotaValue=1000.0;
		Quota qu= new StorageQuota(currentContext,identifier,CallerType.USER,timeInterval,quotaValue);
		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(qu, sw);
		Quota quCopy= (Quota)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
			System.out.println(quCopy.getQuotaAsString());
			System.out.println(qu.getQuotaAsString());
		Assert.assertEquals(qu, quCopy);
	}
	
	
	@Test
	public void serializeServicesPackage() throws Exception{
		context = JAXBContext.newInstance(ServicePackage.class);
		long idServicesPackage=1;
		String content="InformationSystem:IColllector";
		ServicePackageDetail servicePackagesDetail =new ServicePackageDetail(idServicesPackage,content);
		List<ServicePackageDetail> listServicePackagesDetail =new ArrayList<ServicePackageDetail>();
		listServicePackagesDetail.add(servicePackagesDetail);
		ServicePackage servicePackages= new ServicePackage("pacchetto Test",listServicePackagesDetail);
		
		
		
		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(servicePackages, sw);
		ServicePackage servicePackagesCopy= (ServicePackage)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
			System.out.println(servicePackages.getServicePackagesAsString());
			System.out.println(servicePackagesCopy.getServicePackagesAsString());
		Assert.assertEquals(servicePackages, servicePackagesCopy);
	}
	
}
