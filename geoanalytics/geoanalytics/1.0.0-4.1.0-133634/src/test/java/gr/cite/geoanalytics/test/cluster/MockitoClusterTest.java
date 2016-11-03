package gr.cite.geoanalytics.test.cluster;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.logicallayer.NodeAwareLayerOperations;

@Component
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MockitoClusterTest {

	NodeAwareLayerOperations retryingLayerOperations;
	
	@Inject
	@Qualifier("retryingLayerOperations")
	public void setRetryingLayerOperations(NodeAwareLayerOperations retryingLayerOperations) {
		this.retryingLayerOperations = retryingLayerOperations;
	}
	
	
	@Test
	public void SimpleMockitoTest() throws Exception {
		retryingLayerOperations.getAttributeValuesOfShapesByTerm(Mockito.mock(TaxonomyTermMessenger.class), Mockito.mock(Attribute.class));
	}
	
}
