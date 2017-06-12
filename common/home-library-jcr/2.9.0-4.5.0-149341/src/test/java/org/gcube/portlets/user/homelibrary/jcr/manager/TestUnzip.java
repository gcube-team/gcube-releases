/**
 * 
 */
package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * @author valentina
 *
 */
public class TestUnzip {

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

		
		String zipPath = "/home/valentina/Desktop/PARTE1.zip";
		try{
		UnzipUtil.unzip(destinationFolder, zipPath);
		}catch (Exception e) {
			System.out.println(e);
		}
//		UnzipUtil.unzip(destinationFolder, new FileInputStream(zipPath), "statpress.zip");
	}

}
