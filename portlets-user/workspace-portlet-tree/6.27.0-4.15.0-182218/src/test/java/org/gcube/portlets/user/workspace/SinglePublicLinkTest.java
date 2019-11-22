////TODO IT MUST BE MOVED TO SHUB
///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
//import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
//import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex;
//import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex.RESOLVER_TYPE;
//import org.gcube.portlets.user.workspace.server.util.HttpRequestUtil;
//import org.gcube.portlets.user.workspace.server.util.StringUtil;
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Jul 3, 2013
// *
// */
///*
// * 
// * 
// * IT MUST BE MOVED TO SHUB
// * 
// * 
// * 
//public class SinglePublicLinkTest {
//	
//	
//	protected static Logger logger = Logger.getLogger(SinglePublicLinkTest.class);
//	
////	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION
//	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
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
//					.getHome("francesco.mangiacrapa")
//					.getWorkspace();
//			//
//			
//			System.out.println("start get root");
//			WorkspaceItem root = ws.getRoot();
//			
//
//			for (WorkspaceItem wsi : root.getChildren()) {
//				if(wsi.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
//					try{
//						String publicLink = getPublicLinkForFolderItemId(wsi.getId(), true, ws);
//						System.out.println("\nITEM: "+wsi.getName());
//						System.out.println(publicLink);
////					break;
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}
//
//			
////			System.out.println("start get children");
////			List<? extends WorkspaceItem> children = root.getChildren();
////
////			
////			System.out.println("children size: "+children.size());
////			
////			int i=0;
////			for (WorkspaceItem workspaceItem : children) {
////				
////				
////				if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER) || workspaceItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
////					
////					WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;
////					
////					System.out.println(++i+") folder name: "+folder.getName() + " is shared: "+folder.isShared());
////				}else{
////					
////				
////					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
////					
////					FolderItem folderItem = (FolderItem) workspaceItem;
////					
////					String publicLink = getPubliLinkForFolderItem(folderItem);
////					
////					System.out.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", ### Public link: "+publicLink);
////					}
////					
////				}
////			}
//			System.out.println("end");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//
//	public static String getPublicLinkForFolderItemId(String itemId, boolean shortenUrl, Workspace workspace) throws Exception{
//
//		logger.trace("get Public Link For ItemId: "+ itemId);
//		
//		GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();
//		
//		try{
//
//			WorkspaceItem wsItem = workspace.getItem(itemId);
//			
//			if(wsItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
//	
//				FolderItem folderItem = (FolderItem) wsItem;
//				String storageID = builder.getStorageIDForFolderItem(folderItem);
//				
//				if(storageID==null || storageID.isEmpty())
//					throw new Exception("Sorry, public link on "+wsItem.getName() +" is not available");
//				
//				UriResolverReaderParameterForResolverIndex uriResolver = new UriResolverReaderParameterForResolverIndex(DEFAULT_SCOPE, RESOLVER_TYPE.SMP_ID);
//
//				String uriRequest = "";
//				
//				if(uriResolver!=null && uriResolver.isAvailable()){
//					
//					String itemName = StringUtil.removeSpecialCharacters(folderItem.getName());
//					itemName = StringUtil.replaceAllWhiteSpace(itemName, "_");
//					uriRequest =  uriResolver.resolveAsUriRequest(storageID, itemName, folderItem.getMimeType(), true);
//
//					//VALIDATE CONNECTION
//					if(!HttpRequestUtil.urlExists(uriRequest+"&validation=true"))
//						throw new Exception("Sorry, The Public Link for selected file is unavailable");
//					
////					if(shortenUrl)	
////						uriRequest = getShortUrl(uriRequest);
//					
//					return uriRequest;
//				}
//				else
//					throw new Exception("Sorry, The Uri resolver service is temporarily unavailable. Please try again later");
//				
//			}else{
//				logger.warn("ItemId: "+ itemId +" is not a folder item, sent exception Public Link  unavailable");
//				throw new Exception("Sorry, The Public Link for selected file is unavailable");
//			}
//
//		}catch (Exception e) {
//			logger.error("Error getPublicLinkForFolderItemId for item: "+itemId, e);
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//		
//	}
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
//
//*/