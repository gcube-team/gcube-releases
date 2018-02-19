package org.gcube.spatial.data.geonetwork.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;

public class AdministrationTests {


	private static final String defaultScope="/d4science.research-infrastructures.eu";

//	//******************** CONFIGURATION 
//	// per scope
//	private static final String assignedScopePrefix="assignedScope";
//	private static final String scopeUserPrefix="scopeUser";
//	private static final String scopePasswordPrefix="scopeUserPassword";
//	private static final String ckanUserPrefix="ckanUser";
//	private static final String ckanPasswordPrefix="ckanUserPassword";
//	private static final String defaultGroupPrefix="defaultGroup";
//	private static final String privateGroupPrefix="privateGroup";
//	private static final String publicGroupPrefix="publicGroup";
//	
//	// global
//	private static final String availableGroupSuffixList="availableGroups";	
//	private static final String adminUser="adminUser";
//	private static final String adminPassword="adminPassword";
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		TokenSetter.set(defaultScope);
//		GeoNetworkAdministration client=GeoNetwork.get();
//		ScopeConfiguration config=client.getConfiguration().getScopeConfiguration();
//		System.out.println("Configuration is : "+config);
//		client.login(LoginLevel.ADMIN);
//		
//		// getting INFRA group and user
//				Integer groupId=config.getPrivateGroup();
//				Integer userId=UserUtils.getByName(client.getUsers(), config.getAccounts().get(Type.SCOPE).getUser()).getId();
//		
				
//		client.transferOwnership(1, 0, userId, groupId);
//		System.out.println("DONE");
		
//				client.transferOwnership(36, 46, 30, 40);
//		System.out.println(client.getMetadataOwners());
		
//		final GNSearchRequest req=new GNSearchRequest();
//		req.addParam(GNSearchRequest.Param.any,"");
//		System.out.println(client.query(req));
//				
		
		//Set privileges from query
				
//				String[] scopes=new String[]{
//						"/d4science.research-infrastructures.eu",
//						"/d4science.research-infrastructures.eu/gCubeApps",
//						"/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab"
//				};
//				final GNSearchRequest req=new GNSearchRequest();
//				req.addParam(GNSearchRequest.Param.any,"");
//				
//				//look for any accessible id through scopes
//				
//				HashSet<Long> accessibleIds=new HashSet<>();
//				System.out.println("Loading accessible Ids..");
//				for(String scope:scopes){
//					TokenSetter.set(scope);
//					GeoNetworkAdministration adminClient=GeoNetwork.get();
//					adminClient.login(LoginLevel.SCOPE);
//					GNSearchResponse resp=adminClient.query(req);
//					Iterator<GNMetadata> it=resp.iterator();
//					while(it.hasNext())accessibleIds.add(it.next().getId());
//					System.out.println("Found "+resp.getCount()+" under "+scope);
//				}
//				System.out.println("Globally accessible count : "+accessibleIds.size());
//				
//				TokenSetter.set(scopes[0]);
//				System.out.println("Loading ADMIN IDs");
//				HashSet<Long> adminIds=new HashSet<>();				
//				GeoNetworkAdministration adminClient=GeoNetwork.get();
//				
//				
//				adminClient.login(LoginLevel.ADMIN);
//				Iterator<GNMetadata> it=adminClient.query(req).iterator();
//				while(it.hasNext())adminIds.add(it.next().getId());
//				System.out.println("Found "+adminIds.size());
//				adminIds.removeAll(accessibleIds);
//				System.out.println("Resulting hidden count : "+adminIds.size());
//				
////				// getting INFRA group and user
//				ScopeConfiguration config=adminClient.getConfiguration().getScopeConfiguration();
//				Integer groupId=config.getPrivateGroup();
//				Integer userId=UserUtils.getByName(adminClient.getUsers(), config.getAccounts().get(Type.SCOPE).getUser()).getId();
//				
//				System.out.println("Assignin ownership to root");
//				HashSet<Long> errors=new HashSet<>();
//				List<Long> ids=new ArrayList<Long>();
//				for(Long toMoveId:adminIds){
//					ids.add(toMoveId);
//					if(ids.size()==4){
//						try{
//							adminClient.assignOwnership(ids,userId, groupId);
//						System.out.print("*");
//					
//						}catch(Exception e){
//							System.err.print("*");
//							errors.addAll(ids);
//						}finally{
//							ids.clear();
//						}
//					}
//				}
//				
//				System.out.println();
//				System.out.println("Errros : "+errors.size());
				
		
		//Set privileges on single id
		
		
		TokenSetter.set(defaultScope);
		GeoNetworkAdministration client=GeoNetwork.get();
		ScopeConfiguration config=client.getConfiguration().getScopeConfiguration();
		System.out.println("Configuration is : "+config);
		client.login(LoginLevel.ADMIN);
//		
//		// getting INFRA group and user
				Integer groupId=config.getPrivateGroup();
				Integer userId=UserUtils.getByName(client.getUsers(), config.getAccounts().get(Type.SCOPE).getUser()).getId();
//		
				
