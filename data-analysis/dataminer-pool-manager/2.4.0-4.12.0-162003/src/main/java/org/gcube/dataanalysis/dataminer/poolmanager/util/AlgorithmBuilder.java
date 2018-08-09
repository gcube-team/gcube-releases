package org.gcube.dataanalysis.dataminer.poolmanager.util;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.process.AlgorithmPackageParser;

import java.io.IOException;

/**
 * Created by ggiammat on 5/9/17.
 */
public class AlgorithmBuilder {


  public static Algorithm create(String algorithmPackageURL) throws IOException, InterruptedException {
    return create(algorithmPackageURL, null, null, null, null, null, null, null);
  }

  public static Algorithm create(String algorithmPackageURL, String vre, String hostname, String name, String description,
                                 String category, String algorithmType, String skipJava) throws IOException, InterruptedException {
    
	  
	  Algorithm algo =  new AlgorithmPackageParser().parsePackage(algorithmPackageURL);


    if(category != null){
      algo.setCategory(category);
    }
    if(algorithmType != null){
    	algo.setAlgorithmType(algorithmType);
    }
    if(skipJava != null){
    	algo.setSkipJava(skipJava);
    }
    if(skipJava != null){
    	algo.setSkipJava(skipJava);
    }
    if(name != null){
    	algo.setName(name);
    }
    if(description != null){
    	algo.setDescription(description);
    }
 
    return algo;
  }

}
