//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.publisher.ISPublisher;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class UploadTransformationProgramFromFolder {
//
//	
//	static GCUBESecurityManagerImpl secManager = new GCUBESecurityManagerImpl(){
//		@Override
//		public boolean isSecurityEnabled() {
//			return false;
//		}};
//		
//		
//	public static void main(String[] args) throws Exception {
//		
//		ArrayList<GCUBEScope> scopes = new ArrayList<GCUBEScope>();
//		
//		GCUBEScope voscope = GCUBEScope.getScope(args[1]);
//		if(voscope.getType() != GCUBEScope.Type.VO){
//			System.out.println("Second argument must be the vo scope");
//			return;
//		}
//		scopes.add(voscope);
//		
//		String voScopeToString = voscope.toString();
//		if(!voScopeToString.endsWith("/")){
//			voScopeToString+="/";
//		}
//
//		if(args.length>2){
//			for(int i = 2; i < args.length; i++){
//				GCUBEScope vrescope = GCUBEScope.getScope(voScopeToString+args[i]);
//				if(vrescope.getType() != GCUBEScope.Type.VRE){
//					System.out.println(i+"th argument must be a vre");
//				}
//				scopes.add(vrescope);
//			}
//		}else{
//			System.out.println("Tranformation program will be published only in VO scope");
//		}
//		
//		File[] files = new File(args[0]).listFiles();
//		for(File file: files){
//			if(file.getName().endsWith("TP.xml")){
//				uploadResourceFromFile(file.getAbsolutePath(), voscope, scopes);
//			}else{
//				System.out.println("File found which is not a transformation program");
//			}
//		}
//	}
//	
//	private static void uploadResourceFromFile(String file, GCUBEScope voscope, List<GCUBEScope> scopes) throws Exception {
//		System.out.println("Uploading file: "+file);
//		
//		GCUBEGenericResource resource = GHNContext.getImplementation(GCUBEGenericResource.class);
//		resource.load(new FileReader(file));
//		
//		for(GCUBEScope scope: scopes){
//			resource.addScope(scope);
//		}
//		
//		System.out.println("TPID "+resource.getID());
//		System.out.println("TPName "+resource.getName());
//		System.out.println("TPBody "+resource.getBody());
//		
//		for(Entry<String, GCUBEScope> entry: resource.getScopes().entrySet()){
//			System.out.println("Resource is going to be published in scope: "+entry.getValue());
//		}
//		
//		ISPublisher publisher  = GHNContext.getImplementation(ISPublisher.class);
//		publisher.registerGCUBEResource(resource, voscope, secManager);
//	}
//
//}
