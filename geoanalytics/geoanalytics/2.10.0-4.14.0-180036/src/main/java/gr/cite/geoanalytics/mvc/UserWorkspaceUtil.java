package gr.cite.geoanalytics.mvc;

import gr.cite.gaap.datatransferobjects.layeroperations.UserWorkspaceLayerPersist;

public class UserWorkspaceUtil {
    private String token;
    private String scope;
    private UserWorkspaceLayerPersist userWorkspaceLayerPersist = null;

    public UserWorkspaceUtil(String token, String scope, UserWorkspaceLayerPersist userWorkspaceLayerPersist) {
        this.token = token;
        this.scope = scope;
        this.userWorkspaceLayerPersist = userWorkspaceLayerPersist;
    }

    public String getToken() { return token; }

    public String getScope() { return scope; }

    public UserWorkspaceLayerPersist getUserWorkspaceLayerPersist() { return userWorkspaceLayerPersist; }
}
