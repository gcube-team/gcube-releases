package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.Date;
import java.util.UUID;

public class UserWorkspaceLayerPersist {
    private UUID id;
    private String name;
    private String title;
    private String path;
    private Date lastUpdate;
    private Date creationDate;

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public Date getLastUpdate() { return lastUpdate; }

    public void setLastUpdate(Date lastUpdate) { this.lastUpdate = lastUpdate; }

    public Date getCreationDate() { return creationDate; }

    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
}
