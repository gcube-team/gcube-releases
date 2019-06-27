import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.catalogue.resource.CkanCatalogueConfigurationsReader;
import org.gcube.datatransfer.resolver.catalogue.resource.GatewayCKANCatalogueReference;


/**
 * The Class CatalogueResolverTest.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 13, 2019
 */
public class CatalogueResolverTest {


	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		
		try {
		
			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			GatewayCKANCatalogueReference ckanCatalogueReference = CkanCatalogueConfigurationsReader.loadCatalogueEndPoints();
			System.out.println(ckanCatalogueReference.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
