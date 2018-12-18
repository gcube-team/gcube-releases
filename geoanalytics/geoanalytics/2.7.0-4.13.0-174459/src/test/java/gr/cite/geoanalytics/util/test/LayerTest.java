package gr.cite.geoanalytics.util.test;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.manager.LayerManager;
import junit.framework.Assert;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml", "classpath:geoanalytics-security.xml" })
public class LayerTest {

	LayerManager layerManager;
	
	@Inject
	public void setLayerManager(LayerManager layerManager){
		this.layerManager = layerManager;
	}
	
	
	@Test
	public void test_smth() {
		assertNotNull(layerManager);
	}
	
	
//	public static void main(String [] args) throws Exception{
//		
//		ApplicationContext context =  new ClassPathXmlApplicationContext("applicationContext.xml","geoanalytics-security.xml");
//	    BeanFactory factory = context;
//	    LayerManager layerManager = (LayerManager) factory.getBean("layerManager");
//	    
//	    Layer layer = new Layer();
//	    layer.setId(UUID.randomUUID());
//	    layer.setName("Sample layer "+UUID.randomUUID().toString());
//	    layer.setCreationDate(new Date());
//	    layer.setLastUpdate(new Date());
//	    layer.setIsActive((short)1);
//	    layerManager.createLayer(layer);
//	    System.out.println(layer);
//		
//	}
	
}
