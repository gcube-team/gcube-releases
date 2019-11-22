package gr.cite.geoanalytics.dataaccess.entities.layer;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "\"UserWorkspaceLayer\"")
public class UserWorkspaceLayer implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable, Serializable {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType") // DEPWARN dependency to Hibernate and PostgreSQL
    @Column(name = "\"UWL_ID\"", nullable = false)
    private UUID id = null;

    @Column(name = "\"UWL_Name\"", nullable = false, length = 100)
    private String name;

    @Column(name ="\"UWL_Title\"", nullable = false, length = 100)
    private String title = null;

    @Column(name="\"UWL_LastUpdate\"", nullable = false)
    private Date lastUpdate = null;

    @Column(name="\"UWL_CreationDate\"", nullable = false)
    private Date creationDate = null;

    @ManyToOne
    @JoinColumn(name = "\"UWL_Owner\"", nullable = false)
    private Principal owner = null;

    @Column(name="\"UWL_Path\"", nullable = false)
    private String path = null;

    @Column(name="\"UWL_Directory_ID\"", nullable = false)
    private UUID directoryId = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="\"UWL_Layer\"", nullable = false)
    private Layer layer = null;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Principal getOwner() {
        return owner;
    }

    public void setOwner(Principal owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public UUID getDirectoryId() { return directoryId; }

    public void setDirectoryId(UUID directoryId) { this.directoryId = directoryId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWorkspaceLayer that = (UserWorkspaceLayer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserWorkspaceLayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", creationDate=" + creationDate +
                ", owner=" + owner +
                ", path='" + path + '\'' +
                ", layer=" + layer +
                '}';
    }
}
