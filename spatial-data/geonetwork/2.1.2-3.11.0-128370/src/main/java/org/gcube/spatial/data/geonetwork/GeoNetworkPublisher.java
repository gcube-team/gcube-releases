package org.gcube.spatial.data.geonetwork;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.opengis.metadata.Metadata;

public interface GeoNetworkPublisher extends GeoNetworkReader {

	public void setPrivileges(long metadataId, GNPrivConfiguration cfg)
			throws GNLibException, GNServerException;

	public long insertMetadata(GNInsertConfiguration configuration,
			File metadataFile) throws GNLibException, GNServerException;

	public long insertMetadata(GNInsertConfiguration configuration,
			Metadata meta) throws GNLibException, GNServerException,
			IOException, JAXBException;

	public long insertMetadata(File requestFile) throws GNLibException,
			GNServerException;

	public long insertMetadata(Metadata meta) throws GNLibException,
			GNServerException, IOException,
			JAXBException;

	
	public void updateMetadata(long id, File metadataFile)
			throws GNLibException, GNServerException;

	public void updateMetadata(long id, Metadata meta) throws GNLibException,
			GNServerException, IOException,
			JAXBException;

	public void deleteMetadata(long id) throws GNLibException,
			GNServerException;

	public void registerXMLAdapter(XMLAdapter adapter);
	
}
