package gr.cite.gaap.datatransferobjects;

import java.util.List;

public class DocumentSearchSelection {
	private List<String> terms = null;
	private String creator = null;
	private String tenant = null;
	private String project = null;
	private String shape = null;

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
