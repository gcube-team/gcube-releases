package org.gcube.data.analysis.statisticalmanager.test;

import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;

public class TestPrintUtils {
	
	public static void printServiceCapabilites(SMListGroupedAlgorithms features,  StatisticalManagerFactory factory) {
		
//		System.out.println("-------------------------- START ------------------------------------");
//		for(SMGroupedAlgorithms feature: features.getList()) {
//			
//    		
//    		System.out.println("CATEGORY: " +feature.getCategory());
//    	
//    		for(SMAlgorithm algorithm : feature.getList()) {
//    			System.out.println("ALGORITHM :" + algorithm.getName() + " ");
//    			System.out.println("ALGORITHM DESCRIPTION : " + algorithm.getDescription());
//    			System.out.println("PARAMETERS: ");
//    		 	
// //   		 	printComputationParameter(factory.getAlgorithmParameters(ComputationalAgentClass.fromString(algorithm.getCategory()), algorithm.getName()));
//    		 	
//    	    	System.out.println();
//    	    	System.out.println();
    		}
    	
//    	}
//		System.out.println("--------------------------  END  ------------------------------------");
//	}
	
	public static void printComputationParameter(SMParameters parameters) {
		
//		try {
//			
//			
//	 		for(SMParameter parameter : parameters.getList()) {
//	 			System.out.println();
//	 			System.out.println("  Param: "+ parameter.getName() + "\n" +
//	 					"  Description : " + parameter.getDescription() + "\n" +
//	 					"  Type : " +parameter.getType().getName());
//	 			for(String value : parameter.getType().getValues())
//	 				System.out.println("  Type value : " + value);
//	 			System.out.println("  Default value " + parameter.getDefaultValue());
//	 		}
//    	} catch (Exception e) {
//    		System.out.println(e);
//    	}
	}
	
	public static void printHistory(SMComputations computations) {
		
		
//		System.out.println("---------------- Computations -------------");
//		
//    	for (SMComputation item : computations.getList()) {
//    		System.out.println("Computation : " + item.getOperationId());
//    		System.out.println("StartData : " + item.getSubmissionDate());
//    		System.out.println("EndData : " + item.getCompletedDate());
//    		System.out.println(""+item.getAlgorithm());
//    		System.out.println(""+item.getDescription());
//    		
//    		if (item.getAbstractResource() != null) {
//    			SMResource resource = item.getAbstractResource().getResource();
//    			System.out.println("Resource type " + SMResourceType.values()[ resource.getResourceType()]);
// //   			System.out.println("Resource description " + resource.getDescription());
//    			System.out.println("Resource id" + resource.getResourceId());
//    			switch (SMResourceType.values()[resource.getResourceType()]) {
//				case TABULAR:
//					SMTable table = (SMTable)resource;
//					System.out.println("Table name" + table.getName());
//					System.out.println("Template " + table.getTemplate());
//					break;
//				case FILE:
//					SMFile file = (SMFile)resource;
//					System.out.println("File name " + file.getName());
//					System.out.println("File url " + file.getUrl());
//					break;
//				case OBJECT:
//					SMObject object = (SMObject)resource;
//					System.out.println("Object name " + object.getName());
//					System.out.println("Object url " + object.getUrl());
//					break;
//				default:
//					break;
//				}
//    		}
//    		
//    	}
//    	
//    	System.out.println("Computations size : "+computations.getList().length );
//    	
	}

}
