package org.gcube.vremanagement.softwaregateway.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;

public class RegisterProfileClient {
	
	public static void main(String[] args) throws Exception {
		
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 3 ){
			System.out.println("Usage:");
			System.out.println("\tjava  RegisterProfileClient   SoftwareGatewayEPR  scope  AbsolutePathsOfFileProfile\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  RegisterProfileClient  http://node2.d.d4science.research-infrastructures.eu:9001/wsrf/services/gcube/vremanagement/softwaregateway/Registration  /gcube/devsec /home/gcube/profile.xml \n\n");
			return;
		}
		
		ScopeProvider.instance.set(args[1]);
		SGRegistrationLibrary library;
		try {
			library = Proxies.registrationService().at(new URI(args[0])).withTimeout(1, TimeUnit.MINUTES).build();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		String path= args[2];
//		GCUBEScope scope=GCUBEScope.getScope(args[1]);
			
		File profileFile= new File(path);
		InputStream is= new FileInputStream(profileFile);
		String profile=getStringFromInputStream(is);
		System.out.println("profile string: "+profile);
//		String profileString=FileUtils.fileToString(profileFile.getAbsolutePath());
//		
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(args[0]));
//		
//		String report=registerProfile(endpoint, scope, profileString);
		String report=library.register(profile);
		System.out.println("Report.xml:");
		System.out.println(report);
		
//		FileUtils.stringToFile(report, new File("report.xml"));
		writoToFile(report, "report.xml");
	}

	private static void writoToFile(String report, String fileName) {
		FileOutputStream fop = null;
		File file;
		try {
			 
			file = new File(fileName);
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = report.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
//	public static String registerProfile(GCUBEScope scope, String profile) throws Exception
//	{
//		EndpointReferenceType epr = ISUtil.getSoftwareGatewayEndpoint(scope);
//		return registerProfile(epr, scope, profile);
//	}
//	
//	public static String registerProfile(EndpointReferenceType epr, GCUBEScope scope, String profile) throws Exception
//	{
//		RegistrationPortType stub= new RegistrationServiceAddressingLocator().getRegistrationPortTypePort(epr);
//		stub=GCUBERemotePortTypeContext.getProxy(stub,scope, 120000);
//		String report=stub.register(profile);
//		
//		return report;
//	}
	
	
//		File file= new File(args[2]);
//		if(file.exists()){
//			String scope=args[1];
//			EndpointReferenceType endpoint = new EndpointReferenceType();
//			endpoint.setAddress(new AttributedURI(args[0]));
//			RegistrationPortType stub= new RegistrationServiceAddressingLocator().getRegistrationPortTypePort(endpoint);
//			stub=GCUBERemotePortTypeContext.getProxy(stub,GCUBEScope.getScope(scope));
//			if(file.isDirectory()){
//				File[] fileList=file.listFiles();
//				publicProfiles(stub, fileList);
//			}else{
//				String profile=FileUtils.fileToString(args[2]);
//				String id=stub.register(profile);
//				System.out.println("new resource published with ID: "+id);
//			}
//		}else{
//			System.out.println("The file not exist");
//			throw new FileNotFoundException("The file "+file.getAbsolutePath()+" not exist");
//		}
//	}

//	private static void publicProfiles(RegistrationPortType stub,
//			File[] fileList) throws Exception {
//		for (File f : fileList){
//			if(f.isFile()){
//				
//				try{
////					String id=stub.register(FileUtils.fileToString(f.getAbsolutePath()));
//					System.out.println("new resource published with ID: "+id);
//				}catch(GCUBEFault e){
//					logger.info("the file "+f.getAbsolutePath()+" is not a corrected profile, skip next");
//				}catch(Exception e){
//					logger.error("ERROR IN REGISTER PROFILE the file "+f.getAbsolutePath()+" is not a corrected profile, skip next");
//				}
//			}else{
//				publicProfiles(stub, f.listFiles());
//			}
//		}
//	}

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
}
