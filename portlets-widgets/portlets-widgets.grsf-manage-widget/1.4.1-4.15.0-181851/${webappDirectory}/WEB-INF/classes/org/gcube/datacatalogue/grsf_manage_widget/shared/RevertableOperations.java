package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * For now only Merge can be reverted
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum RevertableOperations {

	MERGE("merge");
	//		DISSECT("dissect");
	private String name;

	private RevertableOperations(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}