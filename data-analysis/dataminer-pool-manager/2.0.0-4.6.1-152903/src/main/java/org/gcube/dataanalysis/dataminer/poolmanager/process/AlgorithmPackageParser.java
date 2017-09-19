package org.gcube.dataanalysis.dataminer.poolmanager.process;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;

public class AlgorithmPackageParser {

  /**
   * The name of the file containing algorithm metadata. Expected in the root
   * directory of the package.
   */
  private static final String METADATA_FILE_NAME = "Info.txt";

  private static final String METADATA_USERNAME = "Username";

  private static final String METADATA_FULLNAME = "Full Name";

  private static final String METADATA_EMAIL = "Email";
  
  private static final String METADATA_LANGUAGE = "Language";
  
  private static final String METADATA_CATEGORY = "Algorithm Category";
  
  private static final String METADATA_ALGORITHM_NAME = "Algorithm Name";

  private static final String METADATA_ALGORITHM_DESCRIPTION = "Algorithm Description";

  private static final String METADATA_CLASS_NAME = "Class Name";

  private static final String METADATA_PACKAGES = "Packages";

  private static final String METADATA_KEY_VALUE_SEPARATOR = ":";

  private static final int BUFFER_SIZE = 4096;


  public Algorithm parsePackage(String url) throws IOException {
    String packageMetadata = this.getPackageMetadata(url);
    if (packageMetadata == null) {
      System.out.println("WARNING: No metadata found for " + url);
      return null;
    } else {
      Map<String, List<String>> parsedMetadata = this.parseMetadata(packageMetadata);
      Algorithm a = this.createAlgorithm(parsedMetadata);
      a.setPackageURL(url);
      return a;
    }
  }


  private String getPackageMetadata(String url) throws IOException {
    InputStream is = new URL(url).openStream();
    ZipInputStream zipIs = new ZipInputStream(is);
    ZipEntry entry = zipIs.getNextEntry();
    String out = null;
    while (entry != null) {
      if (METADATA_FILE_NAME.equalsIgnoreCase(entry.getName())) {
        out = this.getEntryContent(zipIs);
        break;
      }
      entry = zipIs.getNextEntry();
    }
    is.close();
    zipIs.close();
    return out;
  }


  private String getEntryContent(ZipInputStream zipIn) throws IOException {
    StringBuilder s = new StringBuilder();
    byte[] buffer = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(buffer)) != -1) {
      s.append(new String(buffer, 0, read));
    }
    return s.toString();
  }


  private Map<String, List<String>> parseMetadata(String metadata) {
    Map<String, List<String>> out = new HashMap<String, List<String>>();
    String[] lines = metadata.split("\n");

    String key = null;
    String value = null;

    for (String line : lines) {
      // skip empty lines
      if (line.trim().isEmpty()) {
        continue;
      }
      // scan lines one by one, looking for key and values
      String[] parts = line.split(METADATA_KEY_VALUE_SEPARATOR);
      if (parts.length > 1) {
        // key and value on the same line
        key = parts[0].trim();
        value = line.substring(parts[0].length() + 1).trim();
      } else if (parts.length == 1) {
        // either a key or a value
        if (line.trim().endsWith(METADATA_KEY_VALUE_SEPARATOR)) {
          // key
          key = parts[0].trim();
          value = null;
        } else {
          // value
          value = line.trim();
        }
      }
      // add key+value to the map
      if (key != null && value != null) {
        List<String> values = out.get(key);
        if (values == null) {
          values = new Vector<>();
          out.put(key, values);
        }
        values.add(value);
        System.out.println(key + METADATA_KEY_VALUE_SEPARATOR + " " + values);
      }
    }
    return out;
  }

 
