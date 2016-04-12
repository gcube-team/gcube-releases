/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategoryItemInterface;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 3, 2013
 *
 */
public class SmartFolderTest {
	
	
	protected static Logger logger = Logger.getLogger(SmartFolderTest.class);
	
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling"; //PRODUCTION
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV

	private static Workspace workspace;
	
	public static void main(String[] args) {

		try {

			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
			ScopeProvider.instance.set(scope.toString());
			
			System.out.println("instancing workspace");
			workspace = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("francesco.mangiacrapa")
					.getWorkspace();
			//
			System.out.println("start");
			List<FileGridModel> smarts = getSmartFolderResultsByCategory(GXTCategoryItemInterface.SMF_IMAGES);
			
			for (FileGridModel fileGridModel : smarts) {
				System.out.println(fileGridModel.toString());
			}
			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<FileGridModel> getSmartFolderResultsByCategory(String category) throws Exception {
		
		try {
			
	
			GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();
			
			List<SearchItem> listWorkspaceItems = new ArrayList<SearchItem>();
			
			//Category IMAGES
			if(category.equals(GXTCategoryItemInterface.SMF_IMAGES.toString())){
					
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.IMAGE_DOCUMENT, FolderItemType.EXTERNAL_IMAGE);
				
			//Category BIODIVERSITY
			}else if(category.equals(GXTCategoryItemInterface.SMF_BIODIVERSITY.toString())){
					
//				listWorkspaceItems = workspace.getFolderItems(FolderItemType.AQUAMAPS_ITEM);

			//Category DOCUMENTS
			}else if(category.equals(GXTCategoryItemInterface.SMF_DOCUMENTS.toString())){
					
				listWorkspaceItems = workspace.getFolderItems(
						FolderItemType.EXTERNAL_FILE, 
						FolderItemType.EXTERNAL_PDF_FILE,
						FolderItemType.QUERY,
						FolderItemType.PDF_DOCUMENT,
						FolderItemType.METADATA,
//						FolderItemType.WORKFLOW_REPORT,
//						FolderItemType.WORKFLOW_TEMPLATE,
//						FolderItemType.URL_DOCUMENT,
						FolderItemType.DOCUMENT
						);
					
					//Category LINKS
			}else if(category.equals(GXTCategoryItemInterface.SMF_LINKS.toString())){
				
//				listWorkspaceItems = workspace.getFolderItems(FolderItemType.EXTERNAL_URL, FolderItemType.URL_DOCUMENT, FolderItemType.EXTERNAL_RESOURCE_LINK);
								
					//Category REPORTS
			}else if(category.equals(GXTCategoryItemInterface.SMF_REPORTS.toString())){
			
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.REPORT_TEMPLATE, FolderItemType.REPORT);
	
					//Category TIME SERIES
			}else if(category.equals(GXTCategoryItemInterface.SMF_TIMESERIES.toString())){
					
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.TIME_SERIES);
			}
			else
				new Exception("Smart folder category unknown");

			
			return builder.filterListFileGridModelItemByCategory(listWorkspaceItems, category);

		
		} catch (Exception e) {
			System.out.println("Error in server get smart folder by category");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
}
