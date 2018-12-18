import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.WorkspaceStorageHubClientService;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongItemTypeException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.ImageFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.PDFFile;
import org.junit.Before;


/**
 *
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 20, 2018
 */
public class WorkspaceInstance {

	public static String SCOPE = "/gcube";
	//public static String SCOPE = "/d4science.research-infrastructures.eu";


	public static String USERNAME = "massimiliano.assante";
	//public static String USERNAME = "francesco.mangiacrapa";

	public static String TOKEN; //YOU MUST SET THIS AS VM PARAMETER


	public static String FIND_FILE_NAME = "francesco";
	public static String rootId = null;
	private StorageHubWrapper storageHubWrapper = null;

	private WorkspaceStorageHubClientService workspace;

	@Before
	public void init(){

		TOKEN = System.getProperty("token");

		System.out.println("Read TOKEN: "+TOKEN);

		//METHOD 1
		storageHubWrapper = new StorageHubWrapper(SCOPE, TOKEN, false, false, true);
		//workspace = storageHubWrapper.getWorkspace();

		//METHOD 2
//		StorageHubClientService storageHubClientService = new StorageHubClientService(SCOPE, TOKEN);
//		workspace = new WorkspaceStorageHubClientService.WorkspaceStorageHubClientServiceBuilder(storageHubClientService).
//		withAccounting(false).
//		withMapProperties(true).
//		build();
	}

	//@Test
	public void getRoot() {
		System.out.println("Get Root test");
		WorkspaceFolder root;
		try {
			root = storageHubWrapper.getWorkspace().getRoot();
			rootId = root.getId();
			System.out.println("The root is:"+root);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//@Test
	public void getChildren() throws Exception{
		System.out.println("Get children test");

		WorkspaceFolder root = storageHubWrapper.getWorkspace().getRoot();
		List<? extends WorkspaceItem> children = storageHubWrapper.getWorkspace().getChildren(root.getId());

		//WorkspaceFolder root = workspace.getRoot();
		//List<? extends WorkspaceItem> children = workspace.getChildren(root.getId());

		int i = 0;
		for (WorkspaceItem workspaceItem : children) {
			System.out.println(++i+")"+workspaceItem);
			System.out.println(workspaceItem.getType() +" "+workspaceItem.getClass());


//			System.out.println(++i+")");
//			if(workspaceItem.isFolder()){
//				WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;
//				System.out.println("is public? "+folder.isPublicFolder());
//			}

			//printMap(workspaceItem);
		}

	}


	//@Test
	public void getFilteredChildren() throws Exception{
		System.out.println("Get children test");

		WorkspaceFolder root = storageHubWrapper.getWorkspace().getRoot();
		Class filterClass = org.gcube.common.storagehub.model.items.FolderItem.class;
		List<? extends WorkspaceItem> children = storageHubWrapper.getWorkspace().getFilteredChildren(root.getId(), filterClass);

		//WorkspaceFolder root = workspace.getRoot();
		//List<? extends WorkspaceItem> children = workspace.getChildren(root.getId());

		int i = 0;
		for (WorkspaceItem workspaceItem : children) {
			System.out.println(++i+")"+workspaceItem);
			System.out.println(workspaceItem.getType() +" "+workspaceItem.getClass());


//			System.out.println(++i+")");
//			if(workspaceItem.isFolder()){
//				WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;
//				System.out.println("is public? "+folder.isPublicFolder());
//			}

			printMap(workspaceItem);
			System.out.println("\n");
		}

	}

	public static void printMap(WorkspaceItem workspaceItem){
		if(workspaceItem.getPropertyMap()!=null){
			System.out.println(workspaceItem.getId() + " name: "+workspaceItem.getName() + " isFolder: "+workspaceItem.isFolder());
			System.out.println("Property Map: "+workspaceItem.getPropertyMap().getValues().toString());
		}
	}

	//@Test
	public void getWorkspaceId()  {
		System.out.println("Getting the workspaceId test");
		try {

			Workspace ws = storageHubWrapper.getWorkspace();

			//Workspace ws = workspace;

			if(ws!=null){
				WorkspaceFolder root = ws.getRoot();
				System.out.println("Retrieve the root "+root.getId()+" correctly");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void getChildrenPerWorkspaceItem() throws InternalErrorException{
		System.out.println("Get children test per workspace item");

		try {

			WorkspaceFolder root = storageHubWrapper.getWorkspace().getRoot();
			List<? extends WorkspaceItem> children = storageHubWrapper.getWorkspace().getChildren(root.getId());

//			WorkspaceFolder root = workspace.getRoot();
//			List<? extends WorkspaceItem> children = workspace.getChildren(root.getId());

			int i = 0;
			Map<String, List<WorkspaceItem>> map = new HashMap<String,List<WorkspaceItem>>();
			for (WorkspaceItem workspaceItem : children) {
				String key = workspaceItem.getType().name();
				List<WorkspaceItem> list = map.get(key);
				if(list==null){
					list = new ArrayList<WorkspaceItem>();
				}

				list.add(workspaceItem);
				map.put(key, list);
			}

			for (String key : map.keySet()) {
				List<WorkspaceItem> list = map.get(key);
				for (WorkspaceItem workspaceItem : list) {
					switch (workspaceItem.getType()) {
					case FILE_ITEM:
						FileItem fileItem = (FileItem) workspaceItem;
						switch (fileItem.getFileItemType()) {
						case PDF_DOCUMENT:
							PDFFile pdfFile = (PDFFile) fileItem;
							System.out.println("PDF_DOCUMENT "+pdfFile);
							PDFFile completeItem = (PDFFile) storageHubWrapper.getWorkspace().getItem(fileItem.getId(), true, true, true);
							System.out.println(completeItem);
							break;
						case IMAGE_DOCUMENT:
							ImageFile imageFile = (ImageFile) fileItem;
							System.out.println("IMAGE_DOCUMENT "+imageFile);
							break;
						default:
							break;
						}

						System.out.println(fileItem.getFileItemType() +" "+workspaceItem.getClass());


						break;
					default:
						break;
					}
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//@Test
	public void findFileName() throws InternalErrorException{
		System.out.println("Find file name test");
		getRoot();
		try {
			//List<WorkspaceItem> foundItems = storageHubWrapper.getWorkspace().find(FIND_FILE_NAME,rootId);
			List<? extends WorkspaceItem> foundItems = workspace.find(FIND_FILE_NAME,rootId);

			if(foundItems==null || foundItems.size()==0){
				System.out.println("No Items found with name: "+FIND_FILE_NAME+ " in the parent: "+rootId);
				return;
			}

			System.out.println("Items found with name: "+FIND_FILE_NAME+ " in the parent: "+rootId +" are:");
			for (WorkspaceItem workspaceItem : foundItems) {
				System.out.println(workspaceItem);
			}
		}
		catch (ItemNotFoundException | WrongItemTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
