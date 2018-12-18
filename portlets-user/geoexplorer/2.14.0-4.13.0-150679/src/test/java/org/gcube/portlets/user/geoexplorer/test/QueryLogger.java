//package org.gcube.portlets.user.geoexplorer.test;
//
//import it.geosolutions.geonetwork.util.GNSearchRequest;
//import it.geosolutions.geonetwork.util.GNSearchResponse;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import org.apache.commons.io.FileUtils;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.spatial.data.geonetwork.GeoNetwork;
//import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
//import org.gcube.spatial.data.geonetwork.configuration.AuthorizationException;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;
//import org.junit.Test;
//
//public class QueryLogger {
//
//	/**
//	 * @param args
//	 */
//
//	private static final String defaultScope="/gcube/devsec";
////	private static final String defaultScope="/d4science.research-infrastructures.eu/gCubeApps";
//	private static final boolean printEverything=true;
//	private static XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());	
//	public static final String CITATION="Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
//			"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
//			"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
//	
//	public static void main(String[] args) throws Exception, AuthorizationException {
//		new QueryLogger().test();
//	}
//
//	@Test
//	public void test() throws Exception, AuthorizationException{
//		File metaDir=new File("meta");
//		if(!metaDir.exists()) metaDir.mkdirs();
//		else{
//			FileUtils.cleanDirectory(metaDir);
//		}
//		System.out.println(System.getenv());		
//		ScopeProvider.instance.set(defaultScope);
//		System.out.println("Looking for ANY 'aquamaps'");
//		GNSearchRequest req=new GNSearchRequest();		
////		req.addConfig(GNSearchRequest.Config.sortBy, "title");
//		req.addParam(GNSearchRequest.Param.themeKey, "figis");
//		GeoNetworkReader gn=GeoNetwork.get();
//		System.out.println(gn.getConfiguration());
//		gn.login();
//		GNSearchResponse resp=gn.query(req);
//		System.out.println("Found "+resp.getCount()+" items");
//		System.out.println("From "+resp.getFrom());
//		System.out.println("To "+resp.getTo());
//		System.out.println("Summary "+resp.getSummary());
//		System.out.println("Is Complete "+resp.isCompleteResponse());
//		ArrayList<String> files=new ArrayList<String>();
//		long count=0;
//		if(resp.getCount()!=0)
//			for(GNSearchResponse.GNMetadata metadata:resp){
////				long id=metadata.getId();
////				System.out.println(out.outputString(metadata.getInfo()));				
////				System.out.print(id + (metadata+""));
//				try{
//					count++;
//					System.out.println(count+" out of "+resp.getCount());
//					gn.getById(metadata.getUUID());
////				System.out.println(meta);
//				}catch(Exception e){
////					FileUtils.writeStringToFile(new File(metaDir,metadata.getUUID()+".xml"), gn.getByIdAsRawString(metadata.getUUID()));
//					files.add(metadata.getUUID());
//				}
////					ArrayList<InternationalString> key=new ArrayList<InternationalString>();
////					key.add(new DefaultInternationalString("AquaMaps"));
////					Keywords keys= new DefaultKeywords(key);					
////					Identification ident=new DefaultDataIdentification();
////					((Collection<Keywords>)ident.getDescriptiveKeywords()).add(keys);					
////					((Collection<Identification>) meta.getIdentificationInfo()).add(ident);
////					
////					gn.updateMetadata(id, meta);
////					System.out.println("*********************UPDATED************************");
////					System.out.println(gn.getById(id));
////				
////				break;
//				
//				
//			}
//		System.out.println("Wrote "+files.size()+" files into "+metaDir);
//		for(String s:files)System.out.println(s);
//	}
//
//}
