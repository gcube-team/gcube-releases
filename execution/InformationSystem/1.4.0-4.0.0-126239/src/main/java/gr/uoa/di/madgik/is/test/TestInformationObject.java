package gr.uoa.di.madgik.is.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.*;
import gr.uoa.di.madgik.environment.is.elements.invocable.*;
import gr.uoa.di.madgik.environment.is.elements.plot.*;

public class TestInformationObject
{
	public static void main(String []args) throws Exception
	{
		InvocableProfileInfo prof=new PojoInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.Calculator.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.pojo.1.xml"), prof);
		prof=new PojoInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/prof.pojo.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.pojo.2.xml"), prof);

		prof=new WSInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.RemoteCalculator.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.ws.1.xml"), prof);
		prof=new WSInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/prof.ws.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.ws.2.xml"), prof);

		prof=new ShellInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/ls.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.shell.1.xml"), prof);
		prof=new ShellInvocableProfileInfo();
		prof.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/prof.shell.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/prof.shell.2.xml"), prof);
		
		InvocablePlotInfo plotProf=new PojoPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.Calculator.plots.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.pojo.1.xml"), plotProf);
		plotProf=new PojoPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/plotProf.pojo.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.pojo.2.xml"), plotProf);
		
		plotProf=new WSPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.RemoteCalculator.plots.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.ws.1.xml"), plotProf);
		plotProf=new WSPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/plotProf.ws.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.ws.2.xml"), plotProf);
		
		plotProf=new ShellPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("../CallablesTesting/InvocableProfiles/ls.plot.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.shell.1.xml"), plotProf);
		plotProf=new ShellPlotInfo();
		plotProf.FromXML(TestInformationObject.ReadFile(new File("/home/gpapanikos/Desktop/plotProf.shell.1.xml")));
		TestInformationObject.WriteFile(new File("/home/gpapanikos/Desktop/plotProf.shell.2.xml"), plotProf);

		
	}
	
	private static void WriteFile(File f, InvocableProfileInfo prof) throws EnvironmentInformationSystemSerializationException, Exception
	{
		XMLUtils.Serialize(f.toString(), prof.ToXML());
	}
	
	private static void WriteFile(File f, InvocablePlotInfo prof) throws EnvironmentInformationSystemSerializationException, Exception
	{
		XMLUtils.Serialize(f.toString(), prof.ToXML());
	}
	
	private static String ReadFile(File f) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(f));
		StringBuilder buf=new StringBuilder();
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			buf.append(line);
		}
		return buf.toString();
	}
}
