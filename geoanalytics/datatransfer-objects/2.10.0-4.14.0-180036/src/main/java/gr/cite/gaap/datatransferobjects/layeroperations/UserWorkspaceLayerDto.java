package gr.cite.gaap.datatransferobjects.layeroperations;

import gr.cite.gaap.datatransferobjects.LayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserWorkspaceLayerDto {
    private UUID id;
    private String title;
    private String name;
    private LayerInfo layer;
    private boolean isFolder;
    private List<UserWorkspaceLayerDto> contents = new ArrayList<UserWorkspaceLayerDto>();

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LayerInfo getLayer() { return layer; }

    public void setLayer(LayerInfo layer) { this.layer = layer; }

    public boolean isFolder() { return isFolder; }

    public void setFolder(boolean folder) { isFolder = folder; }

    public List<UserWorkspaceLayerDto> getContents() { return contents; }

    public void setContents(List<UserWorkspaceLayerDto> contents) { this.contents = contents; }
}
