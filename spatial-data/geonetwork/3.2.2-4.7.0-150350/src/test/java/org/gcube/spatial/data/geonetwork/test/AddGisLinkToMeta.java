package org.gcube.spatial.data.geonetwork.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.distribution.Format;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

public class AddGisLinkToMeta {

	public static final String HTTP_PROTOCOL="WWW:LINK-1.0-http--link";
	public static final String GIS_LINK_NAME="GIS - LINK";
	
	static String[] scopes=new String[]{
		"/gcube"
	};


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(String scope:scopes){			
			System.out.println("Checking scope "+scope);			
			TokenSetter.set(scope);

			try{
				System.out.println("Gathering public layers ids..");
				GeoNetworkReader reader=GeoNetwork.get();
				List<Long> publicIds=getIds(queryAll(reader));
				System.out.println("Found "+publicIds.size()+" public ids");

				System.out.println("Gathering locals.. ");				
				reader.login(LoginLevel.CKAN);

				GeoNetworkPublisher publisher=GeoNetwork.get();
				publisher.login(LoginLevel.SCOPE);
				// Query

				
				final GNSearchRequest req=new GNSearchRequest();
				//			NO GIS LINK TEST
				//			req.addParam(GNSearchRequest.Param.any,"tdm657b8e51cd604c869bc6196fb3cbc5bc");

				//			SI GIS LINK TEST
//							req.addParam(GNSearchRequest.Param.any,"546d2cdb-b670-40aa-a8f6-e5df0bf326ef");

				// ALL
				req.addParam(GNSearchRequest.Param.any,"");
				GNSearchResponse resp=reader.query(req);
				System.out.println("Found "+resp.getCount()+" in current view..");

				long count=0;
				List<Long> problematicIds=new ArrayList<>();
				
				Iterator<GNMetadata> iterator=resp.iterator();
				while(iterator.hasNext()){
					GNMetadata gnMeta=iterator.next();
					Long id=gnMeta.getId();
					try{
						// for each element 
						if(!publicIds.contains(id)){
							// update only if local
							// get full Metadata
							DefaultMetadata meta=(DefaultMetadata) reader.getById(id);
							// check if http-gis link is present
							if(!isGIsLinkPresent(meta)){
								// add link
								meta=addGisLink(meta);
								// update
								publisher.updateMetadata(id, meta);
								System.out.println("UPDATED "+id+" "+gnMeta.getUUID());								
								count++;
							}else System.out.println("SKIPPED GIS "+id+" "+gnMeta.getUUID());
						}else System.out.println("SKIPPED PUBLIC : "+id+" "+gnMeta.getUUID());
					}catch(Exception e){
						problematicIds.add(id);
						throw e;
					}

				}

				System.out.println("Updated "+count+" layers");
				System.out.println("Errors on layers "+problematicIds);
				
			}catch(Exception e){
				e.printStackTrace();
			}


		}
	}

	public static boolean isGIsLinkPresent(DefaultMetadata meta){		
		for(Format format:meta.getDistributionInfo().getDistributionFormats()){
			if(format.getName().toString().equals("HTTP")&&format.getVersion().toString().equals("1.1.0")){
				for(DigitalTransferOptions options : meta.getDistributionInfo().getTransferOptions()){
					for(OnlineResource res:options.getOnLines())
						if(res.getProtocol().equals(HTTP_PROTOCOL)&&res.getName().equals(GIS_LINK_NAME)) return true;
				}

			}

		}
		return false;
	}

	public static DefaultMetadata addGisLink(DefaultMetadata meta) throws UriResolverMapException, IllegalArgumentException, URISyntaxException{
		DefaultDistribution distribution=new DefaultDistribution();



		DefaultFormat format4 = new DefaultFormat();
		format4.setName(new DefaultInternationalString("HTTP"));
		format4.setVersion(new DefaultInternationalString("1.1.0"));
		ArrayList<DefaultFormat> formats=new ArrayList<>();
		formats.add(format4);

		for(Format format:meta.getDistributionInfo().getDistributionFormats()){
			formats.add((DefaultFormat) format);
		}

		distribution.setDistributionFormats(formats);

		DefaultDigitalTransferOptions transferOptions=new DefaultDigitalTransferOptions();
		for(DigitalTransferOptions options : meta.getDistributionInfo().getTransferOptions())
			for(OnlineResource res:options.getOnLines())
				transferOptions.getOnLines().add(res);










		String uriString=getGisLinkByUUID(meta.getFileIdentifier());

		URI uri=new URI(uriString);
		DefaultOnlineResource resource=new DefaultOnlineResource(uri);

		resource.setName(GIS_LINK_NAME);
		resource.setProtocol(HTTP_PROTOCOL);
		transferOptions.getOnLines().add(resource);
		distribution.getTransferOptions().add(transferOptions);
		meta.setDistributionInfo(distribution);
		return meta;
	}


	public static String getGisLinkByUUID(String uuid) throws UriResolverMapException, IllegalArgumentException{
		Map<String,String> params=new HashMap();
		params.put("scope", ScopeUtils.getCurrentScope());
		params.put("gis-UUID", uuid);
		UriResolverManager resolver = new UriResolverManager("GIS");
		String toReturn= resolver.getLink(params, false);
		return toReturn;
	}


	private static GNSearchResponse queryAll(GeoNetworkReader reader) throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException{
		System.out.println("Scope configuration : "+reader.getConfiguration().getScopeConfiguration());
		final GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,"");
		return reader.query(req);
	}

	private static List<Long> getIds(GNSearchResponse resp){
		List<Long> toReturn=new ArrayList<>();
		Iterator<GNMetadata> iterator=resp.iterator();
		while(iterator.hasNext()){
			toReturn.add(iterator.next().getId());
		}
		return toReturn;
	}

}
