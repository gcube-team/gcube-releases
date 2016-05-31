//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.QueryParameter;
//import org.gcube.common.core.informationsystem.client.XMLResult;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class GetTransformationPrograms {
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
//			GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//			query.addParameters(new QueryParameter("TYPE",GCUBEGenericResource.TYPE),
//					 new QueryParameter("FILTER","$result/Profile/SecondaryType/string() eq 'DTSTransformationProgram'"),
//					 new QueryParameter("RESULT", "$result/ID/text()"));
//
//			List<XMLResult> results = client.execute(query, scope);
//			if(results.size()==0){
//				System.out.println("Did not manage to find any available transformationUnit programs");
//			}else{
//				ArrayList<String> trProgramIDs = new ArrayList<String>(); 
//				for(XMLResult result: results){
//					System.out.println("Found transformationUnit program \""+result.toString().trim()+"\"");
//					trProgramIDs.add(result.toString().trim());
//				}
//			}
//		} catch (Exception e) {
//			System.out.println("Could not invoke IS to find the available transformationUnit program IDs");
//		}
//	}
//}
