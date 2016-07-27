package org.gcube.spatial.data.geonetwork.test;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;

public class AdministrationTests {


	private static final String defaultScope="/gcube";

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
		ScopeProvider.instance.set(defaultScope);
		GeoNetworkAdministration client=GeoNetwork.get();
		ScopeConfiguration config=client.getConfiguration().getScopeConfiguration();
		System.out.println("Configuration is : "+config);
		client.login(LoginLevel.ADMIN);
		
		// getting INFRA group and user
				Integer groupId=config.getPrivateGroup();
				Integer userId=UserUtils.getByName(client.getUsers(), config.getAccounts().get(Type.SCOPE).getUser()).getId();
		
				
//		client.transferOwnership(null, groupId, userId);
		
//				client.transferOwnership(36, 46, 30, 40);
//		System.out.println(client.getMetadataOwners());
		
//		final GNSearchRequest req=new GNSearchRequest();
//		req.addParam(GNSearchRequest.Param.any,"");
//		System.out.println(client.query(req));
//				
				
				//******************** Check different level accessibility
				
		ScopeProvider.instance.set("/gcube");
		client=GeoNetwork.get();
		client.login(LoginLevel.SCOPE);
		List<Long> foundIds=new ArrayList<Long>();		
		
		final GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,"");
		GNSearchResponse resp=client.query(req);		
				
		Iterator<GNMetadata> iterator=resp.iterator();
		while(iterator.hasNext()){
			foundIds.add(iterator.next().getId());
		}
		
		System.out.println("Found "+foundIds.size()+", checking from second scope ");
		
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		client=GeoNetwork.get();
		client.login(LoginLevel.SCOPE);
		List<Long> unableToAccess=new ArrayList();
		for(Long id:foundIds)
			try{
				client.getById(id);
			}catch(Exception e){
				unableToAccess.add(id);
			}
		
		printOut("noAccess.txt", unableToAccess);
		System.out.println(unableToAccess.size()+" were not accessible");
		
		
				
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
