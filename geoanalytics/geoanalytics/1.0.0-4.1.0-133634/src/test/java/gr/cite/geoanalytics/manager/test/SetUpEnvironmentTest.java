package gr.cite.geoanalytics.manager.test;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import gr.cite.geoanalytics.manager.Environment;

@Component
public class SetUpEnvironmentTest
{
	@Autowired
	Environment env;
	
	public void test() throws Exception
	{
		System.out.println(env.isSetUp());
	}
	
	public static void main(String[] args) throws Exception
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(context.getBean("environment"));
		System.out.println(context.getBean("environment"));
		System.out.println(Arrays.toString(context.getBeanNamesForType(Object.class)));
		SetUpEnvironmentTest t = (SetUpEnvironmentTest)context.getBean("setUpEnvironmentTest");
		t.test();	
	}
}
