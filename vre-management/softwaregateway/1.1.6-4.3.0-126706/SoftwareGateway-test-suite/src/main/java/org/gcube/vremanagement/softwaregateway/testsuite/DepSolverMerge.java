package org.gcube.vremanagement.softwaregateway.testsuite;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.filters.TokenFilter.FileTokenizer;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.FileUtilsExtended;
import org.junit.Before;
import org.junit.Test;

public class DepSolverMerge {

	public ArrayList<String> resultList=new ArrayList<String>();
	
	@Before
	public void init(){
		try {
			resultList.add(FileUtilsExtended.fileToString("target/dep1.txt"));
			resultList.add(FileUtilsExtended.fileToString("target/dep2.txt"));
			resultList.add(FileUtilsExtended.fileToString("target/dep3.txt"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
		StringBuffer resolvedDep=new StringBuffer();
		StringBuffer missingDep=new StringBuffer();
		String merged="";
		for(String result : resultList){
			String temp=result.substring(result.lastIndexOf("<ResolvedDependencies>"), result.lastIndexOf("</ResolvedDependencies>"));
			temp=temp.substring(22);
			
//			System.out.println("appending: "+temp);
			resolvedDep.append(temp);
			String temp2=result.substring(result.lastIndexOf("<MissingDependencies>"), result.lastIndexOf("</MissingDependencies>"));
			temp2=temp2.substring(21);
			
//			System.out.println("appending: "+temp2);
			missingDep.append(temp2);
		}
		String result="<ResolvedDependencies>\n"+resolvedDep.toString()+"</ResolvedDependencies>\n"+"<MissingDependencies>\n"+missingDep.toString()+"</MissingDependencies>\n";
		result=result.replaceAll("<Dependency>", "\t<Dependency>\n");
		result=result.replaceAll("</Dependency>", "\t</Dependency>\n");
		result=result.replaceAll("<MissingDependency>", "\t<MissingDependency>\n");
		result=result.replaceAll("</MissingDependency>", "\t</MissingDependency>\n");
		result=result.replaceAll("<Service>", "\t<Service>\n");
		result=result.replaceAll("</Service>", "\n\t\t</Service>\n");
		result=result.replaceAll("<Class>", "\t\t<Class>");
		result=result.replaceAll("</Class>", "</Class>\n");
		result=result.replaceAll("<Name>", "\t\t<Name>");
		result=result.replaceAll("</Name>", "</Name>\n");
		result=result.replaceAll("<Version>", "\t\t<Version>");
		result=result.replaceAll("</Version>", "</Version>");
		result=result.replaceAll("<Package>", "\t<Package>");
		result=result.replaceAll("</Package>", "</Package>\n");
		System.out.println("result is: \n"+result);
	}

}
