package gr.cite.bluebridge.workspace;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.util.PortalUtil;

import gr.cite.bluebridge.analytics.web.PortletUtils;
import gr.cite.bluebridge.analytics.web.WorkspaceUtils;
import gr.cite.bluebridge.workspace.exceptions.CustomException;

@Controller
@RequestMapping("VIEW")
public class WorkspaceController {

	private static Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

	@ResourceMapping(value = "getWorkspace")
	public void getWorkspace(ResourceRequest request, ResourceResponse response) {
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		String scope = pContext.getCurrentScope(httpServletRequest);
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();

		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			WorkspaceSharedFolder vreFolder = ws.getVREFolderByScope(scope);
			WorkspaceFolder root = ws.getRoot();

			String name = vreFolder.getName();
			name = name.substring(name.lastIndexOf("-") + 1, name.length());

			Map<String, Object> vreNode = new HashMap<>();
			vreNode.put("text", "VRE " + name + " (Shared Folder) ");
			vreNode.put("type", "VRE/Folder");
			vreNode.put("children", true);
			vreNode.put("id", vreFolder.getId());
			vreNode.put("parent", "#");

			Map<String, Object> rootNode = new HashMap<>();
			rootNode.put("text", root.getName() + " of " + username);
			rootNode.put("type", "folder");
			rootNode.put("children", true);
			rootNode.put("id", root.getId());
			rootNode.put("parent", "#");

			List<Map<String, Object>> nodes = new ArrayList<>();
			nodes.add(vreNode);
			nodes.add(rootNode);

			PortletUtils.returnResponseAsJson(response, 200, nodes);
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, 500, "Could not load Workspace");
			logger.error("Could not load Workspace", e);
		}
	}

	@ResourceMapping(value = "getFolders")
	public void getFolders(ResourceRequest request, ResourceResponse response, @RequestParam("folderId") String folderId) {

		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			List<Map<String, Object>> nodes = new ArrayList<>();
			List<? extends WorkspaceItem> children = null;

			WorkspaceItem file = ws.getItem(folderId);
			if (file.isFolder()) {
				children = file.getChildren();
			}

			if (children != null) {
				for (WorkspaceItem item : children) {
					Map<String, Object> node = new HashMap<>();
					String name = item.getName();

					item.getProperties().getPropertyValue("jcr:mimeType");

					if (item.isFolder()) {
						node.put("type", "folder");
						node.put("children", true);
					} else {
						String mimeType = ((FolderItem) item).getMimeType();

						if (mimeType.equals("application/json")) {
							if (!item.getProperties().hasProperty("isEconomic")) {
								String economics = WorkspaceUtils.streamToString(((ExternalFile) item).getData());

								Map<String, String> properties = new HashMap<>();
								if (WorkspaceUtils.isValidAnalysis(economics)) {
									properties.put("isEconomic", "true");
								} else {
									properties.put("isEconomic", "false");
								}
								item.getProperties().addProperties(properties);
							}
							if (item.getProperties().getPropertyValue("isEconomic").equals("true")) {
								mimeType = "analysis";
							}
						}

						if (name.endsWith(".war")) {
							mimeType = "war";
						}

						node.put("type", mimeType);
					}

					node.put("text", name);
					node.put("id", item.getId());
					node.put("parent", folderId);
					nodes.add(node);
				}
			}

			PortletUtils.returnResponseAsJson(response, 200, nodes);
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, 500, "Could not load folders");
			logger.error("Could not load folders", e);
		}
	}

	@ResourceMapping(value = "createFolder")
	public void createFolder(ResourceRequest request, ResourceResponse response, @RequestParam("folderName") String folderName,
			@RequestParam("folderDescription") String folderDescription, @RequestParam("destinationFolderId") String destinationFolderId) {
		try {
			WorkspaceUtils.isEmpty(folderName);

			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			WorkspaceFolder destinationFolder = (WorkspaceFolder) ws.getItem(destinationFolderId);

			if (ws.exists(folderName, destinationFolderId)) {
				folderName = WorkspaceUtil.getUniqueName(folderName, destinationFolder);
			}

			WorkspaceFolder folder = ws.createFolder(folderName, folderDescription, destinationFolderId);

			logger.debug("Created folder " + folderName + " successfully!");

			Map<String, Object> node = new HashMap<>();
			node.put("id", folder.getId());
			node.put("text", folderName);
			node.put("parent", destinationFolderId);
			node.put("type", "folder");
			node.put("children", false);

			PortletUtils.returnResponseAsJson(response, 200, node);

			logger.info("Created folder " + folderName + " successfully!");
		} catch (CustomException e) {
			logger.error(e.getMessage(), e);
			PortletUtils.returnResponseAsJson(response, e.getStatusCode(), e.getMessage());
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, 500, "Could not create folder " + folderName);
			logger.error("Could not create folder " + folderName, e);
		}
	}

	@ResourceMapping(value = "removeFile")
	public void removeFile(ResourceRequest request, ResourceResponse response, @RequestParam("fileId") String fileId) {
		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			ws.removeItem(fileId);
			PortletUtils.returnResponseAsJson(response, 200, fileId);

			logger.info("Removed " + fileId + " successfully!");
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, 500, "Could not remove file");
			logger.error("Could not remove file", e);
		}
	}

	@ResourceMapping(value = "renameFile")
	public void renameFile(ResourceRequest request, ResourceResponse response, @RequestParam("fileId") String fileId, @RequestParam("fileNewName") String fileNewName) {

		try {
			WorkspaceUtils.isEmpty(fileNewName);

			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			WorkspaceItem item = ws.getItem(fileId);
			WorkspaceItem parent = item.getParent();

			WorkspaceUtils.fileExists(ws, fileNewName, parent.getId());
			item.rename(fileNewName);

			PortletUtils.returnResponseAsJson(response, 200, fileId);
		} catch (CustomException e) {
			logger.error(e.getMessage(), e);
			PortletUtils.returnResponseAsJson(response, e.getStatusCode(), e.getMessage());
		} catch (Exception e) {
			logger.error("Could not rename file", e);
			PortletUtils.returnResponseAsJson(response, 500, "Could not rename file");
		}
	}

	@ResourceMapping(value = "saveAnalysis")
	public void saveAnalysis(ResourceRequest request, ResourceResponse response, @RequestParam("analysis") String analysis, @RequestParam("analysisName") String analysisName,
			@RequestParam("analysisDescription") String analysisDescription, @RequestParam("destinationFolderId") String destinationFolderId) {

		logger.debug("analysisName = " + analysisName);
		logger.debug("analysisDescription = " + analysisDescription);
		logger.debug("destinationFolderId = " + destinationFolderId);

		try {
			WorkspaceUtils.isEmpty(analysisName);

			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			String mimeType = "application/json";
			analysisName += ".json";

			WorkspaceUtils.fileExists(ws, analysisName, destinationFolderId);

			InputStream is = new ByteArrayInputStream(analysis.getBytes(StandardCharsets.UTF_8));
			WorkspaceItem item = ws.createExternalFile(analysisName, analysisDescription, mimeType, is, destinationFolderId);

			Map<String, String> properties = new HashMap<>();
			properties.put("isEconomic", "true");
			item.getProperties().addProperties(properties);

			Map<String, Object> node = new HashMap<>();
			node.put("id", item.getId());
			node.put("text", analysisName);
			node.put("parent", destinationFolderId);
			node.put("type", "analysis");
			node.put("children", false);

			PortletUtils.returnResponseAsJson(response, 200, node);

			logger.info("Created analysis " + analysisName + " successfully!");
		} catch (CustomException e) {
			logger.error(e.getMessage(), e);
			PortletUtils.returnResponseAsJson(response, e.getStatusCode(), e.getMessage());
		} catch (Exception e) {
			logger.error("Could not save analysis " + analysisName, e);
			PortletUtils.returnResponseAsJson(response, 500, "Could not save analysis " + analysisName);
		}
	}

	@ResourceMapping(value = "loadAnalysis")
	public void loadAnalysis(ResourceRequest request, ResourceResponse response, @RequestParam("analysisId") String analysisId) {
		logger.debug("analysisId = " + analysisId);

		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			ExternalFile economicsFile = (ExternalFile) ws.getItem(analysisId);
			String economics = WorkspaceUtils.streamToString(economicsFile.getData());

			String date = new SimpleDateFormat("dd.MM.yy - hh.mm a").format(economicsFile.getCreationTime().getTime());
			int index = economics.lastIndexOf("}");
			economics = economics.substring(0, index) + ", \"date\" : \"" + date + "\" }";

			PortletUtils.returnResponse(response, 200, economics);

			logger.info("Loaded analysis " + analysisId + " successfully!");
		} catch (Exception e) {
			logger.error("Could not retrieve analysis from Workspace ", e);
			PortletUtils.returnResponseAsJson(response, 500, "Could not retrieve analysis from Workspace");
		}
	}

	@ResourceMapping(value = "getInfo")
	public void getInfo(ResourceRequest request, ResourceResponse response, @RequestParam("fileId") String fileId) {
		logger.debug("fileId = " + fileId);

		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			WorkspaceItem item = ws.getItem(fileId);

			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");

			Map<String, Object> node = new LinkedHashMap<>();
			node.put("Name", item.getName());
			node.put("Location", item.getPath());
			node.put("Type", item.getType());
			node.put("Created", formatter.format(item.getCreationTime().getTime()));
			node.put("Last Updated", formatter.format(item.getLastModificationTime().getTime()));
			node.put("Description", item.getDescription());

			PortletUtils.returnResponseAsJson(response, 200, node);

			logger.info("Info for " + fileId + " fetched successfully!");
		} catch (Exception e) {
			logger.error("Could not retrieve info ", e);
			PortletUtils.returnResponseAsJson(response, 500, "Could not retrieve info");
		}
	}
}