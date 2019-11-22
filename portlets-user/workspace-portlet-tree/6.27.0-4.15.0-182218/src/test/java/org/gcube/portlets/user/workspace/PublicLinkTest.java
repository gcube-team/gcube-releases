////TODO IT MUST BE MOVED TO SHUB
///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
//import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
//import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Jul 3, 2013
// *
// */
//public class PublicLinkTest {
//	
//	
//	protected static Logger logger = Logger.getLogger(PublicLinkTest.class);
//	
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling"; //PRODUCTION
////	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	
//	public static void main(String[] args) {
//
//		try {
//
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
//			ScopeProvider.instance.set(scope.toString());
//			
//			Workspace ws = HomeLibrary
//					.getHomeManagerFactory()
//					.getHomeManager()
//					.getHome("leonardo.candela")
//					.getWorkspace();
//			//
//			
//			System.out.println("start get root");
//			WorkspaceItem root = ws.getRoot();
//
//			System.out.println("start get children");
//			List<? extends WorkspaceItem> children = root.getChildren();
//
//			
//			System.out.println("children size: "+children.size());
//			
//			int i=0;
//			for (WorkspaceItem workspaceItem : children) {
//				
//				
//				if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER) || workspaceItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
//					
//					WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;
//					
//					System.out.println(++i+") folder name: "+folder.getName() + " is shared: "+folder.isShared());
//				}else{
//					
//				
//					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
//					
//					FolderItem folderItem = (FolderItem) workspaceItem;
//					
//					String publicLink = getPubliLinkForFolderItem(folderItem);
//					
//					System.out.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", ### Public link: "+publicLink);
//					}
//					
//				}
//			}
//			System.out.println("end");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	protected static String getPubliLinkForFolderItem(FolderItem worspaceFolderItem) throws InternalErrorException{
//		
//		if(worspaceFolderItem==null)
//			return "";
//		
//		try{
//			
//			switch(worspaceFolderItem.getFolderItemType())
//			{
//				case EXTERNAL_IMAGE: 
//					return ((ExternalImage) worspaceFolderItem).getPublicLink();
//				case EXTERNAL_FILE: 
//					return ((ExternalFile) worspaceFolderItem).getPublicLink();
//				case EXTERNAL_PDF_FILE: 
//					return ((ExternalPDFFile) worspaceFolderItem).getPublicLink();
//				case EXTERNAL_URL: 
//					break;
//				case REPORT_TEMPLATE: 
//					break;
//				case REPORT:
//					break;
//				case QUERY: 
//					break;
//				case TIME_SERIES: 
//					break;
//	//			case AQUAMAPS_ITEM: 
//	//				break;
//				case PDF_DOCUMENT:
//					break;
//				case IMAGE_DOCUMENT: 
//					GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
//					return imgDoc.getPublicLink(false);
//				case DOCUMENT: 
//					break;
//				case URL_DOCUMENT: 
//					break;
//				case METADATA: 
//					break;
//				default:
//					return "";
//			}
//		
//		}catch (Exception e) {
//			logger.error("an error occurred when get public link for item: "+worspaceFolderItem.getName());
//			return "";
//		}
//		
//		return "";
//	}
//
//}
