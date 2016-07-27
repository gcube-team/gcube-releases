package org.gcube.data.spd;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringWriter;
import java.util.Collections;

import javax.xml.bind.JAXBContext;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.Bindings;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Assert;
import org.junit.Test;


public class BindingsTest {

	@Test
	public void resultItemBindingTest() throws Exception{
		JAXBContext context = JAXBContext.newInstance(ResultItem.class);
		StringWriter sw = new StringWriter();
		ResultItem ri= new ResultItem("id", "scientificName");
		ri.setCommonNames(Collections.singletonList(new CommonName("en", "shark")));
		ri.setProducts(Collections.singletonList(new Product(ProductType.Occurrence, "key")));
		context.createMarshaller().marshal(ri, sw);
		System.out.println(sw.toString());
	}

	@Test
	public void toTree() throws Exception{
		ResultItem item= new ResultItem("id", "scientificName");
		Taxon taxon = new Taxon("parentId");
		taxon.setCitation("accordingTo");
		taxon.setRank("family");
		item.setParent(taxon);
		item.setRank("species");
		item.setCommonNames(Collections.singletonList(new CommonName("en", "shark")));
		
		item.setProducts(Collections.singletonList(new Product(ProductType.Occurrence, "key")));
		DataSet dataSet = new DataSet("id");
		dataSet.setName("name");
		dataSet.setCitation("cit");
		dataSet.setDataProvider(new DataProvider("id"));
		item.setDataSet(dataSet);
		Tree tree = item.toTree();
		StringWriter sw = new StringWriter();
		Bindings.toWriter(tree, sw);
		System.out.println(sw.toString());
		System.out.println(item.toString());
		System.out.println(ResultItem.fromTree(tree).toString());
		Assert.assertTrue(item.toString().equals(ResultItem.fromTree(tree).toString()));
	}
	
	@Test
	public void executeQuery(){
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment");
		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'")
			.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
			.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'") 
			.addCondition("not($resource/Profile/GHN[@UniqueID='8af5bdd0-ac2e-11e2-bbdc-dc9e09398171'])");
		//gcube/data/speciesproductsdiscovery/manager
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
					
		System.out.println(client.submit(query));
	}
	
	
	
}
