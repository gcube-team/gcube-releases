/**
 * 
 */
package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.scope.api.ScopeProvider;
import org.hamcrest.core.Is;

/**
 * @author valentina
 *
 */
public class TestZip {

	/**
	 * @param args
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws IOException 
	 * @throws UserNotFoundException 
	 */
	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, IOException, UserNotFoundException {
		ScopeProvider.instance.set("/gcube/devsec");


		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli")
				.getWorkspace();
		
		WorkspaceFolder destinationFolder = ws.getRoot();

		
		String zipPath = "/Workspace/zip/";
		WorkspaceFolder folder = null;
		try {
			folder = (WorkspaceFolder) ws.getItemByPath(zipPath);
		} catch (ItemNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File zip = ZipUtil.zipFolder(folder);
//		try{
//		List<String> idsToExclude = new ArrayList<String>();
//		idsToExclude.add("f9046c51-acc5-4197-b9cc-5b8bcb15b7dd");
//		idsToExclude.add("7d6992c4-c302-46d6-b841-32a3fd4ec0d4");
//		idsToExclude.add("1efd6adc-9669-429f-9692-643e5f117d52");
//		
//		File zip = ZipUtil.zipFolder(folder, false, idsToExclude);
		
		System.out.println(zip.getPath());
		
		
//		}catch (Exception e) {
//			System.out.println(e);
//		}
//		UnzipUtil.unzip(destinationFolder, new FileInputStream(zipPath), "statpress.zip");
	}

}
