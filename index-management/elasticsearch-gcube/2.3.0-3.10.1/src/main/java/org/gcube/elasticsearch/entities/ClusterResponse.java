package org.gcube.elasticsearch.entities;

import java.io.Serializable;
import java.util.List;

public class ClusterResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String clusterName;
	private Double score;
	private List<String> docs;
	
	public ClusterResponse(String clusterName, Double score,
			List<String> docs) {
		super();
		this.clusterName = clusterName;
		this.score = score;
		this.docs = docs;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public List<String> getDocs() {
		return docs;
	}

	public void setDocs(List<String> docs) {
		this.docs = docs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clusterName == null) ? 0 : clusterName.hashCode());
		result = prime * result + ((docs == null) ? 0 : docs.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
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
		ClusterResponse other = (ClusterResponse) obj;
		if (clusterName == null) {
			if (other.clusterName != null)
				return false;
		} else if (!clusterName.equals(other.clusterName))
			return false;
		if (docs == null) {
			if (other.docs != null)
				return false;
		} else if (!docs.equals(other.docs))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;
	}
	
	
}