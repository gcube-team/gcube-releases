package org.gcube.rest.commons.db.model.app;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.JAXBException;

import org.gcube.rest.commons.db.dao.core.ConverterRecord;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentMap;

import com.google.common.collect.Lists;

@Entity
//@SequenceGenerator(name = "SEQ_STORE", sequenceName = "host_node_model_id_seq", allocationSize = 1)
@Table(name = "host_node_model")
public class HostNodeModel extends ConverterRecord<HostNode> {
		private static final long serialVersionUID = 1L;
		
		@Column(name = "resourceId")
		private String resourceId;
		
		@Column(name = "scopes")
	    @ElementCollection
		private List<String> scopes;

		@Column(name = "profile", columnDefinition="TEXT")
		private String profile;
		
		
		public HostNodeModel() {
			super();
		}
		
		public HostNodeModel(HostNode base){
			this.copyFrom(base);
		}
		
		public String getResourceId() {
			return resourceId;
		}

		public void setResourceId(String resourceId) {
			this.resourceId = resourceId;
		}


		public List<String> getScopes() {
			return scopes;
		}

		public void setScopes(List<String> scopes) {
			this.scopes = scopes;
		}

		public String getProfile() {
			return profile;
		}
		
		public void setProfile(String profile) {
			this.profile = profile;
		}

		///
		@Override
		public final void copyFrom(HostNode resource){
			this.resourceId = resource.getId();
			if (resource.getScopes() != null)
				this.scopes = Lists.newArrayList(resource.getScopes());
			
			if (resource.getProfile() != null)
				try {
					this.profile = XMLConverter.convertToXML(resource.getProfile());
				} catch (JAXBException e) {
					e.printStackTrace();
				}
		}
		
		@Override
		public final HostNode copyTo() throws IllegalStateException {
			HostNode.Profile prof = null;
			try {
				prof = XMLConverter.fromXML(XMLConverter.stringToNode(this.profile), HostNode.Profile.class);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

			boolean scopesInit = false;
			try {
				if (!((PersistentBag) scopes).isEmpty())
					scopesInit = true;
			} catch (Exception e) {
			}
			
			List<String> scopeListsList = scopesInit? Lists.newArrayList(this.scopes) : new ArrayList<String>();
			HostNode resource = new HostNode(resourceId, scopeListsList, prof);
			
			return resource;
		}
}
