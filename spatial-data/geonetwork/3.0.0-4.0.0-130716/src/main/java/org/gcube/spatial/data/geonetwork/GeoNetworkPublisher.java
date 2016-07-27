package org.gcube.spatial.data.geonetwork;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.GeoNetworkException;
import org.gcube.spatial.data.geonetwork.model.faults.InvalidInsertConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.opengis.metadata.Metadata;

public interface GeoNetworkPublisher extends GeoNetworkReader {

	public void setPrivileges(long metadataId, GNPrivConfiguration cfg)
			throws GNLibException, GNServerException,MissingServiceEndpointException;

	public long insertMetadata(GNInsertConfiguration configuration,
			File metadataFile) throws GNLibException, GNServerException,MissingServiceEndpointException, MissingConfigurationException, InvalidInsertConfigurationException, AuthorizationException;

	public long insertMetadata(GNInsertConfiguration configuration,
			Metadata meta) throws GNLibException, GNServerException,
			IOException, JAXBException,MissingServiceEndpointException, MissingConfigurationException, InvalidInsertConfigurationException, AuthorizationException;

	public long insertMetadata(File requestFile) throws GNLibException,
			GNServerException,MissingServiceEndpointException, MissingConfigurationException;

	public long insertMetadata(Metadata meta) throws GNLibException,
			GNServerException, IOException,
			JAXBException,MissingServiceEndpointException, MissingConfigurationException;

	
	public void updateMetadata(long id, File metadataFile)
			throws GNLibException, GNServerException,MissingServiceEndpointException;

	public void updateMetadata(long id, Metadata meta) throws GNLibException,
			GNServerException, IOException,
			JAXBException,MissingServiceEndpointException;

	public void deleteMetadata(long id) throws GNLibException,
			GNServerException,MissingServiceEndpointException;

	public void registerXMLAdapter(XMLAdapter adapter);
	
	public GNInsertConfiguration getCurrentUserConfiguration(String category,String styleSheet) throws AuthorizationException, MissingConfigurationException, MissingServiceEndpointException, GeoNetworkException;
	
}
