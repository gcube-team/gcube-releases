/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * @Dec 17, 2013
 * 
 */
public class DephtVisitDonwloadFilesTester {

	
	protected static Logger logger = Logger.getLogger(SearchTextTest.class);
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling"; //PRODUCTION
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
	
	public static String USER = "francesco.mangiacrapa"; //PRODUCTION
	
	private static BufferedWriter writer;
	private static OutputStreamWriter out;
	
	static final String itemID = "165ba18a-a08a-42c6-ade5-9b93d1f844ac";
	static WorkspaceItem root;

	public static void main(String[] args) {

		try {
			
			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
			ScopeProvider.instance.set(scope.toString());
			
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(USER).getWorkspace();
			out = new OutputStreamWriter(new FileOutputStream("Csv-error-file.csv"), "UTF-8");
			writer = new BufferedWriter(out);
			
		    writer.write("Id;Name;Path;Parent");
		    
		    
			System.out.println("Start");
//			WorkspaceItem root = ws.getRoot();
			
			root = ws.getItem(itemID);
			
			depthVisit(root);

//			new Thread(){
//				public void run() {
//					try {
//						
//						depthVisit(root);
//						
//					} catch (InternalErrorException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				};
//				
//			}.start();
			
			System.out.println("waiting 10 sec..");
			Thread.sleep(10000);
			
			
			System.out.println("End");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.out.println("writer close");
			try {writer.close();} catch (Exception ex) {}
		}
	}

	public static void depthVisit(WorkspaceItem item) throws InternalErrorException {

		if (item.getType().equals(WorkspaceItemType.FOLDER) || item.getType().equals(WorkspaceItemType.SHARED_FOLDER)) {

			WorkspaceFolder folder = (WorkspaceFolder) item;
			System.out.println("\nVisit Folder: " + folder.getName() +";  Folder is shared: "+folder.isShared());
			List<? extends WorkspaceItem> children = folder.getChildren();

			for (WorkspaceItem workspaceItem : children) {
				depthVisit(workspaceItem);
			}
		} else {
			
			System.out.println("Item name: " + item.getName() +";  Parent Folder: "+item.getParent().getName());

			if(item.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
				
				FolderItem folderItem = (FolderItem) item;
				
				if(folderItem.getFolderItemType().equals(FolderItemType.EXTERNAL_FILE)){
					
					System.out.println("Item is external file: " + item.getName());
					
					final ExternalFile f = (ExternalFile) folderItem;
					
					try{

						 Thread t = new Thread(){
							
							 public void run() {
								 
								try {
									tryGetPayload(f);
									
								} catch (Exception e) {
									try {
										System.out.println("Item name: "+f.getName() + "not exists into storage");
										erroFile(f);
									} catch (InternalErrorException e1) {
									}
							
								}
							}
						 };
						 
						 t.start();

//						 System.out.println("join 3 sec");
//						 t.join(1000);
//
//						 System.out.println("join 1 sec terminated");
//						if(is==null)
//							throw new Exception("is is null");
					
					}catch (Exception e) {
						System.out.println("Item name: "+f.getName() + "not exists into storage");
						erroFile(f);
					}
				
				}
			}

		}
	}
		
	public static void tryGetPayload(ExternalFile f) throws Exception{
		
		try {
		
			InputStream is = f.getData();
			System.out.println("Get payload for file : "+f.getName() +" terminated");
			
			if(is==null)
				throw new Exception("is is null");
		
		} catch (InternalErrorException e1) {
			System.out.println("Error on :"+f.getName());
			throw new Exception("is is null");
		}
		
	}
	
	public synchronized static void erroFile(WorkspaceItem item) throws InternalErrorException{
		
		try{
			
			try {
			
				String parentName="";
				
				if(item.getParent()!=null)
					parentName = item.getParent().getName();
				
				writer.write("\n "+item.getId()+";"+item.getName()+";"+item.getPath()+";"+parentName);
			
			} catch (IOException ex) {
				ex.printStackTrace();
			} 


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
