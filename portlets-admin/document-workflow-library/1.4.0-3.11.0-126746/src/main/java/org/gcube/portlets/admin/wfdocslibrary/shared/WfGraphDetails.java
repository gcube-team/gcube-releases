package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class WfGraphDetails implements Serializable {

		private String id;
		private String name;
		private String author;
		private String status;
		private Date dateCreated;
		private String graph;
		
		public WfGraphDetails() {}

		public WfGraphDetails(String id, String name, String author,
				String status, Date dateCreated, String graph) {
			super();
			this.id = id;
			this.name = name;
			this.author = author;
			this.status = status;
			this.dateCreated = dateCreated;
			this.graph = graph;
		}


		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public Date getDateCreated() {
			return dateCreated;
		}

		public void setDateCreated(Date dateCreated) {
			this.dateCreated = dateCreated;
		}

		public String getGraph() {
			return graph;
		}

		public void setGraph(String graph) {
			this.graph = graph;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}		
		
}
