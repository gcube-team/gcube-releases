package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * Connect the current record with another record
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ConnectedBean extends GenericRecord{

	private static final long serialVersionUID = -4863776727351488790L;
	private boolean remove;
	private boolean connect;

	public ConnectedBean() {
		super();
	}

	public ConnectedBean(String knowledgeBaseId, String description,
			String shortName, String title, String url,
			String semanticIdentifier, String domain) {
		super(knowledgeBaseId, description, shortName, title, url, semanticIdentifier,
				domain);
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	@Override
	public String toString() {
		return "ConnectedBean [record=" + super.toString() + ", remove=" + remove + ", connect=" + connect + "]";
	}

}
