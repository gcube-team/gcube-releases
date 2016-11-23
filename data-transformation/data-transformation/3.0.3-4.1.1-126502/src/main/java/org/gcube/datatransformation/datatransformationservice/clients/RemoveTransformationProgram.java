//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.publisher.ISPublisher;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class RemoveTransformationProgram {
//
//	private static GCUBESecurityManagerImpl secManager = new GCUBESecurityManagerImpl(){
//		@Override
//		public boolean isSecurityEnabled() {
//			// TODO Auto-generated method stub
//			return false;
//		}};
//		
//	public static void main(String[] args) throws Exception {
//		GCUBEScope scope = GCUBEScope.getScope(args[0]);
//		ISPublisher publisher  = GHNContext.getImplementation(ISPublisher.class);
//		publisher.removeGCUBEResource(args[1], "GenericResource", scope, secManager);
//	}
//
//	
//}
