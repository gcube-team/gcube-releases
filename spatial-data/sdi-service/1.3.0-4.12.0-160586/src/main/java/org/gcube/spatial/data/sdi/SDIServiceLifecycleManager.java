package org.gcube.spatial.data.sdi;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Stop;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@XmlRootElement(name = "sdi-lifecycle")
public class SDIServiceLifecycleManager extends ApplicationLifecycleHandler{

	
	
	public SDIServiceLifecycleManager() {
//		System.out.println("SDI Lifecycle manager created ");
//		System.out.println("persistence manager is "+persistence);
//		System.out.println("template manager is "+templateManager);
//		for(StackTraceElement el:Thread.currentThread().getStackTrace())
//			System.out.println(""+el);
	}
	
	
	@Override
	public void onStart(Start e) {		
		super.onStart(e);
		
	}
	
	@Override
	public void onStop(Stop e) {		
		super.onStop(e);
//		System.out.println("********************** SDI SHUTDOWN *****************************");
//		persistence.shutdown();
	}
	
}
