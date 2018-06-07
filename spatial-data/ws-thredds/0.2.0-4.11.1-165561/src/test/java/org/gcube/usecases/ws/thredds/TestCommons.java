package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.engine.impl.ThreddsController;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TestCommons {

	@Getter
	@AllArgsConstructor
	static class TestSet{
		String label;
		String scope;
		String folderId;
		String remotePath;
		String targetToken;
		String toCreateCatalogName;
		
		
		public String getFolderId() throws WorkspaceFolderNotFoundException, ItemNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException {
			if(folderId.startsWith("/"))
				return getWSIdByPath(folderId);
			else return folderId;
		}
		
	}
	
	private static Map<String,TestSet> configs=new HashMap<>();
	
	private static String toUseConfig="pre";
	
	
	static {
		
		configs.put("GP", new TestSet("GPTests","/d4science.research-infrastructures.eu","a8cd78d3-69e8-4d02-ac90-681b2d16d84d","","",""));
		
//		folderName="WS-Tests";
		
		configs.put("default", new TestSet("Default Tests","/gcube/devsec/devVRE","/Workspace/ThreddsDev","public/netcdf","f851ba11-bd3e-417a-b2c2-753b02bac506-98187548","main"));
		
		
		configs.put("pre", new TestSet("Default Tests","/gcube/preprod/preVRE","/Workspace/CMEMS","public/netcdf/CMEMS","97cfc53e-7f71-4676-b5e0-bdd149c8460f-98187548","main"));
		
	}
	
	
	public static String getWSIdByPath(String path) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		return ws.getItemByPath(path).getId();
	}
	
	
	public static void setScope() {		
		TokenSetter.set(configs.get(toUseConfig).getScope());
	}
	
	
	public static WorkspaceFolder getTestFolder() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, MalformedURLException, IOException {
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		try{
			return (WorkspaceFolder) ws.getItem(configs.get(toUseConfig).getFolderId());
		}catch(Throwable t) {
			// try to use path
			return (WorkspaceFolder) ws.getItemByPath(configs.get(toUseConfig).getFolderId());
		}
		
		
		
//		WorkspaceFolder folder=null;
//		try{
//			folder=ws.getRoot().createFolder(folderName+"2", "test purposes");
//		}catch(ClassCastException e) {
//			folder=(WorkspaceFolder) ws.getItemByPath("/Workspace/"+folderName+"2");
//		}
//		
//		String datasetUrl="https://thredds-d-d4s.d4science.org/thredds/fileServer/public/netcdf/test%20by%20Francesco/dissolved_oxygen_annual_5deg_ENVIRONMENT_BIOTA_.nc";
//		try {
//			folder.createExternalFileItem("dissolved_oxygen_annual_5deg_ENVIRONMENT_BIOTA_.nc", "nc test file", "application/x-netcdf", new URL(datasetUrl).openStream());
//		}catch(Exception e) {
//			// file already existing..
//		}
//		return folder;
	}
	
	
	public static ThreddsController getThreddsController() throws InternalException, WorkspaceFolderNotFoundException, ItemNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException {
		SynchFolderConfiguration config=getSynchConfig();
		return new ThreddsController(config.getRemotePath(), config.getTargetToken());
	}
	
	public static SynchFolderConfiguration getSynchConfig() throws WorkspaceFolderNotFoundException, ItemNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException {
		TestSet set=configs.get(toUseConfig);		
		return new SynchFolderConfiguration(set.getRemotePath(), "*.nc,*.ncml,*.asc", set.getTargetToken(),set.getToCreateCatalogName(),set.getFolderId());
	}
}
