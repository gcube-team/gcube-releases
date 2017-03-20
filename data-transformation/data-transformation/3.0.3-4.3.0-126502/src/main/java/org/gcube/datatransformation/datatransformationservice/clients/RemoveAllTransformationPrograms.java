//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.QueryParameter;
//import org.gcube.common.core.informationsystem.client.XMLResult;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
//import org.gcube.common.core.informationsystem.publisher.ISPublisher;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//
//public class RemoveAllTransformationPrograms {
//
//	static GCUBESecurityManagerImpl secManager = new GCUBESecurityManagerImpl(){
//		@Override
//		public boolean isSecurityEnabled() {
//			// TODO Auto-generated method stub
//			return false;
//		}};
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		GCUBEScope scope = GCUBEScope.getScope(args[0]);
//
//		String[] allProgramIDs = getAvailableTransformationProgramIDs(scope);
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//		System.out.print("Do you want to delete all TPs(Y): ");
//		String input = in.readLine();
//		if (input!=null && (input.equals("y") || input.equals("Y"))) {
//			System.out.println("Going to delete all program ids from scope: "+scope);
//			for (String programID : allProgramIDs) {
//				System.out.println("Removing program with id: " + programID);
//				removeTransformationProgram(programID, scope);
//			}
//		} else {
//			System.out.println("Programs wont be deleted");
//		}
//	}
//	
//	private static String[] getAvailableTransformationProgramIDs(GCUBEScope scope) throws Exception{
//		try {
//			ISClient client = GHNContext.getImplementation(ISClient.class);
//			GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//			query.addParameters(new QueryParameter("TYPE",GCUBEGenericResource.TYPE),
//					 new QueryParameter("FILTER","$result/Profile/SecondaryType/string() eq 'DTSTransformationProgram'"),
//					 new QueryParameter("RESULT", "$result/ID/text()"));
//
//			List<XMLResult> results = client.execute(query, scope);
//			if(results.size()==0){
//				System.out.println("Did not manage to find any available transformation programs");
//				return new String[0];
//			}else{
//				ArrayList<String> trProgramIDs = new ArrayList<String>(); 
//				for(XMLResult result: results){
//					System.out.println("Found transformation program \""+result.toString().trim()+"\"");
//					trProgramIDs.add(result.toString().trim());
//				}
//				return trProgramIDs.toArray(new String[trProgramIDs.size()]);
//			}
//		} catch (Exception e) {
//			System.out.println("Could not invoke IS to find the available transformation program IDs");
//			e.printStackTrace();
//			throw new Exception("Could not invoke IS to find the available transformation program IDs");
//		}
//	}
//	
//	private static void removeTransformationProgram(String id, GCUBEScope scope) throws Exception {
//		ISPublisher publisher  = GHNContext.getImplementation(ISPublisher.class);
//		publisher.removeGCUBEResource(id, "GenericResource", scope, secManager);
//	}
//
//}
