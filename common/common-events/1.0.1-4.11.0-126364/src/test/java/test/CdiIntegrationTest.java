package test;


import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executors;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.gcube.common.events.Hub;
import org.gcube.common.events.impl.DefaultHub;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;


@RunWith(JeeunitRunner.class)
public class CdiIntegrationTest {

	@Qualifier
	@Target({ElementType.FIELD,ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Configured {}
	
	
	@Inject
	Hub hub;

	@Inject @Configured
	Hub configuredHub;
	
	@Test 
	public void hubIsInjected() throws Exception {
	
		//assertNotNull(hub);
		assertNotNull(configuredHub);
	}
	
	@Produces  @Configured
	public static Hub configuredHub() {
		return new DefaultHub(Executors.newSingleThreadExecutor());
	}
	
	
}
