package org.gcube.data.transfer.plugins.sis;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.model.plugins.thredds.DataSet;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.glassfish.jersey.client.ClientConfig;

public class TestGetCatalog {

	public static void main(String[] args) {
		SecurityTokenProvider.instance.set("f851ba11-bd3e-417a-b2c2-753b02bac506-98187548");
		String dataStorePath="/data/content/thredds/devVRE/indian_ocean_catch_5deg_1m_1952_11_01_2016_01_01_tunaatlasIRD_level1.nc";
		//getting threddsInfo from thredds-d
		Client client=ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class));
		WebTarget target=client.target(String.format("https://%s/data-transfer-service/gcube/service/Capabilities/pluginInfo/REGISTER_CATALOG", "thredds-d-d4s.d4science.org"));
		ThreddsInfo info=target.request(MediaType.APPLICATION_JSON).get(ThreddsInfo.class);
		System.out.println(info.getCatalogByFittingLocation(dataStorePath));
		DataSet catalogDataset=info.getDataSetFromLocation(dataStorePath);
		
		
		String datasetSubPath=dataStorePath.substring(catalogDataset.getLocation().length(), dataStorePath.lastIndexOf("/"));
		String datasetPath=catalogDataset.getPath()+datasetSubPath;
		System.out.println("Path : "+datasetPath);
	}

}
