package org.gcube.vremanagement.vremodeler.resources;

import java.util.ArrayList;
import java.util.List;


public class Body {

	private List<MainFunctionality> mainFunctionalities= new ArrayList<MainFunctionality>();

	public List<MainFunctionality> getMainFunctionalities() {
		return mainFunctionalities;
	}

	public void setMainFunctionalities(List<MainFunctionality> mainFunctionalities) {
		this.mainFunctionalities = mainFunctionalities;
	}	
}
