package org.gcube.portlets.user.topics.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author Massimiliano Assante  at ISTI-CNR 
 * (massimiliano.assante@isti.cnr.it)
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class HashtagsWrapper implements Serializable {
	private static final long serialVersionUID = -532083077958376460L;
	private boolean isInfrastructure;
	private  ArrayList<String> hashtags;
	public HashtagsWrapper(boolean isInfrastructure, ArrayList<String> hashtags) {
		super();
		this.isInfrastructure = isInfrastructure;
		this.hashtags = hashtags;
	}
	public boolean isInfrastructure() {
		return isInfrastructure;
	}
	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}
	public ArrayList<String> getHashtags() {
		return hashtags;
	}
	public void setHashtags(ArrayList<String> hashtags) {
		this.hashtags = hashtags;
	}
	public HashtagsWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
