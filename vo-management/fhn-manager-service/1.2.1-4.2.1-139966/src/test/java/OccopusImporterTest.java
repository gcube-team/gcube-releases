import java.io.IOException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.occopus.OccopusNodeDefinitionImporter;

public class OccopusImporterTest {

	
	public static void main(String[] args) throws IOException {
		ScopeProvider.instance.set("/gcube");
		
		OccopusNodeDefinitionImporter b = new OccopusNodeDefinitionImporter();
		b.importer();
//		try {
//			b.readScriptFromUrl("https://appdb.egi.eu/storage/cs/vapp/15819120-7ee4-4b85-818a-d9bd755a61f0/devsec-init");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
