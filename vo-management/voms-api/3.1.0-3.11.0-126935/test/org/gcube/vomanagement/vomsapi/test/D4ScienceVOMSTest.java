package org.gcube.vomanagement.vomsapi.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import org.gcube.vomanagement.vomsapi.VOMSAttributeManager;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationProperty;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;
import org.gcube.vomanagement.vomsapi.impl.utils.VOMSServerBean;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.gridforum.jgss.ExtendedGSSCredential;

public class D4ScienceVOMSTest {

	// attribute : /d4science.research-infrastructures.eu/Role=NULL/Capability=NULL
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		
		BasicConfigurator.configure(new ConsoleAppender());
		RootLogger.getLogger("org.gcube").setLevel(Level.DEBUG);
		RootLogger.getLogger("org.glite").setLevel(Level.DEBUG);
		
		Properties props = new Properties();
		props.setProperty(VOMSAPIConfigurationProperty.VOMS_HOST.toString(), "voms.research-infrastructures.eu");
		props.setProperty(VOMSAPIConfigurationProperty.VO_NAME.toString(), "d4science.research-infrastructures.eu");
		props.setProperty(VOMSAPIConfigurationProperty.CLIENT_CERT.toString(), "/home/toor/usercert.pem");
		props.setProperty(VOMSAPIConfigurationProperty.CLIENT_KEY.toString(), "/home/toor/userkey_nopass.pem");
		props.setProperty(VOMSAPIConfigurationProperty.VOMS_PORT.toString(), "8443");
		props.setProperty(VOMSAPIConfigurationProperty.RUNS_IN_WS_CORE.toString(), "false");
		props.setProperty(VOMSAPIConfigurationProperty.MYPROXY_HOST.toString(), "grids04.eng.it");

		//create the stub factory
		VOMSAPIFactory factory = new VOMSAPIFactory(new VOMSAPIConfiguration(props));
//		//create VOMS Admin stub
//		
//		VOMSAdmin admin = factory.getVOMSAdmin();
		//User user = admin.getUser("/C=GR/O=HellasGrid/OU=uoa.gr/CN=Vassilis Moustakas", "/C=GR/O=HellasGrid/OU=Certification Authorities/CN=HellasGrid CA 2006");
//		
//		String [] roles = admin.listRoles("/C=GR/O=HellasGrid/OU=uoa.gr/CN=Vassilis Moustakas", "/C=GR/O=HellasGrid/OU=Certification Authorities/CN=HellasGrid CA 2006");
//		String [] groups = admin.listGroups("/C=GR/O=HellasGrid/OU=uoa.gr/CN=Vassilis Moustakas", "/C=GR/O=HellasGrid/OU=Certification Authorities/CN=HellasGrid CA 2006");
//		//System.out.println(user);
//
//		System.out.println("ROLES");
//		
//		for (String r : roles)
//		{
//			System.out.println(r);
//		}
//		
//		System.out.println("GROUPS");
//		
//		for (String c : groups)
//		{
//			System.out.println(c);
//		}
		ExtendedGSSCredential credentials = CredentialsUtil.loadEndEntityCredentials("/home/toor/usercert.pem","/home/toor/userkey_nopass.pem", null);
		System.out.println("CREDENTIALS: "+CredentialsUtil.stringCredentials(credentials));
		
		VOMSServerBean bean = new VOMSServerBean("voms.research-infrastructures.eu", "/C=IT/O=INFN/OU=Host/L=NMIS-ISTI/CN=voms.research-infrastructures.eu", 15000, "d4science.research-infrastructures.eu");
		List<VOMSServerBean> beanList = new ArrayList<VOMSServerBean>();
		beanList.add(bean);
		factory.setServerList(beanList);
		
		VOMSAttributeManager attributeManager = factory.getVOMSAttributeManager();


		ExtendedGSSCredential response = attributeManager.generateAttributedCredentials(credentials);
		System.out.println(CredentialsUtil.stringCredentials(response));
		
	}


	
}
