package org.gcube.datatransformation.datatransformationlibrary.adaptor;


import java.util.ArrayList;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;

public interface DTSAdaptor {

	public void addPlan(ArrayList<TransformationUnit> transformationUnits, ArrayList<ContentType> contentTypes) throws Exception;
	
	public void setTransPlan(TransformationDescription desc);

	public void SetScope(String string);

	public void setRequirements(String requirements);
	
	public void CreatePlan() throws Exception;
	
	public String ExecutePlan() throws Exception;
	
	public void finishedAddingPLans();
}
