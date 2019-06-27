package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;
import org.gcube.spatial.data.sdi.test.factories.ThreddsManagerFactory;

public class TestCreateCatalog {

	public static void main(String[] args) throws MalformedURLException {
//		TokenSetter.set("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		
		TokenSetter.set("/gcube/devNext");
		

		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());

		ThreddsManager mng=new ThreddsManagerFactory().provide();
		
		/*
		 * "PUT /sdi-service/gcube/service/Thredds?
		 * name=Thredds+Root+Catalog&
		 * path=public/netcdf/syncfolderfrancesco&
		 * folder=public/netcdf/syncfolderfrancesco HTTP/1.1" 1057 Jersey/2.13 (HttpUrlConnection 1.8.0_131)
		 */

		
		String baseName="ThreddsRootCatalog";
		String authority="www.d4science.org";
		String path="public/netcdf/syncfolderfrancesco";
		String folder="public/netcdf/Anothersyncfolderfrancesco";
		
		
		/*
		 * 
		  @QueryParam(Constants.AUTHORITY_PARAMETER) @DefaultValue("www.d4science.org") String authority,
			@QueryParam(Constants.BASE_NAME_PARAMETER) String baseName,
			@QueryParam(Constants.PATH_PARAMETER) String path,
			@QueryParam(Constants.FOLDER_PARAMETER) String folder)
			
			
			
		try {
			String scopeName=ScopeUtils.getCurrentScopeName();
			
			log.info("Received register catalog request under scope {} ",scopeName);
			
			if(baseName==null) {
				log.debug("Base name not provided, using VRE {} ",scopeName);
				baseName=scopeName+"_VRE";
			}
			
			if(folder==null) {
				log.debug("Folder not provided, using base name {} ",baseName);
				folder=baseName+"_folder";
			}
			
			if(path==null) {
				log.debug("Path not provided, using baseName {} ",baseName);
				path=baseName;
			}
			
			*/
			String datasetScanName=(baseName+" Catalog").replace("_", " ");
			String datasetScanId=baseName+"_in_"+folder;
			String catalogReference=(baseName).replaceAll("_", " ");
			
		
		
		
		
		try {
		
			mng.createCatalogFromTemplate(authority,path,datasetScanId,datasetScanName,folder,catalogReference);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
