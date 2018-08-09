import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
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
import org.junit.Test;


/**
 *
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 20, 2018
 */
public class WorkspaceInstance {



	public static String USERNAME = "francesco.mangiacrapa";
	public static String SCOPE = "/gcube";
	public static String TOKEN = "0e2c7963-8d3e-4ea6-a56d-ffda530dd0fa-98187548";

	public static String FIND_FILE_NAME = "francesco";

	public static String rootId = null;

	StorageHubWrapper storageHubWrapper = null;
    //Workspace workspace = null;


	@Before
	public void init(){

		//METHOD 1
		storageHubWrapper = new StorageHubWrapper(SCOPE, TOKEN);
		//workspace = storageHubWrapper.getWorkspace();

		//METHOD 2
//		StorageHubClientService storageHubClientService = new StorageHubClientService(SCOPE, TOKEN);
//		workspace = new WorkspaceStorageHubClientService.WorkspaceStorageHubClientServiceBuilder(storageHubClientService).
//		withAccounting(true).
//		withMapProperties(true).
//		build();
	}

	@Test
	public void getRoot() {
		System.out.println("Get Root test");
		WorkspaceFolder root;
		try {
			root = storageHubWrapper.getWorkspace().getRoot();
			rootId = root.getId();
			System.out.println(root);
		}
		catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//@Test
	public void getChildren() throws InternalErrorException{
		System.out.println("Get children test");

		WorkspaceFolder root = storageHubWrapper.getWorkspace().getRoot();

		try {
			List<? extends WorkspaceItem> children = storageHubWrapper.getWorkspace().getChildren(root.getId());
			int i = 0;
			for (WorkspaceItem workspaceItem : children) {
				System.out.println(++i+")"+workspaceItem);
				System.out.println(workspaceItem.getType() +" "+workspaceItem.getClass());
			}
		}
		catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getWorkspaceId()  {
		System.out.println("Getting the workspaceId test");
		try {
			Workspace workspace = storageHubWrapper.getWorkspace();
			if(workspace!=null){
				WorkspaceFolder root = workspace.getRoot();
				System.out.println("Retrieve the root "+root.getId()+" correctly");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getChildrenPerWorkspaceItem() throws InternalErrorException{
		System.out.println("Get children test per workspace item");

		WorkspaceFolder root = storageHubWrapper.getWorkspace().getRoot();

		try {
			List<? extends WorkspaceItem> children = storageHubWrapper.getWorkspace().getChildren(root.getId());
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
		catch (InternalErrorException | ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void findFileName() throws InternalErrorException{
		System.out.println("Find file name test");
		getRoot();
		try {
			List<WorkspaceItem> foundItems = storageHubWrapper.getWorkspace().find(FIND_FILE_NAME,rootId);

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
