package org.gcube.rest.index.common.entities;

import java.util.List;

public class ClusterResponse {
	private String clusterName;
	private Double score;
	private List<String> docs;

	public ClusterResponse(String clusterName, Double score, List<String> docs) {
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
	public String toString() {
		return "ClusterResponse [clusterName=" + clusterName + ", score="
				+ score + ", docs=" + docs + "]";
	}
	
	

}
