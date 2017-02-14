package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

@Entity
@IdClass(PrincipalProjectId.class)
@Table(name="\"PrincipalProject\"")
public class PrincipalProject implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
		@Id
		@ManyToOne
		@JoinColumn(name = "\"Principal_ID\"", nullable = false)
		private Principal participant;

		@Id
		@ManyToOne
		@JoinColumn(name = "\"Project_ID\"", nullable = false)
		private Project project;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "\"PRCP_PRJ_CreationDate\"", nullable = false)
		private Date creationDate = null;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "\"PRCP_PRJ_LastUpdate\"", nullable = false)
		private Date lastUpdate = null;

		public Principal getParticipant() {
			return participant;
		}

		public void setParticipant(Principal participant) {
			this.participant = participant;
		}

		public Project getProject() {
			return project;
		}

		public void setProject(Project project) {
			this.project = project;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public Date getLastUpdate() {
			return lastUpdate;
		}

		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
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
