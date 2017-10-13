package org.gcube.vremanagement.vremodeler.impl.deploy;

import java.util.ArrayList;
import java.util.List;

public class GHNstoUse{
	String candidateForRM;
	List<String> ghns= new ArrayList<String>();
	
	public GHNstoUse(){}
	
	public GHNstoUse(String candidateForRM, List<String> ghns) {
		super();
		this.candidateForRM = candidateForRM;
		this.ghns = ghns;
	}



	public List<String> getGhns() {
		return ghns;
	}

	public void setGhns(List<String> ghns) {
		this.ghns = ghns;
	}
	
	
	
}