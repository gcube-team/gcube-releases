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
