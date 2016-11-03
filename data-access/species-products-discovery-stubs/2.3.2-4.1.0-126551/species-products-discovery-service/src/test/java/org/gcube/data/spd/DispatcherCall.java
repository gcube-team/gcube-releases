package org.gcube.data.spd;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.types.VOID;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.stubs.GetSupportedPluginsResponse;
import org.gcube.data.spd.stubs.ManagerPortType;
import org.gcube.data.spd.stubs.RemoteDispatcherPortType;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.spd.stubs.service.ManagerServiceAddressingLocator;
import org.gcube.data.spd.stubs.service.RemoteDispatcherServiceAddressingLocator;
import org.gcube.data.streams.Stream;

public class DispatcherCall {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		getFile();

	}

	
	public static  void testDisptcher() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		ManagerPortType managerPt = new ManagerServiceAddressingLocator().getManagerPortTypePort(new EndpointReferenceType(new Address("http://node24.d.d4science.research-infrastructures.eu:9000/wsrf/services/gcube/data/speciesproductsdiscovery/manager")));
		managerPt = GCUBERemotePortTypeContext.getProxy(managerPt);
		GetSupportedPluginsResponse supportedPlugins = managerPt.getSupportedPlugins(new VOID());
		for (String plguinDesc : supportedPlugins.getPluginDescriptions())
			System.out.println(plguinDesc);
	}
	
	private static void getFile() throws Exception{
		
		String pkgs_name="java.protocol.handler.pkgs";
		String pkgs = System.getProperty(pkgs_name);
//		String pkg = "org.gcube.contentmanagement.contentmanager.stubs.model.protocol";
		String pkg = "org.gcube.contentmanager.storageclient.model.protocol";
		if (pkgs==null)
			pkgs = pkg ;
		else if (!pkgs.contains(pkg))
			pkgs = pkgs+"|"+pkg;
		System.setProperty(pkgs_name, pkgs);
				
		URL url = new URL("smp://dwca/8c788f50eace11e2b1678f9adc39871b.zip?5ezvFfBOLqb2cBxvyAbVnOhbxBCSqhv+Z4BC5NS/+OwS5RYBeaUL5FS9eDyNubiTI4vSpggUgPA+jm9rQxwbisfhkOW/m6l2IYG9BKb8AEJFLgVvG3FJTk0+4xV9iM/hNQvChZjoJZna0aPXkHN4Eg==");
		InputStream is = url.openStream();
		
		File file = new File("/Users/lucio/chordata", ".zip");
		file.createNewFile();
		
		OutputStream out=new FileOutputStream(file);
		  byte buf[]=new byte[1024];
		  int len;
		  while((len=is.read(buf))>0)
		  out.write(buf,0,len);
		  out.close();
		  is.close();
		  
		  System.out.println(file.getAbsolutePath());
	}
	
}
