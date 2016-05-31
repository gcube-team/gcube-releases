package org.gcube.spatial.data.geonetwork;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.gcube.spatial.data.geonetwork.configuration.AuthorizationException;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.configuration.ConfigurationManager;
import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.geotoolkit.xml.XML;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.opengis.metadata.Metadata;

public class GeoNetwork implements GeoNetworkPublisher {

	private static XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());			
	
	public static GeoNetworkPublisher get() throws Exception{
		return new GeoNetwork(ConfigurationManager.get());
	}
	
	public static GeoNetworkPublisher get(Configuration config){
		return new GeoNetwork(config);
	}
	
	private Configuration config;
	
	
	private GeoNetwork(Configuration config){
		this.config=config;		
	}
	
	@Override
	public Configuration getConfiguration(){
		return config;
	}
	//************** READ ONLY METHODS, LOGIN OPTIONAL
	
	@Override
	public void login(LoginLevel lvl) throws AuthorizationException {
		GNClient client=getClient();
		if(config.getGeoNetworkUsers().containsKey(lvl)&&config.getGeoNetworkPasswords().containsKey(lvl)){
			if(!client.login(config.getGeoNetworkUsers().get(lvl), config.getGeoNetworkPasswords().get(lvl)))throw new AuthorizationException();
		}else throw new AuthorizationException("Login level "+lvl+" not found");
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#query(it.geosolutions.geonetwork.util.GNSearchRequest)
	 */
	@Override
	public GNSearchResponse query(GNSearchRequest request) throws GNLibException, GNServerException{
		return getClient().search(request);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#query(java.io.File)
	 */
	@Override
	public GNSearchResponse query(File fileRequest) throws GNLibException, GNServerException{
		return getClient().search(fileRequest);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#getById(long)
	 */
	@Override
	public Metadata getById(long id) throws GNLibException, GNServerException, JAXBException{
		String xml=out.outputString(getClient().get(id));
		return (Metadata) XML.unmarshal(xml);		
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#getById(java.lang.String)
	 */
	@Override
	public Metadata getById(String UUID) throws GNLibException, GNServerException, JAXBException{
		return (Metadata) XML.unmarshal(getByIdAsRawString(UUID));
	}
	
	@Override
	public String getByIdAsRawString(String UUID) throws GNLibException,
			GNServerException, JAXBException {
		 return out.outputString(getClient().get(UUID));
	}
	
	//************** WRITE METHODS, LOGIN REQUIRED

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#setPrivileges(long, it.geosolutions.geonetwork.util.GNPrivConfiguration)
	 */
	@Override
	public void setPrivileges(long metadataId,GNPrivConfiguration cfg) throws GNLibException, GNServerException{
		GNClient client=getClient();		
		client.setPrivileges(metadataId, cfg);
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, java.io.File)
	 */
	@Override
	public long insertMetadata(GNInsertConfiguration configuration,File metadataFile) throws GNLibException, GNServerException{
		GNClient client=getClient();		
		return client.insertMetadata(configuration, metadataFile);
	}
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, org.opengis.metadata.Metadata)
	 */
	@Override
	public long insertMetadata(GNInsertConfiguration configuration,Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException{		
		return insertMetadata(configuration, meta2File(meta,registeredXMLAdapters));
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(java.io.File)
	 */
	@Override
	public long insertMetadata(File requestFile) throws GNLibException, GNServerException{
		GNClient client=getClient();		
		return client.insertRequest(requestFile);
	}
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(org.opengis.metadata.Metadata)
	 */
	@Override
	public long insertMetadata(Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException{
		return insertMetadata(meta2File(meta,registeredXMLAdapters));
	}
		
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#updateMetadata(long, java.io.File)
	 */
	@Override
	public void updateMetadata(long id,File metadataFile) throws GNLibException, GNServerException{
		GNClient client=getClient();
		client.updateMetadata(id, metadataFile);
	}
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#updateMetadata(long, org.opengis.metadata.Metadata)
	 */
	@Override
	public void updateMetadata(long id,Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException{
		updateMetadata(id, meta2File(meta,registeredXMLAdapters));
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#deleteMetadata(long)
	 */
	@Override
	public void deleteMetadata(long id) throws GNLibException, GNServerException{
		GNClient client=getClient();
		client.deleteMetadata(id);
	}
	
	
	
	@Override
	public void registerXMLAdapter(XMLAdapter adapter) {
		registeredXMLAdapters.add(adapter);
	}
	
	
	
	
	//************* PRIVATE
	
	private GNClient theClient=null;
	
	private synchronized GNClient getClient(){
		if(theClient==null)
			theClient = new GNClient(config.getGeoNetworkEndpoint());
		return theClient;
	}
	
	
	private List<XMLAdapter> registeredXMLAdapters=new ArrayList<XMLAdapter>();
	
	
	private static File meta2File(Metadata meta,List<XMLAdapter> adapters) throws IOException, JAXBException{
		File temp=File.createTempFile("meta", ".xml");
		FileWriter writer=new FileWriter(temp);
		String marshalled=XML.marshal(meta);
		for(XMLAdapter adapter:adapters)
			marshalled=adapter.adaptXML(marshalled);
		writer.write(marshalled);
		writer.close();
		return temp;
	}
	
}
