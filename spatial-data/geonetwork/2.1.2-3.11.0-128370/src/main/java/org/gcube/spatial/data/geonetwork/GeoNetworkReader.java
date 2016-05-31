package org.gcube.spatial.data.geonetwork;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.gcube.spatial.data.geonetwork.configuration.AuthorizationException;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.opengis.metadata.Metadata;

public interface GeoNetworkReader {

	public GNSearchResponse query(GNSearchRequest request)
			throws GNLibException, GNServerException;

	public GNSearchResponse query(File fileRequest) throws GNLibException,
			GNServerException;

	public Metadata getById(long id) throws GNLibException, GNServerException,
			JAXBException;

	public Metadata getById(String UUID) throws GNLibException,
			GNServerException, JAXBException;

	public String getByIdAsRawString(String UUID) throws GNLibException,
	GNServerException, JAXBException;
	
	public Configuration getConfiguration();
	
	public void login(LoginLevel level)throws AuthorizationException;
}