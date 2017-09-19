package gr.cite.gaap.datatransferobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentSearchSelection {
	private static Logger logger = LoggerFactory.getLogger(DocumentSearchSelection.class);
	private List<String> terms = null;
	private String creator = null;
	private String tenant = null;
	private String project = null;
	private String shape = null;
	
	

	public DocumentSearchSelection() {
		super();
		logger.trace("Initialized default contructor for DocumentSearchSelection");
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public String getCreator() {
		return creator;
	}

	public void setTenant(String customer) {
		this.tenant = customer;
	}

	public String getTenant() {
		return tenant;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}
}
