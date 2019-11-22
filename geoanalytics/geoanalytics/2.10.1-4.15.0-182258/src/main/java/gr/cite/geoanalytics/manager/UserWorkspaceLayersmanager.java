package gr.cite.geoanalytics.manager;

import gr.cite.geoanalytics.dataaccess.entities.layer.UserWorkspaceLayer;
import gr.cite.geoanalytics.dataaccess.entities.userworkspacelayer.dao.UserWorkspaceLayerDao;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UserWorkspaceLayersmanager extends BaseManager {
    private static final Logger logger = LoggerFactory.getLogger(UserWorkspaceLayersmanager.class);
    private static final String LAYER_VISUALIZATION_FOLDER_NAME = "Layer Visualization Folder";
    private static final String LAYER_VISUALIZATION_FOLDER_DESCRIPTION = "This folder is used by the geospatial visualization service";

    private final String SCOPE_PROPERTY_KEY = "gcube.scope";

    @Autowired
    private UserWorkspaceLayerDao userWorkspaceLayerDao;

    public UUID retrieveUerWorkspaceLayerFolderId(String userScope, String token) {
        //First check for the folder id in the db.
        try {
            if(this.getSecurityContextAccessor().getPrincipal().getUserWorkspaceLayersOwner().size() > 0)
                return this.getSecurityContextAccessor().getPrincipal().getUserWorkspaceLayersOwner().iterator().next().getDirectoryId();
        } catch (Exception e) {
            logger.error("Attempting to get user workspace folder id from db but failed to retrieve principal. Retrieving folder id from service");
        }

        //If you don't find it in db then get it from the service
        this.configureLibrary(userScope, token);

        logger.info("Initializing user workspace library client");

        StorageHubClient shc = new StorageHubClient();
        FolderContainer rootContainer = shc.getWSRoot();

        return UUID.fromString(rootContainer.getId());

//        if(this.layerVisualizationFolderExists(rootContainer))
//            return UUID.fromString(this.getLayerVisualizationFolderId(rootContainer));
//
//        return UUID.fromString(this.createLayerVisualizationFolder(rootContainer).getId());
    }

    public FolderContainer retrieveUerWorkspaceLayerFolder(String userScope, String token, UUID folderId) throws StorageHubException {
        this.configureLibrary(userScope, token);

        logger.info("Initializing user workspace library client");

        StorageHubClient shc = new StorageHubClient();
        FolderContainer folderContainer = shc.open(folderId.toString()).asFolder();

        return folderContainer;
    }

    public InputStream downloadStreamOfUserWorkspaceFile(String userScope, String token, UUID fileId) throws StorageHubException {
        this.configureLibrary(userScope, token);

        StorageHubClient shc = new StorageHubClient();
        return shc.open(fileId.toString()).asFile().download().getStream();

    }

    public String getItemPathById(String id) {
        String itemPath = "";
        StorageHubClient shc = new StorageHubClient();
        try {
            itemPath = shc.open(id).asItem().get().getPath();
        } catch (StorageHubException e) {
            e.printStackTrace();
        }

        return itemPath;
    }

    @Transactional(rollbackFor = { Exception.class })
    public UserWorkspaceLayer createUserWorkspaceLayer(UserWorkspaceLayer newUserWorkspaceLayer) {
        logger.debug("Creating new user workspace layer in db");
        return this.userWorkspaceLayerDao.create(newUserWorkspaceLayer);
    }

    @Transactional(rollbackFor = { Exception.class })
    public UserWorkspaceLayer findUserWorkspaceLayerById(UUID id) {
        logger.debug("Creating new user workspace layer in db");
        return this.userWorkspaceLayerDao.read(id);
    }

    @Transactional(rollbackFor = { Exception.class })
    public void deleteById(UUID id) {
        logger.debug("Deleting userWorkspace entity with id: " + id + " from db");
        this.userWorkspaceLayerDao.delete(this.userWorkspaceLayerDao.read(id));
    }

    private void setSystemProperty(String userScope) {
        logger.debug("Setting user scope for user storage hub library purposes");

        System.setProperty(SCOPE_PROPERTY_KEY, userScope);
    }

    private void clearSystemProperty(String userScope) {
        logger.debug("Clearing user scope for user storage hub library purposes");

        System.clearProperty(userScope);
    }

    private boolean systemPropertyExists(String userScope) {
        logger.debug("Checking if user scope for user storage hub library exists");

        return System.getProperty(SCOPE_PROPERTY_KEY) != null;
    }

    private void setUserToken(String userToken) {
        logger.debug("Setting user token for user storage hub library purposes");

        SecurityTokenProvider.instance.set(userToken);
    }

    private void configureLibrary(String userScope, String token) {
        this.setUserToken(token);

        if(!this.systemPropertyExists(userScope))
            this.setSystemProperty(userScope);
    }

    private boolean layerVisualizationFolderExists(FolderContainer rootContainer) {
        logger.info("Checking if layer folder exists");

        boolean response = false;
        try {
            response = !rootContainer.findByName(LAYER_VISUALIZATION_FOLDER_NAME).getItems().isEmpty();
            if(response)
                logger.info("Found: " + rootContainer.findByName(LAYER_VISUALIZATION_FOLDER_NAME).getItems().get(0).getName());

        } catch (StorageHubException e) {
            logger.info("Workspace folder with name: " + LAYER_VISUALIZATION_FOLDER_NAME + " does not exis in user workspace");
        } finally {
            return response;
        }
    }

    private String getLayerVisualizationFolderId(FolderContainer rootContainer) {
        logger.info("Retrieve layer folder");

        String id = null;
        try {
            id = rootContainer.findByName(LAYER_VISUALIZATION_FOLDER_NAME).getItems().get(0).getId();
        } catch (StorageHubException e) {
            logger.error("Failed to retrieve workspace folder id of folder with name: " + LAYER_VISUALIZATION_FOLDER_NAME);
        } finally {
            return id;
        }
    }

    private FolderContainer createLayerVisualizationFolder(FolderContainer rootContainer){
        logger.info("Creating layer folder");

        FolderContainer theNewFolder = null;
        try {
            theNewFolder = rootContainer.newFolder(LAYER_VISUALIZATION_FOLDER_NAME, LAYER_VISUALIZATION_FOLDER_DESCRIPTION);
        } catch (Exception e) {
            logger.error("Failed to create folder with name: " + LAYER_VISUALIZATION_FOLDER_NAME + " in user workspace");
        } finally {
            return theNewFolder;
        }
    }
}
