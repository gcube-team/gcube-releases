package org.gcube.datatransformation.datatransformationlibrary.model;

import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.imanagers.LocalInfoManager;
import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraphImpl;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A simple test case for the model package.
 * </p>
 */
public class ModelTest {

	/**
	 * @param args The arguments of the main.
	 * @throws Exception If error in the test.
	 */
	public static void main(String[] args) throws Exception {
		LocalInfoManager iManager = new LocalInfoManager();
		iManager.setProgramsFile("C:\\programs.xml");
		
		TransformationsGraphImpl graph = new TransformationsGraphImpl(iManager);
		
		Thread.sleep(10000);
		
		ContentType sourceContentType = new ContentType();
		sourceContentType.setMimeType("application/msword");
		
		ContentType targetContentType = new ContentType();
		targetContentType.setMimeType("text/xml");
		
		List<TransformationUnit> tus = graph.findApplicableTransformationUnits(sourceContentType, targetContentType, true);
		if(tus == null){
			System.out.println("No transformation units found");
		}else{
		for(TransformationUnit tu: tus){
			System.out.println(tu.getTransformationProgram().getId()+" - "+tu.getId());
		}}
			
		
//		TransformationProgram tp = getProgram("111111");
//		System.out.println("Name of the prorgam = "+tp.getName()+"\n----------");
//		System.out.println(tp.toXML());
//		System.out.println(tp.getTransformer().getSoftwarePackages().get(0).getId());
	}
}
