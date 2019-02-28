package org.gcube.informationsystem.publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;

import org.gcube.informationsystem.publisher.scope.IValidatorContext;
import org.gcube.informationsystem.publisher.scope.ScopeValidatorScanner;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ScopeValidatorTest {

//	static IValidatorContext context;
	static final String IMPL_CLASS="org.gcube.informationsystem.scope.validator.ValidatorContextImpl";
	static final String relativePath="src/test/java/META-INF/services/org.gcube.informationsystem.publisher.scope.IValidatorContext";
	
//	@Rule
//	public static TemporaryFolder testFolder = new TemporaryFolder();
	
	public static File service;
	
//	@BeforeClass
	public static void writeServiceInfo() throws IOException{
		service=new File(relativePath);
		FileOutputStream file = new FileOutputStream(service);
	    PrintStream output = new PrintStream(file);
	    output.print(IMPL_CLASS);
	    output.flush();
	    output.close();
	    System.out.println("file writed ");
	}

	
//	@Test
	public void test(){
		IValidatorContext context=ScopeValidatorScanner.provider();
		List<Validator> list= context.getValidators();
		for(Validator validator : list){
			System.out.println("validator founded: "+validator.type());
		}
	}
	
//	@AfterClass
//	public static void deleteServiceInfo() throws IOException{
//		if(service.exists()){
//			boolean del=service.delete();
//			System.out.println("deleted? "+del);
//
//		}
//			
//	}

}