		client.assignOwnership(Collections.singletonList(new Long(93778)), userId, groupId);
		System.out.println("DONE");
		
		
				
				//******************** Check different level accessibility
				
//		TokenSetter.set("/gcube");
//		client=GeoNetwork.get();
//		client.login(LoginLevel.SCOPE);
//		List<Long> foundIds=new ArrayList<Long>();		
//		
//		final GNSearchRequest req=new GNSearchRequest();
//		req.addParam(GNSearchRequest.Param.any,"");
//		GNSearchResponse resp=client.query(req);		
//				
//		Iterator<GNMetadata> iterator=resp.iterator();
//		while(iterator.hasNext()){
//			foundIds.add(iterator.next().getId());
//		}
//		
//		System.out.println("Found "+foundIds.size()+", checking from second scope ");
//		
//		TokenSetter.set("/gcube/devsec/devVRE");
//		client=GeoNetwork.get();
//		client.login(LoginLevel.SCOPE);
//		List<Long> unableToAccess=new ArrayList();
//		for(Long id:foundIds)
//			try{
//				client.getById(id);
//			}catch(Exception e){
//				unableToAccess.add(id);
//			}
//		
//		printOut("noAccess.txt", unableToAccess);
//		System.out.println(unableToAccess.size()+" were not accessible");
//		
//		
				
				/// checking transfer by transfer
				
//		// getting ids.. 
//		System.out.println("Getting an id.. ");
//		final GNSearchRequest req=new GNSearchRequest();
//		req.addParam(GNSearchRequest.Param.any,"");
//		GNSearchResponse resp=client.query(req);		
//		int totalCount=resp.getCount();
//		List<Long> toTransferIds=new ArrayList<>();
//		List<Long> transferred=new ArrayList<>();
//		List<Long> errorIds=new ArrayList<>();
//		Iterator<GNMetadata> iterator=resp.iterator();
//		while(iterator.hasNext()){
//			GNMetadata meta=iterator.next();
//			toTransferIds.add(meta.getId());
//			if(toTransferIds.size()>=1)
//				try{
//					client.transferOwnership(toTransferIds, userId, groupId);
//					System.out.println("Ownership transferred");
//					transferred.addAll(toTransferIds);					
//				}catch(Exception e){
//					errorIds.addAll(toTransferIds);
//					System.err.println("Error with ids : "+toTransferIds );
//					System.err.println(e.getMessage());
//				}finally{
//					toTransferIds.clear();
//				}
//		}
//		
//		
//		if(!toTransferIds.isEmpty())
//			client.transferOwnership(toTransferIds, userId, groupId);
//		
//		
//		
//		
//		
//		
//		// checking accessibility
//		client.login(LoginLevel.SCOPE);
//		System.out.println("************ Checking accessibility ************** ");
//		List<Long> transferredNotAccessible=checkAccessibility(client, transferred);		
//		List<Long> errorNotAccessible=checkAccessibility(client, errorIds);
//		printOut("transferred_ok.txt", transferred);
//		printOut("transferredButNotAccessible.txt", transferredNotAccessible);
//		printOut("transferred_err.txt",errorIds);
//		printOut("errorsAndNotAccessible.txt", errorNotAccessible);
//		System.err.println("******************** REPORT **************************");
//		System.err.println("TOTAL IDS : "+totalCount);
//		System.err.println("TRANSFERRED  COUNT : "+transferred.size());
//		System.err.println("TRANSFERRED OK, NOT REACHABLE COUNT : "+transferredNotAccessible.size());
//		System.err.println("TRANSFER ERROR  COUNT : "+errorIds.size());
//		System.err.println("TRANSFER ERROR AND NOT REACHABLE COUNT : "+errorNotAccessible.size());
//		
//		
	}

		private static List<Long> checkAccessibility(GeoNetworkReader reader,List<Long> toCheckIds){
			List<Long> errors=new ArrayList<>();
			for(Long id:toCheckIds)		
				try{
					reader.getById(id);
				}catch(Exception e){
					errors.add(id);
				}
			return errors;
		}
	
	
		private static final void printOut(String fileName, List<?> toprintElements) throws FileNotFoundException{
			PrintWriter out=new PrintWriter(fileName);
			for(Object element:toprintElements)
				out.println(element.toString());
			out.flush();
			out.close();			
		}
}
