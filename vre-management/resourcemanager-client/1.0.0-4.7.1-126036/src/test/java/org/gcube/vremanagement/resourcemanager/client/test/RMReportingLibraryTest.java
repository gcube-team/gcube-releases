package org.gcube.vremanagement.resourcemanager.client.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.NoSuchReportException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SendReportParameters;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;

public class RMReportingLibraryTest {

	public static RMReportingLibrary library=null;
	public static String scope="/gcube/devsec";
	public String report;
	public String reportString;
	
//	@Before
	public void initialize(){
		ScopeProvider.instance.set(scope);
		library=Proxies.reportingService().at("node13.d.d4science.research-infrastructures.eu", 8080).withTimeout(1, TimeUnit.MINUTES).build();
		InputStream is=RMReportingLibraryTest.class.getResourceAsStream("/report.xml");
        reportString = new Scanner(is,"UTF-8").useDelimiter("\\A").next();
	}
	
//	@Test
	public void sendReportTest() throws InvalidScopeException {
		SendReportParameters params=new SendReportParameters();
		params.callbackID="5e638260-e4a6-11e2-aa29-a0666f165663";
		params.report=reportString;
		params.targetScope=scope;
		library.sendReport(params);
	}

//	@Test
	public void getReportTest() throws InvalidScopeException, NoSuchReportException{
		String id="5e638260-e4a6-11e2-aa29-a0666f165663";
		report= library.getReport(id);
		System.out.println("report: "+report);
		System.out.println("reportString: "+reportString);
		assertNotNull(report);
	}

	
}
