package org.gcube.informationsystem.publisher;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.log4j.helpers.Loader;
import org.gcube.informationsystem.publisher.scope.IValidatorContext;
import org.gcube.informationsystem.publisher.scope.ScopeValidatorScanner;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultScopeValidatorTest {
	
	static final String relativePath="/src/test/java/META-INF/services/org.gcube.informationsystem.publisher.scope.IValidatorContext";
	
//	@BeforeClass
	public static void deleteServiceInfo(){
		URL url=Thread.currentThread().getClass().getResource("/");
		if(url != null){
	    	File f =new File(url.getPath());
	    	String rootPath=f.getParentFile().getParentFile().getAbsolutePath();
	    	System.out.println(" "+f.exists()+" path "+rootPath);
	    	File service=new File(rootPath+relativePath);
	    	
	    	if(service.exists()){
	    		boolean del=service.delete();
	    		System.out.println("deleted? "+del);
	    	}
	    }
		System.out.println("url founded "+url);
			
	}
	
//	@Test
	public void testDefaultValidator(){
		IValidatorContext context=ScopeValidatorScanner.provider();
		List<Validator> list= context.getValidators();
		for(Validator validator : list){
			System.out.println("validator founded: "+validator.type());
		}
	}

}
