//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.util.List;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class GetTransformationPrograms2 {
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
//		GCUBEScope scope = GCUBEScope.getScope(args[0]);	
//		
//		
//		getAvailableTransformationProgramIDs(scope);
//	}
//
//	public static void getAvailableTransformationProgramIDs(GCUBEScope scope) throws Exception{
//		try {
//			ISClient client = GHNContext.getImplementation(ISClient.class);
//			GCUBEGenericResourceQuery query = client.getQuery(GCUBEGenericResourceQuery.class);
//			query.addAtomicConditions(new AtomicCondition("/Profile/SecondaryType", "DTSTransformationProgram"));
//
//			
//			List<GCUBEGenericResource> results = client.execute(query, scope);
//			if(results.size()==0){
//				System.out.println("Did not manage to find any available transformationUnit programs");
//			}else{
//				
//				for(GCUBEGenericResource result: results){
//					System.out.println("Found transformationUnit program "+result.getName()+" - \""+result.getID()+"\"");
//				}
//			}
//		} catch (Exception e) {
//			System.out.println("Could not invoke IS to find the available transformationUnit program IDs");
//		}
//	}
//}
