//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.FileReader;
//import java.util.Map.Entry;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.publisher.ISPublisher;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class UploadTransformationProgram {
//
//	
//	static GCUBESecurityManagerImpl secManager = new GCUBESecurityManagerImpl(){
//		@Override
//		public boolean isSecurityEnabled() {
//			// TODO Auto-generated method stub
//			return false;
//		}};
//		
//		
//	public static void main(String[] args) throws Exception {
//		
//		GCUBEGenericResource resource = GHNContext.getImplementation(GCUBEGenericResource.class);
//		resource.load(new FileReader(args[0]));
//		
//		GCUBEScope voscope = GCUBEScope.getScope(args[1]);
//		if(voscope.getType() != GCUBEScope.Type.VO){
//			System.out.println("Second argument must be the vo scope");
//			return;
//		}
//		resource.addScope(voscope);
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
//				resource.addScope(vrescope);
//			}
//		}else{
//			System.out.println("Tranformation program will be published only in VO scope");
//		}
//		
//		System.out.println("TPID "+resource.getID());
//		System.out.println("TPName "+resource.getName());
//		System.out.println("TPType "+resource.getType());
//		System.out.println("TPBody "+resource.getBody());
//		
//		for(Entry<String, GCUBEScope> entry: resource.getScopes().entrySet()){/* Key - Value the same eee? */
//			System.out.println("Resource is going to be published in scope: "+entry.getValue()+" - "+entry.getKey());
//		}
//		
//		
//		ISPublisher publisher  = GHNContext.getImplementation(ISPublisher.class);
//		publisher.registerGCUBEResource(resource, voscope, secManager);
//
//	}
//
//}
