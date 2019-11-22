package org.gcube.documentstore.persistence.connections;


public class Nodes {
	
	private String nodes;

	public Nodes(String nodes) {
		super();
		this.nodes = nodes;
	}

	public String getNodes() {
		return nodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nodes other = (Nodes) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Nodes [nodes=" + nodes + "]";
	}
	
	
}
