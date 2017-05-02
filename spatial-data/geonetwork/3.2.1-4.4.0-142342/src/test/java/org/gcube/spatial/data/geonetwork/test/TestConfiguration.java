package org.gcube.spatial.data.geonetwork.test;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.configuration.DefaultConfiguration;
import org.gcube.spatial.data.geonetwork.configuration.LocalResourceConfiguration;
import org.gcube.spatial.data.geonetwork.configuration.LocalResourceConfiguration.ResourceConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import lombok.Synchronized;

public class TestConfiguration {

	private static Configuration toUseConfiguration=null;

	@Synchronized
	public static GeoNetworkAdministration getClient() throws MissingConfigurationException, EncryptionException, MissingServiceEndpointException, GNLibException, GNServerException, AuthorizationException{
		if(toUseConfiguration==null){

			ResourceConfiguration resConfig=new ResourceConfiguration();
			resConfig.setAdminPassword("admin");
			resConfig.setVersion((short)3);
			resConfig.setMinor((short)0);
			resConfig.setRevision((short)5);
			resConfig.setBuild((short)0);
			resConfig.setHost("node3-d-d4s.d4science.org");
			resConfig.setEndpoint("http://node3-d-d4s.d4science.org/geonetwork");
		toUseConfiguration=new LocalResourceConfiguration(resConfig);


//						toUseConfiguration=new DefaultConfiguration();
		}
		return GeoNetwork.get(toUseConfiguration);
	}

}