//  private Algorithm createAlgorithm(Map<String, List<String>> metadata) {
//    Algorithm out = new Algorithm();
//    out.setName(extractSingleValue(metadata, METADATA_ALGORITHM_NAME));
//    out.setDescription(extractSingleValue(metadata, METADATA_ALGORITHM_DESCRIPTION));
//    out.setClazz(extractSingleValue(metadata, METADATA_CLASS_NAME));
//    List<String> dependencies = extractMultipleValues(metadata, METADATA_PACKAGES);
//    if (dependencies != null) {
//      for (String pkg : dependencies) {
//        Dependency dep = new Dependency();
//        dep.setName(pkg);
//        dep.setType("os");
//        out.addDependency(dep);
//      }
//    }
//    return out;
//  }

  
  private Algorithm createAlgorithm(Map<String, List<String>> metadata) {
	    Algorithm out = new Algorithm();
	    out.setName(extractSingleValue(metadata, METADATA_ALGORITHM_NAME));
	    out.setDescription(extractSingleValue(metadata, METADATA_ALGORITHM_DESCRIPTION));
	    out.setClazz(extractSingleValue(metadata, METADATA_CLASS_NAME));
	    out.setEmail(extractSingleValue(metadata, METADATA_EMAIL));
	    out.setFullname(extractSingleValue(metadata, METADATA_FULLNAME));
	    out.setUsername(extractSingleValue(metadata, METADATA_USERNAME));
	    out.setLanguage(extractSingleValue(metadata, METADATA_LANGUAGE));
	    out.setCategory(extractSingleValue(metadata, METADATA_CATEGORY));
	    
	    List<String> dependencies = extractMultipleValues(metadata, "Package Name");
	    	    
	    if (dependencies != null) {
	      for (String pkg : dependencies) {   
	        Dependency dep = new Dependency();
	     	dep.setName(pkg);
	        out.addDependency(dep);
	        }
	    }
	    
	    
	    
	    
	    
	    
	    
	    
//	    List<String> rdependencies = extractMultipleValues(metadata, "cran");
//	    if (rdependencies != null) {
//	      for (String pkg : rdependencies) {   
//	        Dependency dep = new Dependency();
//	        
//	        //if (pkg.startsWith("os:")){
//	        dep.setName(pkg);
//	        dep.setType("cran");
//	        out.addDependency(dep);
//	        }
//	    }
//	    
//	    
//	    List<String> defdependencies = extractMultipleValues(metadata, "Packages");
//	    if (defdependencies != null) {
//	      for (String pkg : defdependencies) {   
//	        Dependency dep = new Dependency();
//	        
//	        //if (pkg.startsWith("os:")){
//	        dep.setName(pkg);
//	        dep.setType("os");
//	        out.addDependency(dep);
//	        }
//	    }
//	    
//	    List<String> osdependencies = extractMultipleValues(metadata, "os");
//	    if (osdependencies != null) {
//	      for (String pkg : osdependencies) {   
//	        Dependency dep = new Dependency();
//	        
//	        //if (pkg.startsWith("os:")){
//	        dep.setName(pkg);
//	        dep.setType("os");
//	        out.addDependency(dep);
//	        }
//	    }  
//	   
//	    
//	    
//	    List<String> gitdependencies = extractMultipleValues(metadata, "github");
//	    if (gitdependencies != null) {
//	      for (String pkg : gitdependencies) {   
//	        Dependency dep = new Dependency();
//	        
//	        //if (pkg.startsWith("os:")){
//	        dep.setName(pkg);
//	        dep.setType("github");
//	        out.addDependency(dep);
//	        }
//	    }  
//	    
//	    
//	    
//	    List<String> cdependencies = extractMultipleValues(metadata, "custom");
//	    if (cdependencies != null) {
//	      for (String pkg : cdependencies) {   
//	        Dependency dep = new Dependency();
//	        
//	        //if (pkg.startsWith("os:")){
//	        dep.setName(pkg);
//	        dep.setType("custom");
//	        out.addDependency(dep);
//	        }
//	    } 
	    
	    
//	        if (pkg.startsWith("r:")){
//	        	//String results = StringEscapeUtils.escapeJava(pkg);
//		        dep.setName(pkg);
//		        dep.setType("cran");
//		    }
//	        if (pkg.startsWith("custom:")){
//		        dep.setName(pkg);
//		        dep.setType("custom");
//		    }
//	        if (!pkg.startsWith("os:")&&!pkg.startsWith("r:")&&!pkg.startsWith("custom:")){
//		        dep.setName(pkg);
//		        dep.setType("os");
//		    }
	    
	      
	    
	    return out;
	  }
  
  
  
  
  private static String extractSingleValue(Map<String, List<String>> metadata,
      String key) {
    List<String> l = metadata.get(key);
    if (l != null && l.size() == 1) {
      return l.get(0);
    } else {
      return null;
    }
  }

  
 
  
  private static List<String> extractMultipleValues(
      Map<String, List<String>> metadata, String key) {
    List<String> l = metadata.get(key);
    if (l != null) {
      return new Vector<>(l);
    } else {
      return null;
    }
  }

  
  public static void main(String[] args) {
	AlgorithmPackageParser ap = new AlgorithmPackageParser();
	String txt = 
			"Username: giancarlo.panichi\n"+
			"Full Name: Giancarlo Panichi\n"+
			"Email: g.panichi@isti.cnr.it\n"+
			"Language: R\n"+
			"Algorithm Name: RBLACKBOX\n"+
			"Class Name: org.gcube.dataanalysis.executor.rscripts.RBlackBox\n"+
			"Algorithm Description: RBlackBox\n"+
			"Algorithm Category: BLACK_BOX\n"+
			"Interpreter Version: 3.2.1\n"+
			"Packages:\n"+
			"Package Name: DBI\n"+
			"Package Name: RPostgreSQL\n"+
			"Package Name: raster\n"+
			"Package Name: maptools\n"+
			"Package Name: sqldf\n"+
			"Package Name: RJSONIO\n"+
			"Package Name: httr  \n"+
			"Package Name: data.table";

	
	ap.parseMetadata(txt);
}
  
}
