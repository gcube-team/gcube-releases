package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

public class WorkspaceManager {

	/*
	public static String uploadOnWorkspaceAndGetURL(AlgorithmConfiguration config, File localfile,String description,String mimeType) throws Exception{
		String url = null;
		ScopeProvider.instance.set(config.getGcubeScope());
		
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("gianpaolo.coro").getWorkspace();
 
		WorkspaceFolder root = ws.getRoot();
		
		String session = config.getParam(processingSession);
		
		WorkspaceFolder folder = root.createFolder("WPS Synch "+session, "WPS Synch - Folder of session: "+session);
		
		InputStream is = new FileInputStream(localfile);
 		FolderItem item = WorkspaceUtil.createExternalFile(root, localfile.getName(), description, mimeType, is);
 		url = item.getPublicLink(false);
 		
		if (url==null)
			throw new Exception ("Error: could not upload "+localfile.getAbsolutePath()+" on the Workspace in scope "+config.getGcubeScope());
		return url;
	}
	*/
	
}
