package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

@Embeddable
public class PrincipalProjectId implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") 
	private UUID participant;
	@Type(type="org.hibernate.type.PostgresUUIDType") 
	private UUID project;

	public UUID getParticipant() {
		return participant;
	}

	public void setParticipant(UUID participant) {
		this.participant = participant;
	}

	public UUID getProject() {
		return project;
	}

	public void setProject(UUID project) {
		this.project = project;
	}

	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrincipalProjectId that = (PrincipalProjectId) o;

        if (participant != null ? !participant.equals(that.participant) : that.participant != null) return false;
        if (project != null ? !project.equals(that.project) : that.project != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (participant != null ? participant.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        return result;
    }

	@Override
	public Date getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getLastUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastUpdate(Date lastUpdate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UUID getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(UUID id) {
		// TODO Auto-generated method stub
		
	}
}
